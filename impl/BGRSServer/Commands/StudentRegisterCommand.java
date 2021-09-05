package bgu.spl.net.impl.BGRSServer.Commands;

import bgu.spl.net.impl.BGRSServer.Database;
import bgu.spl.net.impl.BGRSServer.User;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class StudentRegisterCommand implements Command<User> {

    private String username;
    private String password;

    public StudentRegisterCommand(String username, String password){
        this.password = password;
        this.username = username;
    }

    @Override
    public Serializable execute(User user) {
        if(user == null) {
            boolean success = Database.getInstance().register(username, password, false);
            if (success)
                return new ACKCommand((short) 2);
        }
        return new ERRCommand((short)2);
    }
}
