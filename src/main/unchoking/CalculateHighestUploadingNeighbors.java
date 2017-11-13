package main.unchoking;

import java.util.*;

// Keeps track of peers and the amount of bytes we have downloaded from them.
// When we receive a piece of the file from a neighbor, register with this class.
// Then when it comes time to calculate new preferred neighbors, ask this class for the best uploaders.
// Once done, call the reset method to clear the interval.
public class CalculateHighestUploadingNeighbors {

    // A mapping of peer ids to upload rate in bytes.
    private Map<String,Integer> peerIdToUpload = Collections.synchronizedMap(new HashMap<>());

    public CalculateHighestUploadingNeighbors(List<String> peerIds) {
        for (String peerID : peerIds ) {
            peerIdToUpload.put(peerID,0);
        }
        System.out.println("PeerIdList:"+peerIds.size());
    }
    // Call whenever a package is received from a neighbor.
    public void receivedNewPackageFromNeighbor(String peerId, int bytesReceived) {
        throwInvalidRangeIfNegative(bytesReceived);
        synchronized (peerIdToUpload) {
            Integer bytesUploaded = peerIdToUpload.get(peerId);
            if ( bytesUploaded == null ) {
                bytesUploaded = 0;
            }
            peerIdToUpload.put(peerId, bytesUploaded  + bytesReceived);
        }
    }

    private void throwInvalidRangeIfNegative(int value) {
        if ( value < 0 ) {
            throw new IllegalArgumentException("Value was negative!");
        }
    }

    ;

    private void randomizeArrayList(ArrayList randomize) {
        if ( randomize.size() <= 1 ) {
            // Nothing to randomize
            return;
        } else {
            Collections.shuffle(randomize);
        }
    }


    // Returns the K best uploaders, those who sent us the most data.
    // If we have less than K peers, will print a warning to stderr, and return however many peers we have.
    // On the returned array, index 0 will be highest, and index n will be the lowest.
    public ArrayList<String> getKBestUploaders(int k) {
        throwInvalidRangeIfNegative(k);
        TreeMap<Integer, ArrayList<String>> sortedListofUploads = new TreeMap<>();
        synchronized (peerIdToUpload) {
            printWarningifLessThanKPeers(k);
            for (Map.Entry<String, Integer> entry : peerIdToUpload.entrySet()) {
                ArrayList<String> peerIdsWithUpload = sortedListofUploads.get(entry.getValue());
                if ( peerIdsWithUpload == null ) {
                    peerIdsWithUpload = new ArrayList<String>();
                    sortedListofUploads.put(entry.getValue(),peerIdsWithUpload);
                }
                peerIdsWithUpload.add(entry.getKey());
            }
        }

        // We no longer touch the backing map, so it's free to be mucked with.
        // We want greatest first, but map is in ascending order, so call descending map.
        ArrayList<String> topK = new ArrayList<>();
        for ( ArrayList<String> peerIds : sortedListofUploads.descendingMap().values() ) {
            randomizeArrayList(peerIds);
            for ( String peerID : peerIds ) {
                if ( topK.size() == k ) {
                    return topK;
                } else {
                    topK.add(peerID);
                }
            }
        }
        return topK;
    }


    private void printWarningifLessThanKPeers(int k) {
        if ( k > peerIdToUpload.size() ) {
            System.err.println("Not enough neighbors to get top K uploaders!");
        }
    }

    public void clear(){
        // Reset all values to 0. // TODO bregg make more efficient!
        for ( String peerID : peerIdToUpload.keySet()) {
            peerIdToUpload.put(peerID,0);
        }
    }

}
