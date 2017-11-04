package main.messsage;

import main.file.FileChunkImpl;
import org.junit.Assert;
import org.junit.Test;

public class MessageTest {

    @Test
    public void updateFromByteArray_CreateHaveMessage_ValidHaveMessageWithCorrectPayload() {
       Message message = new Message();

       byte[] m1 = { 0x00, 0x00, 0x00, 0x04 };
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
                            ,'0', '0', '0', '0', '0','0', '0', '0', '0', '0', '1', '2', '3', '4' };
        message.update(handshake);

        Assert.assertEquals(MessageTypeConstants.HANDSHAKE,message.getmType());
        Assert.assertEquals("1234",message.getPeerIdPayload());
    }

    @Test
    public void BytesRemainingInMessageFromHeader_HandShakeMessage_Get27() {
        byte[] header = {0x00, 0x00, 0x00, 0x00, 'I'};
        Assert.assertEquals(27,Message.BytesRemainingInMessageFromHeader(header));
    }

    @Test
    public void BytesRemainingInMessageFromHeader_HaveMessage_GetPayloadLength() {
        byte[] header = {0x00, 0x00, 0x12, 0x34, MessageTypeConstants.HAVE};
        Assert.assertEquals(0x00001234,Message.BytesRemainingInMessageFromHeader(header));
    }

    @Test
    public void mutateIntoHave_MutatesIntoHave_BecomesAHaveMessage() {
        Message message = new Message();
        message.mutateIntoHave(42);
        Assert.assertEquals(MessageTypeConstants.HAVE,message.getmType());
        Assert.assertEquals(42,message.getIndexPayload());
    }

    @Test
    public void mutateIntoHandShake_MutatesIntoHandShake_BecomesAHandShakeMessage() {
        Message message = new Message();
        message.mutateIntoHandshake("1234");
        Assert.assertEquals(MessageTypeConstants.HANDSHAKE,message.getmType());
        Assert.assertEquals("1234",message.getPeerIdPayload());
    }

    @Test
    public void mutateIntoRequest_MutatesIntoRequest_BecomesARequestMessage() {
        Message message = new Message();
        message.mutateIntoRequest(42);
        Assert.assertEquals(MessageTypeConstants.REQUEST,message.getmType());
        Assert.assertEquals(42,message.getIndexPayload());
    }

    @Test
    public void mutateIntoBitfield_MutatesIntoBitfield_BecomesABitfieldMessage() {
        Message message = new Message();
        boolean[] expected = {true,false,true,true,false};
        message.mutateIntoBitField(expected);
        Assert.assertEquals(MessageTypeConstants.BITFIELD,message.getmType());
        Assert.assertArrayEquals(expected,message.getBitFieldPayload(expected.length));
    }

    @Test
    public void mutateIntoPiece_MutatesIntoPiece_BecomesAPieceMessage() {
        Message message = new Message();
        byte[] expected = {(byte)0xDE,(byte)0xED,(byte)0xBE,(byte)0xEF};
        message.mutateIntoPiece(new FileChunkImpl(expected));
        Assert.assertEquals(MessageTypeConstants.PIECE,message.getmType());
        Assert.assertArrayEquals(expected,message.getFileChunkPayload().asByteArray());
    }

    @Test
    public void mutateIntoChoke_MutatesIntoChoke_BecomesAChokeMessage() {
        Message message = new Message();
        message.mutateIntoChoke();
        Assert.assertEquals(MessageTypeConstants.CHOKE,message.getmType());
    }

    @Test
    public void mutateIntoUnChoke_MutatesIntoUnChoke_BecomesAUnChokeMessage() {
        Message message = new Message();
        message.mutateIntoUnChoke();
        Assert.assertEquals(MessageTypeConstants.UNCHOKE,message.getmType());
    }

    @Test
    public void mutateIntoInterested_MutatesIntoInterested_BecomesAInterestedMessage() {
        Message message = new Message();
        message.mutateIntoInterested();
        Assert.assertEquals(MessageTypeConstants.INTERESTED,message.getmType());
    }

    @Test
    public void mutateIntoUnInterested_MutatesIntoUnInterested_BecomesAUnInterestedMessage() {
        Message message = new Message();
        message.mutateIntoUnInterested();
        Assert.assertEquals(MessageTypeConstants.UNINTERESTED,message.getmType());
    }

}