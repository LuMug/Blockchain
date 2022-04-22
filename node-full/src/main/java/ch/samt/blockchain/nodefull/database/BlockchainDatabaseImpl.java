package ch.samt.blockchain.nodefull.database;

import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.tinylog.Logger;

import ch.samt.blockchain.common.protocol.Protocol;

public class BlockchainDatabaseImpl implements BlockchainDatabase {

    private DatabaseConnection connection;

    // TODO ? Index on tx.block_id

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
        // No foreign key for block_id since tx will be placed in future blocks
        """
        CREATE TABLE IF NOT EXISTS tx (
            block_id INT,
            sender_pub BINARY(32),
            recipient BINARY(32),
            amount INT,
            timestamp DATETIME,
            last_tx_hash BINARY(32),
            signature BINARY(64) 
        );
        """, // PRIMARY KEY (timestamp, sender) ?
             // giusta la dimensione di sig?
        """
        CREATE TABLE IF NOT EXISTS wallet (
            address BINARY(32),
            utxo INT
        )
        """
    };

    /* Node cache */

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

        init();
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
        try (result) {
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
        try (rs) {
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
        try (result) {
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

    /* Blockchain */

    private int blockchainLength = 0;

    @Override
    public int getBlockchainLength() {
        return blockchainLength;
    }

    @Override
    public void addBlock(int difficulty, byte[] tx_hash, byte[] nonce, byte[] miner, long mined) {
        var statement = connection.prepareStatement("INSERT INTO block VALUES (?, ?, ?, ?, ?, ?);");

        try {
            statement.setInt(1, ++blockchainLength);
            statement.setInt(2, difficulty);
            statement.setBytes(3, tx_hash);
            statement.setBytes(4, nonce);
            statement.setBytes(5, miner);
            statement.setTimestamp(6, new Timestamp(mined));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTx(int blockId, byte[] senderPublicKey, byte[] recipient, long amount, long timestamp, byte[] lastTxHash, byte[] signature) {
        var statement = connection.prepareStatement("INSERT INTO tx VALUES (?, ?, ?, ?, ?, ?, ?);");

        try {
            statement.setInt(1, blockId);
            statement.setBytes(2, senderPublicKey);
            statement.setBytes(3, recipient);
            statement.setLong(4, amount);
            statement.setTimestamp(5, new Timestamp(timestamp));
            statement.setBytes(6, lastTxHash);
            statement.setBytes(7, signature);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public long getUTXO(byte[] address) {
        var statement = connection.prepareStatement("SELECT utxo FROM wallet WHERE address=?");
        try {
            statement.setBytes(1, address);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        try (var result = statement.getResultSet(); statement) {
            if (!result.next()) {
                return -1;
            }
            return result.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
    
    @Override
    public void updateUTXO(byte[] address, long offset) {
        if (getUTXO(address) == -1) {
            var statement = connection.prepareStatement("INSERT INTO wallet VALUES (?, ?);");
            try {
                statement.setBytes(1, address);
                statement.setLong(2, 0);
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        var statement = connection.prepareStatement("UPDATE wallet SET utxo=utxo+? WHERE address=?");
        
        try {
            statement.setLong(1, offset);
            statement.setBytes(2, address);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        var result = connection.query("SELECT COUNT(id) FROM block;");
        try (result) {
            result.next();
            this.blockchainLength = result.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
