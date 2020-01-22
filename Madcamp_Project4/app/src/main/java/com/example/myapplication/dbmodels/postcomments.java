package com.example.myapplication.dbmodels;

import java.util.List;

public class postcomments {
        private String Register;
        private String Text;
        private String _id;
        private comments Comments;
        private int Likes;
        private String profile;

        postcomments(){

        }

        public postcomments(String Reg, String Txt, String Img, comments Cmts, int Likes, String prof){
            this.Register = Reg;
            this.Text = Txt;
            this._id = Img;
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
        public String get_id(){
            return this._id;
        }
        public comments getComments(){
            return this.Comments;
        }
        public int getLikes(){
            return this.Likes;
        }
        public String getProfile(){return  this.profile;}



}
