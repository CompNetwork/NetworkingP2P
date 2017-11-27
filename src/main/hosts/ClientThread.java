package main.hosts;

import main.file.ChunkifiedFileUtilities;
import main.file.FileChunk;
import main.logger.Logger;
import main.messsage.ByteArrayUtilities;
import main.messsage.Message;
import main.messsage.MessageTypeConstants;

import java.io.*;
import java.net.Socket;
import java.util.*;


public class ClientThread extends Thread {

    // Knowing if this is the localPeer (aka this machine)
    // or the remote peer is super confusing, so I renamed
    Peer localPeer = null;
    RemotePeer remotePeer = null;
    Socket socket = null;
    Message message;
    DataInputStream input = null;
    OutputStream output =  null;
    Logger logger;

    private boolean finHandshake;

    // Is the remote peer interested in us?
    // Are we choked by the remote peer?
    private boolean interested = false;
    private boolean choked = true;
    private Set<Integer> indexesThatPeerHasRequestedFromMe = new HashSet<>();

    public ClientThread(Socket socket, Peer localPeer, RemotePeer remotePeer) throws IOException {
        this.socket = socket;
        this.localPeer = localPeer;
        this.remotePeer = remotePeer;
        this.setupSocketIO();
        this.initHandshake();
        this.logger = new Logger();
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
            System.out.println("seeing if we need to close program");
            getLocalPeer().checkIfEveryoneIsDone();
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
                System.out.println("Received Handshake");
                break;
            case MessageTypeConstants.CHOKE :

                handleChoke();
                System.out.println("Received Choke");
                break;
            case MessageTypeConstants.UNCHOKE:

                handleUnchoke(message);
                System.out.println("Received Unchoke");
                break;
            case MessageTypeConstants.INTERESTED:

                handleInterested();
                System.out.println("Received Interested");
                break;
            case MessageTypeConstants.UNINTERESTED:

                handleNotInterested();
                System.out.println("Received Not Interested");
                break;
            case MessageTypeConstants.HAVE:

                handleHave(message);
                System.out.println("Received Have");
                break;
            case MessageTypeConstants.BITFIELD:

                handleBitField(message);
                System.out.println("Received Bitfield");
                break;
            case MessageTypeConstants.REQUEST:

                handleRequest(message);
                System.out.println("Received Request, index " + message.getIndexPayload());
                break;
            case MessageTypeConstants.PIECE:

                this.handlePiece(message);
                System.out.println("Received Piece");
                break;
            default:
                System.out.print("Received Unknown Message Type");
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

    /************** Getter/Setters *******************/

    private void setFinHandshake(boolean finHandshake) { this.finHandshake = finHandshake; }

    private boolean getFinHandshake() { return finHandshake; }

    public Peer getLocalPeer() { return localPeer; }

    /*************** Protocol Methods ****************/
    
    // Actual Message #0 outgoing
    // Mutates the parameter given
    public void sendChoke(Message m) throws IOException {
        this.remotePeer.setChoked(true);
        m.mutateIntoChoke();
        this.sendMessage(m);
        // Clear out the requested indexes, we will not be fulfilling any requests to a choked peer, and they know it!
        this.indexesThatPeerHasRequestedFromMe.clear();
    }



    // Actual Message #0 incoming
    private void handleChoke() {
        // We need to remove all the indexes that we added to the request list!
        logger.chokedByNeighborLog(remotePeer.getPeerID(),localPeer.getPeerID());
        localPeer.getGloballyRequestedSet().removeAll(remotePeer.getIndexesThatIHaveRequestedFromPeer());
        remotePeer.getIndexesThatIHaveRequestedFromPeer().clear();
        this.choked = true;
    }

    // Actual Message #1 outgoing
    // Mutates the parameter given
    public void sendUnchoke(Message m) throws IOException {
        this.remotePeer.setChoked(false);;
        m.mutateIntoUnChoke();
        this.sendMessage(m);
    }

    // Actual Message #1 incoming
    private void handleUnchoke(Message m) throws IOException {
        logger.unchokedByNeighborLog(localPeer.getPeerID(),remotePeer.getPeerID());
        this.choked = false;
        // if I need pieces sendRequest
        // TODO: This should happen repeatedly, not just one piece per interval!
        // We need to send more requests
        // Also, what if we make a request, and then get choked?
        // We need to remove that request from the quque.
        if(!this.localPeer.getChunky().hasAllChunks()) {
            sendRequest(m);
        }
    }

    // Actual Message #2 outgoing
    // Mutates the parameter given
    private void sendInterested(Message m) throws IOException {
        m.mutateIntoInterested();
        remotePeer.setInterested(true);
        sendMessage(m);
    }

    // Actual Message #2 incoming
    private void handleInterested() {
        logger.receivedInterestedMessageLog(localPeer.getPeerID(),remotePeer.getPeerID());
        this.interested = true;
    }

    // Actual Message #3 outgoing
    // Mutates the parameter given
    private void sendNotInterested(Message m) throws IOException {
        m.mutateIntoUnInterested();
        remotePeer.setInterested(false);
        sendMessage(m);

    }

    // Actual Message #3 incoming
    private void handleNotInterested() {
        logger.receivedNotInterestedMessageLog(localPeer.getPeerID(),remotePeer.getPeerID());
        this.interested = false;
    }

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
        logger.receivedHaveMessageLog(localPeer.getPeerID(),remotePeer.getPeerID(),chunkIndex);

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
        localPeer.checkIfEveryoneIsDone();
    }

