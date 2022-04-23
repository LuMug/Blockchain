package ch.samt.blockchain.nodefull.database.models;

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