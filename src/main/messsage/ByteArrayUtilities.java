package main.messsage;

import java.util.Arrays;

public class ByteArrayUtilities {
    // Assuming bigendianness
    public static int recombine4BytesIntoInts(byte msb, byte a, byte b, byte lsb) {
        return (msb << 24) | (a << 16) | (b << 8 ) | lsb;
    }

    // Assuming bigendianness
    public static byte[] SplitIntInto4ByteArray(int input) {
        byte[] output = new byte[4];
        output[0] = (byte)((input >>> 24) & 0x000000FF);
        output[1] = (byte)((input >>> 16) & 0x000000FF);
        output[2] = (byte)((input >>> 8) & 0x000000FF);
        output[3] = (byte)(input & 0x000000FF);
        return output;
    }


    public static byte[] combineTwoByteArrays(byte[] A, byte[] B) {
        byte[] output = new byte[A.length+B.length];
        System.arraycopy(A,0,output,0,A.length);
        System.arraycopy(B,0,output,A.length,B.length);
        return output;
    }

    public static byte[] combineThreeByteArrays(byte[] A, byte[] B, byte[] C) {
        byte[] output = new byte[A.length+B.length + C.length];
        System.arraycopy(A,0,output,0,A.length);
        System.arraycopy(B,0,output,A.length,B.length);
        System.arraycopy(C,0,output,A.length+B.length,C.length);
        return output;
    }

    public static int recombine4ByteArrayIntoInt(byte[] m3) {
        if ( m3.length != 4 ) {
            System.out.println(Arrays.toString(m3));
            System.out.println("m3 size is " + m3.length);
            throw new IllegalArgumentException("Error, m3 must be size 4");
        }
        return recombine4BytesIntoInts(m3[0],m3[1],m3[2],m3[3]);
    }
}
