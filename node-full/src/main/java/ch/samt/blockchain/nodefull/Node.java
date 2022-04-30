package ch.samt.blockchain.nodefull;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.tinylog.Logger;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.protocol.RegisterNodePacket;
import ch.samt.blockchain.common.protocol.RequestNodesPacket;
import ch.samt.blockchain.common.protocol.ServeNodesPacket;
import ch.samt.blockchain.common.utils.stream.PacketInputStream;
import ch.samt.blockchain.common.utils.stream.PacketOutputStream;
import ch.samt.blockchain.nodefull.database.BlockchainDatabase;
import ch.samt.blockchain.nodefull.database.BlockchainDatabaseImpl;
import ch.samt.blockchain.nodefull.utils.CircularIterator;

public abstract class Node extends Thread {
    
    /**
     * The <code>UUID</code> of this node.
     */
    public final UUID uuid = UUID.randomUUID();

    /**
     * The port of the service.
     */
    public final int port;

    /**
     * The database interface.
     */
    protected BlockchainDatabase database;

    /**
     * The list of peers.
     */
    protected List<Connection> neighbours; // TODO peers

    /**
     * Scheduler.
     */
    protected ScheduledExecutorService scheduler;

    // TODO: discard connection if hasn't registered in time
    // (interrupt Thread)

    /**
     * 
     * @param port the port of the service
     * @param db the name of the sqlite database file
     */
    public Node(int port, String db) {
        this.port = port;
        this.database = new BlockchainDatabaseImpl(db);
        this.neighbours = new LinkedList<>();
        this.scheduler = Executors.newScheduledThreadPool(5);
    }

    /**
     * 
     * @param port the port of the service
     */
    public Node(int port) {
        this(port, "blockchain_" + port + ".db");
    }

    public abstract boolean deployTx(byte[] packet);
    public abstract int getIdByHash(byte[] hash);
    public abstract int getBlockchainLength();
    protected abstract boolean broadcastTx(byte[] packet, Connection exclude, boolean live);
    protected abstract boolean broadcastTx(byte[] packet);
    protected abstract boolean powSolved(byte[] packet, boolean live);
    protected abstract void broadcastPoW(byte[] packet, Connection exclude, boolean live);
    public abstract void downloadEnded(Connection from);
    public abstract void oldTx(byte[] packet);
    public abstract void serveBlockchain(byte[] packet, Connection to);
    
    protected abstract void initHighLevelNode();

    public void broadcast(byte[] packet) {
        for (var peer : neighbours) {
            peer.sendPacket(packet);
        }
    }

