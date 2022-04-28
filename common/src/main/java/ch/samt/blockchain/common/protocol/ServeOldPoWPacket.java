package ch.samt.blockchain.common.protocol;

import ch.samt.blockchain.common.utils.byteutils.Offset;
import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;

public class ServeOldPoWPacket {
    
    private byte[] nonce;

    private byte[] miner;
    
    private long timestamp;

    public ServeOldPoWPacket(byte[] packet) {
        Offset offset = new Offset(1);

        this.nonce = readBlob(packet, offset);
        this.miner = readBlob(packet, offset);
        this.timestamp = readLongBE(packet, offset);
    }

    public byte[] getNonce() {
        return nonce;
    }
    
    public byte[] getMiner() {
        return miner;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static byte[] create(byte[] nonce, byte[] miner, long timestamp) {
        byte[] packet = new byte[17 + miner.length + nonce.length];
        Offset offset = new Offset();

        writeByte(packet, Protocol.SERVE_OLD_POW, offset);
        writeBlob(packet, nonce, offset);
        writeBlob(packet, miner, offset);
        writeLongBE(packet, timestamp, offset);

        return packet;
    }

}
