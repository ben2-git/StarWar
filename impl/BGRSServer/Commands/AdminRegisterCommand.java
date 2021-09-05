package bgu.spl.net.impl.BGRSServer.Commands;
import bgu.spl.net.impl.BGRSServer.Database;
import bgu.spl.net.impl.BGRSServer.User;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class AdminRegisterCommand implements Command<User> {

    private String username;
    private String password;

    public AdminRegisterCommand(String username, String password){
        this.password = password;
        this.username = username;
    }

    @Override
    public Serializable execute(User user) {
        if(user == null) {
            boolean success = Database.getInstance().register(username, password, true);
            if (success)
                return new ACKCommand((short) 1);
        }
        return new ERRCommand((short)1);
    }
}
