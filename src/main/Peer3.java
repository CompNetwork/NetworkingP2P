package main;

import main.hosts.Peer;

import java.io.FileNotFoundException;

public class Peer3 {
    public static void main (String [] args) {

        Peer p = null;
        try {
            p = new Peer(1003);
            p = new Peer(1003);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        p.start();


    }
}
