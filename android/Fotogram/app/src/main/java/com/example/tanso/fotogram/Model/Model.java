package com.example.tanso.fotogram.Model;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.List;

public class Model {

    private static Model instance;
    private static RequestQueue requestQueue;

    private LoggedUser loggedUser;
    private List<Post> homeWall;

    private Model(){}

    public static Model getInstance(){
        if(instance == null)
            instance = new Model();
        return instance;
    }

    public static void destroyInstance(){
        instance = null;
    }

    public static RequestQueue getRequestQueue(Context context){
        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(context);
        return requestQueue;
    }

    public LoggedUser getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(LoggedUser loggedUser) {
        this.loggedUser = loggedUser;
    }

    public List<Post> getHomeWall() {
        return homeWall;
    }

    public void setHomeWall(List<Post> homeWall) {
        this.homeWall = homeWall;
    }
}
