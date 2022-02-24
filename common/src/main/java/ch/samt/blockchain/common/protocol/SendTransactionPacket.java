package ch.samt.blockchain.common.protocol;

import ch.samt.blockchain.common.utils.byteutils.Offset;
import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;

public class SendTransactionPacket {
    
    private byte[] recipient;

    private byte[] sender;

    private long amount;

    private long timestamp;

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
        this.recipient = readBlob(packet, offset);
        this.sender = readBlob(packet, offset);
        this.amount = readLongLE(packet, offset);
        this.timestamp = readLongLE(packet, offset);
        this.lastTransactionHash = readBlob(packet, offset);
        this.signature = readBlob(packet, offset);
    }

    public static byte[] create(byte[] recipient, byte[] sender, long amount, long timestamp, byte[] lastTransactionHash, byte[] signature) {
        int size = 33 + recipient.length + sender.length + lastTransactionHash.length + signature.length;

        byte[] packet = new byte[size];
        Offset offset = new Offset();

        writeByte(packet, Protocol.SEND_TRANSACTION, offset);
        writeBlob(packet, recipient, offset);
        writeBlob(packet, sender, offset);
        writeLongLE(packet, amount, offset);
        writeLongLE(packet, timestamp, offset);
        writeBlob(packet, lastTransactionHash, offset);
        writeBlob(packet, signature, offset);

        return packet;
    }

}