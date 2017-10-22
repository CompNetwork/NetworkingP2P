package main.file;

public class ChunkifiedFileUtilities {
    public static byte[] getByteSet(boolean[] bitset) {
        int bytesetLength = (int) Math.ceil(bitset.length/8.0);
        byte byteset[] = new byte[bytesetLength];

        for ( int i = 0; i !=byteset.length; ++i ) {
            byte current = 0;
            for ( int j = 0; j != 8; ++j ) {
                if ( (8*i + j) < bitset.length ) {
                    if ( bitset[8*i+j] ) {
                        current |= (0x01 << (7-j));
                    }
                }

            }
            byteset[i] = current;
        }
        return byteset;
    }

    public static boolean[] getBitSet(byte[] byteset) {
        return null;
    }
}
