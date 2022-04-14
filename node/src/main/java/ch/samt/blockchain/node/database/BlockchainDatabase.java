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

    void addBlock();

    // ...

}
