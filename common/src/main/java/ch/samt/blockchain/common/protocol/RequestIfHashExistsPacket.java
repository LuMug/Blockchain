package ch.samt.blockchain.common.protocol;
import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;
import ch.samt.blockchain.common.utils.byteutils.Offset;

public class RequestIfHashExistsPacket {

    private byte[] hash;

    public RequestIfHashExistsPacket(byte[] packet) {
        Offset offset = new Offset(1);
        this.hash = readBlob(packet, offset);
    }

    public byte[] getHash() {
        return hash;
    }

    public static byte[] create(byte[] hash) {
        byte[] packet = new byte[1 + hash.length];

        Offset offset = new Offset();

        writeByte(packet, Protocol.REQUEST_IF_HASH_EXISTS, offset);
        writeBlob(packet, hash, offset);

        return packet;
    }
}
