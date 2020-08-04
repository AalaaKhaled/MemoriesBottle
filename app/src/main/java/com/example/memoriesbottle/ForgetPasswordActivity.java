package com.example.memoriesbottle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    EditText EmailPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        EmailPass =findViewById(R.id.WriteEmail);
    }

    public void sendForgetPasswordEmail(View view) {
        //progressbar
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(EmailPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //hideprogressbar
                if (task.isSuccessful()){
                    Toast.makeText(ForgetPasswordActivity.this, "Password reset", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ForgetPasswordActivity.this, "Cannot reset "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
