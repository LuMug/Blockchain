package ch.samt.blockchain.node.blockchain;

public record Transaction(byte[] fromAddress, byte[] toAddress, long amount, long timestamp, byte[] signature) {

    public boolean isValid() {
        // retrieve public key
        // compute hash of transaction
        // check signature on hash
        
        return true;
    }

}