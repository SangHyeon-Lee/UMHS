package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.dbmodels.capsuledatas;
import com.example.myapplication.dbmodels.comments;

import java.util.List;

public class Posts_CardView extends AppCompatActivity {

    private ImageView profile_image;
    private TextView name_text;
    private TextView text_text;
    private ImageView img_view;
    private ListView comment_view;
    private TextView likes_view;
    private ImageButton like;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_cardview);

        profile_image = findViewById(R.id.profile_picture);
        name_text = findViewById(R.id.name);
        text_text = findViewById(R.id.post_text);
        img_view = findViewById(R.id.post_pic);
        comment_view = findViewById(R.id.comment);
        likes_view = findViewById(R.id.like_text);
        like = findViewById(R.id.like_button);


        Intent intent = getIntent();

        capsuledatas capsule= (capsuledatas) intent.getExtras().get("capsule");
        String name = capsule.getRegister();
        String text = capsule.getText();
        //double img = capsule.getImage();
        List<comments> comment = capsule.getComments();
        int likes = capsule.getLikes();

        Comment_adapter comment_adapter = new Comment_adapter();
        for (int i =0; i<comment.size();i++){
            comment_adapter.addItem(comment.get(i));
        }
        

        name_text.setText(name);
        text_text.setText(text);
        Glide.with(this).load(Uri.parse("https://cosmo-madcamp.s3.ap-northeast-2.amazonaws.com/book.png")).into(img_view);
        //img_view.set---
        comment_view.setAdapter(comment_adapter);
        likes_view.setText(likes);

    }
}
