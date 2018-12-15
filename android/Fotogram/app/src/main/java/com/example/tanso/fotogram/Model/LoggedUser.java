package com.example.tanso.fotogram.Model;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.List;

public class LoggedUser extends User {

    private String sessionId;
    private HashMap<String,User> following;

    public LoggedUser(String username, Bitmap profilePicture, String sessionId, HashMap<String,User> following) {
        super(username, profilePicture);
        this.sessionId = sessionId;
        this.following = following;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setFollowing(HashMap<String,User> following) {
        this.following = following;
    }

    public HashMap<String,User> getFollowing() {
        return following;
    }

    public int getFollowingCount(){
        return following.size();
    }
}
