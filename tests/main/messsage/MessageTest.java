package main.messsage;

import main.file.FileChunkImpl;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.util.Arrays;

public class MessageTest {

    @Test
    public void updateFromByteArray_CreateHaveMessage_ValidHaveMessageWithCorrectPayload() {
       Message message = new Message();

       byte[] m1 = { 0x00, 0x00, 0x00, 0x05 };
       byte[] m2 = { MessageTypeConstants.HAVE };
       int index = 42;
       byte[] m3 = ByteArrayUtilities.SplitIntInto4ByteArray(42);
       message.update(ByteArrayUtilities.combineThreeByteArrays(m1,m2,m3));

       Assert.assertEquals(MessageTypeConstants.HAVE,message.getmType());
       Assert.assertEquals(index,message.getIndexPayload());
    }

    @Test
    public void updateFromByteArray_CreateHandShakeMessage_ValidHandshakeMessage() {
        Message message = new Message();
        byte[] handshake = { 'P', '2', 'P', 'F', 'I', 'L', 'E', 'S', 'H', 'A', 'R', 'I', 'N', 'G', 'P', 'R', 'O', 'J'
                            ,0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, '1', '2', '3', '4' };
        message.update(handshake);

        Assert.assertEquals(MessageTypeConstants.HANDSHAKE,message.getmType());
        Assert.assertEquals("1234",message.getPeerIdPayload());
        Assert.assertArrayEquals(handshake,message.getFull());
    }

    @Test
    public void BytesRemainingInMessageFromHeader_HandShakeMessage_Get27() {
        byte[] header = {0x00, 0x00, 0x00, 0x00, 'I'};
        Assert.assertEquals(27,Message.BytesRemainingInMessageFromHeader(header));
    }

    @Test
    public void BytesRemainingInMessageFromHeader_HaveMessage_GetPayloadLength() {
        byte[] header = {0x00, 0x00, 0x12, 0x34, MessageTypeConstants.HAVE};
        // The size in the header includes the following type field.
        // The body however, is only the payload, not the type field. (We had to already read the type filed in to determine message type!)
        // So subtract 1.
        Assert.assertEquals(0x00001233,Message.BytesRemainingInMessageFromHeader(header));
    }

    @Test
    public void mutateIntoHave_MutatesIntoHave_BecomesAHaveMessage() {
        Message message = new Message();
        message.mutateIntoHave(42);
        Assert.assertEquals(MessageTypeConstants.HAVE,message.getmType());
        Assert.assertEquals(42,message.getIndexPayload());
        byte[] expectedMessage = { 0x00,0x00,0x00,0x05, MessageTypeConstants.HAVE, 0x00, 0x00, 0x00,  0x2A};
        Assert.assertArrayEquals(expectedMessage,message.getFull());
    }

    @Test
    public void mutateIntoHandShake_MutatesIntoHandShake_BecomesAHandShakeMessage() {
        Message message = new Message();
        message.mutateIntoHandshake("1234");
        Assert.assertEquals(MessageTypeConstants.HANDSHAKE,message.getmType());
        Assert.assertEquals("1234",message.getPeerIdPayload());
        byte[] expectedMessage = { 'P', '2', 'P', 'F', 'I', 'L', 'E', 'S', 'H', 'A', 'R', 'I', 'N', 'G', 'P', 'R', 'O', 'J',
                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,'1', '2', '3', '4' };
        Assert.assertArrayEquals(expectedMessage,message.getFull());
    }

    @Test
    public void mutateIntoRequest_MutatesIntoRequest_BecomesARequestMessage() {
        Message message = new Message();
        message.mutateIntoRequest(42);
        Assert.assertEquals(MessageTypeConstants.REQUEST,message.getmType());
        Assert.assertEquals(42,message.getIndexPayload());
        byte[] expectedMessage = { 0x00,0x00,0x00,0x05, MessageTypeConstants.REQUEST, 0x00, 0x00, 0x00,  0x2A};
        Assert.assertArrayEquals(expectedMessage,message.getFull());
    }

    @Test
    public void mutateIntoBitfield_MutatesIntoBitfield_BecomesABitfieldMessage() {
        Message message = new Message();
        boolean[] expected = {true,false,true,true,false};
        message.mutateIntoBitField(expected);
        Assert.assertEquals(MessageTypeConstants.BITFIELD,message.getmType());
        Assert.assertArrayEquals(expected,message.getBitFieldPayload(expected.length));
        byte[] expectedMessage = { 0x00,0x00,0x00,0x02, MessageTypeConstants.BITFIELD, (byte)0xB0};
        Assert.assertArrayEquals(expectedMessage,message.getFull());
    }

    @Test
    public void mutateIntoPiece_MutatesIntoPiece_BecomesAPieceMessage() {
        Message message = new Message();
        byte[] expectedFilePayload = {(byte)0xDE,(byte)0xED,(byte)0xBE,(byte)0xEF};
        int expectedIndexPayload = 0x12345678;
        message.mutateIntoPiece(new FileChunkImpl(expectedFilePayload), expectedIndexPayload);
        Assert.assertEquals(MessageTypeConstants.PIECE,message.getmType());
        Assert.assertEquals(expectedIndexPayload,message.getIndexPayload());
        Assert.assertArrayEquals(expectedFilePayload,message.getFileChunkPayload().asByteArray());
        // Size of message, type of message, index of piece, piece
        byte[] expectedMessage = { 0x00,0x00,0x00,0x09, MessageTypeConstants.PIECE, 0x12, 0x34, 0x56, 0x78, (byte)0xDE,(byte)0xED,(byte)0xBE,(byte)0xEF};
        Assert.assertArrayEquals(expectedMessage,message.getFull());
    }

    @Test
    public void mutateIntoChoke_MutatesIntoChoke_BecomesAChokeMessage() {
        Message message = new Message();
        message.mutateIntoChoke();
        Assert.assertEquals(MessageTypeConstants.CHOKE,message.getmType());
        byte[] expectedMessage = { 0x00,0x00,0x00,0x01, MessageTypeConstants.CHOKE};
        Assert.assertArrayEquals(expectedMessage,message.getFull());
    }

    @Test
    public void mutateIntoUnChoke_MutatesIntoUnChoke_BecomesAUnChokeMessage() {
        Message message = new Message();
        message.mutateIntoUnChoke();
        Assert.assertEquals(MessageTypeConstants.UNCHOKE,message.getmType());
        byte[] expectedMessage = { 0x00,0x00,0x00,0x01, MessageTypeConstants.UNCHOKE};
        Assert.assertArrayEquals(expectedMessage,message.getFull());
    }

    @Test
    public void mutateIntoInterested_MutatesIntoInterested_BecomesAInterestedMessage() {
        Message message = new Message();
        message.mutateIntoInterested();
        Assert.assertEquals(MessageTypeConstants.INTERESTED,message.getmType());
        byte[] expectedMessage = { 0x00,0x00,0x00,0x01, MessageTypeConstants.INTERESTED};
        Assert.assertArrayEquals(expectedMessage,message.getFull());
    }

    @Test
    public void mutateIntoUnInterested_MutatesIntoUnInterested_BecomesAUnInterestedMessage() {
        Message message = new Message();
        message.mutateIntoUnInterested();
        Assert.assertEquals(MessageTypeConstants.UNINTERESTED,message.getmType());
        byte[] expectedMessage = { 0x00,0x00,0x00,0x01, MessageTypeConstants.UNINTERESTED};
        Assert.assertArrayEquals(expectedMessage,message.getFull());
    }

}