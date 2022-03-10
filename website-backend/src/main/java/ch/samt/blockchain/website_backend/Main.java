package ch.samt.blockchain.website_backend;

import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        Backend.init(7777);

        // terminal
        new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                switch (scanner.next()) {
                    case "exit", "stop" -> Backend.stop();
                }
            }
        }).start();
    }

}