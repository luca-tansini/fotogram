package com.example.tanso.fotogram;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class WallAdapter extends ArrayAdapter<Post> {

    public WallAdapter(Context context, int
            resource, List<Post> items) {
        super(context, resource, items);
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
            TextView username = v.findViewById(R.id.wallPostUsername);
            username.setText(p.getUser().getUsername());
            ImageView profilePicture = v.findViewById(R.id.wallPostProfilePic);
            profilePicture.setImageResource(p.getUser().getProfilePicture());
            ImageView image = v.findViewById(R.id.wallPostImage);
            image.setImageResource(p.getImage());
            TextView description = v.findViewById(R.id.wallPostDescription);
            description.setText(p.getDescription());
            TextView date = v.findViewById(R.id.wallPostDate);
            date.setText(p.getDate());
        }
        return v;
    }
}
