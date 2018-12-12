package com.example.tanso.fotogram;

import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tanso.fotogram.Model.Model;
import com.example.tanso.fotogram.Model.Post;
import com.example.tanso.fotogram.Model.User;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //recupera nome utente dall'Intent
        String username = getIntent().getExtras().getString("username");
        //chiamata REST profile()
        Model model = Model.getInstance();
        User u=null;
        for(User usr: model.getLoggedUser().getFollowing()){
            if(usr.getUsername().equals(username)){
                u = usr;
                break;
            }
        }
        ArrayList<Post> userWall = new ArrayList<Post>();
        for(Post p: model.getHomeWall()){
            if(p.getUser().equals(u))
                userWall.add(p);
        }

        //Setta nome utente, immagine profilo e bottone follow
        TextView textViewusername = findViewById(R.id.textViewUsername);
        textViewusername.setText(username);
        ImageView imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture);
        RoundedBitmapDrawable rbd = CircularBitmapDrawableFactory.create(getApplicationContext(),Base64Images.base64toBitmap(u.getProfilePicture()));
        imageViewProfilePicture.setImageDrawable(rbd);
        Button buttonFollow = findViewById(R.id.buttonFollow);
        if(model.getLoggedUser().getFollowing().contains(u)){
            buttonFollow.setText("UNFOLLOW");
            buttonFollow.setBackgroundTintList(getResources().getColorStateList(R.color.colorUnfollow));
        }
        else{
            buttonFollow.setText("FOLLOW");
            buttonFollow.setBackgroundTintList(getResources().getColorStateList(R.color.colorFollow));
        }

        //Listview
        ListView lv = findViewById(R.id.userWall);
        WallAdapter adapter = new WallAdapter(getApplicationContext(), R.layout.wall_entry, userWall);
        lv.setAdapter(adapter);

    }
}
