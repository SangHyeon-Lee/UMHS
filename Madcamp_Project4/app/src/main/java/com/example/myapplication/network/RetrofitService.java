package com.example.myapplication.network;

import android.provider.ContactsContract;

import com.example.myapplication.dbmodels.capsuledatas;
import com.example.myapplication.dbmodels.capsulelocdatas;
import com.example.myapplication.dbmodels.rescapdatas;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitService {
    String URL = "http://192.249.19.252:3480/";

    @POST("capsuledatas")
    Call<rescapdatas> postData(@Body capsuledatas param);

    @GET("capsulelocdatas")
    Call<List<capsulelocdatas>> getCapsules();

    @POST("capsulelocdatas")
    Call<capsulelocdatas> postData2(@Body capsulelocdatas param);



}


