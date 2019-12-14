package com.example.iotcloudsmartfarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ArrayList<String> tabNames = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadTabName();
        setTabLayout();
        setViewPager();
    }
    @TargetApi(Build.VERSION_CODES.N)
    private void setTabLayout(){
        tabLayout = findViewById(R.id.tab);


        tabNames.stream().forEach(name -> tabLayout.addTab(tabLayout.newTab().setText(name)));
    }

    private void loadTabName(){
        tabNames.add("제어 리모컨");
        tabNames.add("수집 데이터 확인");
        tabNames.add("제어 이력 확인");
    }

    private void setViewPager(){
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(tabLayout)
        );
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
