package ch.samt.blockchain.common.protocol;

import ch.samt.blockchain.common.utils.byteutils.Offset;
import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;

public class RequestDownloadPacket {

    private int startId;

    public RequestDownloadPacket(byte[] packet) {
        Offset offset = new Offset(1);
        this.startId = readIntLE(packet, offset);
    }

    public int getStartId() {
        return startId;
    }

    public static byte[] create(int startId) {
        byte[] packet = new byte[19];
        Offset offset = new Offset();

        writeByte(packet, Protocol.REQUEST_DOWNLOAD, offset);
        writeIntLE(packet, startId, offset);

        return packet;
    }
    
}
