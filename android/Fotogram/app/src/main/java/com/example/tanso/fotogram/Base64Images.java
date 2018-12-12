package com.example.tanso.fotogram;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class Base64Images {

    public static Bitmap base64toBitmap(String base64img){
        byte[] imgBytes = Base64.decode(base64img, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        if(bitmap == null)
            Log.d("ajeje", "couldn't decode image!");
        return bitmap;
    }

}
