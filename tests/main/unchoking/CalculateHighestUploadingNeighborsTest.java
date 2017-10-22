package main.unchoking;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class CalculateHighestUploadingNeighborsTest {

    @Test
    public void TwoPeersAddedOnce() {
        CalculateHighestUploadingNeighbors calculateHighestUploadingNeighbors = new CalculateHighestUploadingNeighbors();

        calculateHighestUploadingNeighbors.receivedNewPackageFromNeighbor("foo", 1);
        calculateHighestUploadingNeighbors.receivedNewPackageFromNeighbor("bar", 2);

        ArrayList<String> kHighest = calculateHighestUploadingNeighbors.getKBestUploaders(1);
        Assert.assertEquals(kHighest.size(),1);
        Assert.assertEquals(kHighest.get(0),"bar");
    }

}