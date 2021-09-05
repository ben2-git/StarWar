#include <stdlib.h>
#include <mutex>
#include <thread>
#include "../include/ConnectionHandlerBGRS.h"

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/

class IOTask{
private:
    ConnectionHandlerBGRS & _ch;
public:
    IOTask (ConnectionHandlerBGRS &ch) :  _ch(ch){}

    void run(){
        while(true) {
            const short bufsize = 1024;
            char buf[bufsize];
            std::cin.getline(buf, bufsize);
            std::string line(buf);
            if (!_ch.sendLine(line)) {
                //std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
        }
    }
};

int main (int argc, char *argv[]) {

    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    ConnectionHandlerBGRS connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    //std::cout<<"SUCCESSFULLY CONNECTED"<<std::endl;
    IOTask task( connectionHandler);
    std::thread th(&IOTask::run, &task);
    while (1) {
        const short bufsize = 1024;
        std::string answer;
        // Get back an answer: by using the expected number of bytes (len bytes + newline delimiter)
        // We could also use: connectionHandler.getline(answer) and then get the answer without the newline char at the end
        if (!connectionHandler.getLine(answer)) {
            //std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        // A C string must end with a 0 char delimiter.  When we filled the answer buffer from the socket
        // we filled up to the \n char - we must make sure now that a 0 char is also present. So we truncate last character.
        std::cout << answer << std::endl;
        if (answer.compare( "ACK 4") == 0) {
            //std::cout << "Exiting...\n";
            th.detach();
            break;
        }
    }
    return 0;
}
