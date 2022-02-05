package ch.samt.blockchain.common.protocol;

import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;

public class RegisterNodePacket {

    private int port;

    public RegisterNodePacket(byte[] packet) {
        this.port = readUShortLE(packet, 1);
    }

    public int getPort() {
        return port;
    }

    public static byte[] create(int port) {
        byte[] packet = new byte[3];

        writeByte(packet, Protocol.REGISTER_NODE, 0);
        writeShortLE(packet, port, 1);
        
        return packet;
    }

}