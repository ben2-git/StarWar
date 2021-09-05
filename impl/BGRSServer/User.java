package bgu.spl.net.impl.BGRSServer;

public class User {

    private String username;
    private String password;
    private boolean isLogged;

    public User(){
        this("","");
    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
        isLogged = false;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
    }

    public synchronized boolean login(String password){
        if(isLogged | !(this.password.equals(password)))
            return false;
        isLogged = true;
        return true;
    }

    public boolean logout(){
        if(!isLogged)
            return false;
        isLogged = false;
        return true;
    }
}
