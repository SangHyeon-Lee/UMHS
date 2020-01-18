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

import java.io.FileNotFoundException;
import java.io.InputStream;

public class Add_Post extends AppCompatActivity {

    ImageView post_picture;
    ImageView profile;

    EditText post_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__post);
        post_picture = findViewById(R.id.post_pic);
        profile = findViewById(R.id.profile_picture);
        post_text = findViewById(R.id.post_text);


        int permissionCheck = ContextCompat.checkSelfPermission(Add_Post.this, Manifest.permission.CAMERA);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(Add_Post.this, new String[]{Manifest.permission.CAMERA},0);
        }else{
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,1);
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
