import main.hosts.Peer;

import java.io.FileNotFoundException;

public class MultiTest {
    public static void main (String [] args) {
        String homeDirectory = System.getProperty("user.home");
        for ( int i = 1; i != 4; ++i ) {
            int finalI = i;
            new Thread(new Runnable() {
                final int i = finalI;
                @Override
                public void run() {
                    String currentPrefix = homeDirectory+"/p2ptest/t"+i+"/";
                    System.setProperty("user.dir", currentPrefix);
                    Peer p = null;
                    try {
                        p = new Peer(1000+i, "127.0.0.1", 8080+i,currentPrefix);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    p.start();
                }
            }).start();
        }
    }
}
