package com.example.tanso.fotogram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

public class CircularBitmapDrawableFactory {

    public static RoundedBitmapDrawable create(Context context, Bitmap bitmap){
        RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(context.getResources(),SquareBitmapFactory.create(bitmap));
        rbd.setCircular(true);
        return rbd;
    }

    public static RoundedBitmapDrawable create(Context context, int resource){
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),resource);
        RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(context.getResources(),SquareBitmapFactory.create(bitmap));
        rbd.setCircular(true);
        return rbd;
    }

}
