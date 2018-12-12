package com.example.tanso.fotogram;

import android.content.Context;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tanso.fotogram.Model.User;

import java.util.ArrayList;
import java.util.List;

class SuggestionAdapter extends ArrayAdapter<User> {

    private ArrayList<User> items;
    private ArrayList<User> suggestions;

    public SuggestionAdapter(Context context, int
            resource, List<User> items) {
        super(context, resource, items);
        this.items = new ArrayList<User>(items);
        this.suggestions = new ArrayList<User>();
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
            RoundedBitmapDrawable rbd = CircularBitmapDrawableFactory.create(getContext(),Base64Images.base64toBitmap(u.getProfilePicture()));
            profilePicture.setImageDrawable(rbd);
            TextView suggestionUsername = v.findViewById(R.id.suggestionUsername);
            suggestionUsername.setText(u.getUsername());
        }
        return v;
    }

    public void setItems(ArrayList<User> items) {
        this.items = items;
    }

    @Override
    public android.widget.Filter getFilter() {
        return nameFilter;
    }

    private android.widget.Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((User)(resultValue)).getUsername();
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (User u : items) {
                    if(u.getUsername().toLowerCase().startsWith(constraint.toString().toLowerCase())){
                        suggestions.add(u);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<User> filteredList = (ArrayList<User>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (User u : filteredList) {
                    add(u);
                }
                notifyDataSetChanged();
            }
        }
    };
}