    // Actual Message #5 outgoing
    // Mutates the parameter given
    private void sendBitField(int peerID, Message m) throws IOException {
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
        System.out.println("We found the peers file to be interesting, yay or nay?: " + this.isRemotePeerInteresting());
        if (this.remotePeer.getInterested()) {
            this.sendInterested(message);
        } else {
            this.sendNotInterested(message);
        }
        localPeer.checkIfEveryoneIsDone();
    }

    // Actual Message #6 outgoing
    // Mutates the parameter given
    // Selects a random index!
    private void sendRequest(Message m) throws IOException {

        if(!this.choked && !remotePeer.getChoked()) {
            // Careful with concurrency, we'll just lock on the global peer and allow only one request to be made at a time.
            synchronized (this.getLocalPeer()) {
                // Requests random chunk from set of chunks that I do not have
                Set<Integer> requestedIndexes = getLocalPeer().getGloballyRequestedSet();
                ArrayList<Integer> missingChunksIndices = ChunkifiedFileUtilities.getIndexesOfBitsetAthatBitsetBDoesNotHave(this.remotePeer.getBitset(), this.localPeer.getChunky().AvailableChunks());
                missingChunksIndices.removeAll(requestedIndexes); // Remove all the requested indexes also!
                // Pick and return a random index from the list of indexes we don't have - indexes requested!
                if ( missingChunksIndices.size() == 0 ) {
                    System.err.println("We have nothing to request, but are trying to for some reason!");
                    return;
                }
                int payload = missingChunksIndices.get(new Random().nextInt(missingChunksIndices.size()));
                // Add the payload back in to the global requested indexes so it isn't double requested!
                requestedIndexes.add(payload);
                remotePeer.getIndexesThatIHaveRequestedFromPeer().add(payload); // Add to what we locally have requested also!
                m.mutateIntoRequest(payload);
            }
            System.out.println("Sending request for index " + m.getIndexPayload());
            this.sendMessage(m);

        }

    }

    // Actual Message #6 incoming
    private void handleRequest(Message message) throws IOException {
        System.out.println("Received request for piece " + message.getIndexPayload() + " while : " + (!this.choked && !remotePeer.getChoked()) );
        System.out.println("!this.choked : " + !this.choked + ", !remotePeer.choked " + !remotePeer.getChoked());

        // always store the index we were asked to store, even if choked.
        int pieceIndex = message.getIndexPayload();
        indexesThatPeerHasRequestedFromMe.add(pieceIndex);
        fulfillRequest(message);
    }

