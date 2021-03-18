package com.flowerhunt;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.Fade;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class HomeDashboard extends AppCompatActivity {
    public static final String MY_PREFS_NAME = "ProfilePic";
    public static final String MY_PREFS_NAME2 = "Name";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String SHOWCASE_ID = "50";
    public Dialog mDialog;
    FirebaseAuth auth;
    FirebaseUser user;
    ImageView profile_pic, click_image_top;
    ExtendedFloatingActionButton click_image;
    TextView name;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<FlowerList> list = new ArrayList<>();
    FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
    String userId;
    String TAG = "FirebaseData";
    String profilePicUrl;
    NestedScrollView scrollView;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    List<String> listDB = new ArrayList<>();
    String sImage;


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


        String ProfilePic = prefs.getString("ProfilePic", "");
        if (ProfilePic.equalsIgnoreCase("")) {
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
        } else {
            Log.i("PROFILEPICHOME", ProfilePic);
            Glide.with(HomeDashboard.this)
                    .load(ProfilePic)
                    .fitCenter()
                    .placeholder(R.drawable.avater)
                    .into(profile_pic);
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


        //ShowCase One Time
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(click_image,
                "Click Image And be a Salvador", "GOT IT");

        sequence.addSequenceItem(click_image_top,
                "You can Be a Salvador from here ", "GOT IT");

        sequence.addSequenceItem(profile_pic,
                "Check your 'Profile' from here", "GOT IT");

        sequence.start();


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
            Log.i("DATA", String.valueOf(data));
            try {
//                final Uri imageUri = data.getData();
//                Log.i("URI", String.valueOf(imageUri));
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bytes = stream.toByteArray();
                sImage = Base64.encodeToString(bytes, Base64.NO_WRAP);
//                Log.i("BASE64", sImage);
                // name.setText(sImage);
                profile_pic.setImageBitmap(bitmap);

//                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
//                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                String encodedImage = encodeImage(selectedImage);
                Send_Base_sixty_four_image(sImage);
            } catch (Exception e) {
                Log.i("EXCEPTION", e.toString());

            }


        }
    }


    private void Send_Base_sixty_four_image(String encodedImage) {
        showProgress();

        okhttp3.OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("passkey", "bgdguywfdkbqjgvdtfjduigdujyjwd766ew8762ghvghgwdd77ewy")
                .add("imageString", encodedImage)
                .build();
        Log.d("BASE64", encodedImage);
        Request request = new Request.Builder()
                .url("https://us-central1-digifit-staging-786b9.cloudfunctions.net/visionAPI")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i("Failed", e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                Log.i("JSON", json);
                mFirebaseFirestore.collection("CollectionOfFlowers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listDB.add(document.getId());
                            }

                            Log.d(TAG, listDB.toString());
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("label");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Log.i("Label", jsonArray.getString(i));
                        if (listDB.contains(jsonArray.getString(i))) {

                            Log.i("Contains", jsonArray.getString(i));
                            String nameofFlower = jsonArray.getString(i);

                            HomeDashboard.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(HomeDashboard.this, nameofFlower.toUpperCase()  + " Flower Detected", Toast.LENGTH_SHORT).show();

                                }
                            });

                        } else {
                            Log.i("Contains", "Nothing");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("ERROR", e.toString());
                }


                hideProgress();
            }
        });
    }

    public void showProgress() {
        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.custom_progress_layout);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    public void hideProgress() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}