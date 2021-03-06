package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.dbmodels.comments;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Comment_adapter extends BaseAdapter {

    private ArrayList<comments> comments = new ArrayList<>();

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public comments getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_comment_adapter, parent, false);
        }
        ImageView comment_profile = (ImageView) convertView.findViewById(R.id.comment_profile);
        TextView comment_name = (TextView) convertView.findViewById(R.id.comment_name);
        TextView comment_text = (TextView) convertView.findViewById(R.id.comment_text);

        comments comment = getItem(position);
        Glide.with(context).load(Uri.parse(comment.getProfile())).into(comment_profile);
        comment_name.setText(" "+comment.getUsername());
        comment_text.setText(" "+comment.getComment());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            GradientDrawable drawable=
                    (GradientDrawable) context.getDrawable(R.drawable.background_rounding);
            comment_profile.setBackground(drawable);
            comment_profile.setClipToOutline(true);
        }

        return convertView;
    }

    public void addItem(comments comment) {
        comments.add(comment);

    }

}
