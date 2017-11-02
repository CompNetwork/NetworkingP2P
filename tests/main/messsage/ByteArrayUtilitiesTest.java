package main.messsage;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ByteArrayUtilitiesTest {
    @Test
    public void recombine4BytesIntoInts() throws Exception {
        int combined = ByteArrayUtilities.recombine4BytesIntoInts((byte)0xFE,(byte)0xDC,(byte)0xAB,(byte)0x98);
        Assert.assertEquals(0xFEDCAB98,combined);
    }

    @Test
    public void splitIntInto4ByteArray() throws Exception {
        int combined = 0xFEDCAB98;
        byte[] split = ByteArrayUtilities.SplitIntInto4ByteArray(combined);
        Assert.assertEquals(split[0],0xFFFFFFFE);
        Assert.assertEquals(split[1],0xFFFFFFDC);
        Assert.assertEquals(split[2],0xFFFFFFAB);
        Assert.assertEquals(split[3],0xFFFFFF98);
    }

    @Test
    public void combineTwoByteArrays() throws Exception {
        byte[] arrayA = new byte[2];
        arrayA[0] = (byte)0xFA;
        arrayA[1] = (byte)0xCB;
        byte[] arrayB = new byte[2];
        arrayB[0] = (byte)0xE0;
        arrayB[1] = (byte)0xD1;

        byte[] arrayC = ByteArrayUtilities.combineTwoByteArrays(arrayA,arrayB);

        Assert.assertArrayEquals(arrayA, Arrays.copyOfRange(arrayC,0,2));
        Assert.assertArrayEquals(arrayB, Arrays.copyOfRange(arrayC,2,4));


    }

    @Test
    public void combineThreeByteArrays() throws Exception {
        byte[] arrayA = new byte[2];
        arrayA[0] = (byte)0xFA;
        arrayA[1] = (byte)0xCB;
        byte[] arrayB = new byte[2];
        arrayB[0] = (byte)0xE0;
        arrayB[1] = (byte)0xD1;
        byte[] arrayC = new byte[2];
        arrayC[0] = (byte)0x34;
        arrayC[1] = (byte)0x56;

        byte[] arrayD = ByteArrayUtilities.combineThreeByteArrays(arrayA,arrayB,arrayC);

        Assert.assertArrayEquals(arrayA, Arrays.copyOfRange(arrayD,0,2));
        Assert.assertArrayEquals(arrayB, Arrays.copyOfRange(arrayD,2,4));
        Assert.assertArrayEquals(arrayC, Arrays.copyOfRange(arrayD,4,6));
    }

    @Test
    public void recombine4ByteArrayIntoInt() throws Exception {
        byte[] testArray = new byte[4];
        testArray[0] = (byte)0xFE;
        testArray[1] = (byte) 0xDC;
        testArray[2] = (byte) 0xAB;
        testArray[3] = (byte) 0x98;
        int combined = ByteArrayUtilities.recombine4ByteArrayIntoInt(testArray);
        Assert.assertEquals(0xFEDCAB98,combined);
    }

}