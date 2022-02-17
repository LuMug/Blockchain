package ch.samt.blockchain.common.protocol;

import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;

import java.util.UUID;

import ch.samt.blockchain.common.utils.byteutils.Offset;

public class RequestNodesPacket {
    
    private int amount;
    private UUID exclude;

    public RequestNodesPacket(byte[] packet) {
        Offset offset = new Offset(1);

        this.amount = readIntLE(packet, offset);
        this.exclude = readUUID(packet, offset);
    }

    public int getAmount() {
        return amount;
    }

    public UUID getExclude() {
        return exclude;
    }

    public static byte[] create(int amount, UUID exclude) {
        byte[] packet = new byte[21];

        Offset offset = new Offset();

        writeByte(packet, Protocol.REQUEST_NODES, offset);
        writeIntLE(packet, amount, offset);
        writeUUID(packet, exclude, offset);

        return packet;
    }

}