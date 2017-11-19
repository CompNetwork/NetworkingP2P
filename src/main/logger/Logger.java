package main.logger;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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


    public void TCPConnectionLog(int peerIDInt, int otherPeerIDInt) {
        String peerID = "" + peerIDInt;
        String otherPeerID = "" + otherPeerIDInt;

        //gets time
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " makes a connection to Peer " + otherPeerID + ".";

        logString(log,peerID);

    }

    /*
    Whenever a peer is connected from another peer, it generates the following log:
[Time]: Peer [peer_ID 1] is connected from Peer [peer_ID 2].
[peer_ID 1] is the ID of peer who generates the log, [peer_ID 2] is the peer who has
made TCP connection to [peer_ID 1].

     */
    public void acceptConnectionLog(int peerIDInt, int otherPeerIDInt){
        String peerID = "" + peerIDInt;
        String otherPeerID = "" + otherPeerIDInt;
        //called when it has been connected to, not the when it's trying to make a connection
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " is connected from Peer " + otherPeerID + ".";
        logString(log,peerID);
    }

    /*
    Whenever a peer changes its preferred neighbors, it generates the following log:
[Time]: Peer [peer_ID] has the preferred neighbors [preferred neighbor ID list].
[preferred neighbor list] is the list of peer IDs separated by comma ‘,’.
     */

    public void changePreferredNeighborsLog(int peerIDInt,String neighborList ){
        String peerID = "" + peerIDInt;
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " has the preferred neighbors " + neighborList;

        /*for(int i = 0; i < neighborList.length; i++){
            log = log + neighborList[i] + ", ";
        }*/

        log = log + ".";
        logString(log,peerID);
    }

    /*
    Whenever a peer changes its optimistically unchoked neighbor, it generates the
following log:
[Time]: Peer [peer_ID] has the optimistically unchoked neighbor [optimistically
unchoked neighbor ID].
[optimistically unchoked neighbor ID] is the peer ID of the optimistically unchoked
neighbor.
     */

    public void changeOfOptimisticallyUnchockedNeighborLog(int peerIDInt, int otherIDInt){
        String peerID = "" + peerIDInt;
        String otherID = "" + otherIDInt;
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " has the optimistically unchocked neighbor " + otherID + ".";
        logString(log,peerID);
    }

    /*
    Whenever a peer is unchoked by a neighbor (which means when a peer receives an
unchoking message from a neighbor), it generates the following log:
[Time]: Peer [peer_ID 1] is unchoked by [peer_ID 2].
[peer_ID 1] represents the peer who is unchoked and [peer_ID 2] represents the peer
who unchokes [peer_ID 1].
     */

    public void unchokedByNeighborLog(int peerIDInt, int otherIDInt){
        String peerID = "" + peerIDInt;
        String otherID = "" + otherIDInt;
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " is unchoked by " + otherID + ".";
        logString(log,peerID);
    }

    /*
    Whenever a peer is choked by a neighbor (which means when a peer receives a choking
message from a neighbor), it generates the following log:
[Time]: Peer [peer_ID 1] is choked by [peer_ID 2].
[peer_ID 1] represents the peer who is choked and [peer_ID 2] represents the peer
who chokes [peer_ID 1].
     */
    public void chokedByNeighborLog(int peerIDInt, int otherIDInt){
        String peerID = "" + peerIDInt;
        String otherID = "" + otherIDInt;

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " is choked by " + otherID + ".";
        logString(log,peerID);
    }

    /*
    Whenever a peer receives a ‘have’ message, it generates the following log:
[Time]: Peer [peer_ID 1] received the ‘have’ message from [peer_ID 2] for the piece
[piece index].
[peer_ID 1] represents the peer who received the ‘have’ message and [peer_ID 2]
represents the peer who sent the message. [piece index] is the piece index contained
in the message.
     */

    public void receivedHaveMessageLog(int peerIDInt, int otherIDInt, int pieceIndex){
        String peerID = "" + peerIDInt;
        String otherID = "" + otherIDInt;

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " received the 'have' message from " + otherID + " for the piece " + pieceIndex + ".";
        logString(log,peerID);
    }

    /*
    Whenever a peer receives an ‘interested’ message, it generates the following log:
[Time]: Peer [peer_ID 1] received the ‘interested’ message from [peer_ID 2].
[peer_ID 1] represents the peer who received the ‘interested’ message and [peer_ID 2]
represents the peer who sent the message.
     */
    public void receivedInterestedMessageLog(int peerIDInt, int otherIDInt){
        String peerID = "" + peerIDInt;
        String otherID = "" + otherIDInt;
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " received the 'interested' message from " + otherID + ".";
        logString(log,peerID);
    }

    /*
    Whenever a peer receives a ‘not interested’ message, it generates the following log:
[Time]: Peer [peer_ID 1] received the ‘not interested’ message from [peer_ID 2].
[peer_ID 1] represents the peer who received the ‘not interested’ message and
[peer_ID 2] represents the peer who sent the message.
     */
    public void receivedNotInterestedMessageLog(int peerIDInt, int otherIDInt){
        String peerID = "" + peerIDInt;
        String otherID = "" + otherIDInt;
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " received the 'not interested' message from " + otherID + ".";
        logString(log,peerID);
    }

    /*
    Whenever a peer finishes downloading a piece, it generates the following log:
[Time]: Peer [peer_ID 1] has downloaded the piece [piece index] from [peer_ID 2].
Now the number of pieces it has is [number of pieces].
[peer_ID 1] represents the peer who downloaded the piece and [peer_ID 2] represents
the peer who sent the piece. [piece index] is the piece index the peer has downloaded.
[number of pieces] represents the number of pieces the peer currently has.
     */

    public void downloadedPieceMessageLog(int peerIDInt, int otherIDInt, int pieceIndex, int numOfPieces){
        String peerID = "" + peerIDInt;
        String otherID = "" + otherIDInt;
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " has downloaded the piece " + pieceIndex + " from " + otherID + ". Now the number of pieces it has is " + numOfPieces + ".";
        logString(log,peerID);
    }

    /*
    Whenever a peer downloads the complete file, it generates the following log:
[Time]: Peer [peer_ID] has downloaded the complete file.
     */
    public void completedDownloadLog(int peerIDInt){
        String peerID = "" + peerIDInt;
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String log = timeStamp + ": Peer " + peerID + " had downloaded the complete file.";
        logString(log,peerID);
    }

    public void logString(String s, String peerID){

        String fileSep = System.getProperty("file.separator");
        //String peerDirectory = "\\peer_" + peerID + "\\";
        String peerDirectory = fileSep + "peer_" + peerID + fileSep;
        String peerLogFile = "log_peer_" + peerID + ".log";
        String currentDir = System.getProperty("user.dir");
        String fileName = currentDir+peerDirectory+peerLogFile;
        //System.out.println(fileName);

        File theDir = new File(currentDir+peerDirectory);       //creates the directory needed for the file
        if (!theDir.exists()) theDir.mkdir();

        // Write your data

        /*try (PrintStream out = new PrintStream(new FileOutputStream(peerDirectory+peerLogFile))) {
            out.print(s);
            out.close();
        }*/

        try
        {
            //writes the file log and catches errors if they occur.
            //FileWriter fw = new FileWriter(fileName,true); //the true will append the new data
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName,true));
            /*fw.write(s+"\n");
            fw.newLine();
            fw.close();
             */
            bw.write(s+"\n");//appends the string to the file
            bw.newLine();
            bw.close();

        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }

    }



}