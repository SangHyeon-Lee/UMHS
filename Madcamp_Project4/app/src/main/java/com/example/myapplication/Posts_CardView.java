package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.dbmodels.capsuledatas;
import com.example.myapplication.dbmodels.capsulelocdatas;
import com.example.myapplication.dbmodels.comments;
import com.example.myapplication.network.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Posts_CardView extends AppCompatActivity {

    private ImageView profile_image;
    private TextView name_text;
    private TextView text_text;
    private ImageView img_view;
    private ListView comment_view;
    private TextView likes_view;
    private ImageButton like;
    private capsuledatas capsule;
    private EditText edit_comment;
    private Button comment_post;

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

        String id = (String) intent.getExtras().get("id");
//        capsuledatas capsule = getCaps(id);
        getCaps(id);

        edit_comment = findViewById(R.id.add_comment);

        comment_post = findViewById(R.id.comment_post);
        comment_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_comment.getText().toString().length() != 0) {


                }
            }
        });

    }

    public void getCaps(String capid) {
        double sh = 1;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitExService = retrofit.create(RetrofitService.class);
        retrofitExService.getCaps(new capsulelocdatas(capid, sh, sh, sh)).enqueue(new Callback<capsuledatas>() {
            @Override
            public void onResponse(@NonNull Call<capsuledatas> call, @NonNull Response<capsuledatas> response) {
                if (response.isSuccessful()) {
                    capsuledatas body = response.body();
                    String name = body.getRegister();
                    String text = body.getText();
                    String profile = body.getProfile();
                    String img = body.getImage();
                    List<comments> comment = body.getComments();
                    int likes = body.getLikes();

                    Comment_adapter comment_adapter = new Comment_adapter();
                    for (int i = 0; i < comment.size(); i++) {
                        comment_adapter.addItem(comment.get(i));
                    }


                    name_text.setText(name);
                    text_text.setText(text);
                    Glide.with(getApplicationContext()).load(Uri.parse(profile)).into(profile_image);
                    Glide.with(getApplicationContext()).load(Uri.parse("https://cosmo-madcamp.s3.ap-northeast-2.amazonaws.com/" + img)).into(img_view);
                    comment_view.setAdapter(comment_adapter);
                    likes_view.setText(Integer.toString(likes));
                }
            }


            @Override
            public void onFailure(@NonNull Call<capsuledatas> call, @NonNull Throwable t) {
            }
        });
    }
}
