package main.config.reader;

import main.config.pod.CommonConfigData;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;

import static org.junit.Assert.*;
public class CommonConfigReaderTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();


    File createTestFile(String contents) {
        try {
            File testFile = temporaryFolder.newFile();
            FileOutputStream writer = new FileOutputStream(testFile);
            writer.write(contents.getBytes());
            writer.close();
            return testFile;
        } catch(IOException ioexcept) {
            Assert.assertTrue(false);
        }
        return null;
    }

    @Test
    public void FullyPopulatedConfigFile() throws FileNotFoundException {
        File f = createTestFile("NumberOfPreferredNeighbors 2 \n" +
                "UnchokingInterval 5 \n" +
                "OptimisticUnchokingInterval 15 \n" +
                "FileName TheFile.dat \n" +
                "FileSize 10000323 \n " +
                "PieceSize 32768");
        CommonConfigReader reader = new CommonConfigReader(f);
        CommonConfigData data = reader.getData();
        Assert.assertEquals(data.getFileName(),"TheFile.dat");
        Assert.assertEquals(data.getFileSize(),10000323);
        Assert.assertEquals(data.getPieceSize(),32768);
        Assert.assertEquals(data.getOptimisticUnchokeInterval(),15);
        Assert.assertEquals(data.getUnchokeInterval(),5);
        Assert.assertEquals(data.getNumberPreferrredNeighbors(),2);
    }

    @Test
    public void DuplicatedFieldInConfigFile() {
        File f = createTestFile("NumberOfPreferredNeighbors 2 \n" +
                "UnchokingInterval 5 \n" +
                "UnchokingInterval 5 \n" +
                "OptimisticUnchokingInterval 15 \n" +
                "FileName TheFile.dat \n" +
                "FileSize 10000323 \n " +
                "PieceSize 32768");
        boolean found = false;
        try {
            CommonConfigReader reader = new CommonConfigReader(f);
        } catch(Exception e) {
            if ( e.toString().contains("twice") && e.toString().contains("UnchokingInterval") && e.toString().contains("line:2")) {
                found = true;
            }
            else {
                System.out.println(e);
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void MissingFieldInConfigFile() {
        File f = createTestFile("NumberOfPreferredNeighbors 2 \n" +
                "OptimisticUnchokingInterval 15 \n" +
                "FileName TheFile.dat \n" +
                "FileSize 10000323 \n " +
                "PieceSize 32768");
        boolean found = false;
        try {
            CommonConfigReader reader = new CommonConfigReader(f);
        } catch(Exception e) {
            if ( e.toString().contains("expected") && e.toString().contains("UnchokingInterval")) {
                found = true;
            }
            else {
                System.out.println(e);
            }
        }
        Assert.assertTrue(found);
    }


    @Test
    public void MissingValueForFieldInConfigFile() {
        File f = createTestFile("NumberOfPreferredNeighbors 2 \n" +
                "UnchokingInterval \n" +
                "OptimisticUnchokingInterval 15\n" +
                "FileName TheFile.dat \n" +
                "FileSize 10000323 \n " +
                "PieceSize 32768");
        boolean found = false;
        try {
            CommonConfigReader reader = new CommonConfigReader(f);
        } catch(Exception e) {
            if ( e.toString().contains("integer") && e.toString().contains("UnchokingInterval") && e.toString().contains("line:1")) {
                found = true;
            }
            else {
                System.out.println(e);
            }
        }
        Assert.assertTrue(found);
    }

}