package ch.samt.blockchain.common.protocol;

import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;

public class RequestNodesPacket {
    
    private int amount;

    public RequestNodesPacket(byte[] packet) {
        this.amount = readIntLE(packet, 1);
    }

    public int getAmount() {
        return amount;
    }

    public static byte[] create(int amount) {
        byte[] packet = new byte[5];

        writeByte(packet, Protocol.REQUEST_NODES, 0);
        writeIntLE(packet, amount, 1);

        return packet;
    }

}