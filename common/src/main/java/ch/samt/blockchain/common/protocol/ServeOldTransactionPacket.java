package ch.samt.blockchain.common.protocol;

import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;
import ch.samt.blockchain.common.utils.byteutils.Offset;

public class ServeOldTransactionPacket {
    
    private long timestamp;

    private byte[] recipient;

    private byte[] senderPublicKey;

    private long amount;

    private byte[] lastTransactionHash;

    private byte[] signature;

    public long getAmount() {
        return amount;
    }
    public byte[] getLastTransactionHash() {
        return lastTransactionHash;
    }
    public byte[] getRecipient() {
        return recipient;
    }
    public byte[] getSenderPublicKey() {
        return senderPublicKey;
    }

    public byte[] getSignature() {
        return signature;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ServeOldTransactionPacket(byte[] packet) {
        Offset offset = new Offset(1);
        this.timestamp = readLongLE(packet, offset);
        this.recipient = readBlob(packet, offset);
        this.senderPublicKey = readBlob(packet, offset);
        this.amount = readLongLE(packet, offset);
        this.lastTransactionHash = readBlob(packet, offset);
        this.signature = readBlob(packet, offset);
    }

    public static byte[] create(byte[] recipient, byte[] senderPublicKey, long amount, byte[] lastTransactionHash, byte[] signature, long timestamp) {
        int size = 33 + recipient.length + senderPublicKey.length + lastTransactionHash.length + signature.length;

        byte[] packet = new byte[size];
        Offset offset = new Offset();

        writeByte(packet, Protocol.SERVE_OLD_TX, offset);
        writeLongLE(packet, timestamp, offset);
        writeBlob(packet, recipient, offset);
        writeBlob(packet, senderPublicKey, offset);
        writeLongLE(packet, amount, offset);
        writeBlob(packet, lastTransactionHash, offset);
        writeBlob(packet, signature, offset);

        return packet;
    }

}
