package com.flowerhunt;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_SCREEN = 1800;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_screen);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(firebaseUser!=null) {
                    Intent i = new Intent(SplashScreen.this, HomeDashboard.class);
                    startActivity(i);
                }
                else {
                    startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                }
                finish();
            }
        }, SPLASH_SCREEN);
    }
}