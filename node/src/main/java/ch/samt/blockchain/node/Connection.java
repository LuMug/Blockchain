package ch.samt.blockchain.node;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.protocol.RequestNodesPacket;
import ch.samt.blockchain.common.protocol.ServeNodesPacket;
import ch.samt.blockchain.common.utils.stream.PacketInputStream;
import ch.samt.blockchain.common.utils.stream.PacketOutputStream;

public class Connection extends Thread {

    private Node node;
    private Socket socket;
    private PacketInputStream in;
    private PacketOutputStream out;

    private BlockingQueue<byte[]> nodesRequestResponse;

    public Connection(Node node, Socket socket) {
        this.node = node;
        this.socket = socket;
        this.nodesRequestResponse = new ArrayBlockingQueue<>(1);
    }

    @Override
    public void run() {
        try {
            this.in = new PacketInputStream(socket.getInputStream());
            this.out = new PacketOutputStream(socket.getOutputStream());
            
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

    private boolean sendPacket(byte[] packet) {
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
        if (!sendPacket(RequestNodesPacket.create(amount))) {
            return new InetSocketAddress[0];
        }

        try {
            var response = nodesRequestResponse.take();
            var packet = new ServeNodesPacket(response);
            return packet.getNodes();
        } catch (InterruptedException e) {
            return new InetSocketAddress[0];
        }
    }

    private void processPacket(byte[] data) {
        if (data.length == 0) {
            return;
        }
        
        switch (data[0]) {
            case Protocol.REQUEST_NODES -> {
                // TODO
            }
        }
    }
   
}