package ch.samt.blockchain.node;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

import ch.samt.blockchain.common.protocol.ServeNodesPacket;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Arguments: <port>");
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port: " + args[0]);
            return;
        }
        
        var node = new Node(port);

        // Start service
        node.start();

        // Interactive console
        node.attachConsole(System.in, System.out);
    }
}
