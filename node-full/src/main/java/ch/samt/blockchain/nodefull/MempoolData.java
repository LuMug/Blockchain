package ch.samt.blockchain.nodefull;

public class MempoolData {

    private long utxoOffset;
    private byte[] lastTxHash;

    public void setUtxoOffset(long utxoOffset) {
        this.utxoOffset = utxoOffset;
    }

    public void setLastTxHash(byte[] lastTxHash) {
        this.lastTxHash = lastTxHash;
    }

    public long getUtxoOffset() {
        return utxoOffset;
    }

    public byte[] getLastTxHash() {
        return lastTxHash;
    }
    
}
