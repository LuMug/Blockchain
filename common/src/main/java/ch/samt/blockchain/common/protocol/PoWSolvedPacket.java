package ch.samt.blockchain.common.protocol;

import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;

import ch.samt.blockchain.common.utils.byteutils.Offset;

public class PoWSolvedPacket {
    
    private byte[] nonce;
    private byte[] miner;

    public PoWSolvedPacket(byte[] packet) {
        Offset offset = new Offset(1);

        this.nonce = readBlob(packet, offset);
        this.miner = readBlob(packet, offset);
    }

    public byte[] getNonce() {
        return nonce;
    }
    
    public byte[] getMiner() {
        return miner;
    }

    public static byte[] create(byte[] nonce, byte[] miner) {
        byte[] packet = new byte[3 + miner.length];
        Offset offset = new Offset();

        writeByte(packet, Protocol.POW_SOLVED, offset);
        writeBlob(packet, nonce, offset);
        writeBlob(packet, miner, offset);

        return packet;
    }

}
