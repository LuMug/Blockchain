package ch.samt.blockchain.nodefull;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.tinylog.Logger;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.protocol.RequestBlockchainLengthPacket;
import ch.samt.blockchain.common.protocol.RequestIfHashExistsPacket;
import ch.samt.blockchain.common.protocol.ServeBlockchainLengthPacket;
import ch.samt.blockchain.common.protocol.ServeIfHashExistsPacket;

public class HighLevelConnection extends Connection {

    private BlockingQueue<Integer> requestedId = new ArrayBlockingQueue<>(1);
    private BlockingQueue<Integer> requestedLength = new ArrayBlockingQueue<>(1);

    public HighLevelConnection(Node node, Socket socket) {
        super(node, socket);
    }

    protected void processHighLevelPacket(byte[] data) {
        switch (data[0]) {
            case Protocol.SEND_TRANSACTION -> processSendTransactionPacket(data);
            case Protocol.POW_SOLVED -> processPoWSolvedPacket(data);
            case Protocol.REQUEST_IF_HASH_EXISTS -> processRequestIfHashExists(data);
            case Protocol.SERVE_IF_HASH_EXISTS -> processServeIfHashExists(data);
            case Protocol.REQUEST_BLOCKCHAIN_LENGTH -> processRequestBlockchainLength(data);
            case Protocol.SERVE_BLOCKCHAIN_LENGTH -> processServeBlockchainLength(data);
            default -> Logger.info("Unknown packet: " + data[0]);
        }
    }

    @Override
    public int requestIfHashExists(byte[] hash) {
        var packet = RequestIfHashExistsPacket.create(hash);
        
        super.sendPacket(packet);
        
        try {
            return requestedId.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int requestBlockchainLength() {
        var packet = RequestBlockchainLengthPacket.create();
        
        super.sendPacket(packet);
        
        try {
            return requestedLength.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void processPoWSolvedPacket(byte[] data) {
        super.node.broadcastPoW(data, this);
    }
    
    private void processSendTransactionPacket(byte[] data) {
        super.node.broadcastTx(data, this);
    }

    private void processServeIfHashExists(byte[] data) {
        var packet = new ServeIfHashExistsPacket(data);
        requestedId.add(packet.getId());
    }

    private void processServeBlockchainLength(byte[] data) {
        var packet = new ServeBlockchainLengthPacket(data);
        requestedLength.add(packet.getLength());
    }

    private void processRequestIfHashExists(byte[] data) {
        var packet = new RequestIfHashExistsPacket(data);
        int id = super.node.getIdByHash(packet.getHash());
        sendPacket(ServeIfHashExistsPacket.create(id));
    }

    private void processRequestBlockchainLength(byte[] data) {
        int length = super.node.getBlockchainLength();
        sendPacket(ServeBlockchainLengthPacket.create(length));
    }

    protected void onRegistration() {
        //
    }

}