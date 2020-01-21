package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.myapplication.MainActivity;
import com.example.myapplication.facebooklogin.LoginActivity;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;

public class MyPosts extends Fragment {

    public static MyPosts newInstance() {
        MyPosts fragmentFirst = new MyPosts();
        return fragmentFirst;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.activity_my_posts,container,false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // 로그인 설정.
        if (user != null) {
//            String name = user.getDisplayName();
//            String email = user.getEmail();
//            Uri photoUrl = user.getPhotoUrl();
//            String uid = user.getUid();
//
//            nameTextView.setText(name);
//            emailTextView.setText(email);
//            uidTextView.setText(uid);
        } else {
            goLoginScreen();
        }

        //TODO : 나의 ID로 mycapsules 를 받아와야 합니다.


        return rootview;
    }



    private void goLoginScreen() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }



}
