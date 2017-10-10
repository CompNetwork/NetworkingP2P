package main.tests;


import main.ChunkifiedFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ChunkifiedFileTest {
// Eventually we need to move to a proper unit testing framework, but I want to quickly test this works!
    public static void WriteFile(String filePath, int chunkSize, int fileSize) {
        ChunkifiedFile file = ChunkifiedFile.CreateFile(filePath, chunkSize, fileSize);

        try {
            FileInputStream fio = new FileInputStream(filePath);
            byte[] bytes = new byte[21];
            assert (fio.read(bytes) == 21);
            for (int i = 0; i != bytes.length; ++i) {
                assert (bytes[i] == 42);
            }
            assert (fio.read() == -1); // Try to read one more byte, ensure that we get -1.

        } catch (IOException exception) {
            System.out.println(exception);
            if ((!false)) throw new AssertionError();
        }
        if ((file.getChunkCount() != 3)) throw new AssertionError();
    }
    public static void ReadFile(String filePath, int chunkSize, int fileSize) {
        ChunkifiedFile file = ChunkifiedFile.GetFromExisingFile(filePath,chunkSize,fileSize);
        if ((file.getChunkCount() != 3)) throw new AssertionError();
        // TODO actually read the data, currently not yet implemented.
    }

    public static void deleteFile(String filePath) {
        File f = new File(filePath);
        f.delete();
    }
    public static void main(String [] args) {
        int chunkSize = 10;
        int fileSize = 21;
        final String filePath = "./testFiles/foobar.txt";
        WriteFile(filePath,chunkSize,fileSize);
        ReadFile(filePath,chunkSize,fileSize);
        deleteFile(filePath);
        System.out.println("All tests passed");
    }
}
