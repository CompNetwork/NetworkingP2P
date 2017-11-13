package main.config.pod;

public class PeerConfigData {

    public final int peerId;
    public final String hostName;
    public final int listeningPort;
    // True if has the file
    public final boolean hasFileOrNot;

    public PeerConfigData(int peerId, String hostName, int listeningPort, boolean hasFileOrNot) {
        this.peerId = peerId;
        this.hasFileOrNot = hasFileOrNot;
        this.hostName = hostName;
        this.listeningPort = listeningPort;
    }

    // Doesn't really make sense to have this except for testing
    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) {
            return false;
        }
        if ( !PeerConfigData.class.isAssignableFrom(obj.getClass()))  {
            return false;
        }
        PeerConfigData data = (PeerConfigData)obj;
        return this.peerId == data.peerId && this.hostName.equals(data.hostName)
                && this.listeningPort == data.listeningPort
                && this.hasFileOrNot == data.hasFileOrNot;
    }


}
