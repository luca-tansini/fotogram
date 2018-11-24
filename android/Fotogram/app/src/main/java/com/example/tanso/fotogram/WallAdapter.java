package com.example.tanso.fotogram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tanso.fotogram.Model.Post;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

class WallAdapter extends ArrayAdapter<Post> {

    private HashMap<Integer,Bitmap> cache;

    public WallAdapter(Context context, int
            resource, List<Post> items) {
        super(context, resource, items);
        cache = new HashMap<Integer, Bitmap>();
        new ImageLoaderTask(items, cache, context).execute();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.wall_entry, null);
        }
        Post p = getItem(position);
        if (p != null) {

            //Profile picture and Image
            /*
            ImageView profilePicture = v.findViewById(R.id.wallPostProfilePic);
            //Bitmap pic = BitmapFactory.decodeResource(v.getResources(), p.getUser().getProfilePicture());
            profilePicture.setImageBitmap(cache.get(p.getUser().getProfilePicture()));
            //profilePicture.setImageResource(p.getUser().getProfilePicture());
            ImageView image = v.findViewById(R.id.wallPostImage);
            //pic = BitmapFactory.decodeResource(v.getResources(), p.getImage());
            image.setImageBitmap(cache.get(p.getImage()));
            //image.setImageResource(p.getImage());
            */
            new GetViewTask(cache,v,p).execute();

            //Username, description and date
            TextView username = v.findViewById(R.id.wallPostUsername);
            username.setText(p.getUser().getUsername());
            TextView description = v.findViewById(R.id.wallPostDescription);
            description.setText(p.getDescription());
            TextView date = v.findViewById(R.id.wallPostDate);
            date.setText(getDateString(p.getTimestamp()));
        }
        return v;
    }

    private String getDateString(Timestamp t){
        long diff = new Date().getTime() - t.getTime();
        long days = diff / (86400*1000);
        if(days<=7) {
            if (days >= 1) {
                if (days == 1) return "YESTERDAY";
                return days + " DAYS AGO";
            }
            long hours = diff / (3600 * 1000);
            if (hours >= 1) {
                if (hours == 1) return "AN HOUR AGO";
                return hours + " HOURS AGO";
            }
            long minutes = diff / (60 * 1000);
            if (minutes >= 1) {
                if (minutes == 1) return "A MINUTE AGO";
                return minutes + " MINUTES AGO";
            }
            return "MOMENTS AGO";
        }
        return new SimpleDateFormat("dd MMMM yyyy").format(new Date(t.getTime())).toUpperCase();
    }


}

class ImageLoaderTask extends AsyncTask<Void,Void,Void>{

    private List<Post> items;
    private HashMap<Integer,Bitmap> cache;
    private Context context;

    public ImageLoaderTask(List<Post> items, HashMap<Integer,Bitmap> cache, Context context){
        this.items = items;
        this.cache = cache;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for(Post p: items){
            cache.put(p.getImage(),BitmapFactory.decodeResource(context.getResources(),p.getImage()));
            cache.put(p.getUser().getProfilePicture(),BitmapFactory.decodeResource(context.getResources(),p.getUser().getProfilePicture()));
        }
        return null;
    }
}

class GetViewTask extends AsyncTask<Void,Void,Bitmap[]>{

    private HashMap<Integer,Bitmap> cache;
    private View v;
    private Post p;

    public GetViewTask(HashMap<Integer, Bitmap> cache, View v, Post p) {
        this.cache = cache;
        this.v = v;
        this.p = p;
    }

    @Override
    protected Bitmap[] doInBackground(Void... voids) {
        Bitmap bitmaps[] = new Bitmap[]{null,null};

        bitmaps[0] = cache.get(p.getUser().getProfilePicture());
        while(bitmaps[0]==null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bitmaps[0] = cache.get(p.getUser().getProfilePicture());
        }

        bitmaps[1] = cache.get(p.getImage());
        while(bitmaps[1]==null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bitmaps[1] = cache.get(p.getImage());
        }

        return bitmaps;
    }

    @Override
    protected void onPostExecute(Bitmap[] bitmaps) {
        ImageView profilePicture = v.findViewById(R.id.wallPostProfilePic);
        ImageView image = v.findViewById(R.id.wallPostImage);
        profilePicture.setImageBitmap(bitmaps[0]);
        image.setImageBitmap(bitmaps[1]);
    }
}