package ch.samt.blockchain.nodefull;

public record Block(
    int id,
    int difficulty,
    byte[] txHash,
    byte[] nonce,
    byte[] miner,
    long timestamp,
    
    byte[] lastHash,
    byte[] hash,
    int nTx) {

}