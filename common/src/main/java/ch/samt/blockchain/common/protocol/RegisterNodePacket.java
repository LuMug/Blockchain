package ch.samt.blockchain.common.protocol;

import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;

import java.util.UUID;

import ch.samt.blockchain.common.utils.byteutils.Offset;

public class RegisterNodePacket {

    private int port;
    private UUID uuid;

    public RegisterNodePacket(byte[] packet) {
        Offset offset = new Offset(1);
        this.port = readUShortLE(packet, offset);
        this.uuid = readUUID(packet, offset);
    }

    public int getPort() {
        return port;
    }

    public UUID getUUID() {
        return uuid;
    }

    public static byte[] create(int port, UUID uuid) {
        byte[] packet = new byte[19];
        Offset offset = new Offset();

        writeByte(packet, Protocol.REGISTER_NODE, offset);
        writeShortLE(packet, (short) port, offset);
        writeUUID(packet, uuid, offset);

        return packet;
    }

}