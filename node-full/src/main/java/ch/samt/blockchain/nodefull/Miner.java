package ch.samt.blockchain.nodefull;

import java.math.BigInteger;

import ch.samt.blockchain.common.protocol.Protocol;

public class Miner {
    
    private byte[] txHash = new byte[32];
    private byte[] lastHash = new byte[32];
    private byte[] target;

    public synchronized void addTxHash(byte[] hash) {
        for (int i = 0; i < txHash.length; i++) {
            txHash[i] ^= hash[i];
        }
    }

    public synchronized void setLastHash(byte[] lastHash) {
        this.lastHash = lastHash;
    }

    public synchronized void setDifficulty(long difficulty) {
        // target = max_target / difficulty

        var target = Protocol.Blockchain.MAX_TARGET
            .divide(new BigInteger(Long.toString(difficulty)))
            .toByteArray();
        
        // pad target into this.target
        this.target = new byte[32];
        for (int i = 0; i < target.length; i++) {
            this.target[32 - target.length + i] = target[i];
        }
    }
    
    public byte[] getTxHash() {
        return txHash;
    }

    public synchronized void clear() {
        for (int i = 0; i < txHash.length; i++) {
            txHash[i] = lastHash[i] = 0;
        }
    }

    public boolean isMined(byte[] nonce) {
        var hash = Protocol.CRYPTO.hashBlockForPoW(
            txHash,
            nonce,
            lastHash
        );

        // check if hash is less than target
        for (int i = 0; i < hash.length; i++) {
            if ((hash[i] & 255) > (target[i] & 255)) {
                return false;
            } else if ((hash[i] & 255) < (target[i] & 255)) {

                //System.out.print("this ");
                //print(hash);
                //System.out.print("less th ");
                //print(target);
                return true;
            }
        }

        return true;
    }

}
