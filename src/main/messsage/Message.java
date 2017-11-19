package main.messsage;

//import java.io.Serializable;

import main.file.ChunkifiedFileUtilities;
import main.file.FileChunk;
import main.file.FileChunkImpl;

import java.awt.*;
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
    private byte mType;
    private byte[] m1;  //first part of message
    private byte[] m2;  //second part of message
    private byte[] m3;  //third part of message


    // Setters. Provide a safe way to set this message
    // to each of the types.
    // Provide the field, and let the message handle encapsulating it!

    // First all the payloadless messages.
    public void mutateIntoChoke() {
        this.update(MessageTypeConstants.CHOKE,null);
    }

    public void mutateIntoUnChoke() {
        this.update(MessageTypeConstants.UNCHOKE,null);
    }

    public void mutateIntoInterested() {
        this.update(MessageTypeConstants.INTERESTED,null);
    }

    public void mutateIntoUnInterested() {
        this.update(MessageTypeConstants.UNINTERESTED,null);
    }

    public void mutateIntoHandshake(int peerID) {
        this.setAsHandshakeMessage(ByteArrayUtilities.SplitIntInto4ByteArray(peerID));
    }
    // Now the slightly more interesting ones with an integer payload!
    public void mutateIntoHave(int payload) {
        this.update(MessageTypeConstants.HAVE,payload);
    }

    public void mutateIntoRequest(int payload) {
        this.update(MessageTypeConstants.REQUEST,payload);
    }

    // Now for the two actually complicated causes, the bitfield, and the piece!
    public void mutateIntoBitField(boolean[] bitfield) {
        byte[] byteField = ChunkifiedFileUtilities.getByteSetFromBitSet(bitfield);
        this.update(MessageTypeConstants.BITFIELD,byteField);
    }
    public void mutateIntoPiece(FileChunk piece, int pieceIndex) {
        byte[] fileinBytes = piece.asByteArray();
        byte[] fileChunkSize = ByteArrayUtilities.SplitIntInto4ByteArray(pieceIndex);
        byte[] piecePayload = new byte[fileinBytes.length+fileChunkSize.length];
        System.arraycopy(fileChunkSize,0,piecePayload,0,fileChunkSize.length);
        System.arraycopy(fileinBytes,0,piecePayload,4,fileinBytes.length);
        this.update(MessageTypeConstants.PIECE,piecePayload);
    }

    public static Message createHandShakeMessageFromPeerId(int peerID) {
       Message message = new Message();
       message.setAsHandshakeMessage(ByteArrayUtilities.SplitIntInto4ByteArray(peerID));
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
        // TODO: Bregg Add validating for all types of messages. EX: Validate a have message has an index field.

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
            int rawSize = rawData.length;
            if(rawSize-5  > 0)
                m3 = Arrays.copyOfRange(rawData,5,rawSize);
            else
                m3 = new byte[0];

            {
                int givenSize = ByteArrayUtilities.recombine4ByteArrayIntoInt(m1);
                if (rawSize - 4 != givenSize) {
                    System.err.println("Error, specified message size is not equal to payload!");
                    throw new IllegalArgumentException("EError, specified message size is not equal to payload! Length:" + rawSize + " GivenLength:" + givenSize);
                }
            }
        }
    }

    // FIXME: Naming for this signature doesn't seem to be intuitive. This seems to be a helper function for update
    private void update(byte type, int payload) {
        byte[] splitPayload = ByteArrayUtilities.SplitIntInto4ByteArray(payload);
        this.update(type,splitPayload);
    }

    private void update(byte type, byte[] payload) {
        // TODO: Bregg Add validating for all types of messages. EX: Validate a have message has an index field.
        if (payload == null ) {
            payload = new byte[0];
            // Just for simpler impl.
            // Not really worrying about perf atm.
        }
        // Payload.length, + 1 for the type field.
        this.m1 = ByteArrayUtilities.SplitIntInto4ByteArray(payload.length+1);
        this.m2 = new byte[1];
        m2[0] = type;
        mType = type;
        m3 = payload;
    }


    public byte[] getFull(){ return ByteArrayUtilities.combineThreeByteArrays(m1,m2,m3);}

    public byte getmType() {
        return mType;
    }

    private byte[] getM3() {
        return m3;
    }

    public int getPeerIdPayload() {
        if (mType == MessageTypeConstants.HANDSHAKE ) {
            return ByteArrayUtilities.recombine4ByteArrayIntoInt(m3);
        } else {
            throw new IllegalStateException("Error, asked for an peer id, but this is not a handshake message!");
        }
    }


    // If this is a have,or request message, return the index in the payload.
    // throws an illegal state exception otherwise.
    public int getIndexPayload() {
        if ( this.getmType() == MessageTypeConstants.HAVE || this.getmType() == MessageTypeConstants.REQUEST ) {
            return ByteArrayUtilities.recombine4ByteArrayIntoInt(this.getM3());
        }
        if ( this.getmType() == MessageTypeConstants.PIECE ) {
            return ByteArrayUtilities.recombine4ByteArrayIntoInt(Arrays.copyOfRange(this.getM3(),0,4));
        }

        throw new IllegalStateException("Error, trying to get an integer payload from a message with no integer payload!");
    }

    // If this is a bitfield message, return the bitfield in the payload.
    // Requires the length of the expected bitfield to trim the result.
    // throws an illegal state exception otherwise.
    public boolean[] getBitFieldPayload(int length) {
        if ( this.getmType() == MessageTypeConstants.BITFIELD ) {
            return ChunkifiedFileUtilities.getBitSetFromByteSet(this.getM3(),length);
        }
        throw new IllegalStateException("Error, trying to get an bitfield payload from non bitfield message!");
    }

    // If this is a piece message, return the FileChunk in the payload.
    // throws an illegal state exception otherwise.
    public FileChunk getFileChunkPayload() {
        if ( this.getmType() == MessageTypeConstants.PIECE ) {
            byte[] indexAndPayload = this.getM3();
            return new FileChunkImpl(Arrays.copyOfRange(indexAndPayload,4,indexAndPayload.length));

        }
        throw new IllegalStateException("Error, trying to get an FileChunk payload from non piece message!");

    }

    // Determines if the message given in is a handshake.
    // Byte array passed in should include first 5 bytes of message.
    private static boolean isMessageHandShake(byte[] message) {
        return message[4] == MessageTypeConstants.HANDSHAKE;
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
            // Why is this minus 1?
            // Because remaining length is the message size which is | type + payload |.
            // Since we already read in the payload, subtract 1 to account for that!
            return remainingLength-1;
        }
    }
}