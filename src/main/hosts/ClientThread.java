package main.hosts;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import main.file.ChunkifiedFileUtilities;
import main.file.FileChunk;
import main.messsage.ByteArrayUtilities;
import main.messsage.Message;
import main.messsage.MessageTypeConstants;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;


public class ClientThread extends Thread {

    // Knowing if this is the localPeer (aka this machine)
    // or the remote peer is super confusing, so I renamed
    Peer localPeer = null;
    RemotePeer remotePeer = null;
    Socket socket = null;
    Message message;
    DataInputStream input = null;
    OutputStream output =  null;

    private boolean finHandshake;

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
                // Read the header of the message, we must find this out before the body to know how much more to read
                input.readFully(header);
                int bytesInBody = Message.BytesRemainingInMessageFromHeader(header);
                byte[] body = new byte[bytesInBody];
                // Now that we know how much is in the body, read the body
                input.readFully(body);
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
                this.handlePiece(message);
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
            this.input = new DataInputStream(this.socket.getInputStream());
            this.output = this.socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setFinHandshake(boolean finHandshake) { this.finHandshake = finHandshake; }

    private boolean getFinHandshake() { return finHandshake; }

    /*************** Protocol Methods ****************/
    
    private void initHandshake() throws IOException {
        setFinHandshake(false);
        message = Message.createHandShakeMessageFromPeerId(this.localPeer.getPeerID());
        this.sendMessage(message);
    }

    private void completeHandShake(Message m) throws IOException {
        String peerID = m.getPeerIdPayload();
        this.remotePeer.setPeerId(peerID);
        sendBitField(peerID,m);
    }

    // Actual Message #0 outgoing
    // Mutates the parameter given
    private void sendChoke(Message m) throws IOException {
        m.mutateIntoChoke();
        this.sendMessage(m);
    }

    // Actual Message #0 incoming
    private void handleChoke(Message message) { }

    // Actual Message #1 outgoing
    // Mutates the parameter given
    private void sendUnchoke(Message m) throws IOException {
        m.mutateIntoUnChoke();
        this.sendMessage(m);
    }

    // Actual Message #1 incoming
    private void handleUnchoke(Message message) { }

    // Actual Message #2 outgoing
    // Mutates the parameter given
    private void sendInterested(Message m) throws IOException {
        m.mutateIntoUnInterested();
        remotePeer.setInterested(true);
        sendMessage(m);
    }

    // Actual Message #2 incoming
    private void handleInterested(Message message) { }

    // Actual Message #3 outgoing
    // Mutates the parameter given
    private void sendNotInterested(Message m) throws IOException {
        m.mutateIntoUnInterested();
        remotePeer.setInterested(false);
        sendMessage(m);

    }

    public void sendMessage(Message m) throws IOException {
            output.write(m.getFull());
            output.flush();
    }

    // Actual Message #3 incoming
    private void handleNotInterested(Message message) { }

    // Actual Message #4 outgoing
    // Mutates the parameter given
    private void sendHave(Message m, int index) throws IOException {
        m.mutateIntoHave(index);
        sendMessage(m);
    }

    // Actual Message #4 incoming
    private void handleHave(Message message) throws IOException {
        int chunkIndex = message.getIndexPayload();
        System.out.println("Obtained index # " + chunkIndex);

        // Store the chunk before we forget.
        // We should only receive have messages for chunks the peer didn't have.
        // Doesn't matter much anyway, we can send out interested messages as many times as we want.
        // No need for an else here. We are either
        // If the new chunk is interesting, then
          // A) Already interested, in which case we will send another interested message.
          // B) Not interested, in which case we send out a message changing our status.
        // If not an interesting chunk, then
          // A) Already interested, not sending out a new interested message won't change this.
          // B) Not interested, so we leave the remote peer alone, they still have nothing we won't.
        this.remotePeer.setBit(chunkIndex,true);
        if ( !this.getLocalPeer().getChunky().hasChunk(chunkIndex) ) {
            // If we don't have this chunk, then we are interested!
            System.out.println("Sending interested message, as we don't have!");
            sendInterested(message);
            this.remotePeer.setInterested(true);
        }
    }

