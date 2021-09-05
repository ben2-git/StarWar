package bgu.spl.net.impl.BGRSServer.Commands;

import bgu.spl.net.impl.BGRSServer.Course;
import bgu.spl.net.impl.BGRSServer.Database;
import bgu.spl.net.impl.BGRSServer.StudentUser;
import bgu.spl.net.impl.BGRSServer.User;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class IsRegisteredCommand implements Command<User> {
    private Integer courseNum;
    public IsRegisteredCommand(Integer courseNum){
        this.courseNum = courseNum;
    }
    @Override
    public Serializable execute(User arg) {
        if(arg instanceof StudentUser && courseNum!=null){
            if(((StudentUser) arg).registeredToCourse(courseNum))
                return new ACKCommand((short)9, "REGISTERED");
	    return new ACKCommand((short)9, "NOT REGISTERED");
        }
        return new ERRCommand((short)9);
    }
}
