package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Observable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Toast;

import com.example.myapplication.dbmodels.capsuledatas;
import com.example.myapplication.dbmodels.capsulelocdatas;
import com.example.myapplication.dbmodels.comments;
import com.example.myapplication.dbmodels.mycaps;
import com.example.myapplication.dbmodels.mycapsules;
import com.example.myapplication.dbmodels.rescapdatas;
import com.example.myapplication.network.NetRetrofit;
import com.example.myapplication.network.RetrofitService;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import javax.xml.transform.Result;


public class Add_Post extends AppCompatActivity {

    ImageView post_picture;
    ImageView profile_view;
    TextView name_text;
    TextView date;
    EditText post_text;
    String capsuleId = "123123";

    private GpsTracker gpsTracker;

    //TODO: post 1)mycapsules , 2)capsulelocdata, 3)capsuledata 3개를 한번에 올려줘야 합니다.
    //TODO: 3)을 한 후에 capsuleID를 받아온 후 1),2)를 올려줘야 합니다. 자신의 위치정보는 Gpstracker로 받아옵니다.

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
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(Add_Post.this, new String[]{Manifest.permission.CAMERA}, 0);
        } else {
            Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent2, 1);
        }

        //post버튼을 눌렀을때 실행되야 하는 놈. 수정해야할놈들있음.
        //TODO: 상현 여기 txt는 edittext firstUpload안에 Reg는 현재유저 이름으로 넣어줘용
        firstUpload("TextofCapsuleitshouldbechanged",name);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            post_picture.setImageBitmap(photo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void firstUpload(String txt, String name) {
        gpsTracker = new GpsTracker(this);
        final double mylat = gpsTracker.getLatitude();
        final double mylong = gpsTracker.getLongitude();
        final double myal = gpsTracker.getAltitude();
        final String goname = name;
        //Capsule post시 서버에 올라가고, Capsuleid를 받아옴.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitExService = retrofit.create(RetrofitService.class);
        comments temp2 = new comments("chaewon", "getyourcrayon");
        List<comments> temp = new ArrayList<comments>();
        temp.add(temp2);
        retrofitExService.postData(new capsuledatas("Itshouldbechanged", txt, 1234, temp, 0)).enqueue(new Callback<rescapdatas>() {
            @Override
            public void onResponse(@NonNull Call<rescapdatas> call, @NonNull Response<rescapdatas> response) {
                if (response.isSuccessful()) {
                    rescapdatas body = response.body();
                    Toast.makeText(getApplicationContext(), body.get_id(), Toast.LENGTH_LONG).show();
                    secondUpload(body.get_id(), mylat, mylong, myal);
                    thirdUpload(goname ,body.get_id());
                    //uploadImage(imagefile, body.get_id());
                }
            }

            @Override
            public void onFailure(@NonNull Call<rescapdatas> call, @NonNull Throwable t) {
            }
        });
    }

    public void secondUpload(String Cid, double lat, double lon, double al) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitExService = retrofit.create(RetrofitService.class);
        retrofitExService.postData2(new capsulelocdatas(Cid, lat, lon, al)).enqueue(new Callback<capsulelocdatas>() {
            @Override
            public void onResponse(@NonNull Call<capsulelocdatas> call, @NonNull Response<capsulelocdatas> response) {
                if (response.isSuccessful()) {
                    return;
                }
            }

            @Override
            public void onFailure(@NonNull Call<capsulelocdatas> call, @NonNull Throwable t) {
            }
        });
        return;
    }

    public void thirdUpload(String Username, String Cid) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitExService = retrofit.create(RetrofitService.class);
        mycaps temp = new mycaps(Cid);
        retrofitExService.postData3(new mycapsules(Username,temp)).enqueue(new Callback<mycapsules>() {
            @Override
            public void onResponse(@NonNull Call<mycapsules> call, @NonNull Response<mycapsules> response) {
                if (response.isSuccessful()) {
                    return;
                }
            }

            @Override
            public void onFailure(@NonNull Call<mycapsules> call, @NonNull Throwable t) {
            }
        });
        return;
    }

    public void uploadImage(File file, String key) {
        // create multipart
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("img", key, requestFile);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitExService = retrofit.create(RetrofitService.class);
        retrofitExService.uploadImages(body).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
            }
            @Override
            public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
            }
        });

    }


}