    // If we have a index that was requested of us, and we, and peer are both unchoked, fulfill.
    // Else do nohing.
    void fulfillRequest(Message message) throws IOException {
        // Only fullfill a request if have unchoked remote peer unchoked, and have a request to fulfill!
        if (!remotePeer.getChoked() && indexesThatPeerHasRequestedFromMe.size() != 0) {
            // Extracts piece from sent message
            // Updates message.type and payload
            // FIXME: Can take the logic below and call sendPiece or leave it here
            // TODO: Validate we have the piece as well!

             // Get the piece, and then remove it
            Iterator<Integer> requestedPieceIndexIterator = indexesThatPeerHasRequestedFromMe.iterator();
            int pieceIndex = requestedPieceIndexIterator.next();
            requestedPieceIndexIterator.remove();
            // Send the piece over the wire
            message.mutateIntoPiece(this.localPeer.getChunky().getChunk(pieceIndex), pieceIndex);
            sendMessage(message);
        }
    }

    // Actual Message #7 outgoing
    // Mutates the parameter given
    // FIXME:This logic is taken care of in handleRequest(...).
    private void sendPiece(FileChunk chunk, int index, Message m) throws IOException {
        m.mutateIntoPiece(chunk,index);
        this.sendMessage(m);
    }

    // Actual Message #7 outgoing
    private void handlePiece(Message message) throws IOException {
        int pieceIndex = message.getIndexPayload();
        System.out.println("REceived piece index of " + pieceIndex);
        FileChunk pieceGot = message.getFileChunkPayload();
        this.getLocalPeer().getChunky().setChunk(pieceIndex,pieceGot);

        // Send out a new request
        if(!this.localPeer.getChunky().hasAllChunks()) {
            sendRequest(message);
        }
        localPeer.increaseNumberOfPiecesTracked();
        logger.downloadedPieceMessageLog(localPeer.getPeerID(),remotePeer.getPeerID(),pieceIndex,localPeer.getNumberOfPiecesObtained());
        // Inform the remote peer that I have received a piece of size N from peer ID
        // For calculating which peers to unchoke!
        this.getLocalPeer().informOfReceivedPiece(remotePeer.getPeerID(),pieceGot.size());

        // Send a have message to all peers.
        this.getLocalPeer().sendHaveMessageToAllRemotePeers(pieceIndex);

        // Send uninterested message to all uninteresting peers
        this.getLocalPeer().sendUninterestedToAllUninterestingPeers();

        // Now check if we were the last peer, and if so, then terminate ourself
        if(this.getLocalPeer().getChunky().hasAllChunks()){
            logger.completedDownloadLog(this.getLocalPeer().getPeerID());
        }

        this.getLocalPeer().checkIfEveryoneIsDone();


    }

    /*************** Protocol Helper Methods ****************/

    public void sendMessage(Message m) throws IOException {
        output.write(m.getFull());
        output.flush();
    }

    private void initHandshake() throws IOException {
        setFinHandshake(false);
        message = Message.createHandShakeMessageFromPeerId(this.localPeer.getPeerID());
        this.sendMessage(message);
    }

    private void completeHandShake(Message m) throws IOException {
        int peerID = m.getPeerIdPayload();
        this.remotePeer.setPeerId(peerID);
        logger.acceptConnectionLog(getLocalPeer().getPeerID(),remotePeer.getPeerID());
        sendBitField(peerID,m);
    }

    public boolean isRemotePeerInteresting() {
        // If remotePeer has a bit we do not, it is interesting.
        return ChunkifiedFileUtilities.doesAHaveChunksBDoesNot(remotePeer.getBitset(),localPeer.getChunky().AvailableChunks());
    }

    public boolean isPeerDone() {
        boolean[] remotePeerBitset = remotePeer.getBitset();
        for ( int i = 0; i != remotePeerBitset.length; ++i ) {
            if (!remotePeerBitset[i]) {
                return false;
            }
        }
        return true;
    }
}
