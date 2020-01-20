package com.example.myapplication.dbmodels;

import com.google.gson.JsonObject;

import java.util.List;

public class capsuledatas {
    private final String Register;
    private final String Text;
    private final double Image;
    private final List<comments> Comments;
    private final int Likes;

    public capsuledatas(String Reg, String Txt, double Img, List<comments> Cmts , int Likes){
        this.Register = Reg;
        this.Text = Txt;
        this.Image = Img;
        this.Comments = Cmts;
        this.Likes = Likes;
    }
    public String getRegister(){
        return Register;
    }
    public String getText(){
        return Text;
    }
    public double getImage(){
        return Image;
    }
    public List<comments> getComments(){
        return Comments;
    }
    public int getLikes(){
        return Likes;
    }
}
