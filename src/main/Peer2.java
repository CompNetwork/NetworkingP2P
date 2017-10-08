package main;

import main.hosts.Peer;

public class Peer2 {
    public static void main (String [] args) {

        Peer p = new Peer("1002","127.0.0.1", 8082);
        p.start();

    }
}
