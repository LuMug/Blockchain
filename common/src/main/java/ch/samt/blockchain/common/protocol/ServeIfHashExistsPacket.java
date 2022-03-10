package ch.samt.blockchain.common.protocol;
import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;
import ch.samt.blockchain.common.utils.byteutils.Offset;

public class ServeIfHashExistsPacket {

    private byte result;

    public ServeIfHashExistsPacket(byte[] packet) {
        Offset offset = new Offset(1);
        this.result = readByte(packet, offset);
    }

    public byte getHash() {
        return result;
    }

    public static byte[] create(int length) {
        byte[] packet = new byte[];

        Offset offset = new Offset();

        writeByte(packet, Protocol.SERVE_REQUEST_IF_HASH_EXISTS, offset);
        writeBlob(packet, result, offset);

        return packet;
    }
}
