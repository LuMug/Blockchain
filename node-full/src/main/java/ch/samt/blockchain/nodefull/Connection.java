package ch.samt.blockchain.nodefull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.tinylog.Logger;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.protocol.RegisterNodePacket;
import ch.samt.blockchain.common.protocol.RequestNodesPacket;
import ch.samt.blockchain.common.protocol.ServeNodesPacket;
import ch.samt.blockchain.common.utils.stream.PacketInputStream;
import ch.samt.blockchain.common.utils.stream.PacketOutputStream;

public abstract class Connection extends Thread {

    protected UUID nodeUuid;
    protected InetSocketAddress serviceAddress;

    protected Node node;
    protected Socket socket;
    protected PacketInputStream in;
    protected PacketOutputStream out;

    private BlockingQueue<InetSocketAddress[]> nodesRequestResponse = new ArrayBlockingQueue<>(1);

    public Connection(Node node, Socket socket) {
        this.node = node;
        this.socket = socket;
    }

    public abstract int requestIfHashExists(byte[] hash);
    public abstract int requestBlockchainLength();
    abstract void initDownload();

    @Override
    public void run() {
        try {
            this.in = new PacketInputStream(socket.getInputStream());
            this.out = new PacketOutputStream(socket.getOutputStream());

            sendPacket(RegisterNodePacket.create(node.port, node.uuid));
            
            while (!in.hasEnded()) {
                var packet = in.nextPacket();

                if (packet != null) {
                    processPacket(packet);
                }
            }
        } catch (IOException e) {
            // discard connection
            node.disconnect(this);
            return;
        }
    }

    protected abstract void processHighLevelPacket(byte[] data);
    protected abstract void onRegistration();

    // Blocks thread caller until the node registers himself
    public void waitNodeRegistration(int timeout) {
        if (nodeUuid != null) {
            return;
        }

        int update = 100;
        timeout += update;
        while ((timeout -= update) > 0) {
            try {
                Thread.sleep(update);
            } catch (InterruptedException e) {
                return;
            }

            if (nodeUuid != null) {
                return;
            }   
        }
    }

    public void waitNodeRegistration() {
        waitNodeRegistration(-1);
    }

    public synchronized boolean sendPacket(byte[] packet) {
        try {
            out.writePacket(packet);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Socket getSocket() {
        return socket;
    }

    // should not be called by this thread, otherwise it will halt
    public InetSocketAddress[] requestNodes(int amount) {
        if (!sendPacket(RequestNodesPacket.create(amount, node.uuid))) {
            Logger.info("[NODE] :: Disconnecting offline node");
            node.disconnect(this);
            return new InetSocketAddress[0];
        }

        try {
            var response = nodesRequestResponse.take();
            return response;
        } catch (InterruptedException e) {
            return new InetSocketAddress[0];
        }
    }

    private void processPacket(byte[] data) {
        if (data.length == 0) {
            return;
        }
        
        switch (data[0]) {
            case Protocol.REGISTER_NODE -> processRegisterNodePacket(data);
            case Protocol.REQUEST_NODES -> processRequestNodesPacket(data);
            case Protocol.SERVE_NODES -> processServeNodesPacket(data);
            default -> processHighLevelPacket(data);
        }
    }

    private void processRegisterNodePacket(byte[] data) {
        if (nodeUuid != null) { // already registered
            return;
        }
        
        var packet = new RegisterNodePacket(data);
        int port = packet.getPort();
        this.serviceAddress = new InetSocketAddress(stripPort(socket.getRemoteSocketAddress().toString()), port);
        this.nodeUuid = packet.getUUID();
        node.registerNode(this);
        onRegistration();
    }
    
    private void processRequestNodesPacket(byte[] data) {
        var packet = new RequestNodesPacket(data);
        int amount = packet.getAmount();
        UUID exclude = packet.getExclude();
        var nodes = node.drawNodes(amount, exclude);
        var response = ServeNodesPacket.create(nodes);
        sendPacket(response);
    }

    private void processServeNodesPacket(byte[] data) {
        var packet = new ServeNodesPacket(data);

        var nodes = packet.getNodes();
        nodesRequestResponse.add(nodes);
    }
    
    // /xxx.xxx.xxx.xxx:port -> xxx.xxx.xxx.xxx
    private static String stripPort(String address) {
        int v = address.indexOf(":");

        if (v == -1) {
            return address;
        }

        return address.substring(1, v);
    }

    public InetSocketAddress getServiceAddress() {
        return serviceAddress;
    }

    public UUID getUuid() {
        return nodeUuid;
    }
   
}