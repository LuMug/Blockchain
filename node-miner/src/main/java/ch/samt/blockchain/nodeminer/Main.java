package ch.samt.blockchain.nodeminer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.utils.paramhandler.ParamHandler;

public class Main {
    
    public static void main(String[] args) {
        var handler = new ParamHandler();

        handler.addArg("priv", true, "file");
        handler.addArg("p", false, "port");
        handler.addArg("db", false, "file");
        handler.addFlag("help");

        try {
            handler.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.print(e.getMessage());
            return;
        }
        
        if (!handler.isComplete() || handler.getFlag("help")) {
            System.out.println("Arguments: -priv <file> [-p <port>] [-db <file>] [-help]");
            return;
        }

        int port = 5555; // Default prot

        if (!handler.isNull("p")) {
            try {
                port = Integer.parseInt(handler.getArg("p"));
            } catch (NumberFormatException e) {
                System.err.println("Invalid port: " + handler.getArg("p"));
                return;
            }
        }

        byte[] priv = null;

        try {
            priv = Files.readAllBytes(Path.of(handler.getArg("priv")));
            priv = Protocol.CRYPTO.fromBase64(priv);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }

        MinerNode node = handler.isNull("db") ?
            new MinerNode(port, priv) :
            new MinerNode(port, priv, handler.getArg("db"));

        // Start service
        node.start();

        while (true) {
            try {
                Thread.sleep(500);
                node.solvePoW();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        /*try (var scanner = new Scanner(System.in)) {
            while (scanner.hasNext()) {
                switch (scanner.nextLine()) {
                    case "pow" -> node.solvePoW();
                }
            }
        }*/
    }

}
