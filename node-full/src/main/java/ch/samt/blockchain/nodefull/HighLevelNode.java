package ch.samt.blockchain.nodefull;

import org.tinylog.Logger;

import ch.samt.blockchain.common.protocol.PoWSolvedPacket;
import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.protocol.SendTransactionPacket;

public class HighLevelNode extends Node {

    private Mempool mempool = new Mempool();
    private Miner miner = new Miner();
    
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
    void broadcastTx(byte[] packet, Connection exclude) {
        if (!registerTx(packet)) {
            return;
        }

        broadcast(packet, exclude);
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
    void powSolved(byte[] data) {
        var packet = new PoWSolvedPacket(data);

        miner.setNonce(packet.getNonce());
        if (!miner.isMined()) {
            // Setting nonce again removes the nonce,
            // Due to XOR property.
            miner.setNonce(packet.getNonce());
            Logger.info("Invalid PoW received");
            return;
        }

        super.database.addBlock(
            50, // DIFFICULTY
            miner.getTxHash(),
            packet.getNonce(),
            packet.getMiner(),
            packet.getTimestamp()
        );

        newBlock();
    }

    private void newBlock() {
        Logger.info("New block");
        miner.setHeight(super.database.getBlockchainLength() + 1);

        // mempool -> miner + database
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

}