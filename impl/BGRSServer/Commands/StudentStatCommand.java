package bgu.spl.net.impl.BGRSServer.Commands;

import bgu.spl.net.impl.BGRSServer.Database;
import bgu.spl.net.impl.BGRSServer.StudentUser;
import bgu.spl.net.impl.BGRSServer.User;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class StudentStatCommand implements Command<User> {

    private String username;

    public StudentStatCommand(String username){
        this.username = username;
    }
    @Override
    public Serializable execute(User arg) {
        if(arg != null & !(arg instanceof StudentUser) & username != null){
            User user = Database.getInstance().getUser(username);
            if(user instanceof StudentUser)
                return new ACKCommand((short)8, ((StudentUser) user).studentStat());
        }
        return new ERRCommand((short)8);
    }
}
