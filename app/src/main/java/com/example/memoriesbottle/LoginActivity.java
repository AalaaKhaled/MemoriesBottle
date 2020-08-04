package com.example.memoriesbottle;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.base.BaseActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends BaseActivity {
    EditText email;
    EditText password;
    Button login;
    TextView register;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN =1 ;
    TextView forgetPassword;
    private CallbackManager mCallbackManager;
    private static final String TAG="Facbook";
    ImageView facebookBtn,signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        signInButton = findViewById(R.id.google);
        // Configure Google Sign In
        FirebaseAuth auth =FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);

        forgetPassword = findViewById(R.id.forgetPassword);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog(R.string.loading);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        FirebaseAuth mfirebaseAuth = FirebaseAuth.getInstance();
        // Initialize Facebook Login button
        facebookBtn = findViewById(R.id.facebookBtn);



        mCallbackManager = CallbackManager.Factory.create();
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////
                showProgressDialog(R.string.loading);
                facebookBtn.setEnabled(false);
                ////
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this , Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                        // ...
                    }
                });
            }
        });



        if (auth.getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified() ){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();

        }

    }


    public void GoToMain(View view) {
        showProgressDialog(R.string.loading);
        String emailNameText =email.getText().toString();
        String passwordText =password.getText().toString();
        if(emailNameText.trim().isEmpty()){
            email.setError("required");
            return;
        }
        if(passwordText.trim().isEmpty()){
            password.setError("required");
            return;
        }
        Toast.makeText(this, emailNameText, Toast.LENGTH_SHORT).show();
        //Auth

        FirebaseAuth auth =FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(emailNameText, passwordText)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                hideProgressDialog();
                                //
                                if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            hideProgressDialog();
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            hideProgressDialog();
                                        }
                                    });
                                } else {
                                    hideProgressDialog();
                                    Toast.makeText(LoginActivity.this, "Verification failed.", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();}
                        }


                                //

    }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                Toast.makeText(activity, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseAuth mAuth =FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        ////////
        if (currentUser != null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
            Intent accountIntent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(accountIntent);
            finish();
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account != null)firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                Toast.makeText(activity, "e message", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser userfi = auth.getCurrentUser();

                            String ID = userfi.getUid();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(ID);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", ID);
                            map.put("username", userfi.getDisplayName());
                            map.put("fullname", "");
                            map.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/memories-bottle.appspot.com/o/ic_profile.png?alt=media&token=3c825429-3f1c-43b5-85cc-a07e0ebb69f6");
                            map.put("bio", "");
                            map.put("rank", 0);
                            map.put("email", userfi.getEmail());

                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        hideProgressDialog();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            hideProgressDialog();
                            Toast.makeText(LoginActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

                    public void forgetPassword(View view) {
                        startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                        //finish();
                    }


                    //facebook
                    private void handleFacebookAccessToken(AccessToken token) {
                        Log.d(TAG, "handleFacebookAccessToken:" + token);
                        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                        mAuth.signInWithCredential(credential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "signInWithCredential:success");
                                            FirebaseUser userface = mAuth.getCurrentUser();
                                            ///
                                            String ID = userface.getUid();

                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(ID);
                                            //  HashMap<Object,String> hashMap = new HashMap<>();
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("id", ID);
                                            map.put("username", userface.getDisplayName());
                                            map.put("fullname", "");
                                            map.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/memories-bottle.appspot.com/o/ic_profile.png?alt=media&token=3c825429-3f1c-43b5-85cc-a07e0ebb69f6");
                                            map.put("bio", "");
                                            map.put("rank", 0);
                                            map.put("email", userface.getEmail());

                                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        hideProgressDialog();

                                                    }
                                                }
                                            });
                                            ///
                                            //updateUI(userfi);
                                            Toast.makeText(activity, "facebook sucessfully", Toast.LENGTH_SHORT).show();
                                            ////
                                            facebookBtn.setEnabled(true);
                                            ////
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            ////
                                            facebookBtn.setEnabled(true);

                                        }

                                        // ...
                                    }
                                });
                    }

    public void GoToRegisterFromLogin(View view) {
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }
}
