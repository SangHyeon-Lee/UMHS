package com.example.myapplication.dbmodels;


import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.myapplication.network.NetRetrofit;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class capsuledatas {
    private String Register;
    private String Text;
    private String Image;
    private List<comments> Comments;
    private int Likes;
    private String profile;

    capsuledatas(){

    }

    public capsuledatas(String Reg, String Txt, String Img, List<comments> Cmts, int Likes, String prof){
        this.Register = Reg;
        this.Text = Txt;
        this.Image = Img;
        this.Comments = Cmts;
        this.Likes = Likes;
        this.profile = prof;
    }
    public String getRegister(){
        return this.Register;
    }
    public String getText(){
        return this.Text;
    }
    public String getImage(){
        return this.Image;
    }
    public List<comments> getComments(){
        return this.Comments;
    }
    public int getLikes(){
        return this.Likes;
    }
    public String getProfile(){return  this.profile;}

//    public rescapdatas upload(){
//        capsuledatas requestbody = new capsuledatas();
//        final Call<rescapdatas> res = NetRetrofit
//                .getInstance()
//                .getService()
//                .postData(requestbody);
//        rescapdatas response = null;
//        try{
//            response = new AsyncTask<Void, Void, rescapdatas>() {
//                @Override
//                protected rescapdatas doInBackground(Void... voids) {
//                    rescapdatas response2 = null;
//                    try {
//                        Response<rescapdatas> response = res.execute();
//                        response2 = response.body();
//                    } catch (IOException e) {
//
//                    }
//                    return response2;
//                }
//
//                @Override
//                protected void onPostExecute(rescapdatas result) {
//                    super.onPostExecute(result);
//                }
//            }.execute().get();
//        } catch(Exception e) {
//
//        }
//        return  response;
//    }

}


