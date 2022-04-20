package ch.samt.blockchain.common.protocol;

import ch.samt.blockchain.common.utils.byteutils.Offset;
import static ch.samt.blockchain.common.utils.byteutils.ByteUtils.*;

import java.net.InetSocketAddress;

public class ServeNodesPacket {
    
    private InetSocketAddress[] nodes;

    public ServeNodesPacket(byte[] packet) {
        Offset offset = new Offset(1);

        int amount = readIntLE(packet, offset);

        this.nodes = new InetSocketAddress[amount];

        for (int i = 0; i < amount; i++) {
            this.nodes[i] = readAddress(packet, offset);
        }
    }

    public InetSocketAddress[] getNodes() {
        return nodes;
    }

    public static byte[] create(InetSocketAddress... nodes) {
        int size = 5 + 3 * nodes.length;

        for (var node : nodes) {
            size += node.getAddress().toString().length();
        }

        byte[] packet = new byte[size];
        Offset offset = new Offset();

        writeByte(packet, Protocol.SERVE_NODES, offset); 
        writeIntLE(packet, nodes.length, offset);

        for (var node : nodes) {
            writeAddress(packet, node, offset);
        }

        return packet;
    }

}