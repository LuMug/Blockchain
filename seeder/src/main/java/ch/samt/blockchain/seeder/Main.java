package ch.samt.blockchain.seeder;

import java.util.Scanner;

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

        // Interactive console
        printHelp();
        try (var scanner = new Scanner(System.in)) {
            while (true) {
                switch (scanner.nextLine().toLowerCase()) {
                    case "help" -> printHelp();
                    case "list" -> {
                        System.out.println();
                        seeder.printNodes(System.out);
                        System.out.println();
                    }
                    case "stop" -> {
                        System.exit(0);
                    }
                }
            }
        }
    }

    private static void printHelp() {
        System.out.println("""
            Seeder console - help

            help\t\t Display this message
            list\t\t List all nodes
            stop\t\t Stop the service
        """);
    }

}