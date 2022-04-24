package ch.samt.blockchain.common.protocol;
import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;
import ch.samt.blockchain.common.utils.byteutils.Offset;

public class ServeIfHashExistsPacket {

    private int id;

    public ServeIfHashExistsPacket(byte[] packet) {
        Offset offset = new Offset(1);
        this.id = readIntLE(packet, offset);
    }

    public int getId() {
        return id;
    }

    public static byte[] create(int id) {
        byte[] packet = new byte[5];

        Offset offset = new Offset();

        writeByte(packet, Protocol.SERVE_IF_HASH_EXISTS, offset);
        writeIntLE(packet, id, offset);

        return packet;
    }

}
