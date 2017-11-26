package main;

import main.hosts.Peer;

import java.io.FileNotFoundException;

public class Peer2 {
    public static void main (String [] args) {

        Peer p = null;
        String homeDirectory = System.getProperty("user.dir") + "/src/";
        try {
            p = new Peer(1003, homeDirectory);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        p.start();

    }
}
