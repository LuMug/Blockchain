package ch.samt.blockchain.node;

import java.net.Socket;

public class HighLevelConnection extends Connection {

    public HighLevelConnection(Node node, Socket socket) {
        super(node, socket);
    }

    protected void processHighLevelPacket(byte[] data) {
        switch (data[0]) {
        
        }
    }

    protected void onRegistration() {
        //
    }

}