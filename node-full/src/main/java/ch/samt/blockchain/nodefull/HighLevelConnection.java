package ch.samt.blockchain.nodefull;

import java.net.Socket;

import org.tinylog.Logger;

import ch.samt.blockchain.common.protocol.Protocol;

public class HighLevelConnection extends Connection {

    public HighLevelConnection(Node node, Socket socket) {
        super(node, socket);
    }

    protected void processHighLevelPacket(byte[] data) {
        switch (data[0]) {
            case Protocol.SEND_TRANSACTION -> processSendTransactionPacket(data);
            case Protocol.POW_SOLVED -> processPoWSolvedPacket(data);
            default -> Logger.info("Unknown packet: " + data[0]);
        }
    }

    private void processPoWSolvedPacket(byte[] data) {
        super.node.powSolved(data);
    }

    private void processSendTransactionPacket(byte[] data) {
        super.node.broadcastTx(data, this);
    }

    protected void onRegistration() {
        //
    }

}