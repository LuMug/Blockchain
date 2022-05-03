package ch.samt.blockchain.nodefull.models;

public record Block(
    int id,
    long difficulty,
    byte[] txHash,
    byte[] nonce,
    byte[] miner,
    long timestamp,
    byte[] lastHash,
    byte[] hash,
    int nTx) {

}