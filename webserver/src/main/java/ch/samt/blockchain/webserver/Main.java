package ch.samt.blockchain.webserver;

import java.util.Scanner;

import ch.samt.blockchain.common.utils.paramhandler.ParamHandler;
import spark.Service;

public class Main {
    
    public static final int DEFAULT_PORT = 80;

    public static void main(String[] args) {
        var handler = new ParamHandler();

        handler.addArg("www", true, "path");
        handler.addArg("port", false, "port");
        handler.addArg("keystore", false, "file");
        handler.addArg("password", false, "pass");
        handler.addFlag("h");

        try {
            handler.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.print(e.getMessage());
            return;
        }

        if (!handler.isComplete() || handler.getFlag("h")) {
            System.out.println("""
                Arguments: -www <path>
                           [-port <port>]
                           [-db <file>]
                           [-ssl <keystore.jks> <password>]
                           [-h]
            """);
            return;
        }

        int port = DEFAULT_PORT;

        if (!handler.isNull("port")) {
            try {
                port = Integer.parseInt(handler.getArg("port"));
            } catch (NumberFormatException e) {
                System.err.println("Invalid port: " + handler.getArg("port"));
                return;
            }
        }

        Service http = Service
            .ignite()
            .port(port);

        if (handler.any("keystore", "password")) {
            handler.assertAll("keystore", "password");

            http.secure(
                handler.getArg("keystore"),
                handler.getArg("password"),
                null,
                null);
        }

        var path = handler.getArg("www");

        HttpServer webserver = new Webserver(http, path);


        webserver.init();
        
        // terminal
        new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                switch (scanner.next()) {
                    case "exit", "stop" -> webserver.terminate();
                }
            }
        }).start();
    }

}
