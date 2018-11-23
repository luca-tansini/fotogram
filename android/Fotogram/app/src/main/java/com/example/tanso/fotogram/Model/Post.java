package com.example.tanso.fotogram.Model;

import java.sql.Timestamp;

public class Post {

    private User user;
    private Timestamp timestamp;
    private String description;
    private int image;

    public Post(User user, int image, String description, Timestamp timestamp) {
        this.user = user;
        this.image = image;
        this.description = description;
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public int getImage() {
        return image;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

}
