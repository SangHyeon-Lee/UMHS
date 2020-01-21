package com.example.myapplication.dbmodels;

import com.google.gson.JsonObject;

import java.util.List;

//Post전용.
public class mycapsules {
    private final String UserId;
    private final mycaps Mycapsules;

    public  mycapsules(String UserId, mycaps mycaps){
        this.UserId = UserId;
        this.Mycapsules = mycaps;
    }
    public String getUserID(){
        return UserId;
    }
    public  mycaps getMycaps(){
        return Mycapsules;
    }
}
