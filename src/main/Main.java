package main;

import main.hosts.Peer;

public class Main {
    public static void main(String [] args) {
        // Handles args Starts peer
        Peer p = new Peer("1001","127.0.0.1", 8081);
        p.start();
    }
}
