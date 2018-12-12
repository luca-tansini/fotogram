package com.example.tanso.fotogram.Model;

import java.util.List;

public class LoggedUser extends User {

    private String sessionId;
    //TODO: sostituire con hashmap nome,user
    private List<User> following;

    public LoggedUser(String username, String profilePicture, String sessionId, List<User> following) {
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
