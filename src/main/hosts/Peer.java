package main.hosts;

import com.sun.security.ntlm.Client;
import main.config.pod.CommonConfigData;
import main.config.reader.CommonConfigReader;
import main.file.ChunkifiedFile;
import main.logger.Logger;
import main.messsage.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Peer {

    private static final String PEERINFO = "./src/main/hosts/PeerInfo.cfg";
    private static final String FILEPATH = "./src/main/hosts/Common.cfg";
    private static final int PEERID = 0;
    private static final int PEERHOSTNAME = 1;
    private static final int PEERPORT = 2;
    private static final int PEERHASFILE = 3;

    private String peerID;
    private String hostName;
    private int port;
    private ArrayList <ClientThread> connections;
    private ServerSocket sSocket;
    private ChunkifiedFile chunky;
    private Logger logger;

    public Peer(String peerID, String hostname, int port) {
        this.peerID = peerID;
        this.hostName = hostname;
        this.port = port;
        this.connections = new ArrayList<ClientThread>();
        this.logger = new Logger();
        this.chunky = initFileChunk(this.peerID);
        //this.run();
    }

    // Starts P2P process
    public void start() {
        //this.chunky.AvailableChunks();
        this.connect2Peers();
        this.startServer();
    }

    // Connects to all appropriate peers
    public void connect2Peers() {

        try {
            List<String> peers = this.getPeerConnList();
            if (peers.size() == 0) System.out.println("I " + this.peerID + " am the first. No one to connect to.");
            else System.out.println("Connecting with peers " + peers);

            for (String peer : peers) {

                System.out.println("Handling Peer: " + peer);
                String [] peerCol = peer.split(" ");

                System.out.println("I am " + this.peerID + " attempting to connect with " + peerCol[PEERID]);
                Socket s = new Socket(peerCol[PEERHOSTNAME], Integer.valueOf(peerCol[PEERPORT]));
                RemotePeer remotePeer = new RemotePeer(""+PEERID,chunky.getChunkCount());
                ClientThread ct = new ClientThread(s, this,remotePeer);
                ct.start();
                this.connections.add(ct);

                System.out.println("Peer " + this.peerID + " has successfully connected with Peer " + peerCol[PEERID] + " via socket " + s.toString());
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
            this.sSocket = new ServerSocket(this.port);
            while(true) {

                // Select between accepting new sockets
                System.out.println("Awaiting connections to other peers...");
                Socket s = this.sSocket.accept();
                System.out.println("Peer " + this.peerID + " added new connection: " + s.toString() + " to connection list");
                RemotePeer remotePeer = new RemotePeer(RemotePeer.NO_PEER_ID_YET,chunky.getChunkCount());
                ClientThread ct = new ClientThread(s, this, remotePeer);
                ct.start();
                this.connections.add(ct);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                for (ClientThread connection : connections) { connection.close(); }
                this.sSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private ChunkifiedFile initFileChunk(String peerID) {

        ChunkifiedFile cf = null;
        List<String> list = this.getPeerInfo();

        // Checks if I have the file
        for (int i = 0; i < list.size(); i++) {
            String [] lineArr = list.get(i).split(" ");

            if(lineArr[0].equals(this.peerID)) {
                CommonConfigReader commonConfig = null;

                // Checks if the value is valid
                if(lineArr[3].equals("1") || lineArr[3].equals("0")) {

                    try {
                        commonConfig = new CommonConfigReader(new File(FILEPATH));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    CommonConfigData data = commonConfig.getData();

                    // Sets the Chunkified File data if this localPeer has the file
                    if(lineArr[3].equals("1")) {

                        cf = ChunkifiedFile.GetFromExisingFile(data.getFileName(), data.getPieceSize(), data.getFileSize());
                    } else {

                        cf = ChunkifiedFile.CreateFile(data.getFileName(), data.getPieceSize(), data.getFileSize());
                    }
                    break;
                } else {
                    throw new IllegalArgumentException("Error in PeerInfo.cfg");
                }
            }
        }
        return cf;
    }

    private List<String> getPeerInfo() {
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
        return list;
    }

    // Collects all entries from PeerInfo.cfg
    private List<String> getPeerConnList(){

        List<String> list = this.getPeerInfo();

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

    public ArrayList<ClientThread> getConnections() {
        return connections;
    }

    public String getHostName() {
        return hostName;
    }

    public Logger getLogger(){ return logger;}

    public int getPort() {
        return port;
    }

    public ChunkifiedFile getChunky() {
        return chunky;
    }

    public String getPeerID() {
        return peerID;
    }

    public void sendMessageToAllRemotePeers(Message message) throws IOException {
        for (ClientThread thread : connections) {
            thread.sendMessage(message);
        }
    }
    public void sendHaveMessageToAllRemotePeers(int index)  throws IOException {
        // Do not reuse the existing message, multi threading problems!!
        Message m = new Message();
        m.mutateIntoHave(index);
        this.sendMessageToAllRemotePeers(m);
    }

    // For each client thread (remote peer), determine if we are still interested in them.
    // If we are not, send a uninterested message.
    // We do not check if a peer is uninteresting before hand. If a peer was uninteresting before hand.
    public void sendUninterestedToAllUninterestingPeers() throws IOException {
        Message uninterested = new Message();
        uninterested.mutateIntoUnInterested();
        for (ClientThread thread : connections) {
            if (!thread.isRemotePeerInteresting() ) {
                thread.sendMessage(uninterested);
            }
        }
    }

    public void informOfReceivedPiece(String peerID, int sizeOfPiece) {
        // TODO: Andy
    }
}