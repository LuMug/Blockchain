package ch.samt.blockchain.node.database;

import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class NodeCacheDatabaseImpl implements NodeCacheDatabase {

    private DatabaseConnection connection;

    private static final String[] SQL = {
        """
        CREATE TABLE IF NOT EXISTS nodes (
            address VARCHAR(20),
            port INT,
            last_seen_alive DATETIME,
            PRIMARY KEY (address, port)
        );
        """,
    };

    public NodeCacheDatabaseImpl(String database) {
        this.connection = new DatabaseConnection(database);

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

        // TODO: remove oldest lastSeen if there's too many
    }

    @Override
    public boolean isNodeCached(String address, int port) {
        var result = connection.query("SELECT port FROM nodes WHERE address='" + address + "' AND port=" + port + ";");
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
        var statement = connection.prepareStatement("UPDATE nodes SET last_seen_alive=?;");
        long timestamp = System.currentTimeMillis();
        try {
            statement.setTimestamp(1, new Timestamp(timestamp));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public InetSocketAddress[] getCachedNodes() {
        ResultSet rs = connection.query("SELECT address, port FROM nodes;");
        InetSocketAddress[] nodes = new InetSocketAddress[countCachedNodes()];

        int i = 0;
        try {
            while (rs.next()) {
                String address = rs.getString(1);
                int port = rs.getInt(2); // java.sql.SQLException: column 2 out of bounds [1,1]
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
            return connection.query("SELECT COUNT(*) FROM nodes;").getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean isNodeCacheEmpty() {
        var result = connection.query("SELECT address FROM nodes LIMIT 1;");
        try {
            return !result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

}
