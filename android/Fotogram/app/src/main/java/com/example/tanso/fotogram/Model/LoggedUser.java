package com.example.tanso.fotogram.Model;

import java.util.List;

public class LoggedUser extends User {

    private String sessionId;
    private List<User> following;

    public LoggedUser(String username, int profilePicture, String sessionId, List<User> following) {
        super(username, profilePicture);
        this.sessionId = sessionId;
        this.following = following;
    }

    public String getSessionId() {
        return sessionId;
    }

    public List<User> getFollowing() {
        return following;
    }

    public int getFollowingCount(){
        return following.size();
    }
}
