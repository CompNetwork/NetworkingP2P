package main.config.pod;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommonConfigDataTest {
    @Test
    public void testBuilder() {
        CommonConfigData.CommonConfigDataBuilder builder = new CommonConfigData.CommonConfigDataBuilder();
        int numNeighbors = 5;
        int numUnchoke = 6;
        int numOptiUnchoke = 7;

        int fileSize = 8;
        int pieceSize = 9;
        String fileName = "";

        builder.withFileName(fileName);
        builder.withFileSize(fileSize);
        builder.withPieceSize(pieceSize);

        builder.withNumberPreferredNeighbors(numNeighbors);
        builder.withOptimisticUnchokeInterval(numOptiUnchoke);
        builder.withUnchokeInterval(numUnchoke);

        CommonConfigData data = builder.build();

        Assert.assertEquals(data.getFileName(),fileName);
        Assert.assertEquals(data.getFileSize(),fileSize);
        Assert.assertEquals(data.getPieceSize(),pieceSize);

        Assert.assertEquals(data.getNumberPreferrredNeighbors(),numNeighbors);
        Assert.assertEquals(data.getUnchokeInterval(),numUnchoke);
        Assert.assertEquals(data.getOptimisticUnchokeInterval(), numOptiUnchoke);


    }

}
