package main.hosts;

import java.io.Serializable;

public class Message implements Serializable {

    private static final long serialVersionID = 132437293456465438l;
    String m1;
    String m2;
    String m3;

    public Message(String text){
        setMs(text);
    }

    public Message(String message1, String message2, String message3) {
        this.m1 = message1;
        this.m2 = message2;
        this.m3 = message3;
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

    private void setMs(String text){
        this.m1 = text.substring(0)+1;
        this.m2 = text.substring(0)+2;
        this.m3 = text.substring(0)+3;
    }

}
