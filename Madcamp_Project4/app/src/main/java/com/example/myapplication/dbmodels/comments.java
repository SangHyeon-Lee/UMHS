package com.example.myapplication.dbmodels;

public class comments {
    private final String Username;
    private final String Comment;

    public  comments(String username, String com){
        this.Username = username;
        this.Comment = com;

    }
    public String getUsername(){
        return Username;
    }
    public String getComment(){
        return Comment;
    }

}
