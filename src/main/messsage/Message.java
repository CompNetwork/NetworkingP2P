package main.messsage;

//import java.io.Serializable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
    byte mType;
    byte[] m1;  //first part of message
    byte[] m2;  //second part of message
    byte[] m3;  //third part of message


    public static Message createHandShakeMessageFromPeerId(String peerID) {
       Message message = new Message();
       message.setAsHandshakeMessage(peerID.getBytes(StandardCharsets.ISO_8859_1));
       return message;
    }

    private void setAsHandshakeMessage(byte[] peerID) {
        m1 = "P2PFILESHARINGPROJ".getBytes(StandardCharsets.ISO_8859_1);
        m2 = new byte[10]; //28 is where id begins
        Arrays.fill(m2,(byte)0x00);
        m3 = peerID;
        mType = MessageTypeConstants.HANDSHAKE;
    }


    // Updates the state of the Message object depending on the rawData
    public void update(byte[] rawData) {

        // Sets message value for handshaking
        if (isMessageHandShake(rawData)) {
            setAsHandshakeMessage(Arrays.copyOfRange(rawData,28,32));
        }
        // Sets message values for actual message
        else {
            //this.setActualMessage(s);
            m1 = Arrays.copyOfRange(rawData,0,4);      //size
            m2 = Arrays.copyOfRange(rawData,4,5);      //message type
            mType = rawData[4];
            int size = rawData.length;
            if(size-5  > 0)
                m3 = Arrays.copyOfRange(rawData,5,size);
            else
                m3 = new byte[0];
        }
    }


    public byte[] getFull(){ return ByteArrayUtilities.combineThreeByteArrays(m1,m2,m3);}

    public byte getmType() {
        return mType;
    }

    public byte[] getM3() {
        return m3;
    }

    public String getPeerId() {
        if (mType == MessageTypeConstants.HANDSHAKE ) {
            return new String(m3,StandardCharsets.ISO_8859_1);
        } else {
            throw new IllegalStateException("Error, asked for an peer id, but this is not a handshake message!");
        }
    }

    public void update(byte type, byte[] payload) {
        if (payload == null ) {
            payload = new byte[0];
            // Just for simpler impl.
            // Not really worrying about perf atm.
        }
        this.m1 = ByteArrayUtilities.SplitIntInto4ByteArray(payload.length);
        this.m2 = new byte[1];
        m2[0] = type;
        mType = type;
        m3 = payload;

    }

    // Determines if the message given in is a handshake.
    // Byte array passed in should include first 5 bytes of message.
    private static boolean isMessageHandShake(byte[] message) {
        return message[4] == 'I';
    }

    public static int BytesRemainingInMessageFromHeader(byte[] header) {
        if (header.length != 5) {
            throw new IllegalArgumentException("Error! A header is the first 5 bytes of a message. Mainly, the 5th byte tells us the type, and the first 4 the size!");
        }
        // This is a handshake message!
        // Message length can be anything.
        // But the 5th byte must be the I in P@PFILESHARINGPROJ, so
        // WE can consider a handshake message to have a type value of 'I', or 73
        if (isMessageHandShake(header)) {
            // This is a handshake message
            int remainingLength = 32 - 5;
            return remainingLength;
        } else {
            // This is a "actual" message!
            int remainingLength = ByteArrayUtilities.recombine4BytesIntoInts(header[0], header[1], header[2], header[3]);
            return remainingLength;
        }
    }
}