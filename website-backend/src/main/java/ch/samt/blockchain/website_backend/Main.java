package ch.samt.blockchain.website_backend;

import java.util.Scanner;

import spark.Service;

public class Main {

    public static final int PORT = 80;

    public static final String PATH = "/home/paolo/Scrivania/blockchain/website-frontend";

    public static void main(String[] args) {
        // TODO separare webserver e api-node

        var http = Service
                .ignite()
                .port(PORT);

        if (args.length != 2) {
            System.out.println("You can enable SSL/HTTP by");
            System.out.println("java -jar backend-jar <keystore.jks> <password>");
        } else {
            http.secure(args[0], args[1], null, null);
        }

        var api = new BlockchainApi(http, PATH);

        api.init();

        // terminal
        new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                switch (scanner.next()) {
                    case "exit", "stop" -> api.stop();
                }
            }
        }).start();
    }

}