    @Override
    public void run() {
        connect();
        
        initPeriodicUpdate();
        initPeridicRegister();
        
        initHighLevelNode();

        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                try {
                    // Wait for connection
                    var socket = server.accept();
                    var connection = new HighLevelConnection(this, socket);
                    connection.start();
                    Logger.info("Connection incoming");
                } catch (IOException e) {}
            }
        } catch (IOException e) {
            System.err.println("[ERROR] :: IOException. Shutting down");
            e.printStackTrace();
        }
    }

    /**
     * Adds a peer to the list of peers.
     * 
     * @param connection the connection to register
     */
    protected void registerNode(Connection connection) {
        for (var peer : neighbours) {
            if (peer.getServiceAddress().equals(connection.getServiceAddress())) {
                disconnect(connection);
            }
        }

        neighbours.add(connection);

        if (neighbours.size() > Protocol.Node.MAX_CONNECTIONS) {
            // disconnect from random node
            var rnd_connection = getRandomNeighbour();
            disconnect(rnd_connection);

            database.updateLastSeen(rnd_connection.getServiceAddress());
        }
        
        database.cacheNode(connection.getServiceAddress());
    }

    /**
     * Tries to connect to the network.
     */
    public void connect() {
        Logger.info("Connecting to blockchain");

        // Query database nodes cache
        if (!database.isNodeCacheEmpty()) {
            Logger.info("Fetching database for cached nodes");
            updateFromCache();
        }

        if (neighbours.size() == 0) {
            updateFromSeeder();
        }

        registerToSeeder();
        updateFromNeighbours();
    }

    /**
     * 
     * @param address
     * @return
     */
    private boolean neighboursContain(InetSocketAddress address) {
        for (var neighbour : neighbours) {
            if (neighbour.getServiceAddress().equals(address)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 
     * @param amount
     * @return
     */
    private InetSocketAddress[] querySeeders(int amount) {
        int start = (int) (Math.random() * Seeders.SEEDERS.length);
        for (var index : new CircularIterator(Seeders.SEEDERS.length, start)) {
            var address = Seeders.SEEDERS[index];
            Logger.info("Trying seeder: " + address);

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
        }

        return new InetSocketAddress[0];
    }

    /**
     * 
     * @param amount
     * @return
     */
    private InetSocketAddress[] queryNeighbours(int amount) {
        // Select random neighbour
        var neighbour = getRandomNeighbour();

        if (neighbour == null) {
            return new InetSocketAddress[0];
        }

        return neighbour.requestNodes(amount);
    }

    /**
     * 
     * @param address
     * @param port
     * @return
     */
    private Socket tryConnection(String address, int port) {
        try {
            return new Socket(address, port);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 
     * @param address
     * @return
     */
    private Socket tryConnection(InetSocketAddress address) {
        return tryConnection(address.getHostString(), address.getPort());
    }

    /**
     * 
     * @param connection
     */
    public void disconnect(Connection connection) {
        neighbours.remove(connection);
        if (neighbours.size() == 0) {
            Logger.warn("This node isn't connected to any node. Reconneting...");
            connect();
        }
    }

    /**
     * 
     */
    private void updateFromNeighbours() {
        int requested = 0;
        int received = 0;

        for (int i = 0; i < Protocol.Node.MAX_TRIES_NEIGHBOUR &&
                neighbours.size() < Protocol.Node.MIN_CONNECTIONS &&
                requested == received; i++) {
            
            requested = Math.min(Protocol.Seeder.MAX_REQUEST, Protocol.Node.MIN_CONNECTIONS - neighbours.size());
            var nodes = queryNeighbours(requested);
            received = nodes.length;
            for (var node : nodes) {
                if (!neighboursContain(node)) {
                    var socket = tryConnection(node.getHostString(), node.getPort());
                
                    // If has responded
                    if (socket != null) {
                        var connection = new HighLevelConnection(this, socket);
                        connection.start();
                        connection.waitNodeRegistration(1000); // TIMEOUT
                    }
                }
            }
        }
    }

    /**
     * 
     */
    private void updateFromSeeder() {
        Logger.info("No active node found. Connecting to seeder");

        int requested = 0;
        int received = 0;

        for (int i = 0; i < Protocol.Node.MAX_TRIES_SEEDER &&
                neighbours.size() < Protocol.Node.MIN_CONNECTIONS &&
                requested == received; i++) {

            requested = Math.min(Protocol.Seeder.MAX_REQUEST, Protocol.Node.MIN_CONNECTIONS - neighbours.size());
            var nodes = querySeeders(Protocol.Node.MIN_CONNECTIONS);
            received = nodes.length;

            Logger.info("Received " + nodes.length + " candidate nodes");
            for (var node : nodes) {
                if (!neighboursContain(node)) {
                    Logger.info("Trying connection with " + node);
                    var socket = tryConnection(node.getHostString(), node.getPort());
                
                    // If has responded
                    if (socket != null) {
                        var connection = new HighLevelConnection(this, socket);
                        connection.start();
                        connection.waitNodeRegistration(1000); // TIMEOUT
                    }
                }
            }

        }
    }

    /**
     * 
     */
    private void updateFromCache() {
        var nodes = database.getCachedNodes(); // TODO: ritorna [] + interface
        for (int i = 0; i < nodes.length && neighbours.size() < Protocol.Node.MIN_CONNECTIONS; i++) {
            var address = nodes[i];

            if (neighboursContain(address)) {
                continue;
            }
            
            var node = tryConnection(address);

            // If has responded
            if (node != null) {
                var connection = new HighLevelConnection(this, node);
                connection.start();
                connection.waitNodeRegistration(1000); // TIMEOUT
            }
        }
    }

    /**
     * 
     */
    private void initPeriodicUpdate() {
        schedule(
            () -> {
                Logger.info("Updating connections from local cache and neighbour nodes");

                if (neighbours.size() < Protocol.Node.MIN_CONNECTIONS) {
                    updateFromNeighbours();
                }
                
                if (neighbours.size() < Protocol.Node.MIN_CONNECTIONS) {
                    updateFromCache();
                }

                if (neighbours.size() == 0) {
                    updateFromSeeder();
                }
            },
            Protocol.Node.UPDATE_INTERVAL,
            Protocol.Node.UPDATE_INTERVAL,
            TimeUnit.MILLISECONDS);
    }

    /**
     * 
     */
    private void initPeridicRegister() {
        schedule(
            () -> registerToSeeder(),
            Protocol.Node.REGISTER_INTERVAL,
            Protocol.Node.REGISTER_INTERVAL,
            TimeUnit.MILLISECONDS);
    }

    // tries to register to a random seeder, if connection cannot be estblished,
    // it tries every other seeder until one works. If none is found the program dies.
    private void registerToSeeder() {
        Logger.info("Registering to a random seeder");

        int seeders = Seeders.SEEDERS.length;
        int randomIndex = (int) (Math.random() * seeders); // random starting point
        
        for (int index : new CircularIterator(seeders, randomIndex)) {
            var connection = tryConnection(Seeders.SEEDERS[index]);
    
            if (connection != null) {
                try (connection) {
                    var out = new PacketOutputStream(connection.getOutputStream());
                    out.writePacket(RegisterNodePacket.create(port, uuid));
                    Thread.sleep(100);
                } catch (IOException | InterruptedException e) {}
    
                return;
            }
        }

        Logger.error("No active seeder found");
    }

    /**
     * Draws a random peer.
     * 
     * @return a random peer
     */
    private Connection getRandomNeighbour() {
        if (neighbours.size() == 0) {
            return null;
        }

        return neighbours.get((int) (Math.random() * neighbours.size()));
    }

    /**
     * 
     * @param amount
     * @param exclude the <code>UUID</code> to exclude
     * @return
     */
    public InetSocketAddress[] drawNodes(int amount, UUID exclude) {
        synchronized (neighbours) {
            amount = Math.min(
                Math.min(amount, neighbours.size()),
                30 //MAX_REQUEST TODO
            );

            int excludeIndex = 0;

            if (amount == neighbours.size() && -1 != (excludeIndex = getIndexFromUUID(exclude))) {
                --amount;
            }
            
            var result = new InetSocketAddress[amount];
    
            // Generate random indexes
            int[] indexes = new int[amount];
            for (int i = 0; i < amount; i++) {
                int index = 0;
                indexes[i] = -1; // so that index 0 is not contained
    
                do {
                    index = (int) (Math.random() * neighbours.size());
                } while (index == excludeIndex || contains(indexes, index));
    
                indexes[i] = index;
                result[i] = neighbours.get(index).getServiceAddress();
            }
    
            return result;
        }
    }

    /**
     * 
     * @param uuid
     * @return
     */
    private int getIndexFromUUID(UUID uuid) {
        int i = 0; // optimized iteration for LinkedList
        for (var neighbour : neighbours) {
            if (neighbour.getUuid().equals(uuid)) {
                return i;
            }
            i++;
        }

        return -1;
    }

    /**
     * 
     * @param arr
     * @param val
     * @return
     */
    private static boolean contains(int[] arr, int val) {
        for (int v : arr) {
            if (v == val) {
                return true;
            }
        }

        return false;
    }

    /**
     * 
     * @param in
     * @param out
     */
    public void attachConsole(InputStream in, PrintStream out) {
        // Interactive console
        printHelp(out);
        try (var scanner = new Scanner(in)) {
            while (true) {
                switch (scanner.nextLine().toLowerCase()) {
                    case "list", "ls" -> {
                        out.println();
                        printNeighbours(out);
                        out.println();
                    }
                    case "stop" -> {
                        System.exit(0);
                    }
                    case "help" -> printHelp(out);
                    default -> printHelp(out);
                }
            }
        }
    }

    /**
     * 
     * @param command
     * @param initialDelay
     * @param delay
     * @param unit
     */
    protected void schedule(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        scheduler.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    /**
     * 
     * @param ps
     */
    private void printNeighbours(PrintStream ps) {
        ps.println("Total neighbours (" + neighbours.size() + ")");
        for (var node : neighbours) {
            ps.println("\t" + node.getServiceAddress());
        }
        ps.println();
    }

    /**
     * 
     * @param ps
     */
    private void printHelp(PrintStream ps) {
        ps.println("""
            Node console - help

            help\t\t Display this message
            list, ls\t\t List all nodes
            stop\t\t Stop the service
        """);
    }

}
