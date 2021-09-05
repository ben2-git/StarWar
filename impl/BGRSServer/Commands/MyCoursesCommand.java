package bgu.spl.net.impl.BGRSServer.Commands;

import bgu.spl.net.impl.BGRSServer.StudentUser;
import bgu.spl.net.impl.BGRSServer.User;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class MyCoursesCommand implements Command<User> {
    @Override
    public Serializable execute(User arg) {
        if (arg != null & arg instanceof StudentUser){
            return new ACKCommand((short)11, ((StudentUser) arg).myCourses());
        }
        return new ERRCommand((short)11);
    }
}