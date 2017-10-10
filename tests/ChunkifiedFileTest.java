
import main.file.ChunkifiedFile;
import main.file.FileChunkImpl;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ChunkifiedFileTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    void AssertContentsEqual(File testFile, String contents) {
        try {
            assertEquals(contents,new String(Files.readAllBytes(Paths.get(testFile.getAbsolutePath()))));
        } catch(IOException ioexcept) {
            Assert.assertTrue(false);
        }
    }

    File createTestFile() {
        try {
            File testFile = temporaryFolder.newFile();
            testFile.delete();
            return testFile;
        } catch(IOException ioexcept) {
            Assert.assertTrue(false);
        }
        return null;
    }


    @org.junit.Test
    public void AssertChunkCountCorrect() {
        int chunkSize = 10;
        int fileSize = 21;
        File testFile = createTestFile();
        ChunkifiedFile file = ChunkifiedFile.CreateFile(testFile.getPath(), chunkSize, fileSize);
        assertEquals(3,file.getChunkCount());
        AssertContentsEqual(testFile,"*********************");
    }

    byte[] getByteArrayOfSizeAndValue(int size, byte value) {
        byte[] byteArray = new byte[size];
        Arrays.fill(byteArray,value);
        return byteArray;
    }

    @org.junit.Test
    public void AssertWeCanReadEachChunk() {
        int chunkSize = 10;
        int fileSize = 21;
        // REsult is byte 0-9, 10-19, and 20 form chunks.
        File testFile = createTestFile();
        ChunkifiedFile file = ChunkifiedFile.CreateFile(testFile.getPath(), chunkSize, fileSize);

        // Not ideal to do this three times, but ensure all the chunks have our magic value, and are the right size.
        assertEquals(10,file.getChunk(0).size());
        assertArrayEquals(getByteArrayOfSizeAndValue(10,ChunkifiedFile.MAGIC_CONSTANT),file.getChunk(0).asByteArray());
        assertEquals(10,file.getChunk(1).size());
        assertArrayEquals(getByteArrayOfSizeAndValue(10,ChunkifiedFile.MAGIC_CONSTANT),file.getChunk(1).asByteArray());
        assertEquals(1,file.getChunk(2).size());
        assertArrayEquals(getByteArrayOfSizeAndValue(1,ChunkifiedFile.MAGIC_CONSTANT),file.getChunk(2).asByteArray());

    }

    @org.junit.Test
    public void AssertWeCanWriteEachChunk() {
        int chunkSize = 10;
        int fileSize = 21;
        // REsult is byte 0-9, 10-19, and 20 form chunks.
        File testFile = createTestFile();
        ChunkifiedFile file = ChunkifiedFile.CreateFile(testFile.getPath(), chunkSize, fileSize);
        byte valChunk0 = '0';
        byte valChunk1 = '1';
        byte valChunk2 = '2';

        file.setChunk(0,new FileChunkImpl(getByteArrayOfSizeAndValue(10,valChunk0)));
        file.setChunk(1,new FileChunkImpl(getByteArrayOfSizeAndValue(10,valChunk1)));
        file.setChunk(2,new FileChunkImpl(getByteArrayOfSizeAndValue(1,valChunk2)));

        // Not ideal to do this three times, but ensure all the chunks have our magic value, and are the right size.
        assertEquals(10,file.getChunk(0).size());
        assertArrayEquals(getByteArrayOfSizeAndValue(10,valChunk0),file.getChunk(0).asByteArray());
        assertEquals(10,file.getChunk(1).size());
        assertArrayEquals(getByteArrayOfSizeAndValue(10,valChunk1),file.getChunk(1).asByteArray());
        assertEquals(1,file.getChunk(2).size());
        assertArrayEquals(getByteArrayOfSizeAndValue(1,valChunk2),file.getChunk(2).asByteArray());
        AssertContentsEqual(testFile,"000000000011111111112");
    }


}