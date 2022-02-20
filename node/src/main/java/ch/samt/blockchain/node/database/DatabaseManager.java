package ch.samt.blockchain.node.database;

import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DatabaseManager {

    private DatabaseConnection connection;

    private static String[] sql = {
        """
        CREATE TABLE IF NOT EXISTS nodes (
            address VARCHAR(20),
            port INT,
            last_seen_alive DATE,
            PRIMARY KEY (address, port)
        );
        """,
    };

    public DatabaseManager(String database) {
        this.connection = new DatabaseConnection(database);

        for (var instruction : sql) {
            connection.execute(instruction);
        }
    }

    public void cacheNode(InetSocketAddress node) {
        cacheNode(node.getHostString().toString(), node.getPort());
    }

    public void cacheNode(String address, int port) {
        System.out.println("caching node " + address + ":" + port);
        if (!containsNode(address, port)) {
            var statement = connection.prepareStatement("INSERT INTO nodes VALUES (?,?,?);");
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
    }

    private boolean containsNode(String address, int port) {
        var result = connection.query("SELECT port FROM nodes WHERE address='" + address + "' AND port=" + port + ";");
        try {
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public void updateLastSeen(InetSocketAddress node) {
        updateLastSeen(node.getAddress().toString(), node.getPort());
    }

    public void updateLastSeen(String address, int port) {
        var statement = connection.prepareStatement("UPDATE nodes SET last_seen_alive=?;");
        long timestamp = System.currentTimeMillis();
        try {
            statement.setTimestamp(1, new Timestamp(timestamp));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getCachedNodes() {
        return connection.query("SELECT address, port FROM nodes;");
    }

    public boolean isNodeCacheEmpty() {
        var result = connection.query("SELECT address FROM nodes LIMIT 1;");
        try {
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

}
