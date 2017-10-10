package main.file;

import java.util.Arrays;

// Does not check for a negative chunk size, but that is not valid.
public class DefaultValueChunk implements  FileChunk {
    int chunkSize;
    byte returnValue;
    public DefaultValueChunk(int size, byte value) {
        this.returnValue = value;
        this.chunkSize = size;
    }
    @Override
    public int size() {
        return chunkSize;
    }

    @Override
    public byte get(int i) {
        return returnValue;
    }

    @Override
    public byte[] asByteArray() {
        byte[] val = new byte[this.size()];
        Arrays.fill(val,(byte)returnValue);
        return val;
    }
}
