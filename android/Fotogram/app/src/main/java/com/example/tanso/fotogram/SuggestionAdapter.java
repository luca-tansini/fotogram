package com.example.tanso.fotogram;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tanso.fotogram.Model.Base64Images;
import com.example.tanso.fotogram.Model.User;

import java.util.ArrayList;
import java.util.List;

class SuggestionAdapter extends ArrayAdapter<User> {

    public SuggestionAdapter(Context context, int
            resource, List<User> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.suggestion_entry, null);
        }
        User u = getItem(position);
        if (u != null) {
            ImageView profilePicture = v.findViewById(R.id.suggestionProfilePic);
            if(u.getProfilePicture() != null && u.getProfilePicture().getBitmap() != null)
                profilePicture.setImageDrawable(CircularBitmapDrawableFactory.create(getContext(), u.getProfilePicture().getBitmap()));
            else
                profilePicture.setImageDrawable(CircularBitmapDrawableFactory.create(getContext(), BitmapFactory.decodeResource(getContext().getResources(),R.drawable.user)));
            TextView suggestionUsername = v.findViewById(R.id.suggestionUsername);
            suggestionUsername.setText(u.getUsername());
        }
        return v;
    }
}
