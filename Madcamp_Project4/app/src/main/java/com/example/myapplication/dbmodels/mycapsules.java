package com.example.myapplication.dbmodels;

import com.google.gson.JsonObject;

import java.util.List;

public class mycapsules {
    private final String UserId;
    private final List<JsonObject> mycaps;

    public  mycapsules(String UserId, List<JsonObject> mycaps){
        this.UserId = UserId;
        this.mycaps = mycaps;
    }
    public String getUserID(){
        return UserId;
    }
    public  List<JsonObject> getMycaps(){
        return mycaps;
    }
}
