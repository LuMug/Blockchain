
import java.io.IOException;
import java.io.OutputStream;

public class PacketOutputStream {

    private OutputStream out;
    private boolean hasEnded = false;

    public PacketOutputStream(OutputStream out) {
        this.out = out;
    }

    public synchronized void writePacket(byte[] packet) throws IOException {
        byte b0 = (byte) ((packet.length >> 0) & 0xFF);
        byte b1 = (byte) ((packet.length >> 8) & 0xFF);
        byte b2 = (byte) ((packet.length >> 16) & 0xFF);
        byte b3 = (byte) ((packet.length >> 24) & 0xFF);

        out.write(new byte[]{b0, b1, b2, b3}); // write length
        out.write(packet);
    }

    public boolean hasEnded() {
        return hasEnded;
    }

    public void close() throws IOException {
        hasEnded = true;
        out.close();
    }

}
