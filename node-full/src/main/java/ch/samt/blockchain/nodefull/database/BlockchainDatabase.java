package ch.samt.blockchain.nodefull.database;

import java.net.InetSocketAddress;
import java.util.List;

import ch.samt.blockchain.nodefull.models.*;

public interface BlockchainDatabase {

    // Node cache
    
    void cacheNode(InetSocketAddress node);

    void cacheNode(String address, int port);

    boolean isNodeCached(String address, int port);

    void updateLastSeen(InetSocketAddress node);

    void updateLastSeen(String address, int port);

    InetSocketAddress[] getCachedNodes();

    int countCachedNodes();

    boolean isNodeCacheEmpty();

    void removeOldest();

    // Blockchain

    int getBlockchainLength();
    
    void addBlock(int difficulty, byte[] txHash, byte[] nonce, byte[] miner, long mined, byte[] lastHash, byte[] hash);

    void addTx(int blockId, byte[] senderPublicKey, byte[] recipient, long amount, long timestamp, byte[] lastTxHash, byte[] signature, byte[] hash);

    long getUTXO(byte[] address);

    void updateUTXO(byte[] address, long offset);

    byte[] getHash(int id);
    
    Block getBlock(int id);

    int getId(byte[] hash);

    Transaction getTransaction(byte[] hash);

    List<Transaction> getTransactions(byte[] address);

    List<Transaction> getTransactions(int blockId);

    void cacheKey(byte[] key, byte[] address);

    byte[] getPub(byte[] address);

    void deleteBlocksFrom(int blockId);

}
