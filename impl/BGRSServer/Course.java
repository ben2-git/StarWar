package bgu.spl.net.impl.BGRSServer;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeSet;

public class Course {
    private String courseName;
    private LinkedList <Integer> kdamCourses;
    private int capacity;
    private int courseNumber;
    private int courseId; // the place the course show
    private TreeSet <StudentUser> studentList;

    public Course(){
        courseName = "";
        kdamCourses = new LinkedList<>();
        capacity = -1;
        courseNumber = -1;
        courseId = -1;
        studentList = new TreeSet<>(new Comparator<StudentUser>() {
            @Override
            public int compare(StudentUser o1, StudentUser o2) {
                return o1.getUsername().compareTo(o2.getUsername());
            }
        });
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseNumber(int courseNumber) {
        this.courseNumber = courseNumber;
    }
	
	public void kdamSort(){
        TreeSet<Course> sortedCourses = new TreeSet<Course>((Comparator<Course>) (o1, o2) -> o1.getCourseId() - o2.getCourseId());
        for(Integer num: kdamCourses)
            sortedCourses.add(Database.getInstance().getCourse(num));
        kdamCourses = new LinkedList<>();
        for(Course c: sortedCourses)
            kdamCourses.add(c.getCourseNumber());
    }

    public void setKdamCourses(String kdamCourses) {
        kdamCourses = kdamCourses.substring(1, kdamCourses.length()-1);
        for (String curr : kdamCourses.split(",")){
            if(!curr.equals("")) {
                Integer addToList = Integer.parseInt(curr);
                this.kdamCourses.add(addToList);
            }
        }
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() { return capacity;}

    public int getCourseId() { return courseId; }

    public int getCourseNumber() { return courseNumber; }

    public int getCurrentRegister() { return studentList.size(); }

    public String getCourseName() { return courseName; }

    public LinkedList<Integer> getKdamcourses() { return kdamCourses; }

    public String checkKdam (){
        String ret = "[";
	if(kdamCourses.size() == 0)
		ret = ret + " ";
        for(Integer i: kdamCourses)
            ret = ret + i + ",";
        return ret.substring(0,ret.length()-1)+"]"; // add by order return by order
    }

    public synchronized boolean studentRegister (StudentUser studentUser){
        if (!isRegister(studentUser) & studentList.size() < capacity) {
            studentList.add(studentUser);
            return true;
        }
        return false;
    }

    public boolean isRegister (StudentUser studentUser){
        return studentList.contains(studentUser);
    }

    public synchronized boolean studentUnregister (StudentUser studentUser){
        return studentList.remove(studentUser);
    }

    public String courseStat (){
        String ans = "";
        ans = "Course: (" + courseNumber+") "+getCourseName() + "\n";
        ans = ans + "Seats Available: " + (getCapacity() - getCurrentRegister()) + '/' + getCapacity() + '\n';
        ans = ans + "Students Registered: [";
        for (StudentUser studentUser : studentList){
            ans = ans + studentUser.getUsername()+", "; // to add method
        }
	if(studentList.size() == 0)
		ans = ans + "  ";
        return ans.substring(0, ans.length()-2)+"]";
    }
}
