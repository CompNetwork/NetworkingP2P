package main.file;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class ChunkifiedFileUtilitiesTestt {

    @Test
    public void getByteSet() throws Exception {
        boolean[] bitset = getBitSetFromString("10001110 111");
        byte[] byteset = ChunkifiedFileUtilities.getByteSet(bitset);

        Assert.assertEquals(2,byteset.length);
        //System.out.println("Expected: " + Integer.toBinaryString(0xb1) + " Found: " + Integer.toBinaryString(byteset[0] &  0xff));
        byte expectedBytes[] = {(byte)0x8e,(byte)0xE0};
        Assert.assertArrayEquals(expectedBytes,byteset);
        Assert.assertArrayEquals(bitset,ChunkifiedFileUtilities.getBitSetFromByteSet(byteset,bitset.length));
        // Check the string rep also
        char charA = (char)0x8e;
        char charB = (char)0xE0;
        Assert.assertEquals(Character.toString(charA)+Character.toString(charB),ChunkifiedFileUtilities.getStringFromBitSet(bitset));
    }

    @Test
    public void getUnder1ByteSet() throws Exception {
        boolean[] bitset = getBitSetFromString("1011000");
        byte[] byteset = ChunkifiedFileUtilities.getByteSet(bitset);

        Assert.assertEquals(1,byteset.length);
        byte expectedBytes[] = {(byte)0xb0};
        Assert.assertArrayEquals(expectedBytes,byteset);
        Assert.assertArrayEquals(bitset,ChunkifiedFileUtilities.getBitSetFromByteSet(byteset,bitset.length));
        // Check the string rep also
        char charA = (char)0xb0;
        Assert.assertEquals(Character.toString(charA),ChunkifiedFileUtilities.getStringFromBitSet(bitset));
    }

    @Test
    public void getExactly1Byte() throws Exception {
        boolean[] bitset = getBitSetFromString("10110000");
        byte[] byteset = ChunkifiedFileUtilities.getByteSet(bitset);

        Assert.assertEquals(1,byteset.length);
        byte expectedBytes[] = {(byte)0xb0};
        Assert.assertArrayEquals(expectedBytes,byteset);
        Assert.assertArrayEquals(bitset,ChunkifiedFileUtilities.getBitSetFromByteSet(byteset,bitset.length));
        char charA = (char)0xb0;
        Assert.assertEquals(Character.toString(charA),ChunkifiedFileUtilities.getStringFromBitSet(bitset));
    }


    @Test
    public void getOneByteAllZeroes() throws Exception {
        boolean[] bitset = getBitSetFromString("00000000");
        byte[] byteset = ChunkifiedFileUtilities.getByteSet(bitset);

        Assert.assertEquals(1,byteset.length);
        byte expectedBytes[] = {(byte)0x00};
        Assert.assertArrayEquals(expectedBytes,byteset);
        Assert.assertArrayEquals(bitset,ChunkifiedFileUtilities.getBitSetFromByteSet(byteset,bitset.length));
        char charA = (char)0x00;
        Assert.assertEquals(Character.toString(charA),ChunkifiedFileUtilities.getStringFromBitSet(bitset));
    }

    @Test
    public void getOneByteAllOnes() throws Exception {
        boolean[] bitset = getBitSetFromString("11111111");
        byte[] byteset = ChunkifiedFileUtilities.getByteSet(bitset);

        Assert.assertEquals(1,byteset.length);
        byte expectedBytes[] = {(byte)0xFF};
        Assert.assertArrayEquals(expectedBytes,byteset);
        Assert.assertArrayEquals(bitset,ChunkifiedFileUtilities.getBitSetFromByteSet(byteset,bitset.length));
        char charA = (char)0xff;
        Assert.assertEquals(Character.toString(charA),ChunkifiedFileUtilities.getStringFromBitSet(bitset));
    }

    private boolean[] getBitSetFromString(String s) {
        ArrayList<Boolean> bitset = new ArrayList<>();
        for ( int i = 0; i != s.length(); ++i ) {
            if ( s.charAt(i) == '1' ) {
                bitset.add(true);
            } else if (s.charAt(i) == '0' ) {
                bitset.add(false);
            }
            // Ignore othercharacters like space to enable human readability.
        }
        boolean[] booleans = new boolean[bitset.size()];

        for ( int i = 0; i != booleans.length; ++i ) {
            booleans[i] = bitset.get(i);
        }

        return booleans;
    }

}