package com.example.tanso.fotogram;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
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

    private HashMap<String,View> cache;

    public WallAdapter(Context context, int
            resource, List<Post> items) {
        super(context, resource, items);
        this.cache = new HashMap<>();
    }

    //getView protocol has been overridden because
    //recycling view for images with different sizes
    //caused the list to twitch
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Post p = getItem(position);
        if (p != null) {
            View v = cache.get(p.getUser().getUsername()+p.getTimestamp().toString());
            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.wall_entry, null);
                cache.put(p.getUser().getUsername()+p.getTimestamp().toString(), v);
            }

            new GetViewTask(v,p,getContext()).execute();

            //Username, description and date
            TextView username = v.findViewById(R.id.wallPostUsername);
            username.setText(p.getUser().getUsername());
            TextView description = v.findViewById(R.id.wallPostDescription);
            description.setText(p.getDescription());
            TextView date = v.findViewById(R.id.wallPostDate);
            date.setText(getDateString(p.getTimestamp()));
            return v;
        }
        //Don't know how we get here but something bad will happen
        Log.d("ajeje", "WallAdpter: NEVER GET HERE WARNING");
        return null;
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

class GetViewTask extends AsyncTask<Void,Void,Drawable[]>{

    private Context context;
    private View v;
    private Post p;

    public GetViewTask(View v, Post p, Context context) {
        this.v = v;
        this.p = p;
        this.context = context;
    }

    @Override
    protected Drawable[] doInBackground(Void... voids) {
        Drawable pics[] = new Drawable[]{null,null};
        if(p.getUser().getProfilePicture() != null && p.getUser().getProfilePicture() != null)
            pics[0] = CircularBitmapDrawableFactory.create(context, p.getUser().getProfilePicture());
        else
            pics[0] = CircularBitmapDrawableFactory.create(context, BitmapFactory.decodeResource(context.getResources(),R.drawable.user_round_256));
        if(p.getImage() != null && p.getImage() != null)
            pics[1] = new BitmapDrawable(context.getResources(), p.getImage());
        else
            pics[1] = new BitmapDrawable(context.getResources(), BitmapFactory.decodeResource(context.getResources(),R.drawable.dummy_post_xs));
        return pics;
    }

    @Override
    protected void onPostExecute(Drawable[] pics) {
        ImageView profilePicture = v.findViewById(R.id.wallPostProfilePic);
        profilePicture.setImageDrawable(pics[0]);
        ImageView image = v.findViewById(R.id.wallPostImage);
        image.setImageDrawable(pics[1]);
    }
}