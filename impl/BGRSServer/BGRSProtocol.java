package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.BGRSServer.Commands.ACKCommand;
import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.impl.BGRSServer.Commands.LoginCommand;
import bgu.spl.net.impl.BGRSServer.Commands.LogoutCommand;

public class BGRSProtocol implements MessagingProtocol<Command<User>> {

    private User user;
    private boolean shouldTerminate;

    public BGRSProtocol(){
        user = null;
        shouldTerminate = false;
    }

    @Override
    public Command<User> process(Command<User> msg) {
        Command<User> response = (Command<User>)msg.execute(user);
        if(response instanceof ACKCommand) {
            shouldTerminate = msg instanceof LogoutCommand;
            if(msg instanceof LoginCommand)
                user = ((LoginCommand) msg).getUser();
        }
        return response;
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
