package bgu.spl.net.impl.BGRSServer.Commands;

import bgu.spl.net.impl.BGRSServer.Course;
import bgu.spl.net.impl.BGRSServer.Database;
import bgu.spl.net.impl.BGRSServer.StudentUser;
import bgu.spl.net.impl.BGRSServer.User;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;


public class UnregisterCommand implements Command<User> {
    private Integer courseNum;
    public UnregisterCommand(Integer courseNum){
        this.courseNum = courseNum;
    }
    @Override

    public Serializable execute(User arg) {
        if (arg instanceof StudentUser & courseNum != null) {
            Course course = Database.getInstance().getCourse(courseNum);
            if(course!=null && ((StudentUser) arg).courseUnregister(course)){
                return new ACKCommand((short) 10);
            }
        }
        return new ERRCommand((short)10);
    }

}