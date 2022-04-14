package ch.samt.blockchain.node.database;

import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.tinylog.Logger;

import ch.samt.blockchain.common.protocol.Protocol;

public class BlockchainDatabaseImpl implements BlockchainDatabase {

    private DatabaseConnection connection;

    private static final String[] SQL = {
        """
        CREATE TABLE IF NOT EXISTS node (
            address VARCHAR(20),
            port INT,
            last_seen_alive DATETIME,
            PRIMARY KEY (address, port)
        );
        """,
        """
        CREATE TABLE IF NOT EXISTS block (
            id INT PRIMARY KEY,
            difficulty INT,
            tx_hash BINARY(32),
            nonce BINARY(32),
            miner BINARY(32),
            mined DATETIME
        );
        """,
        """
            CREATE TABLE IF NOT EXISTS tx (
                amount INT,
                recipient BINARY(32),
                sender BINARY(32),
                timestamp DATETIME,
                last_tx_hash BINARY(32),
                signature BINARY(32)
            );     
        """
    };

    public BlockchainDatabaseImpl(String database) {
        this.connection = new DatabaseConnectionImpl(database);
        
        if (!connection.connect()) {
            Logger.error("Failed to connect to database");
            System.exit(0);
        } else {
            Logger.info("Connected to " + database + " database");
        }

        for (var instruction : SQL) {
            connection.execute(instruction);
        }
    }

    @Override
    public void cacheNode(InetSocketAddress node) {
        cacheNode(node.getHostString().toString(), node.getPort());
    }

    @Override
    public void cacheNode(String address, int port) {
        if (!isNodeCached(address, port)) {
            var statement = connection.prepareStatement("INSERT INTO node VALUES (?,?,?);");
            long timestamp = System.currentTimeMillis();
            try {
                statement.setString(1, address);
                statement.setInt(2, port);
                statement.setTimestamp(3, new Timestamp(timestamp));
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        updateLastSeen(address, port);

        if (countCachedNodes() > Protocol.Database.MAX_CACHED_NODES) {
            removeOldest();
        }
    }

    @Override
    public boolean isNodeCached(String address, int port) {
        var result = connection.query("SELECT port FROM node WHERE address='" + address + "' AND port=" + port + ";");
        try {
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public void updateLastSeen(InetSocketAddress node) {
        updateLastSeen(node.getAddress().toString(), node.getPort());
    }

    @Override
    public void updateLastSeen(String address, int port) {
        var statement = connection.prepareStatement("UPDATE node SET last_seen_alive=?;");
        long timestamp = System.currentTimeMillis();
        try {
            statement.setTimestamp(1, new Timestamp(timestamp));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public InetSocketAddress[] getCachedNodes() {
        InetSocketAddress[] nodes = new InetSocketAddress[countCachedNodes()];
        ResultSet rs = connection.query("SELECT address, port FROM node;");

        int i = 0;
        try {
            while (rs.next()) {
                String address = rs.getString(1);
                int port = rs.getInt(2);
                nodes[i++] = new InetSocketAddress(address, port);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nodes;
    }

    @Override
    public int countCachedNodes() {
        try {
            return connection.query("SELECT COUNT(*) FROM node;").getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean isNodeCacheEmpty() {
        var result = connection.query("SELECT address FROM node LIMIT 1;");
        try {
            return !result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public void removeOldest() {
        connection.execute(
            "DELETE FROM node WHERE last_seen_alive=(SELECT last_seen_alive FROM node ORDER BY last_seen_alive DESC);");
    }

    @Override
    public void addBlock() {

    }

}
