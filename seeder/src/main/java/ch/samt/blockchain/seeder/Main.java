package ch.samt.blockchain.seeder;

public class Main {

    public static void main(String[] args) {
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

        var seeder = new Seeder(port);

        // Start service
        seeder.start();

        seeder.attachConsole(System.in, System.out);
    }

}