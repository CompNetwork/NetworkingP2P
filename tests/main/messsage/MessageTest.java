package main.messsage;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

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
       Assert.assertEquals(index,ByteArrayUtilities.recombine4ByteArrayIntoInt(message.getM3()));
    }

    @Test
    public void updateFromByteArray_CreateHandShakeMessage_ValidHandshakeMessage() {
        Message message = new Message();
        byte[] handshake = { 'P', '2', 'P', 'F', 'I', 'L', 'E', 'S', 'H', 'A', 'R', 'I', 'N', 'G', 'P', 'R', 'O', 'J'
                            ,'0', '0', '0', '0', '0','0', '0', '0', '0', '0', '1', '2', '3', '4' };
        message.update(handshake);

        Assert.assertEquals(MessageTypeConstants.HANDSHAKE,message.getmType());
        Assert.assertEquals("1234",message.getPeerId());
    }

}