package main.hosts;

//import java.io.Serializable;

public class Message  {
/*
    //different types of messages such as handshake and actual messages
    //
    handshake message has 3 parts
    - handshake header
    - zero bits
    - peer id
    -length is 32 bytes
    -header is 18-byte string "P2PFILESHARINGPROJ"
    -then 10 byte zero bits
    -then 4-byte peerID

    actual messages
    -4 byte message length field,
    -1 byte message type field
    -variable length message payload
    -length does not inlcude the message length field itself
    -message type is single value,
        0 choke
        1 unchoke
        2 interested
        3 not interested
        4 have
        5 bitfield
        6 request
        7 piece
    -0, 1, 2, 3 have no payload
    - 4 has 4-byte piece index field
    - 5 each bit corresponds to whether the peer has the corresponding piece or not
       spare bits are 0
    - 6 has 4-byte piece index field
    - 7 4-byte piece index field

    */
    //private static final long serialVersionID = 132437293456465438l;
    String m1;  //first part of message
    String m2;  //second part of message
    String m3;  //third part of message
    String full;//full string of message

    //handshake
    public Message(int peerID){
        m1 = "P2PFILESHARINGPROJ";
        m2 = "0000000000";
        m3 = Integer.toString(peerID);
        full = m1+m2+m3;

    }
    public Message(int mType, String payload) {
        //setMs(text);
        //need to calculate message length
        int mLength = payload.length() + 1;// +1 because of mType

        //add leading zeroes because it needs to be 4 bytes
        if(mLength >= 1 && mLength <= 9){
            m1 = "000" + Integer.toString(mLength);
        }
        else if(mLength >= 10 && mLength <= 99){
            m1 = "00" + Integer.toString(mLength);
        }
        else if(mLength >= 100 && mLength <= 999){
            m1 = "0" + Integer.toString(mLength);
        }
        else if(mLength >= 1000 && mLength <= 9999)
        {
            m1 = Integer.toString(mLength);
        }
        m2 = Integer.toString(mType);
        m3 = payload;
        full = m1+m2+m3;
    }

    //when it accepts an incoming string, it has to break it down.
    public Message(String s) {

        if(s.length() == 4) {
            m1 = "P2PFILESHARINGPROJ";
            m2 = "0000000000";
            m3 = s;
            full = m1 + m2 + m3;
        }
        //is a handshake
        else if(s.substring(0,1).equalsIgnoreCase("P")){
            m1 = "P2PFILESHARINGPROJ";
            m2 = "0000000000"; //28 is where id begins
            m3 = s.substring(28,31);
        }
        else{
            m1 = s.substring(0,3);      //size
            m2 = s.substring(4);      //message type
            int size = s.length();
            if(size-5  > 0)
                m3 = s.substring(5,size);
            else
                m3 = "";
        }

        full = m1+m2+m3;

    }

    public String getM1() {
        return this.m1;
    }

    public void setM1(String message) {
        this.m1 = message;
    }

    public String getM2() {
        return this.m2;
    }

    public void setM2(String message) {
        this.m2 = message;
    }

    public String getM3() {
        return this.m3;
    }

    public void setM3(String message) {
        this.m3 = message;
    }

    public String getFull(){return this.full;}

    public void setFull(String message){this.full =message; }

}
