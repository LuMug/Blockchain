package ch.samt.blockchain.node;

import java.util.LinkedHashSet;
import java.util.Set;

import org.tinylog.Logger;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.protocol.SendTransactionPacket;

public class HighLevelNode extends Node {

    private Set<byte[]> lastTxSigHashTable = new LinkedHashSet<>();
    
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

        if (lastTxSigHashTable.contains(packet.getSignature())) {
            return false;
        }
        
        // TODO check validity
        
        lastTxSigHashTable.add(packet.getSignature());

        // Limit table capacity
        if (lastTxSigHashTable.size() > Protocol.Node.MAX_TX_POOL_SIZE) {
            var oldest = lastTxSigHashTable.iterator().next();
            lastTxSigHashTable.remove(oldest);
        }

        Logger.info("Received transaction");

        int blockId = 0;

        super.database.addTx(
            blockId,
            packet.getSender(),
            packet.getRecipient(),
            packet.getAmount(),
            packet.getTimestamp(),
            packet.getLastTransactionHash(),
            packet.getSignature());

        return true;
    }


}