package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
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
import com.example.myapplication.dbmodels.postcomments;
import com.example.myapplication.dbmodels.rescapdatas;
import com.example.myapplication.network.RetrofitService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String name = user.getDisplayName();
        Uri profile = user.getPhotoUrl();


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
                    postComment(id,edit_comment.getText().toString(),name,profile.toString());
                    getCaps(id);
                    edit_comment.setText("");
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
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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


                    name_text.setText(" "+name);
                    text_text.setText(text);
                    Glide.with(getApplicationContext()).load(Uri.parse(profile)).into(profile_image);
                    Glide.with(getApplicationContext()).load(Uri.parse("https://cosmo-madcamp.s3.ap-northeast-2.amazonaws.com/" + img)).into(img_view);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        GradientDrawable drawable=
                                (GradientDrawable) getApplicationContext().getDrawable(R.drawable.background_rounding);
                        profile_image.setBackground(drawable);
                        profile_image.setClipToOutline(true);
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        GradientDrawable drawable=
                                (GradientDrawable) getApplicationContext().getDrawable(R.drawable.background_rounding);
                        img_view.setBackground(drawable);
                        img_view.setClipToOutline(true);
                    }

                    comment_view.setAdapter(comment_adapter);
                    comment_view.setScrollContainer(false);
                    likes_view.setText(Integer.toString(likes));
                }
            }


            @Override
            public void onFailure(@NonNull Call<capsuledatas> call, @NonNull Throwable t) {
            }
        });
    }

    public void postComment(String Cid, String txt, String name, String prof) {
        final String goname = name;
        final String v1 = txt;
        final String v2 = prof;
        final int v3 = 0;
        comments onet = new comments(name,txt,prof);
        //Capsule post시 서버에 올라가고, Capsuleid를 받아옴.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitExService = retrofit.create(RetrofitService.class);
        retrofitExService.postComment(new postcomments(goname,v1,Cid,onet,v3,v2)).enqueue(new Callback<capsuledatas>() {
            @Override
            public void onResponse(@NonNull Call<capsuledatas> call, @NonNull Response<capsuledatas> response) {
                if (response.isSuccessful()) {
                    capsuledatas body = response.body();
                }
            }

            @Override
            public void onFailure(@NonNull Call<capsuledatas> call, @NonNull Throwable t) {
            }
        });
    }
}
