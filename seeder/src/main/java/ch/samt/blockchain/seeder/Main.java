package ch.samt.blockchain.seeder;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        var seeder = new Seeder(7676);

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