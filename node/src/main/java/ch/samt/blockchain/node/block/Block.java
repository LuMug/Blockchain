package ch.samt.blockchain.node.block;

import java.security.MessageDigest;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Block {
    
    /**
     * The hash of the block.
     */
    private byte[] hash;
    

    /**
     * The hash of the previous block.
     */
    private byte[] previousHash;

    /**
     * Data of the block.
     */
    private byte[] data;

    /**
     * The timestamp of the block (creation).
     */
    private long timeStamp;

    private int nonce;

    /**
     * Constructor of Block.
     * 
     * @param previousHash the hash of the previous block
     * @param data         any information of the block
     * @param timeStamp    the timestamp of the block
     */
    public Block(byte[] previousHash, byte[] data, long timeStamp) {
        this.previousHash = previousHash;
        this.data = data;
        this.timeStamp = timeStamp;
        this.hash = calculateHash();
    }

    public String getHash() {
        return this.hash;
    }
    
    public void calculateHash() {
        byte[] dataToHash = new byte[previousHash.length + 8 + 4 + data.length];

        MessageDigest digest = null;

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (Exception ex) {
            System.err.println("Error");
        }

        this.hash = digest.digest(dataToHash);
    }
    
    public int mineBlock(int difficulty) {
        var random = new Random();
        
        while (true) {
            nonce = random.nextInt();
            calculateHash();

            // Check if nonce is found
            for (int i = 0; i < difficulty; i++) {
                if (hash[i] != (byte)0) {
                    continue;
                }
            }

            break;
        }

        return nonce;
    }
}