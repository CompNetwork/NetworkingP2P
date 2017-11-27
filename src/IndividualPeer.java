
import main.config.reader.PeerConfigReader;
import main.hosts.Peer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class IndividualPeer {
    public static void main (String [] args) {
        System.out.println("Starting client!");

        if ( args.length < 1 ) {
            System.out.println("Error, need peerID!");
        }

        int peerID = Integer.parseInt(args[0]);

        System.out.println("Starting with peerID: " + peerID + " and filePathPrefix: " + "./");
        Peer p = null;
        try {
            p = new Peer(peerID,"./");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        p.start();

    }
}
