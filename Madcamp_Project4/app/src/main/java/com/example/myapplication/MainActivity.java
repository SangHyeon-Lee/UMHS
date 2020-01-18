package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Gallery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MypagerAdapter adapter = new MypagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(pager);
    }

    public void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(View_Capsule.newInstance(), "View_Capsule");
        adapter.addFragment(MyPosts.newInstance(), "MyPosts");

        viewPager.setAdapter(adapter);
    }

    private class MypagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public MypagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

}
