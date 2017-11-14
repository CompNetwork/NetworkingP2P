package main;

import main.hosts.Peer;

import java.io.FileNotFoundException;

public class Peer2 {
    public static void main (String [] args) {

        Peer p = null;
        try {
            p = new Peer("1002","127.0.0.1", 8082);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        p.start();

    }
}
