package ch.samt.blockchain.common.protocol;
import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;
import ch.samt.blockchain.common.utils.byteutils.Offset;

public class RequestBlockchainLengthPacket{

    private RequestBlockchainLengthPacket() {}

    public static byte[] create(int length) {
        byte[] packet = new byte[1];

        Offset offset = new Offset();

        writeByte(packet, Protocol.REQUEST_BLOCKCHAIN_LENGTH, offset);

        return packet;
    }

}