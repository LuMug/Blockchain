package ch.samt.blockchain.seeder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.protocol.RegisterNodePacket;
import ch.samt.blockchain.common.protocol.RequestNodesPacket;
import ch.samt.blockchain.common.protocol.ServeNodesPacket;
import ch.samt.blockchain.common.utils.stream.PacketInputStream;
import ch.samt.blockchain.common.utils.stream.PacketOutputStream;

public class Connection extends Thread {
    
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
                var packet = new RegisterNodePacket(data);
                int port = packet.getPort();
                var node = new InetSocketAddress(socket.getInetAddress(), port);
                seeder.renew(node);
            }
            case Protocol.REQUEST_NODES -> {
                var packet = new RequestNodesPacket(data);
                int amount = packet.getAmount();
                var nodes = seeder.drawNodes(amount);
                var response = ServeNodesPacket.create(nodes);
                out.writePacket(response);
            }
        }
    }

}
