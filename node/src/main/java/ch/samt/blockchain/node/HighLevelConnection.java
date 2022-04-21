package ch.samt.blockchain.node;

import java.net.Socket;

import ch.samt.blockchain.common.protocol.Protocol;

public class HighLevelConnection extends Connection {

    public HighLevelConnection(Node node, Socket socket) {
        super(node, socket);
    }

    protected void processHighLevelPacket(byte[] data) {
        switch (data[0]) {
            case Protocol.SEND_TRANSACTION -> processSendTransactionPacket(data);
        }
        // serve hash of block bla bla
    }

    private void processSendTransactionPacket(byte[] data) {
        super.node.broadcastTx(data, this);
    }

    protected void onRegistration() {
        //
    }

}