    // Actual Message #5 outgoing
    // Mutates the parameter given
    private void sendBitField(String peerID, Message m) throws IOException {
        if(!this.getFinHandshake()) {
            this.setFinHandshake(true);
            this.remotePeer.setPeerId(peerID);
            this.localPeer.getLogger().TCPConnectionLog(this.localPeer.getPeerID(), this.remotePeer.getPeerID());
            System.out.println("Sending bitfield!" + Arrays.toString(localPeer.getChunky().AvailableChunks()));
            m.mutateIntoBitField(localPeer.getChunky().AvailableChunks());

            // Send BITFIELD
            this.sendMessage(m);
        }
        else {
            throw new IllegalArgumentException("Received Multiple Handshakes");
        }
    }

    // Actual Message #5 incoming
    // Mutates the parameter given
    private void handleBitField(Message message) throws IOException {
        System.out.println("Received a bitfield! " + Arrays.toString(message.getBitFieldPayload(localPeer.getChunky().getChunkCount())));
        this.remotePeer.setBitSet(message.getBitFieldPayload(localPeer.getChunky().getChunkCount()));
        // Evaluate whether interested or not
        // If the remote peer has a chunk we do not, we are interested!
        // Otherwise, inform the peer we are not interested!
        this.remotePeer.setInterested(this.isRemotePeerInteresting());
        if (this.remotePeer.getInterested()) {
            this.sendInterested(message);
        } else {
            this.sendNotInterested(message);
        }

    }

    // Actual Message #6 outgoing
    // Mutates the parameter given
    private void sendRequest(Message m, int payloadNotUsed) throws IOException {


        /*TODO: if Unchoke */
        //if(!RemotePeer.isChoke){
            int [] missingChunksIndices = ChunkifiedFileUtilities.getIndexesOfBitsetAthatBitsetBDoesNotHave(this.remotePeer.getBitset(), this.localPeer.getChunky().AvailableChunks());
            int payload = new Random().nextInt(missingChunksIndices.length);
            m.mutateIntoRequest(payload);
            this.sendMessage(m);
        // }

    }

    // Actual Message #6 incoming
    private void handleRequest(Message message) throws IOException {

        /*TODO: if Unchoke*/
            // Extracts piece from sent message
            int pieceIndex = message.getIndexPayload();
            // Updates message.type and payload
            message.mutateIntoPiece(this.localPeer.getChunky().getChunk(pieceIndex), pieceIndex);
            sendMessage(message);
    }

    // Actual Message #7 outgoing
    // Mutates the parameter given
    private void sendPiece(FileChunk chunk, int index, Message m) throws IOException {
        m.mutateIntoPiece(chunk,index);
        this.sendMessage(m);
    }

    // Actual Message #7 outgoing
    private void handlePiece(Message message) throws IOException {
        int pieceIndex = message.getIndexPayload();
        FileChunk pieceGot = message.getFileChunkPayload();
        this.getLocalPeer().getChunky().setChunk(pieceIndex,pieceGot);

        // Inform the remote peer that I have received a piece of size N from peer ID
        this.getLocalPeer().informOfReceivedPiece(remotePeer.getPeerID(),pieceGot.size());

        // Send a have message to all peers.
        this.getLocalPeer().sendHaveMessageToAllRemotePeers(pieceIndex);

        // Send uninterested message to all uninteresting peers
        this.getLocalPeer().sendUninterestedToAllUninterestingPeers();

    }

    public Peer getLocalPeer() { return localPeer; }

    public boolean isRemotePeerInteresting() {
        // If remotePeer has a bit we do not, it is interesting.
        return ChunkifiedFileUtilities.doesAHaveChunksBDoesNot(remotePeer.getBitset(),localPeer.getChunky().AvailableChunks());
    }
}
