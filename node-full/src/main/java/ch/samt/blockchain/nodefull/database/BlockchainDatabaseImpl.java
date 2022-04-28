package ch.samt.blockchain.nodefull.database;

import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.tinylog.Logger;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.nodefull.models.Block;
import ch.samt.blockchain.nodefull.models.Transaction;

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
            mined DATETIME,
            last_hash BINARY(32),
            hash BINARY(32)
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
            signature BINARY(64),
            hash BINARY(32)
        );
        """, // PRIMARY KEY (timestamp, sender) ?
             // giusta la dimensione di sig?
        """
        CREATE TABLE IF NOT EXISTS wallet (
            address BINARY(32),
            utxo INT
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS keyCache (
            pub_key BINARY(32),
            address BINARY(32)
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
    public synchronized void cacheNode(String address, int port) {
        if (!isNodeCached(address, port)) {
            var statement = connection.prepareStatement("INSERT INTO node VALUES (?,?,?);");
            long timestamp = System.currentTimeMillis();
            try (statement) {
                statement.setString(1, address);
                statement.setInt(2, port);
                statement.setTimestamp(3, new Timestamp(timestamp));
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace(); // this could happen
            }
        }
        
        updateLastSeen(address, port);

        if (countCachedNodes() > Protocol.Database.MAX_CACHED_NODES) {
            removeOldest();
        }
    }

    @Override
    public synchronized boolean isNodeCached(String address, int port) {
        var result = connection.query("SELECT port FROM node WHERE address='" + address + "' AND port=" + port + ";");
        try (result) {
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public synchronized void updateLastSeen(InetSocketAddress node) {
        updateLastSeen(node.getAddress().toString(), node.getPort());
    }

    @Override
    public synchronized void updateLastSeen(String address, int port) {
        var statement = connection.prepareStatement("UPDATE node SET last_seen_alive=?;");
        long timestamp = System.currentTimeMillis();
        try {
            statement.setTimestamp(1, new Timestamp(timestamp));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized InetSocketAddress[] getCachedNodes() {
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
    public synchronized int countCachedNodes() {
        var result = connection.query("SELECT COUNT(*) FROM node;");
        try (result) {
            return result.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public synchronized boolean isNodeCacheEmpty() {
        var result = connection.query("SELECT address FROM node LIMIT 1;");
        try (result) {
            return !result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public synchronized void removeOldest() {
        connection.execute(
            "DELETE FROM node WHERE last_seen_alive=(SELECT last_seen_alive FROM node ORDER BY last_seen_alive DESC);");
    }

    /* Blockchain */

    private int blockchainLength = 0;

    @Override
    public synchronized int getBlockchainLength() {
        return blockchainLength;
    }

    @Override
    public synchronized void addBlock(int difficulty, byte[] tx_hash, byte[] nonce, byte[] miner, long mined, byte[] lastHash, byte[] hash) {
        var statement = connection.prepareStatement("INSERT INTO block VALUES (?, ?, ?, ?, ?, ?, ?, ?);");

        try {
            statement.setInt(1, ++blockchainLength);
            statement.setInt(2, difficulty);
            statement.setBytes(3, tx_hash);
            statement.setBytes(4, nonce);
            statement.setBytes(5, miner);
            statement.setTimestamp(6, new Timestamp(mined));
            statement.setBytes(7, lastHash);
            statement.setBytes(8, hash);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void addTx(int blockId, byte[] senderPublicKey, byte[] recipient, long amount, long timestamp, byte[] lastTxHash, byte[] signature, byte[] hash) {
        var statement = connection.prepareStatement("INSERT INTO tx VALUES (?, ?, ?, ?, ?, ?, ?, ?);");

        try {
            statement.setInt(1, blockId);
            statement.setBytes(2, senderPublicKey);
            statement.setBytes(3, recipient);
            statement.setLong(4, amount);
            statement.setTimestamp(5, new Timestamp(timestamp));
            statement.setBytes(6, lastTxHash);
            statement.setBytes(7, signature);
            statement.setBytes(8, hash);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public synchronized long getUTXO(byte[] address) {
        var statement = connection.prepareStatement("SELECT utxo FROM wallet WHERE address=?;");
        try {
            statement.setBytes(1, address);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        try (var result = statement.executeQuery()) {
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
    public synchronized void updateUTXO(byte[] address, long offset) {
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

        var statement = connection.prepareStatement("UPDATE wallet SET utxo=utxo+? WHERE address=?;");
        
        try {
            statement.setLong(1, offset);
            statement.setBytes(2, address);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized byte[] getHash(int id) {
        var statement = connection.prepareStatement("SELECT hash FROM block WHERE id=?;");
        
        try {
            statement.setInt(1, id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        byte[] hash;

        try (var result = statement.executeQuery()) {
            if (!result.next()) {
                return null;
            }

            hash = result.getBytes(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return hash;
    }

    @Override
    public synchronized Block getBlock(int id) {
        var statement = connection.prepareStatement("SELECT * FROM block WHERE id=?;");
        
        try {
            statement.setInt(1, id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        int difficulty;
        byte[] txHash;
        byte[] nonce;
        byte[] miner;
        long timestamp;
        byte[] lastHash;
        byte[] hash;

        try (var result = statement.executeQuery()) {
            if (!result.next()) {
                return null;
            }

            difficulty = result.getInt(2);
            txHash = result.getBytes(3);
            nonce = result.getBytes(4);
            miner = result.getBytes(5);
            timestamp = result.getTimestamp(6).getTime();
            lastHash = result.getBytes(7);
            hash = result.getBytes(8);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        int nTx = countTx(id);

        return new Block(id, difficulty, txHash, nonce, miner, timestamp, lastHash, hash, nTx);
    }

    @Override
    public synchronized Transaction getTransaction(byte[] hash) {
        var statement = connection.prepareStatement("SELECT * FROM tx WHERE hash=?;");
        
        try {
            statement.setBytes(1, hash);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        int blockId;
        byte[] senderPub;
        byte[] recipient;
        long amount;
        long timestamp;
        byte[] lastTxHash;
        byte[] signature;

        try (var result = statement.executeQuery()) {
            if (!result.next()) {
                return null;
            }

            blockId = result.getInt(1);
            senderPub = result.getBytes(2);
            recipient = result.getBytes(3);
            amount = result.getLong(4);
            timestamp = result.getTimestamp(5).getTime();
            lastTxHash = result.getBytes(6);
            signature = result.getBytes(7);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return new Transaction(blockId, senderPub, recipient, amount, timestamp, lastTxHash, signature, hash);
    }

    @Override
    public synchronized List<Transaction> getTransactions(int blockId) {
        var statement = connection.prepareStatement("SELECT * FROM tx WHERE block_id=?;");
        
        try {
            statement.setInt(1, blockId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        List<Transaction> txs = new LinkedList<>();

        try (var result = statement.executeQuery()) {
            while (result.next()) {
                byte[] senderPub = result.getBytes(2);
                byte[] recipient = result.getBytes(3);
                long amount = result.getLong(4);
                long timestamp = result.getTimestamp(5).getTime();
                byte[] lastTxHash = result.getBytes(6);
                byte[] signature = result.getBytes(7);
                byte[] hash = result.getBytes(8);

                var tx = new Transaction(blockId, senderPub, recipient, amount, timestamp, lastTxHash, signature, hash);
                txs.add(tx);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return txs;
    }

    @Override
    public synchronized List<Transaction> getTransactions(byte[] address) {
        var pub = getPub(address);
        var statement = connection.prepareStatement("SELECT * FROM tx WHERE sender_pub=? OR recipient=?;");
        
        try {
            statement.setBytes(1, pub);
            statement.setBytes(2, address);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        List<Transaction> txs = new LinkedList<>();

        try (var result = statement.executeQuery()) {
            while (result.next()) {
                int blockId = result.getInt(1);
                byte[] senderPub = result.getBytes(2);
                byte[] recipient = result.getBytes(3);
                long amount = result.getLong(4);
                long timestamp = result.getTimestamp(5).getTime();
                byte[] lastTxHash = result.getBytes(6);
                byte[] signature = result.getBytes(7);
                byte[] hash = result.getBytes(8);

                var tx = new Transaction(blockId, senderPub, recipient, amount, timestamp, lastTxHash, signature, hash);
                txs.add(tx);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return txs;
    }

    @Override
    public synchronized byte[] getPub(byte[] address) {
        var statement = connection.prepareStatement("SELECT pub_key FROM keyCache WHERE address=?;");

        try {
            statement.setBytes(1, address);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (var result = statement.executeQuery()) {
            if (!result.next()) {
                return null;
            }

            return result.getBytes(1);
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public synchronized void cacheKey(byte[] key, byte[] address) {
        if (getPub(address) != null) {
            return;
        }

        var statement = connection.prepareStatement("INSERT INTO keyCache VALUES (?, ?);");

        try (statement) {
            statement.setBytes(1, key);
            statement.setBytes(2, address);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized int getId(byte[] hash) {
        var statement = connection.prepareStatement("SELECT id FROM block WHERE hash=?;");

        try {
            statement.setBytes(1, hash);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (var result = statement.executeQuery()) {
            if (!result.next()) {
                return -1;
            }
            
            return result.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private synchronized int countTx(int blockId) {
        var statement = connection.prepareStatement("SELECT COUNT(*) FROM tx WHERE block_id=?;");
        
        try {
            statement.setInt(1, blockId);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    
        try (var result = statement.executeQuery()) {
            if (!result.next()) {
                return -1;
            }
            return result.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public void deleteBlocksFrom(int blockId) {
        var statement = connection.prepareStatement("DELETE FROM block WHERE id>=?;");

        try (statement) {
            statement.setInt(1, blockId);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private synchronized void init() {
        var result = connection.query("SELECT COUNT(id) FROM block;");
        try (result) {
            result.next();
            this.blockchainLength = result.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
