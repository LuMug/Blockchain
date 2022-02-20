package ch.samt.blockchain.seeder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.UUID;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.protocol.RegisterNodePacket;
import ch.samt.blockchain.common.protocol.RequestNodesPacket;
import ch.samt.blockchain.common.protocol.ServeNodesPacket;
import ch.samt.blockchain.common.utils.stream.PacketInputStream;
import ch.samt.blockchain.common.utils.stream.PacketOutputStream;

public class Connection implements Runnable {
    
    private Seeder seeder;
    private Socket socket;
    private PacketInputStream in;
    private PacketOutputStream out;

    public Connection(Seeder seeder, Socket socket) {
        this.seeder = seeder;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            this.in = new PacketInputStream(socket.getInputStream());
            this.out = new PacketOutputStream(socket.getOutputStream());
            
            // the connection should be discarded when one of the
            // two possible scenarios have occured.

            // Scenario 1:
            //      -> RegisterNodePacket
            // Scenario 2:
            //      -> RequestNodesPacket
            //      <- ServeNodesPacket

            var packet = in.nextPacket();
            if (packet != null) {
                processPacket(packet);
            }
        } catch (IOException e) {
            // discard connection
            return;
        }
    }

    // TODO: prevent NullPointerExceptions
    private void processPacket(byte[] data) throws IOException {
        switch (data[0]) {
            case Protocol.REGISTER_NODE -> {
                System.out.println("Node registered " + socket.getRemoteSocketAddress());
                var packet = new RegisterNodePacket(data);
                int port = packet.getPort();
                UUID uuid = packet.getUUID();
                var addr = new InetSocketAddress(socket.getInetAddress(), port);
                var node = new Node(addr, uuid);
                seeder.renew(node);
            }
            case Protocol.REQUEST_NODES -> {
                var packet = new RequestNodesPacket(data);
                int amount = packet.getAmount();
                UUID exclude = packet.getExclude();
                System.out.println(amount + " Node request from " + socket.getRemoteSocketAddress());
                var nodes = seeder.drawNodes(amount, exclude);
                System.out.println(nodes.length + " drawn");
                var response = ServeNodesPacket.create(nodes);
                out.writePacket(response);
            }
        }
    }

    public SocketAddress getSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    @Override
    public String toString() {
        return socket.getRemoteSocketAddress().toString();
    }

}
