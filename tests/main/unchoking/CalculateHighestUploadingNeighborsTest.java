package main.unchoking;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class CalculateHighestUploadingNeighborsTest {
    CalculateHighestUploadingNeighbors calculateHighestUploadingNeighbors = null;
    ArrayList<String> kHighest;
    public void createCalculateHighestUploadingNeighbors() {
        calculateHighestUploadingNeighbors = new CalculateHighestUploadingNeighbors();
    }

    public void addPeer(String peer, int value) {
        calculateHighestUploadingNeighbors.receivedNewPackageFromNeighbor(peer,value);
    }

    public void startWithTwoPeers() {
        createCalculateHighestUploadingNeighbors();
        addPeer("foo",1);
        addPeer("bar",2);
    }

    public void startWithOnePeer() {
        createCalculateHighestUploadingNeighbors();
        addPeer("baz",1);
        calculateHighestUploadingNeighbors.receivedNewPackageFromNeighbor("baz", 1);
    }

    // Test the happy cases
    @Test
    public void IfTwoPeersAddedOneRequestedGetHighest() {
        startWithTwoPeers();
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(1);
        Assert.assertEquals(kHighest.size(), 1);
        Assert.assertEquals(kHighest.get(0), "bar");
    }

    @Test
    public void IfTwoPeersAddedBothTiesGetBoth() {
        startWithTwoPeers();
        addPeer("foo",1);
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(2);
        Assert.assertEquals(kHighest.size(), 2);
        Assert.assertTrue(kHighest.contains("bar"));
        Assert.assertTrue(kHighest.contains("foo"));

        // ensure we get one of the two from a tie.
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(1);
        Assert.assertTrue(kHighest.contains("foo") || kHighest.contains("bar"));
    }

    @Test
    public void IfTwoPeersAddedOneRequestedThenIncrementFooGetHighest() {
        startWithTwoPeers();
        addPeer("foo",2);
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(1);
        Assert.assertEquals(kHighest.size(), 1);
        Assert.assertEquals("foo", kHighest.get(0));
    }

    @Test
    public void IfThreePeersTwoTiedGetBoth() {
        startWithTwoPeers();
        addPeer("baz",2);
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(2);
        Assert.assertEquals(kHighest.size(), 2);
        Assert.assertTrue(kHighest.contains("bar"));
        Assert.assertTrue(kHighest.contains("baz"));

        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(3);
        Assert.assertEquals(kHighest.size(), 3);
        Assert.assertTrue(kHighest.contains("bar"));
        Assert.assertTrue(kHighest.contains("baz"));
        Assert.assertEquals("foo",kHighest.get(2));
    }

    @Test
    public void IfTwoPeersAddedTwoRequetedGetInOrder() {
        startWithTwoPeers();
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(2);
        Assert.assertEquals(kHighest.size(),2);
        Assert.assertEquals(kHighest.get(0),"bar");
        Assert.assertEquals(kHighest.get(1),"foo");
    }

    @Test
    public void IfTwoPeersAddedThreeRequetedGetInOrderTwo() {
        startWithTwoPeers();
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(3);
        Assert.assertEquals(kHighest.size(),2);
        Assert.assertEquals(kHighest.get(0),"bar");
        Assert.assertEquals(kHighest.get(1),"foo");
    }

    @Test
    public void WorksWithSizeOne() {
        startWithOnePeer();
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(1);
        Assert.assertEquals(kHighest.size(),1);
        Assert.assertEquals(kHighest.get(0),"baz");
    }
    //Check when there are ties.

    // Check edge cases
    @Test
    public void IfAskForZeroThenGetNone() {
        startWithTwoPeers();
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(0);
        Assert.assertEquals(kHighest.size(),0);
    }


    @Test
    public void ThrowIfNegativeK() {
        startWithTwoPeers();
        boolean found = false;
        try {
            kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(-1);
        } catch(IllegalArgumentException e) {
            found = true;
        }
        Assert.assertTrue(found);
    }

    @Test
    public void ThrowIfNegativeBytes() {
        boolean found = false;
        try {
            createCalculateHighestUploadingNeighbors();
            calculateHighestUploadingNeighbors.receivedNewPackageFromNeighbor("foo", -1);
        } catch(IllegalArgumentException e) {
            found = true;
        }
        Assert.assertTrue(found);
    }

}