package main.unchoking;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PeerTimer extends TimerTask {


    //schedule the tasks you want to occur when the PeerTimer runs out.
    /*Example of how to use this class
    Timer tim = new Timer();                    Create a new timer.
    tim.schedule(new PeerTimer(), 0, 8000);     Schedule run to activate every " " milliseconds. 8000=8 seconds
    */

    CalculateHighestUploadingNeighbors uploader = new CalculateHighestUploadingNeighbors();

    @Override
    public void run() {
        System.out.println("This is a timer test.");
        ArrayList<String> peerList = uploader.getKBestUploaders(4);          //variable is the number of peers you want back. Returns ArrayList<String>

        //

    }
}
