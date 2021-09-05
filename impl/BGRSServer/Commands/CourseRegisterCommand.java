package bgu.spl.net.impl.BGRSServer.Commands;

import bgu.spl.net.impl.BGRSServer.Database;
import bgu.spl.net.impl.BGRSServer.StudentUser;
import bgu.spl.net.impl.BGRSServer.User;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class CourseRegisterCommand implements Command<User> {

    private Integer courseNum;

    public CourseRegisterCommand(Integer courseNum){
        this.courseNum = courseNum;
    }

    @Override
    public Serializable execute(User arg) {
        if(arg != null && arg instanceof StudentUser & courseNum != null){
            if(((StudentUser) arg).courseRegister(Database.getInstance().getCourse(courseNum)))
                return new ACKCommand((short)5);
        }
        return new ERRCommand((short)5);
    }
}
