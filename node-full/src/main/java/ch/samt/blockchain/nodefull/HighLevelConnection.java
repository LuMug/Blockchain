package ch.samt.blockchain.nodefull;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.protocol.RequestBlockchainLengthPacket;
import ch.samt.blockchain.common.protocol.RequestDownloadPacket;
import ch.samt.blockchain.common.protocol.RequestIfHashExistsPacket;
import ch.samt.blockchain.common.protocol.ServeBlockchainLengthPacket;
import ch.samt.blockchain.common.protocol.ServeIfHashExistsPacket;

import org.tinylog.Logger;

public class HighLevelConnection extends Connection {

    private BlockingQueue<Integer> requestedId = new ArrayBlockingQueue<>(1);
    private BlockingQueue<Integer> requestedLength = new ArrayBlockingQueue<>(1);
    //private BlockingQueue<byte[]> requestedLastHash = new ArrayBlockingQueue<>(1);

    public HighLevelConnection(Node node, Socket socket) {
        super(node, socket);
    }

    protected void processHighLevelPacket(byte[] data) {
        switch (data[0]) {
            case Protocol.SEND_TRANSACTION            -> processSendTransactionPacket(data);
            case Protocol.POW_SOLVED                  -> processPoWSolvedPacket(data);
            case Protocol.REQUEST_IF_HASH_EXISTS      -> processRequestIfHashExistsPacket(data);
            case Protocol.SERVE_IF_HASH_EXISTS        -> processServeIfHashExistsPacket(data);
            case Protocol.REQUEST_BLOCKCHAIN_LENGTH   -> processRequestBlockchainLengthPacket(data);
            case Protocol.SERVE_BLOCKCHAIN_LENGTH     -> processServeBlockchainLengthPacket(data);
            case Protocol.REQUEST_DOWNLOAD            -> processRequestDownloadPacket(data);
            case Protocol.SERVE_OLD_TX                -> processServeOldTransactionPacket(data);
            case Protocol.SERVE_OLD_POW               -> processServeOldPoWPacket(data);
            case Protocol.DOWNLOAD_DONE               -> processDownloadEndedPacket(data);
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

    @Override
    public void initDownload(int startId) {
        var packet = RequestDownloadPacket.create(startId);
        super.sendPacket(packet);
    }

    private void processPoWSolvedPacket(byte[] data) {
        super.node.broadcastPoW(data, this, true);
    }
    
    private void processSendTransactionPacket(byte[] data) {
        super.node.broadcastTx(data, this, true);
    }

    private void processServeIfHashExistsPacket(byte[] data) {
        var packet = new ServeIfHashExistsPacket(data);
        requestedId.add(packet.getId());
    }

    private void processServeBlockchainLengthPacket(byte[] data) {
        var packet = new ServeBlockchainLengthPacket(data);
        requestedLength.add(packet.getLength());
    }

    private void processRequestIfHashExistsPacket(byte[] data) {
        var packet = new RequestIfHashExistsPacket(data);
        int id = super.node.getIdByHash(packet.getHash());
        sendPacket(ServeIfHashExistsPacket.create(id));
    }

    private void processRequestBlockchainLengthPacket(byte[] data) {
        int length = super.node.getBlockchainLength();
        sendPacket(ServeBlockchainLengthPacket.create(length));
    }

    private void processDownloadEndedPacket(byte[] data) {
        super.node.downloadEnded(this);
    }

    private void processServeOldTransactionPacket(byte[] data) {
        super.node.broadcastTx(data, this, false);
    }
    
    private void processServeOldPoWPacket(byte[] data) {
        super.node.broadcastPoW(data, this, false);
    }

    protected void onRegistration() {
        //
    }

    private void processRequestDownloadPacket(byte[] data) {
        Logger.info("Receive download request from a peer");

        super.node.serveBlockchain(data, this);
    }

}