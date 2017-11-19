package main;

import main.hosts.Peer;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String [] args) {
        // Handles args Starts peer
        Peer p = null;
        try {
            p = new Peer(1001,"127.0.0.1", 8081);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        p.start();
    }
}
