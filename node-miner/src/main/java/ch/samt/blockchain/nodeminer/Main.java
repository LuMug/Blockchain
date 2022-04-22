package ch.samt.blockchain.nodeminer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import ch.samt.blockchain.common.utils.paramhandler.ParamHandler;

public class Main {
    
    public static void main(String[] args) {
        var handler = new ParamHandler();

        handler.addArg("p", true, "port");
        handler.addArg("priv", true, "file");
        handler.addArg("db", false, "file");

        handler.parse(args);

        if (!handler.isComplete()) {
            System.out.println("Arguments: -p <port> -priv <file> [-db <file>]");
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(handler.getArg("p"));
        } catch (NumberFormatException e) {
            System.err.println("Invalid port: " + handler.getArg("p"));
            return;
        }

        byte[] priv = null;

        try {
            priv = Files.readAllBytes(Path.of(handler.getArg("priv")));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }

        MinerNode node = handler.isNull("db") ?
            new MinerNode(port, priv) :
            new MinerNode(port, priv, handler.getArg("db"));

        // Start service
        node.start();

        try (var scanner = new Scanner(System.in)) {
            while (scanner.hasNext()) {
                switch (scanner.nextLine()) {
                    case "pow" -> node.solvePoW();
                }
            }
        }
    }

}
