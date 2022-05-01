package ch.samt.blockchain.nodefull;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.utils.paramhandler.ParamHandler;

public class Main {

    public static void main(String[] args) throws Exception {
        var handler = new ParamHandler();

        handler.addArg("p", false, "port");
        handler.addArg("db", false, "file");
        handler.addFlag("help");

        try {
            handler.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.print(e.getMessage());
            return;
        }

        if (handler.getFlag("help")) {
            System.out.println("Arguments: [-p <port>] [-db <file>] [-help]");
            return;
        }

        int port = Protocol.Node.DEFAULT_PORT;

        if (!handler.isNull("p")) {
            try {
                port = Integer.parseInt(handler.getArg("p"));
            } catch (NumberFormatException e) {
                System.err.println("Invalid port: " + handler.getArg("p"));
                return;
            }
        }

        var node = handler.isNull("db") ?
            new HighLevelNode(port) :
            new HighLevelNode(port, handler.getArg("db"));

        // Start service
        node.start();

        // Interactive console
        node.attachConsole(System.in, System.out);
    }
}
