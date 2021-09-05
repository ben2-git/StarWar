package bgu.spl.net.impl.BGRSServer;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
	/**
	 * Passive object representing the Database where all courses and users are stored.
	 * <p>
	 * This class must be implemented safely as a thread-safe singleton.
	 * You must not alter any of the given public methods of this class.
	 * <p>
	 * You can add private fields and methods to this class as you see fit.
	 */

	private ConcurrentHashMap<Integer, Course> coursesMap;
	private ConcurrentHashMap<String, User> usersMap;
	//to prevent user from creating new Database

	private Database() {
		coursesMap = new ConcurrentHashMap<>();
		usersMap = new ConcurrentHashMap<>();
		// TODO: implement
	}

	private static class DatabaseHolder{
		private static volatile Database instance = new Database();
	}

	public static Database getInstance() {
		return Database.DatabaseHolder.instance;
	} // correct singelton

	public User getUser(String username){
		return usersMap.get(username);
	}

	public Course getCourse(Integer courseNum){
		return coursesMap.get(courseNum);
	}

	public boolean register(String username, String password, boolean isAdmin){
		if(username == null || password == null || username == "" || password == "")
			return false;
		User previous = null;
		if (isAdmin)
			previous = usersMap.putIfAbsent(username, new User(username, password));
		else
			previous = usersMap.putIfAbsent(username, new StudentUser(username, password));
		return (previous == null);
	}

	public boolean unregister(User user){
		return usersMap.remove(user.getUsername(), user);
	}


	public boolean initialize(String coursesFilePath) {
		BufferedReader reader;

		try{
			reader = new BufferedReader(new FileReader(coursesFilePath));
			String line = reader.readLine();
			int i = 0;
			while (line != null){
				Course newCourse = new Course();
				Integer setCourseNum = null;
				Integer setMaxStusent = null;
				newCourse.setCourseId(i);
				i++;
				Integer x=0;
				int sum = 0;
				String curr = "";

				// read the number of course
				while (x<line.length()){
					curr = curr + line.charAt(x);
					if (line.charAt(x+1)=='|' && sum  == 0){
						setCourseNum = Integer.parseInt(curr);
						newCourse.setCourseNumber(setCourseNum);
						curr = "";
						sum =1;
						x = x+2;
						continue;
					}
					if (line.charAt(x+1)=='|' && sum  == 1){
						newCourse.setCourseName(curr);
						curr= "";
						x=x+2;
						sum =2;
						continue;
					}
					if (line.charAt(x+1)=='|' && sum  == 2) {
						newCourse.setKdamCourses(curr);
						x=x+2;
						curr = "";
						break;
					}
					x=x+1;
				}
				// read the max student
				while (x<line.length()) {
					curr = curr + line.charAt(x);
					x=x+1;
				}
				setMaxStusent = Integer.parseInt(curr);
				newCourse.setCapacity(setMaxStusent);
				// finish to set the course
				coursesMap.put(newCourse.getCourseNumber(), newCourse);
				line = reader.readLine();
			}
		}catch (IOException e){return false;}

		// all the course were add to the LL
		//er need to sort all of them
		for(Course c : coursesMap.values())
			c.kdamSort();
		return true;
	}

}
