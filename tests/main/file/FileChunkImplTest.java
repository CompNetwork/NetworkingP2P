package main.file;

import org.junit.Test;

import static org.junit.Assert.*;
public class FileChunkImplTest {

    private byte[] values = {(byte) 1, (byte)2, (byte)3};

    private FileChunkImpl  fileChunk = new FileChunkImpl(values);
    @Test
    public void size() throws Exception {
        assertEquals(fileChunk.size(),values.length);
    }

    @Test
    public void get() throws Exception {
        for ( int i = 0; i != fileChunk.size(); ++i ) {
            assertEquals(fileChunk.get(0), values[0]);
        }
    }

    @Test
    public void asByteArray() throws Exception {
        assertArrayEquals(fileChunk.asByteArray(),values);
    }

}