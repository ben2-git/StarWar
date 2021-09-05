package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.*;

public class ReactorMain {
    public static void main(String[] args) {
        Database.getInstance().initialize("Courses.txt");
        int port = Integer.parseInt(args[0]); //getting the correct port
        int numOfThreads = Integer.parseInt(args[1]); //getting num of threads
        new Reactor(numOfThreads, port,
                () -> new BGRSProtocol(),
                () -> new BGRSEncoderDecoder())
                .serve();
    }

}
