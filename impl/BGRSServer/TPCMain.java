package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.*;

public class TPCMain {
    public static void main(String[] args) {
        Database.getInstance().initialize("Courses.txt");
        int port = Integer.parseInt(args[0]); //getting the correct port
        Server.threadPerClient(port, () -> new BGRSProtocol(), () -> new BGRSEncoderDecoder()).serve();
    }
}
