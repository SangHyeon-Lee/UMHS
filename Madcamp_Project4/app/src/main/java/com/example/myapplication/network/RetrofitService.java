package com.example.myapplication.network;

import android.provider.ContactsContract;

import com.example.myapplication.dbmodels.capsuledatas;
import com.example.myapplication.dbmodels.capsulelocdatas;
import com.example.myapplication.dbmodels.mycapsules;
import com.example.myapplication.dbmodels.postcomments;
import com.example.myapplication.dbmodels.rescapdatas;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface RetrofitService {
    String URL = "http://192.249.19.252:3480/";

    @POST("capsuledatas")
    Call<rescapdatas> postData(@Body capsuledatas param);

    @POST("capsuledatas/view")
    Call<capsuledatas> getCaps(@Body capsulelocdatas param);

    @POST("capsuledatas/comments")
    Call<capsuledatas> postComment(@Body postcomments param);

    @GET("capsulelocdatas")
    Call<List<capsulelocdatas>> getCapsules();

    @POST("capsulelocdatas")
    Call<capsulelocdatas> postData2(@Body capsulelocdatas param);

    @POST("mycapsules/afterpost")
    Call<mycapsules> postData3(@Body mycapsules param);

    @Multipart
    @POST("index/post/img")
    Call<ResponseBody> uploadImages(@Part MultipartBody.Part files);

    @PUT
    Call<Void> updateImage(@Url String url, @Body RequestBody image);



    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(RetrofitService.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}


