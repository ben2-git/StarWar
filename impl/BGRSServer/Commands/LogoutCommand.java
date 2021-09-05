package bgu.spl.net.impl.BGRSServer.Commands;

import bgu.spl.net.impl.BGRSServer.User;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class LogoutCommand implements Command<User> {

    @Override
    public Serializable execute(User user) {
        if(user != null){
            if(user.logout())
                return new ACKCommand((short)4);
        }
        return new ERRCommand((short)4);
    }
}
