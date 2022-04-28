package ch.samt.blockchain.nodefull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.tinylog.Logger;

import ch.samt.blockchain.common.protocol.PoWSolvedPacket;
import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.protocol.SendTransactionPacket;

public class HighLevelNode extends Node {

    private Mempool mempool = new Mempool();
    private Miner miner = new Miner();

    private Connection blockchainSeeder;

    private byte[] lastNonce;
    
    public HighLevelNode(int port, String db) {
        super(port, db);
    }

    public HighLevelNode(int port) {
        super(port);
    }

    @Override
    protected void initHighLevelNode() {
        initPeriodicDownload();
    }

    @Override
    protected boolean broadcastTx(byte[] packet, Connection exclude) {
        if (registerTx(packet)) {
            broadcast(packet, exclude);
            return true;
        }
        return false;
    }

    @Override
    protected boolean broadcastTx(byte[] packet) {
        return broadcastTx(packet, null);
    }

    @Override
    public void deployTx(byte[] packet) {
        SendTransactionPacket.setTimestamp(packet, System.currentTimeMillis());
        
        if (registerTx(packet)) {
            for (var peer : super.neighbours) {
                peer.sendPacket(packet);
            }
        }
    }

    @Override
    protected boolean powSolved(byte[] data) {
        var packet = new PoWSolvedPacket(data);

        var nonce = packet.getNonce();

        // Avoid flooding
        if (lastNonce != null && eq(nonce, lastNonce)) {
            return false;
        }

        lastNonce = packet.getNonce();
        
        miner.setNonce(nonce);
        if (!miner.isMined()) {
            Logger.info("Invalid PoW received");
            return false;
        }

        int height = super.database.getBlockchainLength();

        super.database.updateUTXO(packet.getMiner(), Protocol.Blockchain.BLOCK_REWARD);

        var lastHash = super.database.getHash(height);

        if (lastHash == null) {
            lastHash = new byte[32];
        }

        byte[] hash = Protocol.CRYPTO.hashBlock(
            height + 1,
            50,
            miner.getTxHash(),
            nonce,
            packet.getMiner(),
            lastHash,
            packet.getTimestamp()
        );

        super.database.addBlock(
            50, // DIFFICULTY
            miner.getTxHash(),
            nonce,
            packet.getMiner(),
            packet.getTimestamp(),
            lastHash,
            hash
        );

        newBlock();
        return true;
    }

    @Override
    protected void broadcastPoW(byte[] packet, Connection exclude) {
        if (powSolved(packet)) {
            broadcast(packet, exclude);
        }
    }

    @Override
    public int getBlockchainLength() {
        return super.database.getBlockchainLength();
    }

    private void newBlock() {
        Logger.info("New block");
        miner.clear();

        int nextId = super.database.getBlockchainLength() + 1;

        miner.setHeight(nextId);
        
        
        //for (int i = 0; i < 100 && !mempool.isEmpty(); i++) {
        while (!mempool.isEmpty()) {
            var tx = mempool.drawOne();

            byte[] hash = Protocol.CRYPTO.hashTx(
                nextId - 1,
                tx.getSenderPublicKey(),
                tx.getRecipient(),
                tx.getAmount(),
                tx.getTimestamp(),
                tx.getLastTransactionHash(),
                tx.getSignature()
            );

            super.database.addTx(
                nextId,
                tx.getSenderPublicKey(),
                tx.getRecipient(),
                tx.getAmount(),
                tx.getTimestamp(), 
                tx.getLastTransactionHash(),
                tx.getSignature(),
                hash
            );

            System.out.println("Adding tx to db for next block: " + Protocol.CRYPTO.toBase64(hash));

            // miner.addTx(hash);
        }
    }

    private boolean registerTx(byte[] data) {
        var packet = new SendTransactionPacket(data);

        // TODO if late MAX 5 seconds, add to currently minign block

        if (mempool.contains(packet.getSignature())) {
            return false;
        }

        // Check amount

        long amount = packet.getAmount();

        if (amount < 0) {
            Logger.info("Transaction with negative amount");
            return false;
        }

        byte[] sender = Protocol.CRYPTO.sha256(packet.getSenderPublicKey());

        long utxo = super.database.getUTXO(sender);

        if (amount > utxo) {
            Logger.info("Insufficient UTXO for " + Protocol.CRYPTO.toBase64(sender));
            return false;
        }

        // TODO: Check last hash (and if it is the first)

        // Check signature

        byte[] toSig = SendTransactionPacket.toSign(
            packet.getRecipient(),
            packet.getSenderPublicKey(),
            amount,
            packet.getLastTransactionHash()
        );

        var pub = Protocol.CRYPTO.publicKeyFromEncoded(packet.getSenderPublicKey());

        if (!Protocol.CRYPTO.verify(packet.getSignature(), toSig, pub)) {
            Logger.warn("Transaction with invalid signature");
            return false;
        }

        mempool.add(packet);

        Logger.info("Received transaction");

        super.database.cacheKey(packet.getSenderPublicKey(), sender);

        // TODO, do this only when adding tx to db, put these offsets
        // in a map
        super.database.updateUTXO(sender,                -amount);
        super.database.updateUTXO(packet.getRecipient(), +amount);

        return true;
    }

    @Override
    public int getIdByHash(byte[] hash) {
        return super.database.getId(hash);
    }

    private void initPeriodicDownload() {
        schedule(
            () -> downloadBlockchain(),
            40000, // const
            40000, // const
            TimeUnit.MILLISECONDS);
    }



    private void downloadBlockchain() {
        var height = super.database.getBlockchainLength();

        int maxHeight = 0;
        List<Connection> maxConnections = new LinkedList<>();
        for (var peer : neighbours) {
            int _height = peer.requestBlockchainLength();
            if (_height > maxHeight) {
                maxHeight = _height;
                maxConnections.clear();
                maxConnections.add(peer);
            } else if (_height == maxHeight) {
                maxConnections.add(peer);
            }
        }

        if (maxConnections.size() == 0) {
            Logger.warn("No peer to check blockchain length with");
            return;
        }

        if (maxHeight <= height) {
            return;
        }

        // TODO, some trust check/consensus form other peers.
        // e.g. ask all maxConnections if their last hash is the same

        downloadBlockchain(maxConnections.get(0));
    }

    // force download even if same length at startup

    private void downloadBlockchain(Connection peer) {
        var height = super.database.getBlockchainLength();
        
        int max = Math.min(height, 5);
        
        int startId = 0;

        if (max > 0) {
            for (int i = 0; i < max; i++) { // constant
                var req = super.database.getHash(height - i);
                int id = peer.requestIfHashExists(req);
                if (id != -1) {
                    startId = id;
                    break;
                }
            }
        }

        blockchainSeeder = peer;
        blockchainSeeder.initDownload();
    }

    @Override
    public void serveOldBlock(Connection from, byte[] packet) {
        if (!downloading() || from != blockchainSeeder) {
            return;
        }


    }

    @Override
    public void serveOldTx(Connection from, byte[] packet) {
        if (!downloading() || from != blockchainSeeder) {
            return;
        }


    }

    public boolean downloading() {
        return blockchainSeeder != null;
    }





    private void broadcast(byte[] packet, Connection exclude) {
        for (var peer : super.neighbours) {
            if (peer != exclude) {
                peer.sendPacket(packet);
            }
        }
    }

    private static boolean eq(byte[] arr1, byte[] arr2) {
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] != arr2[i]) {
                return false;
            }
        }

        return true;
    }

}