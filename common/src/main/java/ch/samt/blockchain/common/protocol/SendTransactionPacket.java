package ch.samt.blockchain.common.protocol;

import ch.samt.blockchain.common.utils.byteutils.Offset;
import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;

public class SendTransactionPacket {
    
    private long timestamp; // to be set by deployer node

    private byte[] recipient;

    private byte[] sender;

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
    public byte[] getSender() {
        return sender;
    }

    public byte[] getSignature() {
        return signature;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public SendTransactionPacket(byte[] packet) {
        Offset offset = new Offset(1);
        this.timestamp = readLongLE(packet, offset);
        this.recipient = readBlob(packet, offset);
        this.sender = readBlob(packet, offset);
        this.amount = readLongLE(packet, offset);
        this.lastTransactionHash = readBlob(packet, offset);
        this.signature = readBlob(packet, offset);
    }

    public static byte[] create(byte[] recipient, byte[] sender, long amount, byte[] lastTransactionHash, byte[] signature) {
        int size = 33 + recipient.length + sender.length + lastTransactionHash.length + signature.length;

        byte[] packet = new byte[size];
        Offset offset = new Offset();

        writeByte(packet, Protocol.SEND_TRANSACTION, offset);
        writeLongLE(packet, 0, offset);
        writeBlob(packet, recipient, offset);
        writeBlob(packet, sender, offset);
        writeLongLE(packet, amount, offset);
        writeBlob(packet, lastTransactionHash, offset);
        writeBlob(packet, signature, offset);

        return packet;
    }

    public static byte[] toSign(byte[] recipient, byte[] sender, long amount, byte[] lastTransactionHash) {
        int size = 20 + recipient.length + sender.length + lastTransactionHash.length;

        byte[] packet = new byte[size];
        Offset offset = new Offset();

        writeBlob(packet, recipient, offset);
        writeBlob(packet, sender, offset);
        writeLongLE(packet, amount, offset);
        writeBlob(packet, lastTransactionHash, offset);

        return packet;
    }
    
    public static void setTimestamp(byte[] packet, long timestamp) {
        writeLongLE(packet, timestamp, 1);
    }

}