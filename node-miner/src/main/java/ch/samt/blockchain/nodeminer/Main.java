package ch.samt.blockchain.nodeminer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.tinylog.Logger;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.utils.paramhandler.ParamHandler;

public class Main {
    
    public static void main(String[] args) {
        var handler = new ParamHandler();

        handler.addArg("priv", true, "file");
        handler.addArg("p", false, "port");
        handler.addArg("db", false, "file");
        handler.addArg("cores", false, "number");
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

        int cpus = Runtime.getRuntime().availableProcessors();
        if (!handler.isNull("cores")) {
            try {
                cpus = Integer.parseInt(handler.getArg("cores"));
            } catch (NumberFormatException e) {
                Logger.error("Invalid number of cores: " + handler.getArg("cores"));
                System.exit(0);
            }

            if (cpus < 1) {
                Logger.error("Invalid number of cores: " + cpus);
                System.exit(0);
            }
        }

        // Start service
        node.start();

        Logger.info("Starting mining on " + cpus + " cores");
        node.startMining(cpus);

        node.attachConsole(System.in, System.out);
    }

}
