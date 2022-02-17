package ch.samt.blockchain.node.block;

import java.security.MessageDigest;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Block {
    /**
     * The hash of the block.
     */
    private String hash;

    /**
     * The hash of the previous block.
     */
    private String previousHash;

    /**
     * Data of the block, any information having value.
     */
    private String data;

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
    public Block(String previousHash, String data, long timeStamp) {
        this.previousHash = previousHash;
        this.data = data;
        this.timeStamp = timeStamp;
        this.hash = calculateHash();
    }

    public String getHash() {
        return this.hash;
    }

    public String calculateHash() {
        String dataToHash = previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + data;
        MessageDigest digest = null;
        byte[] bytes = null;

        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes(UTF_8));
        } catch (Exception ex) {
            System.out.println("Error");
        }

        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

    public String mineBlock(int prefix) {
        String prefixString = new String(new char[prefix]).replace('\0', '0');
        while (!hash.substring(0, prefix).equals(prefixString)) {
            nonce++;
            hash = calculateHash();
        }
        return hash;
    }
}