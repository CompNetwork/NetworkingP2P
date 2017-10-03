package Peers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Peer {

    private static final String PEERINFO = "./src/PeerInfo.cfg";
    private static final int PEERID = 0;
    private static final int PEERHOSTNAME = 1;
    private static final int PEERPORT = 2;
    private static final int PEERHASFILE = 3;

    private String peerID;
    private String hostName;
    private int port;
    private ArrayList <Socket> connections;
    private ServerSocket sSocket;
    private Socket cSocket;

    public Peer(String peerID, String hostname, int port) {
        this.peerID = peerID;
        this.hostName = hostname;
        this.port = port;
        //this.run();
    }

    // Starts P2P process
    private void run(){
        this.connect2Peers();
        this.startServer();
    }

    // Connects to all appropriate peers
    public void connect2Peers(){

        try {
            List<String> peers = this.getPeerConnList();
            for (String peer : peers) {

                String [] peerCol = peer.split(" ");
                System.out.println("I am " + this.peerID + " attempting to connect with " + peerCol[PEERID]);
                Socket c = new Socket(peerCol[PEERHOSTNAME], Integer.valueOf(peerCol[PEERPORT]));
                PrintWriter out =  new PrintWriter(c.getOutputStream(), true);
                BufferedReader stdIn = new BufferedReader((new InputStreamReader(System.in)));

                String outputLine = stdIn.readLine();
                System.out.println(outputLine + " received");

                out.println(outputLine);
            }

        } catch (UnknownHostException e) {
            System.out.println("Unknown host");
        } catch (IOException e) {
            System.out.println("No I/O");
        }
    }

    // Opens server connects
    public void startServer(){
        try {

//            while(true) {
//            }
            // Start ServerSocket
            this.sSocket = new ServerSocket(this.port);
            // Log Server connected to PORT
            System.out.println("Server started and connected to " + this.port);

            Socket s = this.sSocket.accept();

            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedReader stdIn = new BufferedReader((new InputStreamReader(System.in)));

            String inputLine = in.readLine();
            //out.println(inputLine);

            System.out.println("I am server " + this.peerID + " retreived " + inputLine + " from a sender.");

            System.out.println("Ending Program...");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                this.sSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // Collects all entries from PeerInfo.cfg
    private List<String> getPeerConnList(){
        List<String> list = new ArrayList<String>();

        try {
            String line;
            BufferedReader in = new BufferedReader(new FileReader(PEERINFO));

            while((line = in.readLine()) != null){
                list.add(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int i = 0;
        boolean found = false;
        List<String> result = new ArrayList<String>();

        while(!found && i < list.size()) {
            String [] lineArr = list.get(i).split(" ");

            if(lineArr[0].equals(this.peerID)){
                found = true;
                result = list.subList(0,i);
            }
            i++;
        }

        return result;
    }

}
