package com.example.myapplication.dbmodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class rescapdatas {
    @SerializedName("Register")
    private  String Register = "";
    @SerializedName("Text")
    private  String Text = "";
    @SerializedName("Image")
    private  double Image = 0;
    @SerializedName("_id")
    private  String _id = "";
    @SerializedName("Likes")
    private  int Likes = 0;
    @SerializedName("Comments")
    private List<comments> Comments = null;

    public rescapdatas(String reg, String txt, double img, String _id, int lks, List<comments> commts) {
        this.Register = reg;
        this.Text = txt;
        this.Image = img;
        this._id = _id;
        this.Likes = lks;
        this.Comments = commts;
    }

    public String get_id(){
        return _id;
    }

}
