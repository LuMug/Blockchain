package ch.samt.blockchain.nodefull.utils;

import java.util.Arrays;

public class ByteArrayKey {
    
    private byte[] array;

    public ByteArrayKey(byte[] array) {
        this.array = array;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        };

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        var key = (ByteArrayKey) obj;
        return Arrays.equals(array, key.array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }

    public byte[] key() {
        return array;
    }

}