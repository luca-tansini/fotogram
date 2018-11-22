package com.example.tanso.fotogram;

class Post {

    private User user;
    private String date,description;
    private int image;

    Post(User user, int image, String description, String date) {
        this.user = user;
        this.image = image;
        this.description = description;
        this.date = date.toUpperCase();
    }

    public User getUser() {
        return user;
    }

    public int getImage() {
        return image;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
