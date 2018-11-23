package com.example.tanso.fotogram.Model;

import java.util.HashMap;
import java.util.List;

public class Model {

    private static Model instance;

    private LoggedUser loggedUser;
    private List<Post> homeWall;
    private List<Post> loggeUserProfileWall;

    public Model(LoggedUser loggedUser, List<Post> homeWall, List<Post> loggeUserProfileWall) {
        this.loggedUser = loggedUser;
        this.homeWall = homeWall;
        this.loggeUserProfileWall = loggeUserProfileWall;
    }

    public static Model getInstance() {
        return instance;
    }

    public static void buildInstance(LoggedUser loggedUser, List<Post> homeWall, List<Post> loggeUserProfileWall){
        if(instance == null)
            instance = new Model(loggedUser, homeWall, loggeUserProfileWall);
        return;
    }

    public static void destroyInstance(){
        instance = null;
    }

    public LoggedUser getLoggedUser() {
        return loggedUser;
    }

    public List<Post> getHomeWall() {
        return homeWall;
    }

    public List<Post> getLoggeUserProfileWall() {
        return loggeUserProfileWall;
    }

}
