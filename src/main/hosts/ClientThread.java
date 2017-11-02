package main.hosts;

import main.file.ChunkifiedFileUtilities;
import main.messsage.ByteArrayUtilities;
import main.messsage.Message;
import main.messsage.MessageTypeConstants;

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
    DataInputStream userInput = null;
    OutputStream userOutput =  null;

    private boolean finHandshake;
    private void setFinHandshake(boolean finHandshake) {
        this.finHandshake = finHandshake;
    }
    private boolean getFinHandshake() {
        return finHandshake;
    }

    public ClientThread(Socket socket, Peer localPeer, RemotePeer remotePeer) throws IOException {
        this.socket = socket;
        this.localPeer = localPeer;
        this.remotePeer = remotePeer;
        this.setupSocketIO();
        this.initHandshake();
    }


    public void run() {
        try {
            while (true) {
                byte[] header = new byte[5];
                userInput.readFully(header);
                byte[] body = null;
                // This is a handshake message!
                // Message length can be anything.
                // But the 5th byte must be the I in P@PFILESHARINGPROJ, so
                // WE can consider a handshake message to have a type value of 'I', or 73
                if (header[4] == 'I') {
                    // This is a handshake message
                    int remainingLength = 32 - 5;
                    body = new byte[remainingLength];
                    userInput.read(body);
                } else {
                    // This is a "actual" message!
                    int remainingLength = ByteArrayUtilities.recombine4BytesIntoInts(header[0], header[1], header[2], header[3]);
                    body = new byte[remainingLength];
                    userInput.read(body);
                }
                byte[] fullMessage = ByteArrayUtilities.combineTwoByteArrays(header, body);
                message.update(fullMessage);
                handle(message);
            }
        } catch (EOFException e) {
            System.out.println("EOF hit, socket closed. Hopefully this is because the program is terminating.");
        } catch (IOException e) {
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

    public void handle(Message message) throws IOException {

        byte type = message.getmType();

        switch ( type ) {
            case MessageTypeConstants.HANDSHAKE :
                completeHandShake(message);
                System.out.println("Handshake");
                break;
            case MessageTypeConstants.CHOKE :
                System.out.println("Choke");
                break;
            case MessageTypeConstants.UNCHOKE:
                System.out.println("Unchoke");
                break;
            case MessageTypeConstants.INTERESTED:
                System.out.println("Interested");
                break;
            case MessageTypeConstants.UNINTERESTED:
                System.out.println("Not Interested");
                break;
            case MessageTypeConstants.HAVE:
                System.out.println("Have");
                handleHave(message);
                break;
            case MessageTypeConstants.BITFIELD:
                handleBitField(message);
                System.out.println("Bitfield");
                break;
            case MessageTypeConstants.REQUEST:
                System.out.println("Request");
                break;
            case MessageTypeConstants.PIECE:
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

    private void setupSocketIO() {
        try {
            this.userInput = new DataInputStream(this.socket.getInputStream());
            this.userOutput = this.socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*************** Protocol Methods ****************/
    
    private void initHandshake() throws IOException {
        setFinHandshake(false);
        message = Message.createHandShakeMessageFromPeerId(this.localPeer.getPeerID());
        userOutput.write(message.getFull());
        userOutput.flush();
    }

    private void completeHandShake(Message message) throws IOException {
        String peerID = message.getPeerId();
        this.remotePeer.setPeerId(peerID);
        sendBitField(peerID);
    }

    // Actual Message #0 outgoing
    private void sendChoke(){}

    // Actual Message #0 incoming
    private void handleChoke(Message message) { }

    // Actual Message #1 outgoing
    private void sendUnchoke(){}

    // Actual Message #1 incoming
    private void handleUnchoke(Message message) { }

    // Actual Message #2 outgoing
    private void sendInterested(){}

    // Actual Message #2 incoming
    private void handleInterested(Message message) { }

    // Actual Message #3 outgoing
    private void sendNotInterested(){}

    // Actual Message #3 incoming
    private void handleNotInterested(Message message) { }

    // Actual Message #4 outgoing
    private void sendHave(){}

    // Actual Message #4 incoming
    private void handleHave(Message message) throws IOException {
        // TODO: Mbregg Use a pattern to fix this, this is ugly!
        int chunkIndex = ByteArrayUtilities.recombine4ByteArrayIntoInt(message.getM3());
        System.out.println("Obtained index # " + chunkIndex);

        // Store the chunk before we forget.
        // We should only receive have messages for chunks the peer didn't have.
        // Doesn't matter much anyway, we can send out interested messages as many times as we want.
        this.remotePeer.setBit(chunkIndex,true);
        if ( !this.getLocalPeer().getChunky().hasChunk(chunkIndex) ) {
            // If we don't have this chunk, then we are interested!
            System.out.println("Sending interested message, as we don't have!");
            sendInterestedMessageToRemotePeer();
        }
    }

    // Actual Message #5 outgoing
    private void sendBitField(String peerID) throws IOException {
        if(!this.getFinHandshake()) {
            this.setFinHandshake(true);
            this.remotePeer.setPeerId(peerID);
            this.localPeer.getLogger().TCPConnectionLog(this.localPeer.getPeerID(), this.remotePeer.getPeerID());

            byte[] payload = ChunkifiedFileUtilities.getByteSetFromBitSet(localPeer.getChunky().AvailableChunks());
            message.update(MessageTypeConstants.BITFIELD, payload);

            // Send BITFIELD
            userOutput.write(message.getFull());
            userOutput.flush();
        }
        else {
            throw new IllegalArgumentException("Received Multiple Handshakes");
        }
    }

    private void sendInterestedMessageToRemotePeer() throws IOException {
        message.update(MessageTypeConstants.INTERESTED,null);
        userOutput.write(message.getFull());
        userOutput.flush();
    }

    // Actual Message #5 incoming
    private void handleBitField(Message message) {
        System.out.println(message.getM3());
        // Evaluate whether interested or not
    }

    // Actual Message #6 outgoing
    private void sendRequest() {
//        String payload = message.getM3();
//        byte[] bSet = ChunkifiedFileUtilities.getByteSetFromString(payload);
//        this.peer.getChunky().
//        String payload = "";
//        message.update(1,message.REQUEST, payload);
//        userOutput.println(message.getFull());
    }

    // Actual Message #6 incoming
    private void handleRequest(Message message) {

        // randomly pick an index with an empty bit value

//        if(this.peer.getChunky().hasChunk(0));

    }

    public Peer getLocalPeer() { return localPeer; }

    // Actual Message #7 outgoing
    private void sendPiece() {}
    // Actual Message #7 incoming
    private void handlePiece(Message message) { }
}
