package ch.samt.blockchain.nodeapi;

import java.util.Scanner;

import ch.samt.blockchain.common.utils.paramhandler.ParamHandler;
import spark.Service;

public class Main {

    public static final int DEFAULT_NODE_PORT = 5555;

    public static final int DEFAULT_API_PORT = 6767;

    public static void main(String[] args) {
        var handler = new ParamHandler();

        handler.addArg("nodeport", false, "port");
        handler.addArg("apiport", false, "port");
        handler.addArg("db", false, "file");
        handler.addArg("keystore", false, "file");
        handler.addArg("password", false, "pass");
        handler.addFlag("h");

        try {
            handler.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.print(e.getMessage());
            return;
        }

        if (handler.getFlag("h")) {
            System.out.println("""
                Arguments: [-nodeport <port>]
                           [-apiport <port>]
                           [-db <file>]
                           [-keystore <keystore.jks> -password <password>]
                           [-h]
            """);
            return;
        }

        int nodePort = DEFAULT_NODE_PORT;

        if (!handler.isNull("nodeport")) {
            try {
                nodePort = Integer.parseInt(handler.getArg("nodeport"));
            } catch (NumberFormatException e) {
                System.err.println("Invalid port: " + handler.getArg("nodeport"));
                return;
            }
        }

        int apiPort = DEFAULT_API_PORT;

        if (!handler.isNull("apiport")) {
            try {
                apiPort = Integer.parseInt(handler.getArg("apiport"));
            } catch (NumberFormatException e) {
                System.err.println("Invalid port: " + handler.getArg("apiport"));
                return;
            }
        }

        var http = Service
                .ignite()
                .port(apiPort);

        if (handler.any("keystore", "password")) { // non va
            handler.assertAll("keystore", "password");

            http.secure(
                handler.getArg("keystore"),
                handler.getArg("password"),
                null,
                null);
        }

        BlockchainApi api = handler.isNull("db") ?
            new BlockchainApi(http, nodePort) :
            new BlockchainApi(http, nodePort, handler.getArg("db"));

        api.start();

        api.init();

        // terminal
        new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                switch (scanner.next()) {
                    case "exit", "stop" -> api.terminate();
                }
            }
        }).start();
    }

}