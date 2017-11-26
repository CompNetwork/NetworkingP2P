
import main.config.reader.PeerConfigReader;
import main.hosts.Peer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class peerProcess {
    public static void main (String [] args) {
        System.out.println("Starting client!");

        if ( args.length < 1 ) {
            System.out.println("Error, first argument is the filepath prefix, second is the peerID!");
        }

        int peerID = Integer.parseInt(args[0]);

        String configPathPrefix = System.getProperty("user.dir") + "/src/";
        System.out.println("Starting with peerID: " + peerID + " and filePathPrefix: " + configPathPrefix);
        Peer p = null;
        try {
            p = new Peer(peerID,configPathPrefix);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        p.start();

    }
}
