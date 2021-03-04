package com.flowerhunt;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.flowerhunt.Model.UserDetails;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;

public class LoginScreen extends AppCompatActivity {
    private static final String EMAIL = "email";

    public Dialog mDialog;
    TextView new_user_login, greeting_message, welcome_back, btn_login, btn_forgot_password;
    RelativeLayout gmail_login, twitter_login, RRfb, RL_Email, RL_Password;
    ImageView fb, img_app_icon;
    float v = 0;
    LinearLayout LLExtraLogin;
    FirebaseAuth auth;
    FirebaseFirestore database;
    FirebaseUser firebaseUser;
    EditText username_input, pass;
    ProgressBar progressBar;
    String userId, name;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG = "LoginScreen";
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 1;
    private CallbackManager callbackManager;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        new_user_login = findViewById(R.id.new_user_login);
        gmail_login = findViewById(R.id.gmail_login);
        fb = findViewById(R.id.fb);
        greeting_message = findViewById(R.id.greeting_message);
        twitter_login = findViewById(R.id.twitter_login);
        RRfb = findViewById(R.id.RRfb);
        LLExtraLogin = findViewById(R.id.LLExtraLogin);
        img_app_icon = findViewById(R.id.img_app_icon);
        welcome_back = findViewById(R.id.welcome_back);
        RL_Email = findViewById(R.id.RL_Email);
        RL_Password = findViewById(R.id.RL_Password);
        btn_login = findViewById(R.id.btn_login);
        btn_forgot_password = findViewById(R.id.btn_forgot_password);
        username_input = findViewById(R.id.username_input);
        pass = findViewById(R.id.pass);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        firebaseUser = auth.getCurrentUser();

        animate();

        WelcomeMessage();

        mAuth = FirebaseAuth.getInstance();
        //Google Login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        gmail_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIN();
            }
        });


        //facebook login
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setPermissions(Arrays.asList(EMAIL));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                showProgress();
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                hideProgress();
                Toast.makeText(LoginScreen.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                hideProgress();
                Toast.makeText(LoginScreen.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //New User Login
        new_user_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this, RegisterActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
            }
        });

        //Login Using Email
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(username_input.getText().toString())) {
                    username_input.setError("Please Enter Your Email");
                    username_input.requestFocus();
                } else if (TextUtils.isEmpty(pass.getText().toString())) {
                    pass.setError("Please Enter Your Password");
                    pass.requestFocus();
                } else {
                    Login_using_email();
                }
            }
        });
    }

    private void Login_using_email() {
        showProgress();

        String email = username_input.getText().toString();
        String password = pass.getText().toString();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    hideProgress();
                    LoadHomedashboard();

                } else {
                    hideProgress();
                    Toast.makeText(LoginScreen.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }


    private void animate() {
        RRfb.setTranslationY(300);
        gmail_login.setTranslationY(300);
        twitter_login.setTranslationY(300);
        LLExtraLogin.setTranslationY(300);
        img_app_icon.setTranslationY(300);
        greeting_message.setTranslationY(300);
        welcome_back.setTranslationY(300);
        RL_Email.setTranslationY(300);
        RL_Password.setTranslationY(300);
        btn_forgot_password.setTranslationY(300);
        btn_login.setTranslationY(300);
        new_user_login.setTranslationY(300);

        RRfb.setAlpha(v);
        gmail_login.setAlpha(v);
        twitter_login.setAlpha(v);
        LLExtraLogin.setAlpha(v);
        img_app_icon.setAlpha(v);
        greeting_message.setAlpha(v);
        welcome_back.setAlpha(v);
        RL_Email.setAlpha(v);
        RL_Password.setAlpha(v);
        btn_forgot_password.setAlpha(v);
        btn_login.setAlpha(v);
        new_user_login.setAlpha(v);

        RRfb.animate().translationY(0).alpha(1).setDuration(1500).setStartDelay(400).start();
        gmail_login.animate().translationY(0).alpha(1).setDuration(1500).setStartDelay(400).start();
        twitter_login.animate().translationY(0).alpha(1).setDuration(1500).setStartDelay(400).start();
        LLExtraLogin.animate().translationY(0).alpha(1).setDuration(1500).setStartDelay(400).start();
        img_app_icon.animate().translationY(0).alpha(1).setDuration(1500).setStartDelay(400).start();
        greeting_message.animate().translationY(0).alpha(1).setDuration(1500).setStartDelay(400).start();
        welcome_back.animate().translationY(0).alpha(1).setDuration(1500).setStartDelay(400).start();
        RL_Email.animate().translationY(0).alpha(1).setDuration(1500).setStartDelay(400).start();
        RL_Password.animate().translationY(0).alpha(1).setDuration(1500).setStartDelay(400).start();
        btn_forgot_password.animate().translationY(0).alpha(1).setDuration(1500).setStartDelay(400).start();
        btn_login.animate().translationY(0).alpha(1).setDuration(1500).setStartDelay(400).start();
        new_user_login.animate().translationY(0).alpha(1).setDuration(1500).setStartDelay(400).start();
    }

    private void WelcomeMessage() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("Hi...Good Morning")
                    .append(" ")
                    .append(" ", new ImageSpan(this, R.drawable.sunrise), 0);
            greeting_message.setText(builder);
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("Hi...Good Afternoon")
                    .append(" ")
                    .append(" ", new ImageSpan(this, R.drawable.afternoon), 0);
            greeting_message.setText(builder);


        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("Hi...Good Evening")
                    .append(" ")
                    .append(" ", new ImageSpan(this, R.drawable.sunset), 0);
            greeting_message.setText(builder);


        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("Hi...Good Night")
                    .append(" ")
                    .append(" ", new ImageSpan(this, R.drawable.night), 0);
            greeting_message.setText(builder);

        }
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken:" + accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                            userId = mAuth.getCurrentUser().getUid();
                            UserDetails userDetails = new UserDetails(name, userId);
                            database.collection("Users")
                                    .document(userId).set(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Toast.makeText(LoginScreen.this, "Saved", Toast.LENGTH_SHORT).show();
                                }
                            });
                            // Sign in success, update UI with the signed-in user's information
                            hideProgress();
                            //  Log.d(TAG, "signInWithCredential:success");

                            LoadHomedashboard();

                        } else {
                            // If sign in fails, display a message to the user.
                            hideProgress();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginScreen.this, "Authentication failed./n" + task.getException(),
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }

    public void onClick(View v) {
        if (v == fb) {
            loginButton.performClick();
        }
    }

    private void signIN() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            showProgress();
            handleSignResult(task);
        }
    }

    private void handleSignResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount acc = task.getResult(ApiException.class);
            FirebaseGoogleAuth(acc);


        } catch (Exception e) {
            Toast.makeText(this, "Sign in Failed", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {

        AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    userId = mAuth.getCurrentUser().getUid();
                    UserDetails userDetails = new UserDetails(name, userId);

                    database.collection("Users")
                            .document(userId).set(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //  Toast.makeText(LoginScreen.this, "Saved", Toast.LENGTH_SHORT).show();
                        }
                    });
                    hideProgress();
                    LoadHomedashboard();


                } else {
                    hideProgress();
                    Toast.makeText(LoginScreen.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void LoadHomedashboard() {
        startActivity(new Intent(LoginScreen.this, HomeDashboard.class));
        finish();
    }

    private void showProgress() {
        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.custom_progress_layout);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    private void hideProgress() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

}