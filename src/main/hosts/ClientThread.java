package main.hosts;

import main.hosts.Message;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {

    Socket socket = null;
    Peer peer = null;
    BufferedReader cmdInput = null;
    BufferedReader userInput = null;
    PrintWriter userOutput =  null;

    public ClientThread(Socket socket, Peer peer) {
        this.socket = socket;
        this.peer = peer;
        this.setSocketIO();
        this.handshake();
    }

    public void run() {
        Message message = null;
        try {
            while(true) {

                if(!cmdInput.ready() && !userInput.ready()) {
                    Thread.sleep(500);
                }

                if(userInput.ready()) {
                    String text = userInput.readLine();
                    userOutput.println(text);

                }

                if(cmdInput.ready()) {
                    String text =  this.cmdInput.readLine();
                    //message = new Message(text);
                    System.out.println(text);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Closing socket" + this.socket + " to Peer" + peer.getPeerID());
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close(){
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setSocketIO(){
        try {
            this.userInput = new BufferedReader(new InputStreamReader(System.in));
            this.cmdInput = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.userOutput = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handshake() {

        Message m = new Message(this.peer.getPeerID());
        userOutput.println(m.getFull());
    }
}
