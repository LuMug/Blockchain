package ch.samt.blockchain.node;

import org.tinylog.Logger;

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

    @Override
    void broadcastTx(byte[] packet, Connection exclude) {
        if (!registerTx(packet)) {
            return;
        }

        for (var peer : super.neighbours) {
            if (peer != exclude) {
                peer.sendPacket(packet);
            }
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

    private boolean registerTx(byte[] data) {
        var packet = new SendTransactionPacket(data);

        if (mempool.contains(packet.getSignature())) {
            return false;
        }

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

        // miner.addTx(hash);

        return true;
    }


}