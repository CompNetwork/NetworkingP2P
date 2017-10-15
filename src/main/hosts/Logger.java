package main.hosts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;

public class Logger{



    /*
    Whenever a peer makes a TCP connection to other peer, it generates the following log:
[Time]: Peer [peer_ID 1] makes a connection to Peer [peer_ID 2].
[peer_ID 1] is the ID of peer who generates the log, [peer_ID 2] is the peer connected
from [peer_ID 1]. The [Time] field represents the current time, which contains the date,
hour, minute, and second. The format of [Time] is up to you.
     */


    public void TCPConnectionLog(int peerID, int otherPeerID) {
        //gets time
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " makes a connection to Peer " + otherPeerID + ".";

    }

    /*
    Whenever a peer is connected from another peer, it generates the following log:
[Time]: Peer [peer_ID 1] is connected from Peer [peer_ID 2].
[peer_ID 1] is the ID of peer who generates the log, [peer_ID 2] is the peer who has
made TCP connection to [peer_ID 1].

     */
    public void acceptConnectionLog(int peerID, int otherPeerID){
        //called when it has been connected to, not the when it's trying to make a connection
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " is connected from Peer " + otherPeerID + ".";
    }

    /*
    Whenever a peer changes its preferred neighbors, it generates the following log:
[Time]: Peer [peer_ID] has the preferred neighbors [preferred neighbor ID list].
[preferred neighbor list] is the list of peer IDs separated by comma ‘,’.
     */

    public void changePreferredNeighborsLog(int peerID,int neighborList[] ){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " has the preferred neighbors ";

        for(int i = 0; i > neighborList.length; i++){
            log = log + neighborList[i] + ", ";
        }

        log = log + ".";
    }

    /*
    Whenever a peer changes its optimistically unchoked neighbor, it generates the
following log:
[Time]: Peer [peer_ID] has the optimistically unchoked neighbor [optimistically
unchoked neighbor ID].
[optimistically unchoked neighbor ID] is the peer ID of the optimistically unchoked
neighbor.
     */

    public void changeOfOptimisticallyUnchockedNeighborLog(int peerID, int otherID){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " has the optimistically unchocked neighbor " + otherID + ".";
    }

    /*
    Whenever a peer is unchoked by a neighbor (which means when a peer receives an
unchoking message from a neighbor), it generates the following log:
[Time]: Peer [peer_ID 1] is unchoked by [peer_ID 2].
[peer_ID 1] represents the peer who is unchoked and [peer_ID 2] represents the peer
who unchokes [peer_ID 1].
     */

    public void unchokedByNeighborLog(int peerID, int otherID){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " is unchoked by " + otherID + ".";
    }

    /*
    Whenever a peer is choked by a neighbor (which means when a peer receives a choking
message from a neighbor), it generates the following log:
[Time]: Peer [peer_ID 1] is choked by [peer_ID 2].
[peer_ID 1] represents the peer who is choked and [peer_ID 2] represents the peer
who chokes [peer_ID 1].
     */
    public void chokedByNeighborLog(int peerID, int otherID){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " is choked by " + otherID + ".";
    }

    /*
    Whenever a peer receives a ‘have’ message, it generates the following log:
[Time]: Peer [peer_ID 1] received the ‘have’ message from [peer_ID 2] for the piece
[piece index].
[peer_ID 1] represents the peer who received the ‘have’ message and [peer_ID 2]
represents the peer who sent the message. [piece index] is the piece index contained
in the message.
     */

    public void receivedHaveMessageLog(int peerID, int otherID, int pieceIndex){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " received the 'have' message from " + otherID + "for the piece " + pieceIndex + ".";
    }

    /*
    Whenever a peer receives an ‘interested’ message, it generates the following log:
[Time]: Peer [peer_ID 1] received the ‘interested’ message from [peer_ID 2].
[peer_ID 1] represents the peer who received the ‘interested’ message and [peer_ID 2]
represents the peer who sent the message.
     */
    public void receivedInterestedMessageLog(int peerID, int otherID){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " received the 'interested' message from " + otherID + ".";
    }

    /*
    Whenever a peer receives a ‘not interested’ message, it generates the following log:
[Time]: Peer [peer_ID 1] received the ‘not interested’ message from [peer_ID 2].
[peer_ID 1] represents the peer who received the ‘not interested’ message and
[peer_ID 2] represents the peer who sent the message.
     */
    public void receivedNotInterestedMessageLog(int peerID, int otherID){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " received the 'not interested' message from " + otherID + ".";
    }

    /*
    Whenever a peer finishes downloading a piece, it generates the following log:
[Time]: Peer [peer_ID 1] has downloaded the piece [piece index] from [peer_ID 2].
Now the number of pieces it has is [number of pieces].
[peer_ID 1] represents the peer who downloaded the piece and [peer_ID 2] represents
the peer who sent the piece. [piece index] is the piece index the peer has downloaded.
[number of pieces] represents the number of pieces the peer currently has.
     */

    public void downloadedPieceMessageLog(int peerID, int otherID,int pieceIndex, int numOfPieces){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " has downloaded the piece " + "from " + otherID + ". Now the number of pieces it has is " + numOfPieces + ".";
    }

    /*
    Whenever a peer downloads the complete file, it generates the following log:
[Time]: Peer [peer_ID] has downloaded the complete file.
     */
    public void completedDownloadLog(int peerID){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " had downloaded the complete file.";
    }

    public void logString(String s, int peerID){

        String peerDirectory = "./peer_" + peerID + "/";
        String peerLogFile = "log_peer_" + peerID + ".log";


        /*try (PrintStream out = new PrintStream(new FileOutputStream(peerDirectory+peerLogFile))) {
            out.print(s);
            out.close();
        }*/

        try
        {

            FileWriter fw = new FileWriter(peerDirectory+peerLogFile,true); //the true will append the new data
            fw.write(s+"\n");//appends the string to the file
            fw.close();
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }

    }


}