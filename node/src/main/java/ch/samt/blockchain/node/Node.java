package ch.samt.blockchain.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ch.samt.blockchain.common.protocol.RequestNodesPacket;
import ch.samt.blockchain.common.protocol.ServeNodesPacket;
import ch.samt.blockchain.common.utils.stream.PacketInputStream;
import ch.samt.blockchain.common.utils.stream.PacketOutputStream;

public class Node extends Thread {
    
    private int port;

    public Node(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("[NODE] :: Connecting to blockchain");

        try (var seeder = new Socket("127.0.0.1", 7676)) {
            var in = new PacketInputStream(seeder.getInputStream());
            var out = new PacketOutputStream(seeder.getOutputStream());

            var reqPacket = RequestNodesPacket.create(5);
            out.writePacket(reqPacket);

            var responseData = in.nextPacket();
            var responsePacket = new ServeNodesPacket(responseData);
            System.out.println("received: " + responsePacket.getNodes().length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
