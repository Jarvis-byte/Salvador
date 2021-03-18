package com.flowerhunt;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_SCREEN = 1800;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    SharedPreferences onBoardingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_screen);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
                boolean isFastTime = onBoardingScreen.getBoolean("firstTime", true);

                if (isFastTime) {
                    SharedPreferences.Editor editor = onBoardingScreen.edit();
                    editor.putBoolean("firstTime", false);
                    editor.commit();
                    Intent i = new Intent(SplashScreen.this, OnBoarding.class);
                         startActivity(i);
                    finish();
                }
                else {
                    if (firebaseUser != null) {
                        Intent i = new Intent(SplashScreen.this, HomeDashboard.class);
                        startActivity(i);
                    } else {
                        startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                    }
                    finish();
                }

            }
        }, SPLASH_SCREEN);
    }
}