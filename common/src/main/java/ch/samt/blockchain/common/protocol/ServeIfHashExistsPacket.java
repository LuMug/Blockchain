package ch.samt.blockchain.common.protocol;
import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;
import ch.samt.blockchain.common.utils.byteutils.Offset;

public class ServeIfHashExistsPacket {

    private boolean result;

    public ServeIfHashExistsPacket(byte[] packet) {
        Offset offset = new Offset(1);
        this.result = readByte(packet, offset) == 1;
    }

    public boolean getResult() {
        return result;
    }

    public static byte[] create(boolean result) {
        byte[] packet = new byte[2];

        Offset offset = new Offset();

        writeByte(packet, Protocol.SERVE_REQUEST_IF_HASH_EXISTS, offset);
        writeByte(packet, result ? (byte) 1 : (byte) 0, offset);

        return packet;
    }

}
