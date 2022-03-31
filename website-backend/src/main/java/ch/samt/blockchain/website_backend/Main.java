package ch.samt.blockchain.website_backend;

import java.util.Scanner;

import spark.Service;

public class Main {

    public static final int PORT = 80;
    
    public static void main(String[] args) {
        var http = Service
            .ignite()
            .port(PORT);
        
        var api = new BlockchainApi(http);
        var webserver = new WebServer(http);
        
        webserver.init(); // must be initialized before
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