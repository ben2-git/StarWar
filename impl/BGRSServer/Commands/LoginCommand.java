package bgu.spl.net.impl.BGRSServer.Commands;

import bgu.spl.net.impl.BGRSServer.Database;
import bgu.spl.net.impl.BGRSServer.User;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class LoginCommand implements Command<User> {

    private String username;
    private String password;
    private User loggedUser;

    public LoginCommand(String username, String password){
        this.username = username;
        this.password = password;
        loggedUser = null;
    }

    @Override
    public Serializable execute(User arg) {
        if(arg == null & username != null & password != null){ //the client is not already logged in
            User user = Database.getInstance().getUser(username);
            if(user != null) {
                if(user.login(password)){
                    loggedUser = user;
                    return new ACKCommand((short)3);
                }
            }
        }
        return new ERRCommand((short)3);
    }

    public User getUser(){
        return loggedUser;
    }
}
