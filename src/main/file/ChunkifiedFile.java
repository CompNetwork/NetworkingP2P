package main.file;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.RandomAccess;

// Handles reading/writing a file to disk, and mantaining which portions of it are known.
// We don't want to keep the whole file in memory, we want to split it up for the purposes of this.
public class ChunkifiedFile {
    public static final byte MAGIC_CONSTANT = 42;
    private File file;
    private boolean[] bitset;
    private int chunkSize;
    int fileSize;

    private ChunkifiedFile(File f, boolean[] bitSet, int fileSize, int chunkSize) {
        this.file = f;
        this.bitset = bitSet;
        this.chunkSize = chunkSize;
        this.fileSize = fileSize;
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
            return new ChunkifiedFile(file,bitset,fileSize, chunkSize);
    }

    // Create a file on the disk, initialized to all 0s.
    // Null if no permission or other error.
    public static ChunkifiedFile CreateFile(String filePath, int chunkSize, int fileSize) {
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(filePath);
            //System.out.println("Making a file at! " + file.getAbsoluteFile());
            file.createNewFile();
             fileOutputStream = new FileOutputStream(file);
            byte bytes[] = new byte[chunkSize];
            Arrays.fill(bytes,MAGIC_CONSTANT);
            for ( int i = 0; i < fileSize; i+=chunkSize) {
                int length_to_write = Math.min(chunkSize,fileSize-i);
                fileOutputStream.write(bytes,0, length_to_write);
            }
            return new ChunkifiedFile(file,new boolean[(getChunkCount(chunkSize,fileSize))],fileSize, chunkSize);
        } catch(IOException except) {
            System.err.println("Error: " + except);
            return null;
        } finally {
            if ( fileOutputStream != null ) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                   System.out.println("Unable to close file!");
                   e.printStackTrace();
                }
            }
        }

    }

    public boolean hasChunk(int i) {
        return bitset[i];
    }

    // Return the chunk if exists, else return a chunk fully populated with 42
    public FileChunk getChunk(int i) {
        if ( ! hasChunk(i)) {
            //System.out.println("Warning, attempting to access a chunk that doesn't exist!!");

            return new DefaultValueChunk(Math.min(fileSize-i*chunkSize, chunkSize),MAGIC_CONSTANT);
        }
        synchronized (this) {
            RandomAccessFile randomAccessFile = null;
            try {
                randomAccessFile = new RandomAccessFile(file, "r");
                randomAccessFile.seek(chunkSize * i);
                byte[] read_in = new byte[chunkSize];
                int actually_read_in = randomAccessFile.read(read_in);
                byte[] read_in_truncated = new byte[actually_read_in];
                System.arraycopy(read_in, 0, read_in_truncated, 0, actually_read_in);
                return new FileChunkImpl(read_in_truncated);
            } catch (IOException except) {
                System.err.println("Returning null as a error happened, was the file deleted while the program was running?");
                return null;
            } finally {
                if (randomAccessFile != null) {
                    try {
                        randomAccessFile.close();
                    } catch (IOException e) {
                        System.out.println("Unable to close file!");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Sets the chunk, and writes it to disk immediately.
    public void setChunk(int i, FileChunk data) {
        if (  i * chunkSize > fileSize || i < 0 || (i*chunkSize + data.size()) > fileSize ) {
            // Check that is within bounds
            throw new IndexOutOfBoundsException("Error, trying to write past the limit of this file!");
        }
        // Check the data size if we are setting the last chunk
        if ( i == this.getChunkCount()-1 ) {
            if ( data.size() != fileSize - i*chunkSize ) {
                throw new IllegalArgumentException("Chunk is too large, trying to set the last chunk!");
            }
        } else {
            // Check the data size otherwise
            if ( data.size() != chunkSize ) {
                throw new IllegalArgumentException("Chunk is too large");
            }
        }

        synchronized (this) {
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(chunkSize * i);
                randomAccessFile.write(data.asByteArray());
                bitset[i] = true; // Set this chunk to have been written!

            } catch (IOException except) {
                System.err.println("Returning null as a error happened, was the file deleted while the program was running?");
            }
        }
    }

    public int getChunkCount() {
        return bitset.length;
    }

    // Chunks that are 1 are currently on the disk, chunks that are 0 are not available.
    public boolean[] AvailableChunks() { return bitset; }
}
