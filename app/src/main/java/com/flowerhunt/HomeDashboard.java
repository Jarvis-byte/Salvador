package com.flowerhunt;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.Fade;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.flowerhunt.Adapter.MyAdapter;
import com.flowerhunt.Model.FlowerList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class HomeDashboard extends AppCompatActivity {
    public static final String MY_PREFS_NAME = "ProfilePic";
    public static final String MY_PREFS_NAME2 = "Name";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    FirebaseAuth auth;
    FirebaseUser user;
    ImageView profile_pic, click_image_top;
    ExtendedFloatingActionButton click_image;
    TextView name;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<FlowerList> list = new ArrayList<>();
    String photoUrl;
    FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
    String userId;
    String TAG = "FirebaseData";
    String profilePicUrl;
    NestedScrollView scrollView;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dash_board);

        //Animation For next Activity
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        Task<GetTokenResult> token = auth.getAccessToken(true);
        Log.i("TOKEN", String.valueOf(token));
        ImageSlider imageSlider = findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<SlideModel>();

        slideModels.add(new SlideModel(R.drawable.plant1, "Welcome To Salvador"));
        slideModels.add(new SlideModel(R.drawable.plant2, "Save Tree Save Life"));
        slideModels.add(new SlideModel(R.drawable.plant3, "Plant Tree"));
        slideModels.add(new SlideModel(R.drawable.plant4, "A tree is our most intimate contact with nature"));
        slideModels.add(new SlideModel(R.drawable.plant5, "The clearest way into the Universe is through a forest wilderness"));
        imageSlider.setImageList(slideModels, false);


        name = findViewById(R.id.name);
        editor = prefs.edit();
        if (!prefs.contains("Name")) {
            Log.i("NoData", "NO");
            getNameFromFirestore();
        } else {
            name.setText(prefs.getString("Name", ""));
        }
        scrollView = findViewById(R.id.scrollView);
        profile_pic = findViewById(R.id.profile_pic);
        click_image = findViewById(R.id.click_image);
        click_image_top = findViewById(R.id.click_image_top);
        click_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();
            }
        });
        click_image_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeDashboard.this, Profile.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        HomeDashboard.this, profile_pic, ViewCompat.getTransitionName(profile_pic));
                startActivity(intent, options.toBundle());
            }
        });
        Log.i("Providers", String.valueOf(user.getProviderData()));
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        if (auth.getCurrentUser().getPhotoUrl() != null) {
            profilePicUrl = auth.getCurrentUser().getPhotoUrl().toString();
            for (UserInfo profile : auth.getCurrentUser().getProviderData()) {
                if (profile.getProviderId().equals("facebook.com")) {
//                    String facebookUserId = profile.getUid();
                    Bundle params = new Bundle();
                    params.putString("fields", "id,picture.type(large)");
                    new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response) {
                                    if (response != null) {
                                        try {
                                            JSONObject data = response.getJSONObject();
                                            if (data.has("picture")) {
                                                profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                                editor.putString("ProfilePic", profilePicUrl);
                                                editor.apply();
                                                Glide.with(HomeDashboard.this)
                                                        .load(profilePicUrl)
                                                        .fitCenter()
                                                        .placeholder(R.drawable.avater)
                                                        .into(profile_pic);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).executeAsync();
                } else {

                    editor.putString("ProfilePic", profilePicUrl);
                    editor.apply();
                    Glide.with(HomeDashboard.this)
                            .load(profilePicUrl)
                            .fitCenter()
                            .placeholder(R.drawable.avater)
                            .into(profile_pic);
                }
            }


        }


        //Recycler View
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        FlowerList FlowerList = new FlowerList("Rose", "20", R.drawable.rose_1);
        FlowerList FlowerList2 = new FlowerList("Sunflower", "5", R.drawable.sunflower);
        FlowerList FlowerList3 = new FlowerList("Bougainvillea", "2", R.drawable.bougainvillea);
        FlowerList FlowerList4 = new FlowerList("Calla Lily", "3", R.drawable.calla_lily);
        FlowerList FlowerList5 = new FlowerList("Orchid", "6", R.drawable.orchid);
        FlowerList FlowerList6 = new FlowerList("Tulip", "18", R.drawable.tulip);

        list.add(FlowerList);
        list.add(FlowerList2);
        list.add(FlowerList3);
        list.add(FlowerList4);
        list.add(FlowerList5);
        list.add(FlowerList6);


        myAdapter = new MyAdapter(this, list);
        recyclerView.setAdapter(myAdapter);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY + 12 && click_image.isShown()) {
                    click_image.hide();
                }

                // the delay of the extension of the FAB is set for 12 items
                if (scrollY < oldScrollY - 12 && !click_image.isShown()) {
                    click_image.show();
                }

                // if the nestedScrollView is at the first item of the list then the
                // floating action should be in show state
                if (scrollY == 0) {
                    click_image.show();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void getNameFromFirestore() {
        userId = auth.getCurrentUser().getUid();
        mFirebaseFirestore.collection("Users").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                name.setText("Hi " + document.getString("name"));
                                editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

                                editor.putString("Name", "Hi " + document.getString("name"));
                                editor.apply();

                            } else {
                                Toast.makeText(HomeDashboard.this, "Please Input Name", Toast.LENGTH_SHORT).
                                        show();
                            }
                        } else {
                            Toast.makeText(HomeDashboard.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
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