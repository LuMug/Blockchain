package ch.samt.blockchain.node;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.protocol.RegisterNodePacket;
import ch.samt.blockchain.common.protocol.RequestNodesPacket;
import ch.samt.blockchain.common.protocol.ServeNodesPacket;
import ch.samt.blockchain.common.utils.stream.PacketInputStream;
import ch.samt.blockchain.common.utils.stream.PacketOutputStream;
import ch.samt.blockchain.node.database.DatabaseManager;

public class Node extends Thread {
    
    public final UUID uuid = UUID.randomUUID();

    private int port;

    private DatabaseManager database;

    private List<Connection> neighbours;

    private ScheduledExecutorService scheduler;

    public Node(int port) {
        this.port = port;
        this.database = new DatabaseManager("blockchain.db");
        this.neighbours = new LinkedList<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void run() {
        connect();

        initPeriodicUpdate();
        initPeridicRegister();

        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                try {
                    // Wait for connection
                    var socket = server.accept();
                    var connection = new Connection(this, socket);
                    connection.start();
                    
                    // SHOULD BE DONE ASYNC
                    database.cacheNode(socket.getRemoteSocketAddress().toString(), socket.getPort());
                    if (neighbours.size() > Protocol.Node.MAX_CONNECTIONS) {
                        // disconnect from random node
                        var rnd_connection = getRandomNeighbour();
                        disconnect(rnd_connection);
                        database.updateLastSeen(rnd_connection.getSocket().getRemoteSocketAddress().toString(), rnd_connection.getSocket().getPort());
                    }

                    if (neighbours.size() < Protocol.Node.MIN_CONNECTIONS) {
                        var nodes = connection.requestNodes(Protocol.Node.MIN_CONNECTIONS - neighbours.size());
                        for (var node : nodes) {
                            if (!neighboursContain(node)) {
                                var node_socket = tryConnection(node.getHostString(), node.getPort());
                            
                                // If has responded
                                if (socket != null) {
                                    var node_connection = new Connection(this, node_socket);
                                    node_connection.start();
                                    neighbours.add(node_connection);
                                    database.cacheNode(node.getHostString(), node.getPort());
                                }
                            }
                        }
                    }
                } catch (IOException e) {}
            }
        } catch (IOException e) {
            System.err.println("[ERROR] :: IOException. Shutting down");
            e.printStackTrace();
        }
    }

    public void connect() {
        System.out.println("[NODE] :: Connecting to blockchain");

        // Query database nodes cache
        if (!database.isNodeCacheEmpty()) {
            System.out.println("[NODE] :: Fetching database for cached nodes");
            updateFromCache();
        }

        if (neighbours.size() == 0) {
            System.out.println("[NODE] :: No active node found. Connecting to seeder");
            List<Connection> result = new LinkedList<>();
            for (int i = 0; i < Protocol.Node.MAX_TRIES_SEEDER && result.size() < Protocol.Node.MIN_CONNECTIONS; i++) {
                var nodes = querySeeders(Protocol.Node.MIN_CONNECTIONS);
                System.out.println("[NODE] :: Received " + nodes.length + " candidate nodes");
                for (var node : nodes) {
                    if (!neighboursContain(node)) {
                        System.out.println("Trying connection with " + node);
                        var socket = tryConnection(node.getHostString(), node.getPort());
                    
                        // If has responded
                        if (socket != null) {
                            System.out.println("Conneted to node: " + socket.getRemoteSocketAddress());
                            var connection = new Connection(this, socket);
                            connection.start();
                            neighbours.add(connection);
                            database.cacheNode(node.getHostString(), node.getPort());
                        }
                    }
                }

            }
        }

        if (neighbours.size() == 0) {
            System.out.println("[NODE] :: No active node found. I am possibly the first node");
        } else {
            updateFromNeighbours();
        }

        registerToSeeder();
    }

    private boolean neighboursContain(InetSocketAddress address) {
        for (var neighbour : neighbours) {
            // ???
            System.out.println("XXX Checking " +
                neighbour.getSocket().getRemoteSocketAddress().toString() +
                " with " + address.getHostString());
            if (neighbour.getSocket().getRemoteSocketAddress().toString().contains(address.getHostString())) {
                return true;
            }
        }

        return false;
    }

    public void broadcast(byte[] data) {
        // TODO:

    }

    private InetSocketAddress[] querySeeders(int amount) {
        // Select random seeder
        var address = getRandomSeeder();

        try (var seeder = new Socket(address.getHostString(), address.getPort())) {
            var in = new PacketInputStream(seeder.getInputStream());
            var out = new PacketOutputStream(seeder.getOutputStream());

            var reqPacket = RequestNodesPacket.create(amount, uuid);
            out.writePacket(reqPacket);

            var responseData = in.nextPacket();
            if (responseData != null) {
                var responsePacket = new ServeNodesPacket(responseData);
                return responsePacket.getNodes();
            }
        } catch (IOException e) {}
        return new InetSocketAddress[0];
    }

    private InetSocketAddress[] queryNeighbours(int amount) {
        // Select random neighbour
        var neighbour = getRandomNeighbour();
        return neighbour.requestNodes(amount);
    }

    private Socket tryConnection(String address, int port) {
        try {
            return new Socket(address, port);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void disconnect(Connection connection) {
        neighbours.remove(connection);
        if (neighbours.size() == 0) {
            System.out.println("[NODE] :: This node isn't connected to any node. Reconneting...");
            connect();
        }
    }

    private void updateFromNeighbours() {
        for (int i = 0; i < Protocol.Node.MAX_TRIES_NEIGHBOUR && neighbours.size() < Protocol.Node.MIN_CONNECTIONS; i++) {
            var nodes = queryNeighbours(Protocol.Node.MIN_CONNECTIONS - neighbours.size());
            for (var node : nodes) {
                if (!neighboursContain(node)) {
                    var socket = tryConnection(node.getHostString(), node.getPort());
                
                    // If has responded
                    if (socket != null) {
                        var connection = new Connection(this, socket);
                        connection.start();
                        neighbours.add(connection);
                        database.cacheNode(node.getHostString(), node.getPort());
                    }
                }
            }
        }
    }

    private void updateFromCache() {
        var nodes = database.getCachedNodes();
        try {
            while (neighbours.size() < Protocol.Node.MIN_CONNECTIONS && nodes.next()) {
                String address = nodes.getString(0);
                int port = nodes.getInt(1);
                var node = tryConnection(address, port);

                // If has responded
                if (node != null) {
                    var connection = new Connection(this, node);
                    connection.start();
                    neighbours.add(connection);
                    database.updateLastSeen(address, port);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initPeriodicUpdate() {
        scheduler.scheduleAtFixedRate(
            () -> {
                System.out.println("[NODE] :: Updating connections from local cache and neighbour nodes");
                
                if (neighbours.size() < Protocol.Node.MIN_CONNECTIONS) {
                    updateFromNeighbours();
                }
                
                if (neighbours.size() < Protocol.Node.MIN_CONNECTIONS) {
                    updateFromCache();
                }  
            },
            Protocol.Node.UPDATE_INTERVAL,
            Protocol.Node.UPDATE_INTERVAL,
            TimeUnit.MILLISECONDS);
    }

    private void initPeridicRegister() {
        scheduler.scheduleAtFixedRate(
            () -> registerToSeeder(),
            Protocol.Node.REGISTER_INTERVAL,
            Protocol.Node.REGISTER_INTERVAL,
            TimeUnit.MILLISECONDS);
    }

    private void registerToSeeder() { // should be exhaustive
        var seeder = getRandomSeeder();
        System.out.println("[NODE] :: Registering to a random seeder " + seeder.getHostString() + ":" + seeder.getPort());
        
        var connection = tryConnection(seeder.getHostString(), seeder.getPort());
        
        if (connection != null) {
            try {
                var out = new PacketOutputStream(connection.getOutputStream());
                out.writePacket(RegisterNodePacket.create(port, uuid));
                Thread.sleep(100);
                connection.close();
            } catch (IOException | InterruptedException e) {

            }
        }
    }

    private Connection getRandomNeighbour() {
        return neighbours.get((int) (Math.random() * neighbours.size()));
    }

    private InetSocketAddress getRandomSeeder() {
        return Seeders.seeders[(int) (Math.random() * Seeders.seeders.length)];
    }

    public InetSocketAddress[] drawNodes(int amount, UUID exclude) {
        synchronized (neighbours) {
            amount = Math.min(
                Math.min(amount, neighbours.size()),
                30//MAX_REQUEST
            );

            // TODO exclude exclude
            
            var result = new InetSocketAddress[amount];
    
            // Generate random indexes
            int[] indexes = new int[amount];
            for (int i = 0; i < amount; i++) {
                int index = 0;
    
                do {
                    index = (int) (Math.random() * neighbours.size());
                } while (contains(indexes, index));
    
                indexes[i] = index;
                var socket = neighbours.get(index).getSocket();
                result[i] = new InetSocketAddress(socket.getInetAddress(), socket.getPort());
            }
    
            return result;
        }

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
        printHelp();
        try (var scanner = new Scanner(in)) {
            while (true) {
                switch (scanner.nextLine().toLowerCase()) {
                    case "help" -> printHelp();
                    case "list" -> {
                        out.println();
                        printBeighbours(out);
                        out.println();
                    }
                    case "stop" -> {
                        System.exit(0);
                    }
                }
            }
        }
    }

    private void printBeighbours(PrintStream ps) {
        ps.println("Total neighbours (" + neighbours.size() + ")");
        for (var node : neighbours) {
            ps.println("\t" + node);
        }
        ps.println();
    }

    private void printHelp() {
        
    }

    /**
     * TODO:
     * queryNeighbours, querySeeder and registerToSeeder shoul be more exhaustive
     * when the connection fails
     * 
     * simplify var in, var out with class
     * 
     * Excluse same node from serveNodesPacket
     */

}
