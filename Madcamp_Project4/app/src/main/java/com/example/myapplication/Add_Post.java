package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Add_Post extends AppCompatActivity {

    ImageView post_picture;
    ImageView profile_view;
    TextView name_text;
    TextView date;
    EditText post_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__post);
        post_picture = findViewById(R.id.post_pic);
        profile_view = findViewById(R.id.profile_picture);
        post_text = findViewById(R.id.post_text);
        name_text = findViewById(R.id.name);
        date = findViewById(R.id.date);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getDisplayName();
        Uri profile = user.getPhotoUrl();

        name_text.setText(name);
        Glide.with(this).load(profile).into(profile_view);


        int permissionCheck = ContextCompat.checkSelfPermission(Add_Post.this, Manifest.permission.CAMERA);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(Add_Post.this, new String[]{Manifest.permission.CAMERA},0);
        }else{
            Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent2,1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            post_picture.setImageBitmap(photo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
