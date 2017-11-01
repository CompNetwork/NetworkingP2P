package main.hosts;

import main.file.ChunkifiedFileUtilities;
import main.messsage.Message;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class ClientThread extends Thread {

    boolean finHandshake;
    Peer peer = null;
    Socket socket = null;
    Message message;
    BufferedReader cmdInput = null;
    BufferedReader userInput = null;
    PrintWriter userOutput =  null;
    String destination = null;

    public ClientThread(Socket socket, Peer peer) {
        this.socket = socket;
        this.peer = peer;
        this.setupSocketIO();
        this.initHandshake();
    }

    public void run() {
        try {
            while(true) {

                if(!cmdInput.ready() && !userInput.ready()) {
                    Thread.sleep(500);
                }

                if(userInput.ready()) {
                    String rawData = userInput.readLine();
                    message.update(rawData);
                    handle(message);
                    userOutput.println(rawData);
                }

                if(cmdInput.ready()) {
                    String rawData = this.cmdInput.readLine();
                    message.update(rawData);
                    handle(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Closing socket" + this.socket + " to Peer" + peer.getPeerID());
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handle(Message message) {

        int type = message.getmType();

        switch ( type ) {
            case -1 :
                completeHandShake(message);
                System.out.println("Handshake");
                break;
            case 0 :
                System.out.println("Choke");
                break;
            case 1 :
                System.out.println("Unchoke");
                break;
            case 2 :
                System.out.println("Interested");
                break;
            case 3 :
                System.out.println("Not Interested");
                break;
            case 4 :
                System.out.println("Have");
                break;
            case 5 :
                handleBitField(message);
                System.out.println("Bitfield");
                break;
            case 6 :
                System.out.println("Request");
                break;
            case 7 :
                System.out.println("Piece");
                break;
            default:
                System.out.print("Unknown Message Type");
        }
    }

    public void close(){
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupSocketIO(){
        try {
            this.userInput = new BufferedReader(new InputStreamReader(System.in));
            this.cmdInput = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.userOutput = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initHandshake() {
        this.finHandshake = false;
        message = new Message(this.peer.getPeerID());
        userOutput.println(message.getFull());
    }

    private void completeHandShake(Message message) {

        String peerID = message.getM3();

        if(!finHandshake) {
            finHandshake = true;
            this.destination = peerID;
            this.peer.getLogger().TCPConnectionLog(this.peer.getPeerID(), this.destination);

            String payload = ChunkifiedFileUtilities.getStringFromBitSet(peer.getChunky().AvailableChunks());
            int messageLength = payload.getBytes(StandardCharsets.ISO_8859_1).length;
            message.update(messageLength, message.BITFIELD, payload);

            // Send BITFIELD
            userOutput.println(message.getFull());
        }
        else {
            throw new IllegalArgumentException("Received Multiple Handshakes");
        }
    }

    // Actual Message #0
    private void handleChoke(Message message) { }

    // Actual Message #1
    private void handleUnchoke(Message message) { }

    // Actual Message #2
    private void interested(Message message) { }

    // Actual Message #3
    private void notInterested(Message message) { }

    // Actual Message #4
    private void have(Message message) { }

    // Actual Message #5
    private void handleBitField(Message message) {

        System.out.println(message.getM3());
        // Evaluate whether interested or not
    }

    // Actual Message #6
    private void handleRequest(Message message) {
        String payload = message.getM3();
        byte[] bSet = ChunkifiedFileUtilities.getByteSetFromString(payload);


    }

    // Actual Message #7
    private void handlePiece(Message message) { }

    public Peer getPeer() { return peer; }

}
