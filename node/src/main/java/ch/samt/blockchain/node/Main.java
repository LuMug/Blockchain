package ch.samt.blockchain.node;

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
        
        var node = new HighLevelNode(port);

        // Start service
        node.start();

        // Interactive console
        node.attachConsole(System.in, System.out);
    } // CHECK IF SERVICE ALREADY EXISTS
}
