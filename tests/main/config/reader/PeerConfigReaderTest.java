package main.config.reader;

import main.config.pod.PeerConfigData;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class PeerConfigReaderTest {
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

    public void assertPeerConfigDataEqual(PeerConfigData expected, PeerConfigData actual) {
        Assert.assertEquals(expected,actual);
    }

    public void assertUnorderedPeerArraysEqual(ArrayList<PeerConfigData> expected, ArrayList<PeerConfigData> actual) {
        Assert.assertTrue(actual.containsAll(expected) && expected.containsAll(actual));
    }


    @Test
    public void ZeroPeersInFile() throws FileNotFoundException{
        File testFile = createTestFile("");
        PeerConfigReader reader = new PeerConfigReader(testFile);
        Assert.assertEquals(0,reader.getPeerConfigDatas().size());
    }

    @Test
    public void onePeerInFile() throws FileNotFoundException{
        File testFile = createTestFile("1001 lin114-00.cise.ufl.edu 6008 1");
        PeerConfigReader reader = new PeerConfigReader(testFile);
        Assert.assertEquals(1,reader.getPeerConfigDatas().size());
        PeerConfigData expectedData = new PeerConfigData(1001, "lin114-00.cise.ufl.edu", 6008, true);
        assertPeerConfigDataEqual(expectedData,reader.getPeerConfigDatas().get(0));
    }

    @Test
    public void TwoPeersInFile() throws FileNotFoundException{
        File testFile = createTestFile("1001 lin114-00.cise.ufl.edu 6008 1 \n 1002 lin115-00.cise.ufl.edu 6009 0");
        PeerConfigReader reader = new PeerConfigReader(testFile);
        Assert.assertEquals(2,reader.getPeerConfigDatas().size());
        ArrayList<PeerConfigData> expectedPeerConfigs = new ArrayList<>();
        expectedPeerConfigs.add(new PeerConfigData(1001, "lin114-00.cise.ufl.edu", 6008, true));
        expectedPeerConfigs.add(new PeerConfigData(1002, "lin115-00.cise.ufl.edu", 6009, false));
        assertUnorderedPeerArraysEqual(expectedPeerConfigs,reader.getPeerConfigDatas());
    }


    @Test
    public void MissingAField() throws FileNotFoundException{
        File testFile = createTestFile("lin114-00.cise.ufl.edu 6008 1");
        boolean found = false;
        try {
            PeerConfigReader reader = new PeerConfigReader(testFile);
        } catch(IllegalArgumentException e) {
            if ( e.toString().contains("Field:PeerId") && e.toString().contains("line:0")) {
                found = true;
            } else {
                System.out.println(e.toString());
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void ExtraField() throws FileNotFoundException{
        File testFile = createTestFile("1001 lin114-00.cise.ufl.edu 6008 1 100");
        boolean found = false;
        try {
            PeerConfigReader reader = new PeerConfigReader(testFile);
        } catch(IllegalArgumentException e) {
            if ( e.toString().contains("Field:N/A") && e.toString().contains("line:0") && e.toString().contains("extra data")) {
                found = true;
            } else {
                System.out.println(e.toString());
            }
        }
        Assert.assertTrue(found);
    }

}