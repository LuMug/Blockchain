package ch.samt.blockchain.nodefull.database;

import java.net.InetSocketAddress;
import java.util.List;

import ch.samt.blockchain.nodefull.models.*;

public interface BlockchainDatabase {

    // Node cache
    
    /**
     * Caches a node.
     * 
     * @param node the address of the node
     */
    void cacheNode(InetSocketAddress node);

    /**
     * Caches a node.
     * 
     * @param address the address of the node
     * @param port the port of the node
     */
    void cacheNode(String address, int port);

    /**
     * Returns <code>true</code> if a given node node is cached,
     * <code>false</code> otherwise.
     * 
     * @param address the address of the node
     * @param port the port of the node
     * @return if the node is cached
     */
    boolean isNodeCached(String address, int port);

    /**
     * Updates the last seen field for a given node.
     * 
     * @param node the address of the node
     */
    void updateLastSeen(InetSocketAddress node);

    /**
     * Updates the last seen field for a given node.
     * 
     * @param address the address of the node
     * @param port the port of the node
     */
    void updateLastSeen(String address, int port);

    /**
     * Returns an array of all the cached nodes service addresses.
     * 
     * @return the cached service addresses
     */
    InetSocketAddress[] getCachedNodes();

    /**
     * Counts the amount of cached nodes.
     * 
     * @return the amount of cached nodes.
     */
    int countCachedNodes();

    /**
     * Returns <code>true</code> if the node cache is empty,
     * <code>false</code> otherwise.
     * 
     * @return if the node cache if empty
     */
    boolean isNodeCacheEmpty();

    /**
     * Removes the oldest cached node.
     */
    void removeOldest();

    // Blockchain

    /**
     * Retruns the height of the blockchain.
     * 
     * @return the height of the blockchain
     */
    int getBlockchainLength();
    
    /**
     * Adds a block.
     * 
     * @param difficulty the difficulty of the block
     * @param txHash the hash of all the transactions within the block
     * @param nonce the proof-of-work nonce
     * @param miner the miner address
     * @param mined the timestamp
     * @param lastHash the hash of the last block
     * @param hash the hash of this block
     */
    void addBlock(long difficulty, byte[] txHash, byte[] nonce, byte[] miner, long mined, byte[] lastHash, byte[] hash);

    /**
     * Adds a transaction.
     * 
     * @param blockId the id of the block
     * @param senderPublicKey the public key of the sender
     * @param recipient the address of the recipient
     * @param amount the amount spent
     * @param timestamp the timestamp of the transaction
     * @param lastTxHash the hash of the last transaction
     * @param signature the signature of the hash
     * @param hash the hash of the transaction
     */
    void addTx(int blockId, byte[] senderPublicKey, byte[] recipient, long amount, long timestamp, byte[] lastTxHash, byte[] signature, byte[] hash);

    /**
     * Retrives the UTXO of a given wallet.
     * 
     * @param address the address of the wallet
     * @return the UTXO
     */
    long getUTXO(byte[] address);

    /**
     * Updates the UTXO of a wallet.
     * 
     * @param address the address of the wallet.
     * @param offset the UTXO offset.
     */
    void updateUTXO(byte[] address, long offset);

    /**
     * Retrives the hash of a block given its id.
     * 
     * @param id the id of the block
     * @return the hash of the block
     */
    byte[] getHash(int id);
    
    /**
     * Retrieves a block given its id.
     * 
     * @param id the id of the block
     * @return the block
     */
    Block getBlock(int id);

    /**
     * Searches the id a block with a given hash.
     * 
     * @param hash the hash to search
     * @return the id of the block
     */
    int getId(byte[] hash);

    /**
     * Retrieves a single transaction from its hash,
     * 
     * @param hash the hash to search
     * @return the transaction
     */
    Transaction getTransaction(byte[] hash);

    /**
     * Retrives the last spent transaction of a wallet.
     * 
     * @param address the wallet address
     * @return the transaction
     */
    Transaction getLastTransaction(byte[] address);

    /**
     * Retrives the hash of the last spent transaction of a wallet.
     * 
     * @param address the wallet address
     * @return the transaction
     */
    byte[] getLastTransactionHash(byte[] address);

    /**
     * Retrieves a list of all the transaction received or spent by an address.
     * 
     * @param address the address to search
     * @return the list of transactions
     */
    List<Transaction> getTransactions(byte[] address);

    /**
     * Retrieves a list of all the transactions within a block.
     * 
     * @param blockId the block id
     * @return the list of transactions
     */
    List<Transaction> getTransactions(int blockId);

    /**
     * Caches a public key if it isn't alreay cached.
     * 
     * @param key the key to cache
     * @param address the address to associate the key with
     */
    void cacheKey(byte[] key, byte[] address);

    /**
     * Returns the public key of a given address.
     * 
     * @param address the address to retrieve the public key from
     * @return the public key
     */
    byte[] getPub(byte[] address);

    /**
     * <p>
     * Reverses all the block rewards where the block id is greater than or equal to <code>blockId</code>.
     * </p>
     * <p>
     * Reverses all the transactions where the block id is greater than or equal to <code>blockId</code>.
     * </p>
     * <p>
     * Deletes all the transactions where the block id is greater than or equal to <code>blockId</code>.
     * </p>
     * <p>
     * Deletes all the blocks where the block id is greater than or equal to <code>blockId</code>.
     * </p>
     * 
     * @param blockId the starting block id
     */
    void deleteBlocksFrom(int blockId);

    /**
     * Returns the current difficulty.
     * 
     * @return the current difficulry
     */
    long getDifficulty();

    /**
     * Clears all the tables except for `keyCache`.
     */
    void clear();

    void updateDifficulty();

}
