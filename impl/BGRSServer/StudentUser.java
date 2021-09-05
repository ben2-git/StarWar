package bgu.spl.net.impl.BGRSServer;

import java.util.Comparator;
import java.util.TreeSet;

public class StudentUser extends User{

    private TreeSet<Course> registeredCourses;

    public StudentUser(String username, String password){
        super(username, password);
        registeredCourses = new TreeSet<>(new Comparator<Course>() {
            @Override
            public int compare(Course o1, Course o2) {
                return o1.getCourseId() - o2.getCourseId();
            }
        });
    }

    public boolean registeredToCourse(Integer courseNum){
        for(Course course : registeredCourses)
            if(course.getCourseNumber() == courseNum)
                return true;
        return false;
    }

    private boolean checkKdam(Course course){
        if(course == null)
            return false;
        for(Integer kdam : course.getKdamcourses())
            if(!registeredToCourse(kdam))
                return false;
        return true;
    }

    public boolean courseRegister(Course course){
        if(!checkKdam(course) || !course.studentRegister(this))
            return false;
        registeredCourses.add(course);
        return true;
    }

    public boolean courseUnregister(Course course){
        if(course == null)
            return false;
	course.studentUnregister(this);
        return registeredCourses.remove(course);
    }

    public String studentStat(){
        String ret = "";
        ret += "Student: "+getUsername()+"\n";
        ret += "Courses: "+myCourses();
        return ret;
    }

    public String myCourses(){
        String ret = "[";
	if(registeredCourses.size() == 0)
	    ret += " ";
        for(Course course : registeredCourses)
            ret += course.getCourseNumber()+",";
        return ret.substring(0, ret.length()-1)+"]";
    }
}
