
// Created by shach on 31/12/2020.
//
using namespace std;

#include "../include/ConnectionHandlerBGRS.h"

using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

ConnectionHandlerBGRS::ConnectionHandlerBGRS(string host, short port): isFinished(false), host_(host), port_(port), io_service_(),opcode1(), opcode2(),info(), socket_(io_service_){}

ConnectionHandlerBGRS::~ConnectionHandlerBGRS() {
    close();
}

bool ConnectionHandlerBGRS::connect() {
    std::cout << "Starting connect to "
              << host_ << ":" << port_ << std::endl;
    try {
        tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
        boost::system::error_code error;
        socket_.connect(endpoint, error);
        if (error)
            throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandlerBGRS::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
            tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);
        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandlerBGRS::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandlerBGRS::getLine(std::string& line) {
    if(!isFinished)
        getMessage();
    if(isFinished){
        if(opcode1[1] == 0x0D)
            line = line.append("ERROR ");
        else
            line  = line.append("ACK ");
        switch(opcode2[1]){
            case 0x01:{line = line.append("1"); break;}
            case 0x02:{line = line.append("2"); break;}
            case 0x03:{line = line.append("3"); break;}
            case 0x04:{line = line.append("4"); break;}
            case 0x05:{line = line.append("5"); break;}
            case 0x06:{line = line.append("6"); break;}
            case 0x07:{line = line.append("7"); break;}
            case 0x08:{line = line.append("8"); break;}
            case 0x09:{line = line.append("9"); break;}
            case 0x0A:{line = line.append("10"); break;}
            case 0x0B:{line = line.append("11"); break;}
        }
        if(info.size() > 0)
            line = line.append(1, '\n');
        for(int i=0; i<info.size(); i++)
            line = line.append(1, info[i]);
    }
    isFinished = false;
    opcode1.clear();
    opcode2.clear();
    info.clear();
    return true;
}

bool ConnectionHandlerBGRS::sendLine(std::string& line) {
    short opcode = -1;
    string command = "";
    string bytesToSend;
    int index = 0;
    int arguments = 0;
    for(; index < line.size() && line[index]!=' '; index++)
        command = command.append(1, line[index]); //reading the command
    index++;
    if(opcode == -1 && command.compare("ADMINREG") == 0)
    {opcode = 1; arguments = 2;}
    if(opcode == -1 && command.compare("STUDENTREG") == 0)
    {opcode = 2; arguments = 2;}
    if(opcode == -1 && command.compare("LOGIN") == 0)
    {opcode = 3; arguments = 2;}
    if(opcode == -1 && command.compare("LOGOUT") == 0)
    {opcode = 4; arguments = 0;}
    if(opcode == -1 && command.compare("COURSEREG") == 0)
    {opcode = 5; arguments = -1;}
    if(opcode == -1 && command.compare("KDAMCHECK") == 0)
    {opcode = 6; arguments = -1;}
    if(opcode == -1 && command.compare("COURSESTAT") == 0)
    {opcode = 7; arguments = -1;}
    if(opcode == -1 && command.compare("STUDENTSTAT") == 0)
    {opcode = 8; arguments = 1;}
    if(opcode == -1 && command.compare("ISREGISTERED") == 0)
    {opcode = 9; arguments = -1;}
    if(opcode == -1 && command.compare("UNREGISTER") == 0)
    {opcode = 10; arguments = -1;}
    if(opcode == -1 && command.compare("MYCOURSES") == 0)
    {opcode = 11; arguments = 0;}

    bytesToSend.push_back(0x00);
    bytesToSend.push_back(opcode & 0xFF); //adding the opcode
    switch(arguments) {
        case -1:{
            //the rest of the string is a number
            short coursenum = 0;
            int pwr = 10;
            int pwrNum = line.size() - index - 1;
            if (pwrNum == 0)
                pwr = 1;
            for (; pwrNum > 1; pwrNum--)
                pwr = pwr * 10;
            for (; index < line.size(); index++) {
                coursenum += pwr*(((short)line[index]-48));
                pwr = pwr/10;
            }
            bytesToSend.push_back(coursenum>>8 & 0xFF);
            bytesToSend.push_back(coursenum & 0xFF);}
            break;
        case 1:{ //rest of string is 1 string to send, adding the 0 byte
            for(; index<line.size(); index++)
                bytesToSend.push_back(line[index]);
            bytesToSend.push_back(0x00);
            break;
        }
        case 2:{//rest of string is 2 string to send seperated by ' ', adding the 0 byte
            for(; index<line.size(); index++) {
                if(line[index] == ' ')
                    bytesToSend.push_back(0x00);
                else
                    bytesToSend.push_back(line[index]);
            }
            bytesToSend.push_back(0x00);
        }
    }

    return sendBytes(bytesToSend.c_str(), bytesToSend.length()); //sending the bytes

}

bool ConnectionHandlerBGRS::getMessage() {
    char ch;
    //start by finding the msg opcode
    try {
        do{
            if(!getBytes(&ch, 1))
            {
                return false;
            }
            if(opcode1.size()<2)
                opcode1.push_back(ch);
            else
            if(opcode2.size()<2)
                opcode2.push_back(ch);
            else
            if(ch!='\0')
                info.push_back(ch);
            updateFinished(ch);
        }while (!isFinished);
    } catch (std::exception& e) {
        std::cerr << "recv failed2 (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

void ConnectionHandlerBGRS::updateFinished(char &ch) {
    if(opcode1.size() == 2){
        if(opcode1[1] == 0x0D & opcode2.size() == 2)
            isFinished = true;
        if(opcode1[1] == 0x0C & opcode2.size() == 2 & ch == '\000')
            isFinished = true;
    }
}

// Close down the connection properly.
void ConnectionHandlerBGRS::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}//
// Created by spl211 on 02/01/2021.
//

#include "../include/ConnectionHandlerBGRS.h"
