package RemoteStart;

import RemoteStart.RemotePeerInfo;

import java.io.*;
import java.util.*;

/*
 *                     CEN5501C Project2
 * This is the program starting remote processes.
 * This program was only tested on CISE SunOS environment.
 * If you use another environment, for example, linux environment in CISE
 * or other environments not in CISE, it is not guaranteed to work properly.
 * It is your responsibility to adapt this program to your running environment.
 */



/*
 * The StartRemotePeers class begins remote peer processes.
 * It reads configuration file PeerInfo.cfg and starts remote peer processes.
 * You must modify this program a little bit if your peer processes are written in C or C++.
 * Please look at the lines below the comment saying IMPORTANT.
 */
public class StartRemotePeers {

    public ArrayList<RemotePeerInfo> peerInfoVector;

    public void getConfiguration()
    {
        String st;
        int i1;
        peerInfoVector = new ArrayList<>();
        try {

            String configDir = System.getProperty("user.dir")  + "/PeerConfig/PeerInfo.cfg";
            System.out.println("Config Dir: " + configDir);
            BufferedReader in = new BufferedReader(new FileReader(configDir));
            while((st = in.readLine()) != null) {

                String[] tokens = st.split("\\s+");
                //System.out.println("tokens begin ----");
                //for (int x=0; x<tokens.length; x++) {
                //    System.out.println(tokens[x]);
                //}
                //System.out.println("tokens end ----");

                peerInfoVector.add(new RemotePeerInfo(tokens[0], tokens[1], tokens[2]));

            }

            in.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        ArrayList<Process> processes = new ArrayList<>();
        try {
            StartRemotePeers myStart = new StartRemotePeers();
            myStart.getConfiguration();

            // get current path
            String path = System.getProperty("user.dir");
            System.out.println("Root path: " + path);

            // start clients at remote hosts
            for (int i = 0; i < myStart.peerInfoVector.size(); i++) {
                RemotePeerInfo pInfo = myStart.peerInfoVector.get(i);

                System.out.println("Start remote peer " + pInfo.peerId +  " at " + pInfo.peerAddress );

                // *********************** IMPORTANT *************************** //
                // If your program is JAVA, use this line.
                String exec = "ssh " + pInfo.peerAddress + " cd " + path + "; java -jar IndividualPeer.jar " + pInfo.peerId;
                if ( args[0].equals("kill") ) {
                    exec = "ssh " + pInfo.peerAddress + " cd " + path + "; pkill \"java -jar *\"";
                }
                System.out.println("Executing the following line " + exec);
                processes.add(Runtime.getRuntime().exec(exec));

            }
            System.out.println("Starting all remote peers has done." );


            int i = 0;
            for ( Process proc : processes ) {
                System.out.println("------------- Printing out info for peer #" +i);

                BufferedReader stdInput = new BufferedReader(new
                        InputStreamReader(proc.getInputStream()));
                BufferedReader stdError = new BufferedReader(new
                        InputStreamReader(proc.getErrorStream()));
                String s = null;
                while ((s = stdError.readLine()) != null) {
                    System.err.println(s);
                }

                while ((s = stdInput.readLine()) != null) {
                    System.out.println(s);
                }
                ++i;

            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
