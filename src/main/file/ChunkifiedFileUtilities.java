package main.file;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class ChunkifiedFileUtilities {
    public static byte[] getByteSetFromBitSet(boolean[] bitset) {
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

    public static String getStringFromByteSet(byte[] byteset) {
        return new String(byteset,  StandardCharsets.ISO_8859_1);
    }

    // Bitset A and bitset B should be the same size.
    // Else illegal argument exception.
    // Returns true if A that has a bit set that b does not.
    public static boolean doesAHaveChunksBDoesNot(boolean[] bitsetA, boolean[] bitsetB) {
        if (bitsetA.length != bitsetB.length ) {
            throw new IllegalArgumentException("Error, the bitsets must be the same length!");
        }

        for ( int i = 0; i != bitsetA.length; ++i ) {
            if ( bitsetA[i] ) {
                if (!bitsetB[i] ) {
                    // If bitsetA is true, but bitsetB is not, then we have found such a case.
                    return true;
                }
            }
        }
        // If we never find one of the above cases, return false!
        return false;
    }

    // Bitset A and bitset B should be the same size.
    // Else illegal argument exception.
    // Otherwise, get the all the indexes for which bitset[A] is true, and bitset[B] is false.
    public static ArrayList<Integer> getIndexesOfBitsetAthatBitsetBDoesNotHave(boolean[] bitsetA, boolean[] bitsetB) {
        if (bitsetA.length != bitsetB.length ) {
            throw new IllegalArgumentException("Error, the bitsets must be the same length!");
        }
        ArrayList<Integer> found = new ArrayList<>();

        for ( int i = 0; i != bitsetA.length; ++i ) {
            if (bitsetA[i]) {
                if (!bitsetB[i]) {
                    // If bitsetA is true, but bitsetB is not, then we have found such a case.
                    found.add(i);
                }
            }
        }
        return found;
    }

    public static String getStringFromBitSet(boolean[] bitset) {
        return getStringFromByteSet(getByteSetFromBitSet(bitset));
    }

    // Convert the given byteset to a bitset.
    // Size is used to handle trailing 0s, as other wise there is no way of
    // knowing if the original bitset consisted of those 0s, or if they are an artifact of having a byte.
    public static boolean[] getBitSetFromByteSet(byte[] byteset, int size) {
        ArrayList<Boolean> bitset = new ArrayList<>();

        // Copy all the values in from the byte set.
        for ( int i = 0; i != byteset.length; ++i ) {
            for ( int j = 0; j != 8; ++j ) {
                int shiftFactor = 7-j;
                byte mask = (byte) (0x01 << shiftFactor);
                bitset.add((mask & byteset[i]) == mask);
            }
        }

        boolean nonBoxedBitSet[] = new boolean[size];
        for ( int i = 0; i != size; ++i ) {
            nonBoxedBitSet[i] = bitset.get(i);
        }

        return nonBoxedBitSet;
    }

    public static byte[] getByteSetFromString(String input) {
        return input.getBytes(StandardCharsets.ISO_8859_1);
    }


}
