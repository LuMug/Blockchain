package ch.samt.blockchain.nodefull;

public record Block(
    int id,
    int difficulty,
    byte[] txHash,
    byte[] nonce,
    byte[] miner,
    byte[] lastHash,
    long timestamp,
    
    byte[] hash,
    int nTx) {

}