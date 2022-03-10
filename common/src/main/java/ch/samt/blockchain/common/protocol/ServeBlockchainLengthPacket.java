package ch.samt.blockchain.common.protocol;
import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;
import ch.samt.blockchain.common.utils.byteutils.Offset;

public class ServeBlockchainLengthPacket {
    
    private int length;

    public ServeBlockchainLengthPacket(byte[] packet) {
        Offset offset = new Offset(1);
        this.length = readIntLE(packet, offset);
    }

    public int getLength() {
        return length;
    }

    public static byte[] create(int length) {
        byte[] packet = new byte[5];

        Offset offset = new Offset();

        writeByte(packet, Protocol.SERVE_REQUEST_BLOCKCHAIN_LENGTH, offset);
        writeIntLE(packet, length, offset);

        return packet;
    }
    
}