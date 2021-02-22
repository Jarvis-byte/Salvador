package com.flowerhunt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flowerhunt.Model.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    TextView login_button, btn_sign_up;
    FirebaseAuth auth;
    FirebaseFirestore database;
    FirebaseUser firebaseUser;
    UserDetails userDetails;
    TextInputEditText inputtxtname, inputtxtemail_address, inputtxtPassword;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        login_button = findViewById(R.id.login_button);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        inputtxtname = findViewById(R.id.inputtxtname);
        inputtxtemail_address = findViewById(R.id.inputtxtemail_address);
        inputtxtPassword = findViewById(R.id.inputtxtPassword);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        firebaseUser = auth.getCurrentUser();

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginScreen.class));
                overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String name = inputtxtname.getText().toString();
        String email = inputtxtemail_address.getText().toString();
        String password = inputtxtPassword.getText().toString();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    userId = auth.getCurrentUser().getUid();
                    UserDetails userDetails = new UserDetails(name, userId);
                    database.collection("Users")
                            .document(userId).set(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterActivity.this, "Hello" + userId, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(RegisterActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void onLoginClick(View view) {
        startActivity(new Intent(this, LoginScreen.class));
        finish();
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}