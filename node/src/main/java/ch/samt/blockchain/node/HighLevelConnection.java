package ch.samt.blockchain.node;

public class HighLevelConnection extends Connection {

    public Connection(Node node, Socket socket) {
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