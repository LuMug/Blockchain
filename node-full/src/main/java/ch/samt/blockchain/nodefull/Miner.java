package ch.samt.blockchain.nodefull;

import ch.samt.blockchain.common.protocol.Protocol;

public class Miner {
    
    private byte[] txHash = new byte[32];
    private byte[] nonceHash = new byte[32];
    private byte[] heightHash = new byte[32];
    private byte[] lastHash = new byte[32];

    public synchronized void addTx(byte[] hash) {
        for (int i = 0; i < txHash.length; i++) {
            txHash[i] ^= hash[i];
        }
    }
    
    public void setNonce(byte[] nonce) {
        this.nonceHash = Protocol.CRYPTO.sha256(nonce);
    }

    public void setHeight(int height) {
        this.heightHash = Protocol.CRYPTO.sha256(toBytes(height));
    }

    public void setLastHash(byte[] lastHash) {
        this.lastHash = lastHash;
    }
    
    public byte[] getTxHash() {
        return txHash;
    }

    public void clear() {
        for (int i = 0; i < txHash.length; i++) {
            txHash[i] = nonceHash[i] = heightHash[i] = lastHash[i] = 0;
        }
    }

    private static byte[] toBytes(int v) {
        return new byte[]{
            (byte) ((v >> 030) & 255),
            (byte) ((v >> 020) & 255),
            (byte) ((v >> 010) & 255),
            (byte) ((v >> 000) & 255)
        };
    }

    public boolean isMined() {
        return true;
    }

}
