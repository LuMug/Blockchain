package ch.samt.blockchain.seeder;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Seeder extends Thread {
    
    // move to Protocol
    public static final int POOL_CAPACITY = 100;
    public static final int MAX_REQUEST = 10;

    // Top is older nodes
    private List<Node> nodes = new LinkedList<>();

    private int port;

    public Seeder(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                try {
                    // Wait for connection
                    var socket = server.accept();
                    var connection = new Connection(this, socket);
                    System.out.println("[LOG] :: incoming " + socket.getRemoteSocketAddress());
                    new Thread(connection).start();
                } catch (IOException e) {}
            }
        } catch (IOException e) {
            System.err.println("[ERROR] :: IOException. Shutting down");
            e.printStackTrace();
        }
    }

    public void renew(Node node) {
        synchronized (nodes) {
            int index = nodes.indexOf(node);
            if (index != -1) { // renew existing entry
                nodes.remove(index);
            } else { // adding new entry
                if (nodes.size() == POOL_CAPACITY - 1) {
                    // remove oldest
                    nodes.remove(0);
                }
            }

            // push to bottom
            nodes.add(node);
        }
    }

    /**
     * Draw random nodes from the pool.
     * 
     * @param amount the amount of nodes
     * @return
     */
    public InetSocketAddress[] drawNodes(int amount, UUID exclude) {
        synchronized (nodes) {
            amount = Math.min(
                Math.min(amount, nodes.size()),
                MAX_REQUEST
            );

            int excludeIndex = 0;

            if (amount == nodes.size() && -1 != (excludeIndex = getIndexFromUUID(exclude))) {
                --amount;
            }
            
            var result = new InetSocketAddress[amount];
    
            // Generate random indexes
            int[] indexes = new int[amount];
            for (int i = 0; i < amount; i++) {
                int index = 0;
                indexes[i] = -1; // so that index 0 is not contained
    
                do {
                    index = (int) (Math.random() * nodes.size());
                } while (index == excludeIndex || contains(indexes, index));
    
                indexes[i] = index;
                result[i] = nodes.get(index).address();
            }
    
            return result;
        }
    }

    private int getIndexFromUUID(UUID uuid) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).uuid().equals(uuid)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Prints a list of all the nodes in the pool.
     * 
     * @param ps The <code>PrintStream</code> to print to.
     */
    public void printNodes(PrintStream ps) {
        ps.println("Total nodes (" + nodes.size() + ")");
        for (var node : nodes) {
            ps.println("\t" + node);
        }
        ps.println();
    }

    private static boolean contains(int[] arr, int val) {
        for (int v : arr) {
            if (v == val) {
                return true;
            }
        }

        return false;
    }

    public void attachConsole(InputStream in, PrintStream out) {
        // Interactive console
        printHelp(out);
        try (var scanner = new Scanner(in)) {
            while (true) {
                switch (scanner.nextLine().toLowerCase()) {
                    case "list" -> {
                        out.println();
                        printNodes(out);
                        out.println();
                    }
                    case "stop" -> {
                        System.exit(0);
                    }
                    case "help" -> printHelp(out);
                    default ->  printHelp(out);
                }
            }
        }
    }

    private static void printHelp(PrintStream ps) {
        ps.println("""
            Seeder console - help

            help\t\t Display this message
            list\t\t List all nodes
            stop\t\t Stop the service
        """);
    }

}