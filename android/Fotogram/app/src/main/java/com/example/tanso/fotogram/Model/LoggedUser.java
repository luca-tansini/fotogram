package com.example.tanso.fotogram.Model;

import android.graphics.Bitmap;

import java.util.List;

public class LoggedUser extends User {

    private String sessionId;
    //TODO: sostituire con hashmap nome,user
    private List<User> following;

    public LoggedUser(String username, Image profilePicture, String sessionId, List<User> following) {
        super(username, profilePicture);
        this.sessionId = sessionId;
        this.following = following;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setFollowing(List<User> following) {
        this.following = following;
    }

    public List<User> getFollowing() {
        return following;
    }

    public int getFollowingCount(){
        return following.size();
    }
}
