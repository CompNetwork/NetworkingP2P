package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

// Handles reading/writing a file to disk, and mantaining which portions of it are known.
// We don't want to keep the whole file in memory, we want to split it up for the purposes of this.
public class ChunkifiedFile {
    public static final byte MAGIC_CONSTANT = 42;
    private File file;
    private boolean[] bitset;
    private int chunkSize;

    public ChunkifiedFile(File f, boolean[] bitSet, int chunkSize) {
        this.file = f;
        this.bitset = bitSet;
        this.chunkSize = chunkSize;
    }

    private static int getChunkCount(int chunkSize, int fileSize) {
        int chunkCount = fileSize/chunkSize;
        if ( fileSize%chunkSize != 0 ) { ++ chunkCount; }
        return chunkCount;
    }

    public static ChunkifiedFile GetFromExisingFile(String filePath, int chunkSize, int fileSize) {
            File file = new File(filePath);
            if ( file.length() != fileSize ) {
                System.err.println("Mismatch between expected length, and actual length!");
                return null;
            }
            boolean[] bitset = new boolean[getChunkCount(chunkSize,fileSize)];
            Arrays.fill(bitset,true);
            return new ChunkifiedFile(file,bitset,chunkSize);
    }

    // Create a file on the disk, initialized to all 0s.
    // Null if no permission or other error.
    public static ChunkifiedFile CreateFile(String filePath, int chunkSize, int fileSize) {
        try {
            File file = new File(filePath);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte bytes[] = new byte[chunkSize];
            Arrays.fill(bytes,MAGIC_CONSTANT);
            for ( int i = 0; i < fileSize; i+=chunkSize) {
                fileOutputStream.write(bytes,i,Math.min(chunkSize,i-fileSize));
            }
            return new ChunkifiedFile(file,new boolean[(getChunkCount(chunkSize,fileSize))],chunkSize);
        } catch(IOException except) {
            System.err.println("Error: " + except);
            return null;
        }
    }
    public boolean hasChunk(int i) {
        return false;
    }
    // Return the chunk if exists, else return a chunk fully populated with 42
    public FileChunk getChunk() {
        return null;
    }
    // Sets the chunk, and writes it to disk immediately.
    public void setChunk(int i, FileChunk data) {

    }
    public int getChunkCount() {
        return 0;
    }
    // Chunks that are 1 are currently on the disk, chunks that are 0 are not available.
    public BitSet AvailableChunks() {
        return null;
    }
}
