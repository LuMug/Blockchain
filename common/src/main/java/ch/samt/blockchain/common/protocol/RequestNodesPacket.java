package ch.samt.blockchain.common.protocol;

import ch.samt.blockchain.common.utils.byteutils.Offset;

public class RequestNodesPacket {
    
    public RequestNodesPacket(byte[] packet) {
        Offset offset = new Offset(1);
    }

    public static byte[] create(String username, byte[] password) {
        byte[] packet = new byte[username.length() + password.length + 6];
        Offset offset = new Offset();

        //writeByte(packet, Protocol.LOGIN, offset);
        //writeString(packet, username, offset);
        //writeBlob(packet, password, offset);

        return packet;
    }

}