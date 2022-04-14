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
    }

    private void processSendTransactionPacket(byte[] data) {
        
    }

    protected void onRegistration() {
        //
    }

}