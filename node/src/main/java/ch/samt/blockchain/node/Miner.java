package ch.samt.blockchain.node;

public class Miner {
    
    private byte[] theta = new byte[32];

    public void addTx(byte[] hash) {
        xor(hash);
    }
    
    public void setNonce(byte[] nonceHash) {
        xor(nonceHash);
    }

    public void setHeight(byte[] heightHash) {
        xor(heightHash);
    }
    
    public byte[] getTheta() {
        return theta;
    }

    public void clear() {
        for (int i = 0; i < theta.length; i++) {
            theta[i] = 0;
        }
    }

    private void xor(byte[] data) {
        for (int i = 0; i < theta.length; i++) {
            theta[i] ^= data[i];
        }
    }

}
