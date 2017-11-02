package main.hosts;

import main.file.ChunkifiedFileUtilities;
import main.messsage.Message;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class ClientThread extends Thread {

    // Knowing if this is the localPeer (aka this machine)
    // or the remote peer is super confusing, so I renamed
    Peer localPeer = null;
    RemotePeer remotePeer = null;
    Socket socket = null;
    Message message;
    BufferedReader cmdInput = null;
    BufferedReader userInput = null;
    PrintWriter userOutput =  null;

    private boolean finHandshake;
    private void setFinHandshake(boolean finHandshake) {
        this.finHandshake = finHandshake;
    }
    private boolean getFinHandshake() {
        return finHandshake;
    }

    public ClientThread(Socket socket, Peer localPeer, RemotePeer remotePeer) {
        this.socket = socket;
        this.localPeer = localPeer;
        this.remotePeer = remotePeer;
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
            System.out.println("Closing socket" + this.socket + " to Peer" + localPeer.getPeerID());
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
        setFinHandshake(false);
        message = new Message(this.localPeer.getPeerID());
        userOutput.println(message.getFull());
    }

    private void completeHandShake(Message message) {

        String peerID = message.getM3();
        this.remotePeer.setPeerId(peerID);

        if(!getFinHandshake()) {
            setFinHandshake(true);
            this.remotePeer.setPeerId(peerID);
            this.localPeer.getLogger().TCPConnectionLog(this.localPeer.getPeerID(), remotePeer.getPeerID());

            String payload = ChunkifiedFileUtilities.getStringFromBitSet(localPeer.getChunky().AvailableChunks());
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
    private void have(Message message) {
        String indexHave = message.getM3();
        // TODO: Mbregg Use a pattern to fix this, this is ugly!
        // Also test that this works!
        int chunkIndex = Integer.parseInt(indexHave);

        // Store the chunk before we forget.
        // We should only receive have messages for chunks the peer didn't have.
        // Doesn't matter much anyway, we can send out interested messages as many times as we want.
        this.remotePeer.setBit(chunkIndex,true);
        if ( !this.getLocalPeer().getChunky().hasChunk(chunkIndex) ) {
            // If we don't have this chunk, then we are interested!
            sendInterestedMessageToRemotePeer();
        }
    }

    private void sendInterestedMessageToRemotePeer() {
        // TODO: Mbregg
    }

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

    public Peer getLocalPeer() { return localPeer; }

}
