package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.BGRSServer.Commands.*;
import bgu.spl.net.impl.rci.Command;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BGRSEncoderDecoder implements MessageEncoderDecoder<Command<User>> {

    private byte[] bytes;
    private int len;
    private short opcode;
    private enum  MsgType {A, B, C, D};
    private MsgType msgType;
    private Integer courseNum;
    private String username;
    private String password;

    public BGRSEncoderDecoder(){
        clear();
    }

    public void clear(){
        bytes = new byte[256];
        len = 0;
        opcode = -1;
        msgType = MsgType.A;
        courseNum = null;
        username = null;
        password = null;
    }

    @Override
    public Command<User> decodeNextByte(byte nextByte) {
        switch (msgType){
            case A:{
                pushByte(nextByte);
                if(len == 2){
                    opcode = bytesToShort(bytes);
                    msgTypeUpdate();
                    bytes = new byte[256];
                    len = 0;
                }
                break;
            }
            case B:{
                pushByte(nextByte);
                if(len == 2){
                    courseNum = (int)(bytesToShort(bytes));
                    bytes = new byte[256];
                    len = 0;
                }
                break;
            }
            case C:{
                if(nextByte == 0) {
                    username = new String(bytes, 0, len, StandardCharsets.UTF_8);
                    bytes = new byte[256];
                    len = 0;
                }
                else
                    pushByte(nextByte);
                break;
            }
            case D:{
                if(nextByte == 0) {
                    if(username == null)
                        username = new String(bytes, 0, len, StandardCharsets.UTF_8);
                    else
                        password = new String(bytes, 0, len, StandardCharsets.UTF_8);
                    bytes = new byte[256];
                    len = 0;
                }
                else
                    pushByte(nextByte);
                break;
            }
        }
        Command<User> ret = createMsg();
        if(ret != null){
            clear();
        }
        return ret;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    @Override
    public byte[] encode(Command<User> message) {
        byte[] opcode1;
        byte[] opcode2;
        byte[] info = new byte[0];
        byte[] zero = new byte[0];
        if(message instanceof ACKCommand) {
            opcode1 = shortToBytes((short) 12);
            opcode2 = shortToBytes(((ACKCommand) message).getOpcode());
            zero = new byte[1];
            zero[0] = 0x00;
        }
        else {
            opcode1 = shortToBytes((short) 13);
            opcode2 = shortToBytes(((ERRCommand) message).getOpcode());
        }
        if(message instanceof ACKCommand && ((ACKCommand) message).getInfo() != null)
            info = ((ACKCommand) message).getInfo().getBytes();
        int index = 0;
        byte[] msg = new byte[4+info.length+zero.length];
        for(byte b : opcode1)
            msg[index++] = b;
        for(byte b : opcode2)
            msg[index++] = b;
        for(byte b : info)
            msg[index++] = b;
        for(byte b : zero)
            msg[index++] = b;
        return msg;
    }

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    private void msgTypeUpdate(){
        switch (opcode){
            case 1:{msgType = msgType.D; break;}
            case 2:{msgType = msgType.D; break;}
            case 3:{msgType = msgType.D; break;}
            case 8:{msgType = msgType.C; break;}
            case 5:{msgType = msgType.B; break;}
            case 6:{msgType = msgType.B; break;}
            case 7:{msgType = msgType.B; break;}
            case 9:{msgType = msgType.B; break;}
            case 10:{msgType = msgType.B; break;}
        }
    }

    private Command<User> createMsg(){
        switch (opcode) {
            case 1: {
                if(username == null || password == null)
                    return null;
                return new AdminRegisterCommand(username, password);
            }
            case 2: {
                if(username == null || password == null)
                    return null;
                return new StudentRegisterCommand(username, password);
            }
            case 3: {
                if(username == null || password == null)
                    return null;
                return new LoginCommand(username, password);
            }
            case 4: {
                return new LogoutCommand();
            }
            case 5: {
                if(courseNum == null)
                    return null;
                return new CourseRegisterCommand(courseNum);
            }
            case 6: {
                if(courseNum == null)
                    return null;
                return new KdamCheckCommand(courseNum);
            }
            case 7: {
                if(courseNum == null)
                    return null;
                return new CourseStatCommand(courseNum);
            }
            case 8: {
                if(username == null)
                    return null;
                return new StudentStatCommand(username);
            }
            case 9:{
                if(courseNum == null)
                    return null;
                return new IsRegisteredCommand(courseNum);
            }
            case 10:{
                if(courseNum == null)
                    return null;
                return new UnregisterCommand(courseNum);
            }
            case 11:{
                return new MyCoursesCommand();
            }
            default: {
                return null;
            }
        }
    }
}
