package ch.samt.blockchain.nodefull;

import ch.samt.blockchain.common.utils.paramhandler.ParamHandler;

public class Main {

    public static void main(String[] args) throws Exception {
        var handler = new ParamHandler();

        handler.addArg("p", false, "port");
        handler.addArg("db", false, "file");
        handler.addFlag("h");

        try {
            handler.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.print(e.getMessage());
            return;
        }

        if (handler.getFlag("h")) {
            System.out.println("Arguments: [-p <port>] [-db <file>] [-h]");
            return;
        }

        int port = 5555; // Default port

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
