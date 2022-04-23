package ch.samt.blockchain.nodefull.database.models;

public record Transaction(
    int blockId,
    byte[] senderPub,
    byte[] recipient,
    long amount,
    long timestamp,
    byte[] lastTxHash,
    byte[] signature,
    byte[] hash) {
    
}
