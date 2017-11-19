package main.unchoking;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalculateHighestUploadingNeighborsTest {
    CalculateHighestUploadingNeighbors calculateHighestUploadingNeighbors = null;
    ArrayList<Integer> kHighest;
    public void createCalculateHighestUploadingNeighbors() {
        List<Integer> peers = Arrays.asList(new Integer[]{});
        calculateHighestUploadingNeighbors = new CalculateHighestUploadingNeighbors(peers);
    }

    public void addPeer(Integer peer, int value) {
        calculateHighestUploadingNeighbors.receivedNewPackageFromNeighbor(peer,value);
    }

    public void startWithTwoPeers() {
        createCalculateHighestUploadingNeighbors();
        addPeer(1,1); // foo
        addPeer(2,2); // bar
    }

    public void startWithOnePeer() {
        createCalculateHighestUploadingNeighbors();
        addPeer(3,1); // baz
        calculateHighestUploadingNeighbors.receivedNewPackageFromNeighbor(3, 1);
    }

    @Test
    public void clearDoesnotRemoveKeys() {
        startWithTwoPeers();
        calculateHighestUploadingNeighbors.clear();
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(2);
        Assert.assertEquals(kHighest.size(), 2);
        Assert.assertTrue(kHighest.contains(2));
        Assert.assertTrue(kHighest.contains(1));
    }

    // Test the happy cases
    @Test
    public void IfTwoPeersAddedOneRequestedGetHighest() {
        startWithTwoPeers();
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(1);
        Assert.assertEquals(kHighest.size(), 1);
        Assert.assertEquals(kHighest.get(0), new Integer(2));
    }

    @Test
    public void IfTwoPeersAddedBothTiesGetBoth() {
        startWithTwoPeers();
        addPeer(1,1);
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(2);
        Assert.assertEquals(kHighest.size(), 2);
        Assert.assertTrue(kHighest.contains(2));
        Assert.assertTrue(kHighest.contains(1));

        // ensure we get one of the two from a tie.
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(1);
        Assert.assertTrue(kHighest.contains(1) || kHighest.contains(2));
    }

    @Test
    public void IfTwoPeersAddedOneRequestedThenIncrementFooGetHighest() {
        startWithTwoPeers();
        addPeer(1,2);
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(1);
        Assert.assertEquals(kHighest.size(), 1);
        Assert.assertEquals(new Integer(1), kHighest.get(0));
    }

    @Test
    public void IfThreePeersTwoTiedGetBoth() {
        startWithTwoPeers();
        addPeer(3,2);
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(2);
        Assert.assertEquals(kHighest.size(), 2);
        Assert.assertTrue(kHighest.contains(2));
        Assert.assertTrue(kHighest.contains(3));

        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(3);
        Assert.assertEquals(kHighest.size(), 3);
        Assert.assertTrue(kHighest.contains(2));
        Assert.assertTrue(kHighest.contains(3));
        Assert.assertEquals(new Integer(1),kHighest.get(2));
    }

    @Test
    public void IfTwoPeersAddedTwoRequetedGetInOrder() {
        startWithTwoPeers();
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(2);
        Assert.assertEquals(kHighest.size(),2);
        Assert.assertEquals(kHighest.get(0),new Integer(2));
        Assert.assertEquals(kHighest.get(1),new Integer(1));
    }

    @Test
    public void IfTwoPeersAddedThreeRequetedGetInOrderTwo() {
        startWithTwoPeers();
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(3);
        Assert.assertEquals(kHighest.size(),2);
        Assert.assertEquals(kHighest.get(0),new Integer(2));
        Assert.assertEquals(kHighest.get(1),new Integer(1));
    }

    @Test
    public void WorksWithSizeOne() {
        startWithOnePeer();
        kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(1);
        Assert.assertEquals(kHighest.size(),1);
        Assert.assertEquals(kHighest.get(0),new Integer(3));
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
            calculateHighestUploadingNeighbors.receivedNewPackageFromNeighbor(1, -1);
        } catch(IllegalArgumentException e) {
            found = true;
        }
        Assert.assertTrue(found);
    }

}