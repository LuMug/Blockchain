package ch.samt.blockchain.node;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Arguments: <port> [-db <file>]");
            return;
        }

        // TODO db param

        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port: " + args[0]);
            return;
        }
        
        var node = new HighLevelNode(port);

        // Start service
        node.start();

        if (port == 7777) {
            var data = Files.readAllBytes(Path.of("/home/paolo/Scrivania/blockchain/test.tx"));
            node.deployTx(data);
        }

        // Interactive console
        node.attachConsole(System.in, System.out);
    } // CHECK IF SERVICE ALREADY EXISTS
}
