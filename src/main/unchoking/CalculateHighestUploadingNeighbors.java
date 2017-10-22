package main.unchoking;

import java.util.*;

// Keeps track of peers and the amount of bytes we have downloaded from them.
public class CalculateHighestUploadingNeighbors {

    // A mapping of peer ids to upload rate in bytes.
    private Map<String,Integer> peerIdToUpload = Collections.synchronizedMap(new HashMap<>());

    // Call whenever a package is received from a neighbor.
    public void receivedNewPackageFromNeighbor(String peerId, int bytesReceived) {
        synchronized (peerIdToUpload) {
            Integer bytesUploaded = peerIdToUpload.get(peerId);
            if ( bytesUploaded == null ) {
                bytesUploaded = 0;
            }
            peerIdToUpload.put(peerId, bytesUploaded  + bytesReceived);
        }
    };

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
        TreeMap<Integer, ArrayList<String>> sortedListofUploads = new TreeMap<>();
        synchronized (peerIdToUpload) {
            printWarningifLessThanKPeers(k);
            for (Map.Entry<String, Integer> entry : peerIdToUpload.entrySet()) {
                ArrayList<String> peerIdsWithUpload = sortedListofUploads.get(entry.getValue());
                peerIdsWithUpload.add(entry.getKey());
            }
        }

        // We no longer touch the backing map, so it's free to be mucked with.
        ArrayList<String> topK = new ArrayList<>();
        for ( ArrayList<String> peerIds : sortedListofUploads.values() ) {
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
        if ( k < peerIdToUpload.size() ) {
            System.err.println("Not enough neighbors to get top K uploaders!");
        }
    }

}
