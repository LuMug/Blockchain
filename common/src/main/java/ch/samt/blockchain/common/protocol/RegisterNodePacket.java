package ch.samt.blockchain.common.protocol;

import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;

public class RegisterNodePacket {

    private RegisterNodePacket() {}

    private static byte[] packet = new byte[1];

    static {
        writeByte(packet, Protocol.REGISTER_NODE, 0);
    }

    public static byte[] create() {
        return packet;
    }

}