package ch.samt.blockchain.nodefull;

import org.tinylog.Logger;

import ch.samt.blockchain.common.protocol.PoWSolvedPacket;
import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.protocol.SendTransactionPacket;

public class HighLevelNode extends Node {

    private Mempool mempool = new Mempool();
    private Miner miner = new Miner();

    private byte[] lastNonce;
    
    public HighLevelNode(int port, String db) {
        super(port, db);
    }

    public HighLevelNode(int port) {
        super(port);
    }

    {
        newBlock();
    }

    @Override
    protected void broadcastTx(byte[] packet, Connection exclude) {
        if (registerTx(packet)) {
            broadcast(packet, exclude);
        }
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
            System.out.println("sono uguali o nonulla");
            return false;
        }

        lastNonce = packet.getNonce();
        
        miner.setNonce(nonce);
        if (!miner.isMined()) {
            Logger.info("Invalid PoW received");
            return false;
        }

        super.database.updateUTXO(packet.getMiner(), Protocol.Blockchain.BLOCK_REWARD);

        super.database.addBlock(
            50, // DIFFICULTY
            miner.getTxHash(),
            nonce,
            packet.getMiner(),
            packet.getTimestamp()
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


    private void newBlock() {
        Logger.info("New blockS");
        
        miner.clear();

        int nextId = super.database.getBlockchainLength() + 1;

        miner.setHeight(nextId);

        // mempool -> miner + database
        for (int i = 0; i < 100 && !mempool.isEmpty(); i++) {
            var tx = mempool.drawOne();
            super.database.addTx(
                nextId,
                tx.getSenderPublicKey(),
                tx.getRecipient(),
                tx.getAmount(),
                tx.getAmount(), 
                tx.getLastTransactionHash(),
                tx.getSignature()
            );
        }
    }

    private boolean registerTx(byte[] data) {
        var packet = new SendTransactionPacket(data);

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
            Logger.info("Insufficient UTXO");
            return false;
        }

        // TODO: Check last hash

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

        int blockId = 0;

        super.database.addTx(
            blockId,
            packet.getSenderPublicKey(),
            packet.getRecipient(),
            packet.getAmount(),
            packet.getTimestamp(),
            packet.getLastTransactionHash(),
            packet.getSignature());

        super.database.updateUTXO(sender,                -amount);
        super.database.updateUTXO(packet.getRecipient(), +amount);

        var txHash = Protocol.CRYPTO.sha256(data);

        miner.addTx(txHash);

        return true;
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