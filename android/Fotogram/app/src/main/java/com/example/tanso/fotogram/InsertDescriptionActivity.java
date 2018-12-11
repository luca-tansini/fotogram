package com.example.tanso.fotogram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.tanso.fotogram.Model.Model;

public class InsertDescriptionActivity extends AppCompatActivity {

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_description);

        //Toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Create post - set description");
        myToolbar.setTitleTextAppearance(this,R.style.upload_toolbar_text);

        //Set the image
        Uri imageUri = Uri.parse(getIntent().getExtras().getString("img"));
        ImageView iv = findViewById(R.id.imageViewUpload);
        try{
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            iv.setImageBitmap(bitmap);
        } catch(Exception e){
            e.printStackTrace();
        }

        //Make post button
        final Button uploadButton = findViewById(R.id.buttonCreatePost);
        uploadButton.setEnabled(false);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePost();
            }
        });

        //Set editText listener
        EditText tv = findViewById(R.id.editTextDescription);
        tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(""))
                    uploadButton.setEnabled(false);
                else
                    uploadButton.setEnabled(true);
            }
        });

    }

    private void makePost(){
        //TODO: fare vera chiamata REST
        EditText tv = findViewById(R.id.editTextDescription);
        String description = tv.getText().toString();
        Model model = Model.getInstance();
        //model.getHomeWall().add(new Post(model.getLoggedUser(), bitmap, description));
        Intent home = new Intent(this, HomeActivity.class);
        startActivity(home);
        finish();
    }

}
