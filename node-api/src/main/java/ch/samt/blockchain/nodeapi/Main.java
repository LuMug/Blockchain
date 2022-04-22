package ch.samt.blockchain.nodeapi;

import java.util.Scanner;

import spark.Service;

public class Main {

    public static final int PORT = 6767;

    public static void main(String[] args) {
        // TODO params

        var http = Service
                .ignite()
                .port(PORT);

        if (args.length != 2) {
            System.out.println("You can enable SSL/HTTP by");
            System.out.println("java -jar backend-jar <keystore.jks> <password>");
        } else {
            http.secure(args[0], args[1], null, null);
        }

        HttpServer api = new BlockchainApi(http);

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