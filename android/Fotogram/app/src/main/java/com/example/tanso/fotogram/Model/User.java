package com.example.tanso.fotogram.Model;

import android.graphics.Bitmap;

public class User {

    private String username;
    private Bitmap profilePicture;

    public User(String username, Bitmap profilePicture) {
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public String getUsername() {
        return username;
    }

    public Bitmap getProfilePicture() {
        return profilePicture;
    }

    public void updateProfilePicture(Bitmap newProfilePicture){
        this.profilePicture = newProfilePicture;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof User)
            return this.getUsername().equals(((User) obj).getUsername());
        else return false;
    }
}
