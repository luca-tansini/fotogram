package com.example.tanso.fotogram.Model;

public class User {

    private String username;
    private String profilePicture;

    public User(String username, String profilePicture) {
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void updateProfilePicture(String newProfilePicture){
        this.profilePicture = newProfilePicture;
    }

}
