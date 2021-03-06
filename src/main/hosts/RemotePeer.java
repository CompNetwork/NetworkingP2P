package main.hosts;

import java.util.HashSet;
import java.util.Set;

// Holds the info of a remote peer.
// All we care about is the ID, and the files it has.
// All fields in here are in the context of the remote peer.
// AKA, choked is are we choking the remote peer?
// AKA, interested is are we interested in the remote peer?
// Bitset is the remote peers bitset
public class RemotePeer {
    public static final int NO_PEER_ID_YET = -1;

    // If equals NO_PEER_ID_YET, then we haven't received the handshake message and don't know the peerID!
    private int peerID;
    private boolean[] bitset;
    private boolean interested;
    private boolean choked;


    private Set<Integer> indexesThatIHaveRequestedFromPeer = new HashSet<>();

    public RemotePeer(int peerID, int chunkCount) {
        this.peerID = peerID;
        this.bitset = new boolean[chunkCount];
        this.interested = false;
        this.choked = false;
    }

    public boolean getBit(int i) {
        return bitset[i];
    }

    public int getPeerID() {
        return peerID;
    }

    public void setBit(int i, boolean val) {
        bitset[i] = val;
    }

    public void setBitSet(boolean[] bitset) {
        this.bitset = bitset;
    }

    public void setPeerId(int peerID) {
        this.peerID = peerID;
    }

    public boolean[] getBitset() {
        return bitset;
    }

    public void setInterested(boolean set){
        this.interested = set;
    }

    public boolean getInterested(){
        return interested;
    }

    public void setChoked(boolean set){
        this.choked = set;
    }

    public boolean getChoked(){
        return choked;
    }

    public Set<Integer> getIndexesThatIHaveRequestedFromPeer() {
        return indexesThatIHaveRequestedFromPeer;
    }
}
