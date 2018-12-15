package com.example.tanso.fotogram.Model;

import android.graphics.Bitmap;

import java.sql.Timestamp;

//TODO:refactoring per usare GSON?
public class Post {

    private User user;
    private Timestamp timestamp;
    private String description;
    private Bitmap image;

    public Post(User user, Bitmap image, String description, Timestamp timestamp) {
        this.user = user;
        this.image = image;
        this.description = description;
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public Bitmap getImage() {
        return image;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

}
