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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tanso.fotogram.Model.Model;

import java.util.HashMap;
import java.util.Map;

public class InsertDescriptionActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private String base64;

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
        ImageView iv = findViewById(R.id.imageViewUpdateProfilePicture);
        try{
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            iv.setImageBitmap(bitmap);
        } catch(Exception e){
            e.printStackTrace();
        }
        base64 = getIntent().getExtras().getString("base64");

        //Make post button
        final Button uploadButton = findViewById(R.id.buttonChooseFromGallery);
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
        final String description = tv.getText().toString();

        RequestQueue rq = Model.getRequestQueue(this);
        String url = "https://ewserver.di.unimi.it/mobicomp/fotogram/create_post";
        StringRequest createPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(home);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ajeje", "create post error: "+error.toString());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                HashMap<String,String> params = new HashMap<>();
                params.put("session_id",Model.getInstance().getLoggedUser().getSessionId());
                params.put("img", base64);
                params.put("message", description);
                return params;
            }
        };
        rq.add(createPostRequest);
    }
}