package com.example.tanso.fotogram.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Base64Images {

    //max size for a post img (100KB)
    public static final int POST_IMG_LIMIT = 100000;

    //max size for a profile picture (10KB)
    public static final int PROFILE_PICTURE_LIMIT = 10000;

    public static Bitmap base64toBitmap(String base64img){
        try {
            byte[] imgBytes = Base64.decode(base64img, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
            if (bitmap == null)
                Log.d("ajeje", "couldn't decode image!");
            return bitmap;
        } catch (IllegalArgumentException e){
            Log.d("ajeje", "couldn't decode image (Bad base64)!");
            return null;
        }
    }

    public static String bitmaptoBase64(Bitmap img){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] imgBytes = stream.toByteArray();
        try{
            stream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return Base64.encodeToString(imgBytes, 0, imgBytes.length, Base64.DEFAULT);
    }
}
