package com.example.tanso.fotogram.Model;

import android.graphics.Bitmap;

public class Image {

    private Bitmap bitmap;
    private String hash;

    public Image(String base64img){
        bitmap = Base64Images.base64toBitmap(base64img);
        hash = base64img.substring(0,16<=base64img.length()? 16:base64img.length());
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String hash() {
        return hash;
    }
}
