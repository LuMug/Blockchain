package ch.samt.blockchain.common.protocol;

import ch.samt.blockchain.common.utils.byteutils.Offset;
import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;

public class RequestNodesPacket {
    
    private int amount;

    public RequestNodesPacket(byte[] packet) {
        Offset offset = new Offset(1);

        this.amount = readIntLE(packet, offset);
    }

    public int getAmount() {
        return amount;
    }

    public static byte[] create(int amount) {
        byte[] packet = new byte[5];
        Offset offset = new Offset();

        writeByte(packet, Protocol.REQUEST_NODES, offset);
        writeIntLE(packet, amount, offset);

        return packet;
    }

}