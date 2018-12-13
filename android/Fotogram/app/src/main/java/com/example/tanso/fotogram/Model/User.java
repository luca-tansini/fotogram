package com.example.tanso.fotogram.Model;

import android.graphics.Bitmap;

public class User {

    private String username;
    private Image profilePicture;

    public User(String username, Image profilePicture) {
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public String getUsername() {
        return username;
    }

    public Image getProfilePicture() {
        return profilePicture;
    }

    public void updateProfilePicture(Image newProfilePicture){
        this.profilePicture = newProfilePicture;
    }

}
