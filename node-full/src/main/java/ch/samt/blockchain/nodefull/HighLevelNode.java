package ch.samt.blockchain.nodefull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.tinylog.Logger;

import ch.samt.blockchain.common.protocol.DownloadDonePacket;
import ch.samt.blockchain.common.protocol.PoWSolvedPacket;
import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.protocol.RequestDownloadPacket;
import ch.samt.blockchain.common.protocol.SendTransactionPacket;
import ch.samt.blockchain.common.protocol.ServeOldPoWPacket;
import ch.samt.blockchain.common.protocol.ServeOldTransactionPacket;
import ch.samt.blockchain.nodefull.utils.ByteArrayKey;

public class HighLevelNode extends Node {

    private Mempool mempool = new Mempool();
    protected Miner miner = new Miner();

    private List<DownloadListener> downloadListeners = new LinkedList<>();

    private Connection blockchainSeeder;

    private Map<ByteArrayKey, MempoolData> mempoolDataMap = new HashMap<>();

    private byte[] lastNonce;
    private long lastBlockTimestamp = -1;

    private long difficulty;

    public HighLevelNode(int port, String db) {
        super(port, db);
    }

    public HighLevelNode(int port) {
        super(port);
    }

    @Override
    protected void initHighLevelNode() {
        difficulty = super.database.getDifficulty();
        miner.setDifficulty(difficulty);

        initPeriodicDownload();
    }

    @Override
    protected boolean broadcastTx(byte[] packet, Connection exclude, boolean live) {
        if (downloading() ^ !live) {
            return false;
        }

        if (registerTx(packet, live) && live) {
            broadcast(packet, exclude);
            return true;
        }
        return false;
    }

    @Override
    protected boolean broadcastPoW(byte[] packet, Connection exclude, boolean live) {
        if (downloading() ^ !live) {
            return false;
        }

        if (powSolved(packet, live) && live) {
            broadcast(packet, exclude);
            return true;
        }

        return false;
    }

    @Override
    protected boolean broadcastTx(byte[] packet) {
        return broadcastTx(packet, null, true);
    }

