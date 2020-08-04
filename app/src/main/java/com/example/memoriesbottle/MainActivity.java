package com.example.memoriesbottle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memoriesbottle.Fragment.AddRommChat;
import com.example.memoriesbottle.Fragment.AllMapsFragment;
import com.example.memoriesbottle.Fragment.HomeFragment;
import com.example.memoriesbottle.Fragment.NotificationFragment;
import com.example.memoriesbottle.Fragment.ProfileFragment;
import com.example.memoriesbottle.Fragment.SearchFragment;
import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottom_navigation;
    Fragment selectedfragment = null;
    TextView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        });*/
        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String publisher = intent.getString("publisherid");
            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();

        } else {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        }


    }
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedfragment = new HomeFragment();
                            break;
                        case R.id.nav_world:
                            selectedfragment = new AllMapsFragment();
                            break;
                        case R.id.nav_chat:
                            selectedfragment = new AddRommChat();
                            break;
                        case R.id.nav_heart:
                            selectedfragment = new NotificationFragment();
                            break;
                        case R.id.nav_profile:
                            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            selectedfragment = new ProfileFragment();
                            break;
                    }
                    if (selectedfragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedfragment).commit();
                    }

                    return true;
                }
            };

    @Override
    public void onStart() {
        super.onStart();
        //user
        FirebaseAuth mAuth =FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            updateUI();
        }


    }
    //here
    private void updateUI() {
        Toast.makeText(this,"Byeeeeeee",Toast.LENGTH_LONG).show();
        Intent accountIntent = new Intent(this,LoginActivity.class);
        startActivity(accountIntent);
        finish();
    }


    public void logOut(View view) {

        FirebaseAuth.getInstance().signOut();
        // LoginManager.getInstance().logOut();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();

    }


    public void search(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SearchFragment()).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AllMapsFragment()).commit();


        }
    }

    public void ChatRoom(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AddRommChat()).commit();


    }
}
