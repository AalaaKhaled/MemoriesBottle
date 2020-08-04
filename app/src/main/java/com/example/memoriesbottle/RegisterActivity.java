package com.example.memoriesbottle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.base.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends BaseActivity {

    MaterialEditText email, password,fullname,username;
    Button register;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        auth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog(R.string.wait);

                    register();

            }
        });

    }
    private void init() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        fullname = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        register = findViewById(R.id.register);

    }
    public void register() {
        String emailNameText = email.getText().toString();
        String passwordText = password.getText().toString();
        final String fullnameText = fullname.getText().toString();
        final String usernameText = username.getText().toString();

        if (emailNameText.trim().isEmpty()) {
            email.setError("required");
            return;
        }
        if (passwordText.trim().isEmpty() || passwordText.length() < 6) {
            password.setError("required");
            return;
        }

        Toast.makeText(this, emailNameText, Toast.LENGTH_SHORT).show();
        auth.createUserWithEmailAndPassword(emailNameText, passwordText)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser userfi = auth.getCurrentUser();

                            String ID = userfi.getUid();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(ID);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", ID);
                            map.put("username", usernameText);
                            map.put("fullname", fullnameText);
                            map.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/memories-bottle.appspot.com/o/ic_profile.png?alt=media&token=3c825429-3f1c-43b5-85cc-a07e0ebb69f6");
                            map.put("bio", "");
                            map.put("rank", 0);
                            map.put("email", userfi.getEmail());
                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                       // hideProgressDialog();
                                        //Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        //startActivity(intent);
                                        //finish();
                                        Toast.makeText(RegisterActivity.this, "Registration successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        hideProgressDialog();
                                        Toast.makeText(RegisterActivity.this, "please check verification", Toast.LENGTH_SHORT).show();

                                    } else {
                                        hideProgressDialog();
                                        Toast.makeText(RegisterActivity.this, "failed to send email" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        }


                    }
                });
    }

    public void GoToLogin(View view) {
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));

    }
}
