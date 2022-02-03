
import java.net.InetSocketAddress;
import java.util.UUID;

public class ByteUtils {

    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeByte(byte[] data, int value, int offset) {
        data[offset] = (byte) (value & 0xFF);
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return byte
     */
    public static byte readByte(byte[] data, int offset) {
        return data[offset];
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return int
     */
    public static int readUByte(byte[] data, int offset) {
        return data[offset] & 0xFF;
    }

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeShortLE(byte[] data, int value, int offset) {
        data[offset++] = (byte) ((value >> 000) & 0xFF);
        data[offset  ] = (byte) ((value >> 010) & 0xFF);
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return short
     */
    public static short readShortLE(byte[] data, int offset) {
        return (short) (
            ((data[offset++] & 0xFF) << 000) |
            ((data[offset  ] & 0xFF) << 010)
        );
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return int
     */
    public static int readUShortLE(byte[] data, int offset) {
        return
            ((data[offset++] & 0xFF) << 000) |
            ((data[offset  ] & 0xFF) << 010);
    }

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeIntLE(byte[] data, int value, int offset) {
        data[offset++] = (byte) ((value >> 000) & 0xFF);
        data[offset++] = (byte) ((value >> 010) & 0xFF);
        data[offset++] = (byte) ((value >> 020) & 0xFF);
        data[offset  ] = (byte) ((value >> 030) & 0xFF);
    }

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeIntLE(byte[] data, long value, int offset) {
        data[offset++] = (byte) ((value >> 000) & 0xFF);
        data[offset++] = (byte) ((value >> 010) & 0xFF);
        data[offset++] = (byte) ((value >> 020) & 0xFF);
        data[offset  ] = (byte) ((value >> 030) & 0xFF);
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return int
     */
    public static int readIntLE(byte[] data, int offset) {
        return (
            ((data[offset++] & 0xFF) << 000) |
            ((data[offset++] & 0xFF) << 010) |
            ((data[offset++] & 0xFF) << 020) |
            ((data[offset  ] & 0xFF) << 030)
        );
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return long
     */
    public static long readUIntLE(byte[] data, int offset) {
        return 0L | (
            ((data[offset++] & 0xFF) << 000) |
            ((data[offset++] & 0xFF) << 010) |
            ((data[offset++] & 0xFF) << 020) |
            ((data[offset  ] & 0xFF) << 030)
        );
    }

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeLongLE(byte[] data, long value, int offset) {
        data[offset++] = (byte) ((value >> 000) & 0xFF);
        data[offset++] = (byte) ((value >> 010) & 0xFF);
        data[offset++] = (byte) ((value >> 020) & 0xFF);
        data[offset++] = (byte) ((value >> 030) & 0xFF);
        data[offset++] = (byte) ((value >> 040) & 0xFF);
        data[offset++] = (byte) ((value >> 050) & 0xFF);
        data[offset++] = (byte) ((value >> 060) & 0xFF);
        data[offset  ] = (byte) ((value >> 070) & 0xFF);
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return long
     */
    public static long readLongLE(byte[] data, int offset) {
        long res = 0;

        res |= (data[offset++] & 0xFFL) << 000;
        res |= (data[offset++] & 0xFFL) << 010;
        res |= (data[offset++] & 0xFFL) << 020;
        res |= (data[offset++] & 0xFFL) << 030;
        res |= (data[offset++] & 0xFFL) << 040;
        res |= (data[offset++] & 0xFFL) << 050;
        res |= (data[offset++] & 0xFFL) << 060;
        res |= (data[offset  ] & 0xFFL) << 070;

        return res;
    }

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeShortBE(byte[] data, short value, int offset) {
        data[offset++] = (byte) ((value >> 010) & 0xFF);
        data[offset  ] = (byte) ((value >> 000) & 0xFF);
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return short
     */
    public static short readShortBE(byte[] data, int offset) {
        return (short) (
            ((data[offset++] & 0xFF) << 010) |
            ((data[offset  ] & 0xFF) << 000)
        );
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return int
     */
    public static int readUShortBE(byte[] data, int offset) {
        return
            ((data[offset++] & 0xFF) << 010) |
            ((data[offset  ] & 0xFF) << 000);
    }

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeIntBE(byte[] data, int value, int offset) {
        data[offset++] = (byte) ((value >> 030) & 0xFF);
        data[offset++] = (byte) ((value >> 020) & 0xFF);
        data[offset++] = (byte) ((value >> 010) & 0xFF);
        data[offset  ] = (byte) ((value >> 000) & 0xFF);
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return int
     */
    public static int readIntBE(byte[] data, int offset) {
        return (
            ((data[offset++] & 0xFF) << 030) |
            ((data[offset++] & 0xFF) << 020) |
            ((data[offset++] & 0xFF) << 010) |
            ((data[offset  ] & 0xFF) << 000)
        );
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return long
     */
    public static long readUIntBE(byte[] data, int offset) {
        return 0L | (
            ((data[offset++] & 0xFF) << 030) |
            ((data[offset++] & 0xFF) << 020) |
            ((data[offset++] & 0xFF) << 010) |
            ((data[offset  ] & 0xFF) << 000)
        );
    }

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array the offset in the array
     */
    public static void writeLongBE(byte[] data, long value, int offset) {
        data[offset++] = (byte) ((value >> 070) & 0xFF);
        data[offset++] = (byte) ((value >> 060) & 0xFF);
        data[offset++] = (byte) ((value >> 050) & 0xFF);
        data[offset++] = (byte) ((value >> 040) & 0xFF);
        data[offset++] = (byte) ((value >> 030) & 0xFF);
        data[offset++] = (byte) ((value >> 020) & 0xFF);
        data[offset++] = (byte) ((value >> 010) & 0xFF);
        data[offset  ] = (byte) ((value >> 000) & 0xFF);
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return long
     */
    public static long readLongBE(byte[] data, int offset) {
        long res = 0;

        res |= (data[offset++] & 0xFFL) << 070;
        res |= (data[offset++] & 0xFFL) << 060;
        res |= (data[offset++] & 0xFFL) << 050;
        res |= (data[offset++] & 0xFFL) << 040;
        res |= (data[offset++] & 0xFFL) << 030;
        res |= (data[offset++] & 0xFFL) << 020;
        res |= (data[offset++] & 0xFFL) << 010;
        res |= (data[offset  ] & 0xFFL) << 000;

        return res;
    }

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeByte(byte[] data, byte value, Offset offset) {
        data[offset.getAndIncrement()] = value;
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return byte
     */
    public static byte readByte(byte[] data, Offset offset) {
        return data[offset.getAndIncrement()];
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return int
     */
    public static int readUByte(byte[] data, Offset offset) {
        return data[offset.getAndIncrement()] & 0xFF;
    }

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeShortLE(byte[] data, short value, Offset offset) {
        data[offset.getAndIncrement()] = (byte) ((value >> 000) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 010) & 0xFF);
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return short
     */
    public static short readShortLE(byte[] data, Offset offset) {
        return (short) (
            ((data[offset.getAndIncrement()] & 0xFF) << 000) |
            ((data[offset.getAndIncrement()] & 0xFF) << 010)
        );
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return int
     */
    public static int readUShortLE(byte[] data, Offset offset) {
        return
            ((data[offset.getAndIncrement()] & 0xFF) << 000) |
            ((data[offset.getAndIncrement()] & 0xFF) << 010);
    }

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeIntLE(byte[] data, int value, Offset offset) {
        data[offset.getAndIncrement()] = (byte) ((value >> 000) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 010) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 020) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 030) & 0xFF);
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return int
     */
    public static int readIntLE(byte[] data, Offset offset) {
        return (
            ((data[offset.getAndIncrement()] & 0xFF) << 000) |
            ((data[offset.getAndIncrement()] & 0xFF) << 010) |
            ((data[offset.getAndIncrement()] & 0xFF) << 020) |
            ((data[offset.getAndIncrement()] & 0xFF) << 030)
        );
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return long
     */
    public static long readUIntLE(byte[] data, Offset offset) {
        return 0L | (
            ((data[offset.getAndIncrement()] & 0xFF) << 000) |
            ((data[offset.getAndIncrement()] & 0xFF) << 010) |
            ((data[offset.getAndIncrement()] & 0xFF) << 020) |
            ((data[offset.getAndIncrement()] & 0xFF) << 030)
        );
    }

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeLongLE(byte[] data, long value, Offset offset) {
        data[offset.getAndIncrement()] = (byte) ((value >> 000) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 010) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 020) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 030) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 040) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 050) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 060) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 070) & 0xFF);
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return long
     */
    public static long readLongLE(byte[] data, Offset offset) {
        long res = 0;

        res |= (data[offset.getAndIncrement()] & 0xFFL) << 000;
        res |= (data[offset.getAndIncrement()] & 0xFFL) << 010;
        res |= (data[offset.getAndIncrement()] & 0xFFL) << 020;
        res |= (data[offset.getAndIncrement()] & 0xFFL) << 030;
        res |= (data[offset.getAndIncrement()] & 0xFFL) << 040;
        res |= (data[offset.getAndIncrement()] & 0xFFL) << 050;
        res |= (data[offset.getAndIncrement()] & 0xFFL) << 060;
        res |= (data[offset.getAndIncrement()] & 0xFFL) << 070;

        return res;
    }

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeShortBE(byte[] data, short value, Offset offset) {
        data[offset.getAndIncrement()] = (byte) ((value >> 010) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 000) & 0xFF);
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return short
     */
    public static short readShortBE(byte[] data, Offset offset) {
        return (short) (
            ((data[offset.getAndIncrement()] & 0xFF) << 010) |
            ((data[offset.getAndIncrement()] & 0xFF) << 000)
        );
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return int
     */
    public static int readUShortBE(byte[] data, Offset offset) {
        return
            ((data[offset.getAndIncrement()] & 0xFF) << 010) |
            ((data[offset.getAndIncrement()] & 0xFF) << 000);
    }

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeIntBE(byte[] data, int value, Offset offset) {
        data[offset.getAndIncrement()] = (byte) ((value >> 030) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 020) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 010) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 000) & 0xFF);
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return int
     */
    public static int readIntBE(byte[] data, Offset offset) {
        return (
            ((data[offset.getAndIncrement()] & 0xFF) << 030) |
            ((data[offset.getAndIncrement()] & 0xFF) << 020) |
            ((data[offset.getAndIncrement()] & 0xFF) << 010) |
            ((data[offset.getAndIncrement()] & 0xFF) << 000)
        );
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return long
     */
    public static long readUIntBE(byte[] data, Offset offset) {
        return 0L | (
            ((data[offset.getAndIncrement()] & 0xFF) << 030) |
            ((data[offset.getAndIncrement()] & 0xFF) << 020) |
            ((data[offset.getAndIncrement()] & 0xFF) << 010) |
            ((data[offset.getAndIncrement()] & 0xFF) << 000)
        );
    }

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeLongBE(byte[] data, long value, Offset offset) {
        data[offset.getAndIncrement()] = (byte) ((value >> 070) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 060) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 050) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 040) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 030) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 020) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 010) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value >> 000) & 0xFF);
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return long
     */
    public static long readLongBE(byte[] data, Offset offset) {
        long res = 0;

        res |= (data[offset.getAndIncrement()] & 0xFFL) << 070;
        res |= (data[offset.getAndIncrement()] & 0xFFL) << 060;
        res |= (data[offset.getAndIncrement()] & 0xFFL) << 050;
        res |= (data[offset.getAndIncrement()] & 0xFFL) << 040;
        res |= (data[offset.getAndIncrement()] & 0xFFL) << 030;
        res |= (data[offset.getAndIncrement()] & 0xFFL) << 020;
        res |= (data[offset.getAndIncrement()] & 0xFFL) << 010;
        res |= (data[offset.getAndIncrement()] & 0xFFL) << 000;

        return res;
    }

    
    /* Other */

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeString(byte[] data, String value, int offset) {
        data[offset] = (byte) value.length();

        for (int i = 0; i < value.length(); i++) {
            data[offset + i + 1] = (byte) value.charAt(i);
        }
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return String
     */
    public static String readString(byte[] data, int offset) {
        return new String(data, offset + 1, data[offset] & 0xFF);
    }

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeString(byte[] data, String value, Offset offset) {
        data[offset.getAndIncrement()] = (byte) value.length();

        for (int i = 0; i < value.length(); i++) {
            data[offset.getAndIncrement()] = (byte) value.charAt(i);
        }

    }


    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return String
     */
    public static String readString(byte[] data, Offset offset) {
        int length = data[offset.get()] & 0xFF;
        return new String(data, offset.getAndAdd(length + 1) + 1, length);
    }
    
    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeUUID(byte[] data, UUID value, int offset) {
        writeLongLE(data, value.getMostSignificantBits(),  offset);
        writeLongLE(data, value.getLeastSignificantBits(), offset + 8);
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return UUID
     */
    public static UUID readUUID(byte[] data, int offset) {
        return new UUID(
            readLongLE(data, offset),
            readLongLE(data, offset + 8)
        );
    }

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeUUID(byte[] data, UUID value, Offset offset) {
        writeLongLE(data, value.getMostSignificantBits(),  offset);
        writeLongLE(data, value.getLeastSignificantBits(), offset);
    }
    
    
    /** 
     * @param packet
     * @param offset the offset in the array
     * @return UUID
     */
    public static UUID readUUID(byte[] packet, Offset offset) {
        return new UUID(
            readLongLE(packet, offset),
            readLongLE(packet, offset)
        );
    }

    
    /** 
     * @param data the byte array
     * @param address
     * @param port
     * @param offset the offset in the array
     */
    public static void writeAddress(byte[] data, String address, int port, int offset) {
        writeString(data, address, offset);
        writeShortLE(data, (short) port, offset + address.length() + 1);
    }
    
    
    /** 
     * @param data the byte array
     * @param address
     * @param port
     * @param offset the offset in the array
     */
    public static void writeAddress(byte[] data, String address, int port, Offset offset) {
        writeString(data, address, offset);
        writeShortLE(data, (short) port, offset);
    }

    
    /** 
     * @param data the byte array
     * @param address
     * @param offset the offset in the array
     */
    public static void writeAddress(byte[] data, InetSocketAddress address, int offset) {
        writeString(data, address.getHostString(), offset);
        writeShortLE(data, (short) address.getPort(), offset + address.getHostString().length() + 1);
    }
    
    
    /** 
     * @param data the byte array
     * @param address
     * @param offset the offset in the array
     */
    public static void writeAddress(byte[] data, InetSocketAddress address, Offset offset) {
        writeString(data, address.getHostString(), offset);
        writeShortLE(data, (short) address.getPort(), offset);
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return InetSocketAddress
     */
    public static InetSocketAddress readAddress(byte[] data, int offset) {
        var address = readString(data, offset);
        
        return new InetSocketAddress(
            address,
            readUShortLE(data, offset + address.length() + 1)
        );
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return InetSocketAddress
     */
    public static InetSocketAddress readAddress(byte[] data, Offset offset) {
        return new InetSocketAddress(
            readString(data, offset),
            readUShortLE(data, offset)
        );
    }

    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeBlob(byte[] data, byte[] value, int offset) {
        data[offset++] = (byte) ((value.length >> 000) & 0xFF);
        data[offset++] = (byte) ((value.length >> 010) & 0xFF);
        data[offset++] = (byte) ((value.length >> 020) & 0xFF);
        data[offset++] = (byte) ((value.length >> 030) & 0xFF);

        for (int i = 0; i < value.length; i++) {
            data[offset++] = value[i];
        }
    }

    
    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return byte[]
     */
    public static byte[] readBlob(byte[] data, int offset) {
        int length =
            ((data[offset++] & 0xFF) << 000) |
            ((data[offset++] & 0xFF) << 010) |
            ((data[offset++] & 0xFF) << 020) |
            ((data[offset++] & 0xFF) << 030);

        byte[] result = new byte[length];

        for (int i = 0; i < length; i++) {
            result[i] = data[offset++];
        }

        return result;
    }

    
    /** 
     * @param data the byte array
     * @param value the value to encode
     * @param offset the offset in the array
     */
    public static void writeBlob(byte[] data, byte[] value, Offset offset) {
        data[offset.getAndIncrement()] = (byte) ((value.length >> 000) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value.length >> 010) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value.length >> 020) & 0xFF);
        data[offset.getAndIncrement()] = (byte) ((value.length >> 030) & 0xFF);

        for (int i = 0; i < value.length; i++) {
            data[offset.getAndIncrement()] = value[i];
        }
    }

    /** 
     * @param data the byte array
     * @param offset the offset in the array
     * @return byte[]
     */
    public static byte[] readBlob(byte[] data, Offset offset) {
        int length =
            ((data[offset.getAndIncrement()] & 0xFF) << 000) |
            ((data[offset.getAndIncrement()] & 0xFF) << 010) |
            ((data[offset.getAndIncrement()] & 0xFF) << 020) |
            ((data[offset.getAndIncrement()] & 0xFF) << 030);
        
        byte[] result = new byte[length];

        for (int i = 0; i < length; i++) {
            result[i] = data[offset.getAndIncrement()];
        }

        return result;
    }

}