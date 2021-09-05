package bgu.spl.net.impl.BGRSServer.Commands;

import bgu.spl.net.impl.BGRSServer.User;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class ERRCommand implements Command<User> {

    private short opcode;

    public ERRCommand(short opcode){
        this.opcode = opcode;
    }

    public short getOpcode() {
        return opcode;
    }

    @Override
    public Serializable execute(User arg) {
        return null;
    }
}
