package ch.samt.blockchain.nodefull;

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
            System.err.println("Invalid port: " + args[0]);
            return;
        }
        
        var node = new HighLevelNode(port);

        // Start service
        node.start();

        // Interactive console
        node.attachConsole(System.in, System.out);
    } // TODO CHECK IF SERVICE ALREADY EXISTS
}
