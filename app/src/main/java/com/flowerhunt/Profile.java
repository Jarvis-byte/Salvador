package com.flowerhunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;

import static com.flowerhunt.HomeDashboard.MY_PREFS_NAME;

public class Profile extends AppCompatActivity {
    ImageView profile_pic;
    Button log_out;
    FirebaseAuth auth;
    LinearLayout avt_1, avt_2, avt_3;
    int avt_number=0;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    TextView name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();
        name = findViewById(R.id.name);
        name.setText(prefs.getString("Name", ""));
        auth = FirebaseAuth.getInstance();
        profile_pic = findViewById(R.id.profile_pic);
        log_out = findViewById(R.id.log_out);
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(Profile.this, LoginScreen.class));
                finish();
                editor.clear();
                editor.apply();

            }
        });

        avt_number=prefs.getInt("avt_number", 0);
        if (avt_number==0)
        {
            String ProfilePic = prefs.getString("ProfilePic", "");
            Log.i("ProfilePicACT2", ProfilePic);
            Glide.with(Profile.this)
                    .load(ProfilePic)
                    .fitCenter()
                    .placeholder(R.drawable.avater)
                    .into(profile_pic);
        }
        else if (avt_number==1)
        {
            profile_pic.setImageResource(R.drawable.bussiness_man);

        }
        else if (avt_number==2)
        {
            profile_pic.setImageResource(R.drawable.nun);
        }
        else if (avt_number==3)
        {
            profile_pic.setImageResource(R.drawable.woman);
        }






        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialougeForAvatar();
            }
        });
    }

    private void showDialougeForAvatar() {
        BottomSheetDialog dialog = new BottomSheetDialog(Profile.this);
        dialog.setContentView(R.layout.gal_cam_bottom_sheet);
        avt_1 = dialog.findViewById(R.id.avt_1);
        avt_2 = dialog.findViewById(R.id.avt_2);
        avt_3 = dialog.findViewById(R.id.avt_3);
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        avt_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("avt_number", 1);
                editor.apply();
                profile_pic.setImageResource(R.drawable.bussiness_man);
                dialog.dismiss();
            }
        });
        avt_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("avt_number", 2);
                editor.apply();
                profile_pic.setImageResource(R.drawable.nun);
                dialog.dismiss();
            }
        });
        avt_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("avt_number", 3);
                editor.apply();
                profile_pic.setImageResource(R.drawable.woman);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}