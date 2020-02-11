package com.e.reconbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0;
    Button btnGetStarted;
    Animation btnAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // Check if we need to show the intro or not
        if(restorePrefData()){
            Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(mainActivity);
            finish();
        }



        setContentView(R.layout.activity_intro);

        // Hide top bar
        try {
            Objects.requireNonNull(getSupportActionBar()).hide();
        }
        catch (NullPointerException ex){
            System.out.println("Support action bar hide error \n\n "  + ex.getMessage());
        }
        //getSupportActionBar().hide();


        // Ini views
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_getStarted);
        tabIndicator = findViewById(R.id.tab_indicator);
        //Context context;
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);


        // Fill list screen

        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("ReconBot","Hi! I'm ReconBot, your image recognition buddy. I can identify objects and you can ask me anything about them.",R.drawable.recobot_icon_sombra_teste));
        mList.add(new ScreenItem("Camera","The name says it all. It's where you take a photo of the object i'll identify the object, just give me one or two seconds.",R.drawable.image));
        mList.add(new ScreenItem("Chat","Here we can talk about the object. You can ask me anything about it and i'll do my best to provide you with the answers you're looking for. Don't be shy!",R.drawable.icon_mensagem));
        mList.add(new ScreenItem("Gallery","This is where you can see your previous products recognized by the ReconBot.",R.drawable.icon_historico));



        // Setup viewPager
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);


        // Setup tablayot  with viewpager
        tabIndicator.setupWithViewPager(screenPager);

        // Next button click listener

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = screenPager.getCurrentItem();
                if(position < mList.size()){
                    position++;
                    screenPager.setCurrentItem(position);
                }

                if(position == mList.size() - 1){
                    loadlastScreen();

                }

            }
        });



        // tablayout add change listener
        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if( tab.getPosition() == mList.size() - 1 ){
                    loadlastScreen();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




        // Get Started button click listener

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Open main activity

                Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(mainActivity);

                // save boolean that tutorial was already showed , to don't show next time the app is oppened
                
                savePrefsData();
                finish();
            }
        });

    }

    private boolean restorePrefData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOppenedBefore = pref.getBoolean("isIntroOppened",false);
        return isIntroActivityOppenedBefore;
    }

    private void savePrefsData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOppened",true);
        editor.apply();

    }

    private void loadlastScreen() {

        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);

        // animation button getStarted
        btnGetStarted.setAnimation(btnAnim);

    }
}
