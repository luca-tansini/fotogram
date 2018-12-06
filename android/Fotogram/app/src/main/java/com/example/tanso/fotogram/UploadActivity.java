package com.example.tanso.fotogram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class UploadActivity extends AppCompatActivity {

    private MyNavigationItemSelectedListener myNavigationItemSelectedListener;
    private static final int PICK_IMAGE = 1;
    private Uri imageUri;

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
        Button upload = findViewById(R.id.buttonCreatePost);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromGallery();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (data != null && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ImageView iv = findViewById(R.id.imageViewUpload);
                iv.setImageBitmap(bitmap);
                Toolbar toolbar = findViewById(R.id.my_toolbar);
                if(toolbar.getMenu().size() == 0)
                    toolbar.inflateMenu(R.menu.action_bar);
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
