package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.myapplication.dbmodels.rescapdatas;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.MalformedURLException;
import java.net.URL;


public class Add_Post extends AppCompatActivity {

    ImageView post_picture;
    ImageView profile_view;
    TextView name_text;
    TextView date;
    EditText post_text;
    String capsuleId = "";
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private String imageFilePath;
    private Uri photoUri;
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



        sendTakePhotoIntent();

        gpsTracker = new GpsTracker(this);
        double mylat = gpsTracker.getLatitude();
        double mylong = gpsTracker.getLongitude();
        double myal = gpsTracker.getAltitude();

        capsuleId = firstUpload("TextofCapsule");
        secondUpload(capsuleId,mylat,mylong,myal);

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void sendTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;

            try {
                exif = new ExifInterface(imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegrees(exifOrientation);
            } else {
                exifDegree = 0;
            }
            post_picture.setImageBitmap(rotate(bitmap, exifDegree));
        }
    }

    public String firstUpload(String txt){
        //Capsule post시 서버에 올라가고, Capsuleid를 받아옴.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitExService = retrofit.create(RetrofitService.class);
        final String[] capid = new String[1];
        comments temp2 = new comments("chaewon","getyourcrayon");
        List<comments> temp = new ArrayList<comments>();
        temp.add(temp2);
        retrofitExService.postData(new capsuledatas("Itshouldbechanged", txt, 1234, temp,0)).enqueue(new Callback<rescapdatas>() {
            @Override
            public void onResponse(@NonNull Call<rescapdatas> call, @NonNull Response<rescapdatas> response) {
                if (response.isSuccessful()) {
                    rescapdatas body = response.body();
                    if (body != null) {
                        capid[0] = body.get_id();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<rescapdatas> call, @NonNull Throwable t) {
            }
        });
        return capid[0];
    }
    public void secondUpload(String Cid, double lat, double lon, double al){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitExService = retrofit.create(RetrofitService.class);
        final String[] capid = new String[1];
        comments temp2 = new comments("chaewon","getyourcrayon");
        List<comments> temp = new ArrayList<comments>();
        temp.add(temp2);
        retrofitExService.postData2(new capsulelocdatas(Cid, lat,lon,al)).enqueue(new Callback<capsulelocdatas>() {
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
    public void thirdUpload(String Cid){
        return;
    }
}
