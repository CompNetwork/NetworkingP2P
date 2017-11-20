package main.hosts;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import main.hosts.Peer;

import java.io.FileNotFoundException;

public class PeerTest {

    Peer peer = new Peer(1001);

    public PeerTest() throws FileNotFoundException {
    }

    @Test
    public void getPeerID_returnPeerID_whenCalled(){

        // arrange
        int expectedPeerID = 1001;

        // act
        int result = peer.getPeerID();

        // assert
        Assert.assertEquals(result,expectedPeerID);
    }
}

