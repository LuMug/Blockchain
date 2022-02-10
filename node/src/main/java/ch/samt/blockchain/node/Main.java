package ch.samt.blockchain.node;

import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        var node = new Node(3432);

        // Start service
        node.start();

        // Interactive console
        try (var scanner = new Scanner(System.in)) {
            while (true) {
                switch (scanner.nextLine().toLowerCase()) {

                }
            }
        }
    }
}