    @Override
    public boolean deployTx(byte[] packet) {
        SendTransactionPacket.setTimestamp(packet, System.currentTimeMillis());
        
        if (registerTx(packet, true)) {
            for (var peer : super.peers) {
                peer.sendPacket(packet);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean powSolved(byte[] data, boolean live) {
        var packet = new PoWSolvedPacket(data);

        var timestamp = packet.getTimestamp();

        if (live && System.currentTimeMillis() - timestamp > 15000) {
            Logger.info("PoW with invalid timestamp");
            return false;
        }

        var nonce = packet.getNonce();

        // Avoid flooding
        if (lastNonce != null && eq(nonce, lastNonce)) {
            return false;
        }

        lastNonce = packet.getNonce();

        if (!miner.isMined(nonce)) {
            Logger.info("Invalid Proof-of-Work received");
            return false;
        }

        int height = super.database.getBlockchainLength();

        super.database.updateUTXO(packet.getMiner(), Protocol.Blockchain.BLOCK_REWARD);

        var lastHash = super.database.getHash(height);

        if (lastHash == null) {
            lastHash = new byte[32];
        }

        ++height;

        byte[] hash = Protocol.CRYPTO.hashBlock(
            height,
            difficulty,
            miner.getTxHash(),
            nonce,
            packet.getMiner(),
            lastHash,
            timestamp
        );

        super.database.addBlock(
            difficulty,
            miner.getTxHash(),
            nonce,
            packet.getMiner(),
            timestamp,
            lastHash,
            hash
        );

        lastBlockTimestamp = packet.getTimestamp();

        if (height % Protocol.Blockchain.DIFFICULTY_ADJUSTMENT_RATE == 0) {
            var id = Math.max(height - Protocol.Blockchain.DIFFICULTY_ADJUSTMENT_DEPTH + 1, 1);
            var oldBlock = super.database.getBlock(id);
            var time = lastBlockTimestamp - oldBlock.timestamp();
            difficulty = (int) ((double) difficulty * Protocol.Blockchain.BLOCK_RATE * Protocol.Blockchain.DIFFICULTY_ADJUSTMENT_DEPTH / (double) time);

            difficulty = Math.max(1, difficulty);

            miner.setDifficulty(difficulty);
            Logger.info("New Difficulty: " + difficulty);
        }

        newBlock();
        return true;
    }

    @Override
    public int getBlockchainLength() {
        return super.database.getBlockchainLength();
    }

    private void newBlock() {
        int nextId = super.database.getBlockchainLength() + 1;
        
        Logger.info("Block: " + (nextId - 1));
        miner.clear();

        updateLastHash();
        miner.setDifficulty(super.database.getDifficulty()); //
        
        while (!mempool.isEmpty()) {
            var tx = mempool.drawOne();

            addToNextBlock(tx, nextId);
        }

        mempoolDataMap.clear();
    }

    private void addToNextBlock(SendTransactionPacket tx) {
        addToNextBlock(tx, super.database.getBlockchainLength() + 1);
    }
    
    private void addToNextBlock(SendTransactionPacket tx, int nextId) {
        byte[] hash = Protocol.CRYPTO.hashTx(
            tx.getSenderPublicKey(),
            tx.getRecipient(),
            tx.getAmount(),
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

        var sender = Protocol.CRYPTO.sha256(tx.getSenderPublicKey());
        var recipient = tx.getRecipient();
        var amount = tx.getAmount();

        super.database.updateUTXO(sender,    -amount);
        super.database.updateUTXO(recipient, +amount);
        
        var senderKey = new ByteArrayKey(sender);
        if (mempoolDataMap.containsKey(senderKey)) {
            var data = mempoolDataMap.get(senderKey);
            long v = data.getUtxoOffset() + amount;
            
            data.setUtxoOffset(v);
        }

        var recipientKey = new ByteArrayKey(recipient);
        if (mempoolDataMap.containsKey(recipientKey)) {
            var data = mempoolDataMap.get(recipientKey);
            long v = data.getUtxoOffset() - amount;
            
            data.setUtxoOffset(v);
        }

        System.out.println("Adding tx to db for next block: " + Protocol.CRYPTO.toBase64(hash));

        miner.addTxHash(hash);
    }

    private void updateLastHash() {
        int height = super.database.getBlockchainLength();
        var lastBlock = super.database.getBlock(height);
        
        if (lastBlock != null) {
            miner.setLastHash(lastBlock.hash());
        }
    }

    private boolean registerTx(byte[] data, boolean live) {
        var packet = new SendTransactionPacket(data);

        if (mempool.contains(packet.getSignature())) {
            return false;
        }

        // Check amount

        long amount = packet.getAmount();

        if (amount < 1) {
            Logger.info("Transaction with amount less than 1");
            return false;
        }

        var pubKey = packet.getSenderPublicKey();

        byte[] sender = Protocol.CRYPTO.sha256(pubKey);

        long utxo = super.database.getUTXO(sender);

        var senderKey = new ByteArrayKey(sender);
        MempoolData mempoolData = null;
        if (mempoolDataMap.containsKey(senderKey)) {
            mempoolData = mempoolDataMap.get(senderKey);
            utxo += mempoolData.getUtxoOffset();
        }

        if (amount > utxo) {
            Logger.info("Insufficient UTXO for " + Protocol.CRYPTO.toBase64(sender));
            return false;
        }

        var lastHash = packet.getLastTransactionHash();
        var actualLastHash = mempoolData != null ?
            mempoolData.getLastTxHash() :
            super.database.getLastTransactionHash(sender);

        if (!eq(lastHash, actualLastHash)) {
            Logger.info("Transaction with wrong lastHash");
            return false;
        }

        // Check signature

        byte[] toSig = SendTransactionPacket.toSign(
            packet.getRecipient(),
            pubKey,
            amount,
            lastHash
        );

        var pub = Protocol.CRYPTO.publicKeyFromEncoded(pubKey);

        var sig = packet.getSignature();

        if (!Protocol.CRYPTO.verify(sig, toSig, pub)) {
            Logger.warn("Transaction with invalid signature");
            return false;
        }

        super.database.cacheKey(packet.getSenderPublicKey(), sender);

        if (live && lastBlockTimestamp != -1 && lastBlockTimestamp - System.currentTimeMillis() > 5000) {
            addToNextBlock(packet);
        } else {
            mempool.add(packet);

            var recipient = packet.getRecipient();

            var hash = Protocol.CRYPTO.hashTx(
                pubKey,
                recipient,
                amount,
                lastHash,
                sig);
            
            var senderMapKey = new ByteArrayKey(sender);
            if (mempoolDataMap.containsKey(senderMapKey)) {
                var poolData = mempoolDataMap.get(senderMapKey);

                poolData.setUtxoOffset(poolData.getUtxoOffset() - amount);
                poolData.setLastTxHash(hash);
            } else {
                var tmp = new MempoolData();
                tmp.setUtxoOffset(-amount);
                tmp.setLastTxHash(hash);

                mempoolDataMap.put(senderMapKey, tmp);
            }

            var recipientKey = new ByteArrayKey(recipient);
            if (mempoolDataMap.containsKey(recipientKey)) {
                var poolData = mempoolDataMap.get(recipientKey);

                poolData.setUtxoOffset(poolData.getUtxoOffset() + amount);
            } else {
                var tmp = new MempoolData();
                tmp.setUtxoOffset(+amount);
                mempoolDataMap.put(recipientKey, tmp);
            }
        }

        Logger.info("Received transaction");

        return true;
    }

    @Override
    public int getIdByHash(byte[] hash) {
        return super.database.getId(hash);
    }

    private void initPeriodicDownload() {
        downloadBlockchain(true);

        schedule(
            () -> downloadBlockchain(false),
            40000, // const
            40000, // const
            TimeUnit.MILLISECONDS);
    }

    private void downloadBlockchain(boolean forceIfSameLength) {
        Logger.info("Checking peers blockchain lengths");
        var height = super.database.getBlockchainLength();

        int maxHeight = 0;
        List<Connection> maxConnections = new LinkedList<>();
        for (var peer : peers) {
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

            if (forceIfSameLength) {
                for (var listener : downloadListeners) {
                    listener.onDownloadStart();
                    listener.onDownloadEnd();
                }
            }

            return;
        }

        // TODO, some trust check/consensus form other peers.
        // e.g. ask all maxConnections if their last hash is the same

        if (maxHeight > height || (maxHeight == height && forceIfSameLength)) {
            downloadBlockchain(maxConnections.get(0));
        }
    }

    // force download even if same length at startup

    private void downloadBlockchain(Connection peer) {
        // notify listeners
        for (var listener : downloadListeners) {
            listener.onDownloadStart();
        }

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

        Logger.info("Downloading blockchain from peer");

        blockchainSeeder = peer;
        mempool.clear();
        miner.clear();
        
        if (startId < height) {
            if (startId == 0) {
                super.database.clear(); // faster to just wipe everything
            } else {
                super.database.deleteBlocksFrom(startId + 1);
            }
            Logger.info("Adopting another blockchain branch");
        }

        updateLastHash();
        miner.setDifficulty(super.database.getDifficulty());
        
        blockchainSeeder.initDownload(startId);
    }

    @Override
    public void downloadEnded(Connection from) {
        if (from == blockchainSeeder) {
            blockchainSeeder = null;

            for (var listener : downloadListeners) {
                listener.onDownloadEnd();
            }

            difficulty = super.database.getDifficulty();
        }
    }

    @Override
    public void oldTx(byte[] data) {

    }

    public boolean downloading() {
        return blockchainSeeder != null;
    }

    @Override
    public void serveBlockchain(byte[] data, Connection to) {
        super.scheduler.execute(() -> {
            var packet = new RequestDownloadPacket(data);

            int id = packet.getStartId() + 1;

            // e.g.
            // Send all tx of block 2
            // Send PoW of block 1
            // Send all tx of block 3
            // Send PoW of block 2 

            while (id <= super.database.getBlockchainLength()) {
                // send transactions
                var txs = super.database.getTransactions(id + 1); // TODO order by timestamp

                for (var tx : txs) {
                    var toSend = ServeOldTransactionPacket.create(
                        tx.recipient(),
                        tx.senderPub(),
                        tx.amount(),
                        tx.lastTxHash(),
                        tx.signature(),
                        tx.timestamp());
                    
                    to.sendPacket(toSend);
                }

                // send pow
                var pow = super.database.getBlock(id);

                var toSend = ServeOldPoWPacket.create(
                    pow.nonce(),
                    pow.miner(),
                    pow.timestamp()
                );
                
                to.sendPacket(toSend);

                ++id;
            }

            // send mempool
            
            // wait until active download ends
            while (downloading()) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {}
            }

            synchronized (mempool) {
                for (var tx : mempool.getTransactions()) {
                    var toSend = ServeOldTransactionPacket.create(
                        tx.getRecipient(),
                        tx.getSenderPublicKey(),
                        tx.getAmount(),
                        tx.getLastTransactionHash(),
                        tx.getSignature(),
                        tx.getTimestamp());

                    to.sendPacket(toSend);
                }
            }
 
            to.sendPacket(DownloadDonePacket.create());
        });
    }

    @Override
    public void registerDownloadListener(DownloadListener listener) {
        downloadListeners.add(listener);
    }

    @Override
    public void unregisterDownloadListener(DownloadListener listener) {
        downloadListeners.remove(listener);
    }

    private void broadcast(byte[] packet, Connection exclude) {
        for (var peer : super.peers) {
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