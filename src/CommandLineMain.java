
import main.config.reader.PeerConfigReader;
import main.hosts.Peer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class CommandLineMain {
    public static void main (String [] args) {
        System.out.println("Starting client!");
        if ( args.length < 2 ) {
            System.out.println("Error, first argument is the filepath prefix, second is the peerID!");
        }
        String filePathPrefix = args[0];
        int peerID = Integer.parseInt(args[1]);
        System.out.println("Starting with peerID: " + peerID + " and filePathPrefix: " + filePathPrefix);
        Peer p = null;
        try {
            p = new Peer(peerID,filePathPrefix);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        p.start();

    }
}
