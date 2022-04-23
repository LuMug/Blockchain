package ch.samt.blockchain.nodefull.database;

import java.net.InetSocketAddress;

import ch.samt.blockchain.nodefull.Block;

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

    Block getBlock(int id);

    byte[] getHash(int id);

}
