package main.file;

import java.util.Arrays;

public class DefaultValueChunk implements  FileChunk {
    int chunkSize;
    int returnValue;
    public DefaultValueChunk(int size, int value) {
        this.returnValue = value;
        this.chunkSize = size;
    }
    @Override
    public int size() {
        return chunkSize;
    }

    @Override
    public int get(int i) {
        return returnValue;
    }

    @Override
    public byte[] asByteArray() {
        byte[] val = new byte[this.size()];
        Arrays.fill(val,(byte)returnValue);
        return val;
    }
}
