package bgu.spl.net.impl.BGRSServer.Commands;

import bgu.spl.net.impl.BGRSServer.Course;
import bgu.spl.net.impl.BGRSServer.Database;
import bgu.spl.net.impl.BGRSServer.StudentUser;
import bgu.spl.net.impl.BGRSServer.User;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class CourseStatCommand implements Command<User> {

    private Integer courseNum;

    public CourseStatCommand(Integer courseNum){
        this.courseNum = courseNum;
    }

    @Override
    public Serializable execute(User user) {
        if(user!= null & !(user instanceof StudentUser) & courseNum != null){
            Course course = Database.getInstance().getCourse(courseNum);
            if(course != null)
                return new ACKCommand((short)7, course.courseStat());
        }
        return new ERRCommand((short)7);
    }
}
