package com.example.tanso.fotogram;

class User {

    private String username;
    private int profilePicture;

    public User(String username, int profilePicture) {
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public String getUsername() {
        return username;
    }

    public int getProfilePicture() {
        return profilePicture;
    }

    public void updateProfilePicture(int newProfilePicture){
        this.profilePicture = newProfilePicture;
    }

}
