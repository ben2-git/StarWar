package bgu.spl.net.impl.BGRSServer.Commands;

import bgu.spl.net.impl.BGRSServer.User;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class ACKCommand implements Command<User> {

    private short opcode;
    private String info;

    public ACKCommand(short opcode, String info){
        this.opcode = opcode;
        this.info = info;
    }

    public ACKCommand(short opcode){
        this(opcode, null);
    }

    public short getOpcode() {
        return opcode;
    }

    public String getInfo(){
        return info;
    }

    @Override
    public Serializable execute(User arg) {
        return null;
    }
}