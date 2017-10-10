package main.file;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class DefaultValueChunkTest {
    int size = 0;
    byte value = 0;
    DefaultValueChunk defaultValueChunk = null;
    @Before
    public void before() {
        size = 42;
        value = 2;
         defaultValueChunk = new DefaultValueChunk(size,value);
    }

    @Test
    public void size() throws Exception {
        Assert.assertEquals(size,defaultValueChunk.size());
    }

    @Test
    public void get() throws Exception {
        Assert.assertEquals(value,defaultValueChunk.get(0));
    }

    @Test
    public void asByteArray() throws Exception {
        byte[] array = new byte[size];
        Arrays.fill(array,(byte)value);
        Assert.assertArrayEquals(array,defaultValueChunk.asByteArray());
    }

}