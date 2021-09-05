package bgu.spl.net.impl.BGRSServer.Commands;

import bgu.spl.net.impl.BGRSServer.Course;
import bgu.spl.net.impl.BGRSServer.Database;
import bgu.spl.net.impl.BGRSServer.StudentUser;
import bgu.spl.net.impl.BGRSServer.User;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class KdamCheckCommand implements Command<User> {

    private Integer courseNum;

    public KdamCheckCommand(Integer courseNum){
        this.courseNum = courseNum;
    }

    @Override
    public Serializable execute(User user) {
        if(user != null & courseNum != null & user instanceof StudentUser){
            Course course = Database.getInstance().getCourse(courseNum);
            if(course != null)
                return new ACKCommand((short)6, course.checkKdam());
        }
        return new ERRCommand((short)6);
    }
}
