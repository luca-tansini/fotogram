package com.example.tanso.fotogram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tanso.fotogram.Model.Base64Images;

public class UploadActivity extends AppCompatActivity {

    private MyNavigationItemSelectedListener myNavigationItemSelectedListener;
    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private String base64;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload);

        //Toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Create post - choose a picture");
        myToolbar.setTitleTextAppearance(this,R.style.upload_toolbar_text);
        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.action_next:
                        Intent insertDescription = new Intent(getApplicationContext(),InsertDescriptionActivity.class);
                        insertDescription.putExtra("img", imageUri.toString());
                        insertDescription.putExtra("base64", base64);
                        startActivity(insertDescription);
                        return true;
                    default:
                        return false;
                }
            }
        });

        //Navigation
        BottomNavigationView nav = findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.navigation_upload);
        myNavigationItemSelectedListener = new MyNavigationItemSelectedListener(this);
        nav.setOnNavigationItemSelectedListener(myNavigationItemSelectedListener);

        //Upload button
        Button chooseButton = findViewById(R.id.buttonChooseFromGallery);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromGallery();
            }
        });

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
                iv.setImageBitmap(bitmap);
                base64 = Base64Images.bitmaptoBase64(bitmap);
                Log.d("ajeje", "img base64 len: "+base64.length());
                Toolbar toolbar = findViewById(R.id.my_toolbar);
                if(toolbar.getMenu().size() == 0)
                    toolbar.inflateMenu(R.menu.upload_action_bar);
                if(base64.length() >= Base64Images.POST_IMG_LIMIT){
                    toolbar.getMenu().removeItem(R.id.action_next);
                    errorTV.setText("image size too large (max 100KB)");
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

    @Override
    protected void onRestart() {
        super.onRestart();
        BottomNavigationView nav = findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.navigation_upload);
    }

}
