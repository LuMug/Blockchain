package ch.samt.blockchain.node;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.protocol.RequestNodesPacket;
import ch.samt.blockchain.common.protocol.ServeNodesPacket;
import ch.samt.blockchain.common.utils.stream.PacketInputStream;
import ch.samt.blockchain.common.utils.stream.PacketOutputStream;
import ch.samt.blockchain.node.database.DatabaseManager;

public class Node extends Thread {
    
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
                    database.cacheNode(socket.getLocalSocketAddress().toString(), socket.getPort());
                    if (neighbours.size() > Protocol.Node.MAX_CONNECTIONS) {
                        // disconnect from random node
                        var rnd_connection = getRandomNeighbour();
                        disconnect(rnd_connection);
                        database.updateLastSeen(rnd_connection.getSocket().getLocalAddress().toString(), rnd_connection.getSocket().getPort());
                    }

                    if (neighbours.size() < Protocol.Node.MIN_CONNECTIONS) {
                        var nodes = connection.requestNodes(Protocol.Node.MIN_CONNECTIONS - neighbours.size());
                        for (var node : nodes) {
                            // TODO check if is not myself
                            if (!neighboursContain(node)) {
                                var node_socket = tryConnection(node.getAddress().toString(), node.getPort());
                            
                                // If has responded
                                if (socket != null) {
                                    var node_connection = new Connection(this, node_socket);
                                    node_connection.start();
                                    neighbours.add(node_connection);
                                    database.cacheNode(node.getAddress().toString(), node.getPort());
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
                for (var node : nodes) {
                    // TODO check if is not myself
                    if (!neighboursContain(node)) {
                        var socket = tryConnection(node.getAddress().toString(), node.getPort());
                    
                        // If has responded
                        if (socket != null) {
                            var connection = new Connection(this, socket);
                            connection.start();
                            neighbours.add(connection);
                            database.cacheNode(node.getAddress().toString(), node.getPort());
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
    }

    private boolean neighboursContain(InetSocketAddress address) {
        for (var neighbour : neighbours) {
            // ???
            if (neighbour.getSocket().getLocalSocketAddress().equals(address)) {
                return true;
            }
        }

        return false;
    }

    public void broadcast(byte[] data) {

    }

    private InetSocketAddress[] querySeeders(int amount) {
        // Select random seeder
        var address = Seeders.seeders[(int) (Math.random() * Seeders.seeders.length)];

        try (var seeder = new Socket(address.getAddress().toString(), address.getPort())) {
            var in = new PacketInputStream(seeder.getInputStream());
            var out = new PacketOutputStream(seeder.getOutputStream());

            var reqPacket = RequestNodesPacket.create(amount);
            out.writePacket(reqPacket);

            var responseData = in.nextPacket();
            var responsePacket = new ServeNodesPacket(responseData);
            return responsePacket.getNodes();
        } catch (IOException e) {
            return new InetSocketAddress[0];
        }
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
                // TODO check if is not myself
                if (!neighboursContain(node)) {
                    var socket = tryConnection(node.getAddress().toString(), node.getPort());
                
                    // If has responded
                    if (socket != null) {
                        var connection = new Connection(this, socket);
                        connection.start();
                        neighbours.add(connection);
                        database.cacheNode(node.getAddress().toString(), node.getPort());
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
        System.out.println("[NODE] :: Initializing periodic connection updates");
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
        System.out.println("[NODE] :: Initializing periodic register to a random seeder");
        scheduler.scheduleAtFixedRate(
            () -> {
                System.out.println("[NODE] :: Registering to a random seeder");
            
            },
            Protocol.Node.REGISTER_INTERVAL,
            Protocol.Node.REGISTER_INTERVAL,
            TimeUnit.MILLISECONDS);
    }

    private Connection getRandomNeighbour() {
        return neighbours.get((int) (Math.random() * neighbours.size()));
    }

}
