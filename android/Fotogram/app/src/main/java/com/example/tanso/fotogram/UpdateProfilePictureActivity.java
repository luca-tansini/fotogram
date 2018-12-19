package com.example.tanso.fotogram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tanso.fotogram.Controller.FotogramAPI;
import com.example.tanso.fotogram.Controller.ResponseCode;
import com.example.tanso.fotogram.Model.Base64Images;
import com.example.tanso.fotogram.Model.Model;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfilePictureActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private String base64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_picture);

        //Toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Update profile picture");
        myToolbar.setTitleTextAppearance(this,R.style.upload_toolbar_text);
        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.action_update:
                        pictureUpdateCall();
                        return true;
                    default:
                        return false;
                }
            }
        });

        //Upload button
        Button chooseButton = findViewById(R.id.buttonChooseFromGallery);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromGallery();
            }
        });

    }

    private void pictureUpdateCall() {
        HashMap<String,String> params = new HashMap<>();
        params.put("session_id",Model.getInstance().getLoggedUser().getSessionId());
        params.put("picture", base64);
        FotogramAPI.makeAPICall(FotogramAPI.API.PICTURE_UPDATE, getApplicationContext(), params,
                new ResponseCode() {
                    @Override
                    public void run(String response) {
                        startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
                        finish();
                    }
                },
                null
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (data != null && requestCode == PICK_IMAGE) {
            TextView errorTV = findViewById(R.id.errorTextView);
            errorTV.setText("");
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ImageView iv = findViewById(R.id.imageViewUpdateProfilePicture);
                iv.setImageDrawable(CircularBitmapDrawableFactory.create(getApplicationContext(),bitmap));
                base64 = Base64Images.bitmaptoBase64(bitmap);
                Log.d("ajeje", "img base64 len: "+base64.length());
                Toolbar toolbar = findViewById(R.id.my_toolbar);
                if(toolbar.getMenu().size() == 0)
                    toolbar.inflateMenu(R.menu.update_profile_picture_action_bar);
                if(base64.length() >= Base64Images.PROFILE_PICTURE_LIMIT){
                    toolbar.getMenu().removeItem(R.id.action_update);
                    errorTV.setText("image size too large (max 10KB)");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void chooseFromGallery(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, PICK_IMAGE);
    }

}
