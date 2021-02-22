package com.flowerhunt;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.io.ByteArrayOutputStream;

public class HomeDashboard extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    TextView name_of_user;
    FirebaseAuth auth;
    FirebaseUser user;
    ImageView profile_pic;
    Button click_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dash_board);
        user = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        name_of_user = findViewById(R.id.name_of_user);
        profile_pic = findViewById(R.id.profile_pic);
        click_image = findViewById(R.id.click_image);
        click_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(HomeDashboard.this, LoginScreen.class));
                finish();
            }
        });

        name_of_user.setText("Hi, " + user.getDisplayName());
        String photoUrl = auth.getCurrentUser().getPhotoUrl().toString();
        for (UserInfo profile : auth.getCurrentUser().getProviderData()) {
            if (profile.getProviderId().equals("facebook.com")) {
                String facebookUserId = profile.getUid();
                photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";
            }
        }

        Log.i("Image", photoUrl);
        Glide.with(HomeDashboard.this)
                .load(photoUrl)
                .fitCenter()
                .placeholder(R.drawable.man)
                .into(profile_pic);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            Log.i("Base64", encodedImage);
            profile_pic.setImageBitmap(imageBitmap);
        }
    }
}