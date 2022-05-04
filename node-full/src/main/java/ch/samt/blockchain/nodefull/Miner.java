package ch.samt.blockchain.nodefull;

import java.math.BigInteger;

import ch.samt.blockchain.common.protocol.Protocol;

public class Miner {
    
    private byte[] txHash = new byte[32];
    private byte[] nonceHash = new byte[32];
    private byte[] heightHash = new byte[32];
    private byte[] lastHash = new byte[32];
    private byte[] target;

    public synchronized void addTxHash(byte[] hash) {
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

    public void setDifficulty(long difficulty) {
        // target = max_target / difficulty

        target = Protocol.Blockchain.MAX_TARGET
            .divide(new BigInteger(Long.toString(difficulty)))
            .toByteArray();
        System.out.println(Protocol.CRYPTO.toBase64(target));
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
        var hash = Protocol.CRYPTO.hashBlockForPoW(
            txHash,
            nonceHash,
            lastHash
        );

        // check if hash is less than target
        for (int i = 0; i < hash.length; i++) {
            if (hash[i] > target[i]) {
                return false;
            } else if (hash[i] < target[i]) {
                return true;
            }
        }

        return true;
    }

    public static int getBits(long difficulty) {
        return 0;
    }

    public static byte[] getTarget(int bits) {
        return null;
    }

}
