package ch.samt.blockchain.common.protocol;

import ch.samt.blockchain.common.utils.byteutils.Offset;
import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;

public class DownloadDonePacket {
    
    private DownloadDonePacket() {}

    public static byte[] create() {
        byte[] packet = new byte[1];

        Offset offset = new Offset();

        writeByte(packet, Protocol.DOWNLOAD_DONE, offset);

        return packet;
    }

}
