package main.hosts;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import main.hosts.Peer;

public class PeerTest {

    Peer peer = new Peer("1001","127.0.0.1", 8081);

    @Test
    public void getPeerID_returnPeerID_whenCalled(){

        // arrange
        String expectedPeerID = "1001";

        // act
        String result = peer.getPeerID();

        // assert
        Assert.assertEquals(result,expectedPeerID);
    }
}
