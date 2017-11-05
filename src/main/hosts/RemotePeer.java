package main.hosts;

// Holds the info of a remote peer.
// All we care about is the ID, and the files it has.
public class RemotePeer {
    public static final String NO_PEER_ID_YET = "-1";

    // If equals NO_PEER_ID_YET, then we haven't received the handshake message and don't know the peerID!
    private String peerID;
    private boolean[] bitset;

    public RemotePeer(String peerID, int chunkCount) {
        this.peerID = peerID;
        this.bitset = new boolean[chunkCount];
    }

    public boolean getBit(int i) {
        return bitset[i];
    }

    public String getPeerID() {
        return peerID;
    }

    public void setBit(int i, boolean val) {
        bitset[i] = val;
    }

    public void setBitSet(boolean[] bitset) {
        this.bitset = bitset;
    }

    public void setPeerId(String peerID) {
        this.peerID = peerID;
    }
}
