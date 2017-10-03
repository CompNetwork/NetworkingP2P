public class Main {
    public static void main(String [] args) {

        Peers.Peer s = new Peers.Peer("1001","127.0.0.1", 8080);
        s.startServer();
    }
}
