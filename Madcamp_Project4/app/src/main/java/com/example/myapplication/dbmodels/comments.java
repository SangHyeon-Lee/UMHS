package com.example.myapplication.dbmodels;

public class comments {
    private final String Username;
    private final String Comment;
    private final String profiles;

    public comments(String username, String com, String prof){
        this.Username = username;
        this.Comment = com;
        this.profiles = prof;

    }
    public String getUsername(){
        return Username;
    }
    public String getComment(){
        return Comment;
    }
    public String getProfile(){
        return profiles;
    }

}
