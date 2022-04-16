package ch.samt.blockchain.node.database;

import java.net.InetSocketAddress;

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
    
    void addBlock(int difficulty, byte[] tx_hash, byte[] nonce, byte[] miner, long mined);

    void addTx(int blockId, byte[] sender, byte[] recipient, long amount, long timestamp, byte[] lastTxHash, byte[] signature);

}
