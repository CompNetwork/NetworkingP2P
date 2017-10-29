package main.messsage;

// 1 byte field that goes in messages to determine the type.
// Using unsigned representation for now.
public class MessageTypeConstants {
    public static final byte CHOKE = 0x00;
    public static final byte UNCHOKE = 0x01;
    public static final byte INTERESTED = 0x02;
    public static final byte UNINTERESTED = 0x03;
    public static final byte HAVE = 0x04;
    public static final byte BITFIELD = 0x05;
    public static final byte REQUEST = 0x06;
    public static final byte PIECE = 0x07;
}
