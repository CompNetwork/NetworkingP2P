package main;

import main.hosts.Peer;

public class Peer3 {
    public static void main (String [] args) {

        Peer p = new Peer("1003","127.0.0.1", 8083);
        p.start();


    }
}
