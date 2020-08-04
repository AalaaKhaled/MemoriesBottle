package com.example.memoriesbottle.Fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.base.BaseFragment;
import com.example.memoriesbottle.EditProfileActivity;
import com.example.memoriesbottle.FollowersActivity;
import com.example.memoriesbottle.Model.MyLocation;
import com.example.memoriesbottle.Model.Post;
import com.example.memoriesbottle.Model.User;
import com.example.memoriesbottle.PostActivity;
import com.example.memoriesbottle.PostDetailActivity;
import com.example.memoriesbottle.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends BaseFragment implements OnMapReadyCallback {

    ImageView image_profile;
    TextView posts,followers,following,fullname,rank,username;
    Button edit_profile;
    FirebaseUser firebaseUser;
    String profileId;
    ImageButton my_photo,saved_photo;
   // RecyclerView recyclerView;
   // MyFotoAdapter myFotoAdapter;
    //List<Post> postList;

    //List<String> mySaves;
    //RecyclerView recyclerView_save;
    //Save
    //MyFotoAdapter myFotoAdapter_save;
    //List<Post> postList_save;

    //
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    ImageView button;
    Button save;
    Location mylocation;
    GoogleMap googleMap;
    DatabaseReference databaseReference;
    MapView mapView;
   // String profileId;
    Context mContext;
    String locationId;
    //
    ImageView saved;
    ProgressBar progressBar;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs =getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("profileid","none");
        //Toast.makeText(getContext(), ""+profileId, Toast.LENGTH_SHORT).show();
        progressBar = view.findViewById(R.id.progress_circular);


        image_profile = view.findViewById(R.id.image_profile);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        fullname = view.findViewById(R.id.fullname);
        rank = view.findViewById(R.id.rank);
        username = view.findViewById(R.id.username);
        //options = view.findViewById(R.id.options);
        edit_profile = view.findViewById(R.id.edit_profile);
       // my_photo = view.findViewById(R.id.my_fotos);
//        saved_photo = view.findViewById(R.id.saved_fotos);

        //
        saved = view.findViewById(R.id.saved);
        mapView=view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        //Initialize Fused Location
        client = LocationServices.getFusedLocationProviderClient(getContext());

        button = view.findViewById(R.id.add_map);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw();
            }
        });
        if (profileId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            button.setVisibility(View.VISIBLE);
        }else {
            button.setVisibility(View.GONE);
        }
        if (profileId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            saved.setVisibility(View.VISIBLE);
        }else {
            saved.setVisibility(View.GONE);
        }

        if(ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            //when permission granted
            getLocation();
        }else{
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }


        //

        //recyclerView = view.findViewById(R.id.recycler_view);
        //recyclerView.setHasFixedSize(true);
        //LinearLayoutManager layoutManager = new GridLayoutManager(getContext(),3);
        //recyclerView.setLayoutManager(layoutManager);
       // postList = new ArrayList<>();
       /* myFotoAdapter = new MyFotoAdapter(getContext(),postList);
        recyclerView.setAdapter(myFotoAdapter);

        recyclerView_save = view.findViewById(R.id.recycler_view_save);
        recyclerView_save.setHasFixedSize(true);
        LinearLayoutManager layoutManager_save = new GridLayoutManager(getContext(),3);
        recyclerView_save.setLayoutManager(layoutManager_save);
        postList_save = new ArrayList<>();
        myFotoAdapter_save = new MyFotoAdapter(getContext(),postList_save);
        recyclerView_save.setAdapter(myFotoAdapter_save);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_save.setVisibility(View.GONE);
*/


        userInfo();
        getFollowers();
        getNrPosts();
        //myFotos();
        //mysaves();


        if (profileId.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit Profile");
        }else {
            checkFollow();
        }

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = edit_profile.getText().toString();
                if (btn.equals("Edit Profile")){
                    //go to edit profile
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }else if (btn.equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                    addNotification();

                }else if (btn.equals("following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
/*
        my_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_save.setVisibility(View.GONE);
            }
        });
        saved_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_save.setVisibility(View.VISIBLE);

            }
        });*/
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","follower");
                startActivity(intent);
            }
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","following");
                startActivity(intent);
            }
        });
        /*
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OptionsActivity.class);
                startActivity(intent);
            }
        });*/
        saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SaveFragment()).commit();

            }
        });


        return view;
    }
    private void addNotification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileId);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","Started following you");
        hashMap.put("postid","");
        hashMap.put("ispost",false);

        reference.push().setValue(hashMap);

    }

    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null){
                    return;
                }

                User user = dataSnapshot.getValue(User.class);
                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                rank.setText(""+user.getRank());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileId).exists()){
                    edit_profile.setText("following");
                }else {
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getNrPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)){
                        i++;
                    }

                }

                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
/*
    private void myFotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myFotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void mysaves(){
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mySaves.add(snapshot.getKey());
                }
                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readSaves() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList_save.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    for (String id : mySaves){
                        if (post.getPostId().equals(id)){
                            postList_save.add(post);
                        }
                    }
                }
                myFotoAdapter_save.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
@Override
public void onStart() {
    super.onStart();
    mapView.onStart();
}

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

    }




    private void saveOnDB(double longitude , double latitude ,String CountyName ,String gov) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Locations");
        locationId = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("locatinId", locationId);
        hashMap.put("Longitude", longitude);
        hashMap.put("Latitude", latitude);
        hashMap.put("CountyName", CountyName);
        hashMap.put("gov", gov);
        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("postid", "new");
        hashMap.put("description", "");
        reference.child(locationId).setValue(hashMap);
        hideProgressDialog();

    }


    private void getLocation() {
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if(location !=null){
                    mylocation = location;

                }else{
                    Toast.makeText(getContext(), "null loction", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void draw(){
        if (mylocation == null)return;
        if(googleMap ==null)return;
        LatLng latLng = new LatLng(mylocation.getLatitude(),
                mylocation.getLongitude());
        //create marker option
        MarkerOptions options = new MarkerOptions().position(latLng).title("Latidude : " + latLng.latitude + "Longitude : " + latLng.longitude).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        //zoom map
        //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        //add Marker on map
        googleMap.addMarker(options);
        try {
            //intialize geoCoder
            Geocoder geocoder =new Geocoder(getContext() ,
                    Locale.getDefault());
            // Intialize Address List
            List<Address> addresses = geocoder.getFromLocation(
                    mylocation.getLatitude(),mylocation.getLongitude(),1);
            String CountyName=addresses.get(0).getCountryName();
            String gov = addresses.get(0).getAdminArea();
            Double Latitude = addresses.get(0).getLatitude();
            Double Longitude = addresses.get(0).getLongitude();
            //set Latitude
            saveOnDB(Longitude, Latitude ,CountyName ,gov);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Locations");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> contryRank =new ArrayList<>();
                    int counter =0;
                    for (DataSnapshot s : dataSnapshot.getChildren()){
                        MyLocation L = s.getValue(MyLocation.class);
                        if (profileId.equals(L.getPublisher())) {
                            if (!contryRank.contains(L.getCountyName())){
                                counter++;
                                contryRank.add(L.getCountyName());
                            }

                        }
                    }
                    HashMap<String,Object> hashMaplocation = new HashMap<>();
                    hashMaplocation.put("rank",counter);
                    FirebaseDatabase.getInstance().getReference("Users").child(profileId).updateChildren(hashMaplocation);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Intent intent = new Intent(getContext(), PostActivity.class);
            intent.putExtra("locationid",locationId);
            getContext().startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //when permission granted call method
                getLocation();

            }
        }
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (googleMap != null){
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View row = getLayoutInflater().inflate(R.layout.custom_address,null);
                    final TextView t = row.findViewById(R.id.text);
                    TextView seemore = row.findViewById(R.id.seeMore);
                    t.setText(marker.getTitle());
                    seemore.setText(marker.getSnippet());


                    return row;
                }
            });
        }

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String postId = marker.getTag().toString();
                if (marker.getTag().equals("")){
                    return;
                }else{

                    SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("postid",postId);
                    editor.apply();
                    ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailsFragment()).commit();
                   /*
                    Intent intent = new Intent(getContext(), PostDetailActivity.class);
                    intent.putExtra("postid",postId);
                    getContext().startActivity(intent);
                    */

                }
            }
        });

        googleMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(final Marker marker) {
                final String postId = marker.getTag().toString();
                DatabaseReference reference =  FirebaseDatabase.getInstance().getReference("Posts").child(postId);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Post post = dataSnapshot.getValue(Post.class);
                        final String postLocationId = post.getLocationId();
                        final String postUserId = post.getPublisher();
                        if (postUserId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                            alertDialog.setTitle("Do You Want TO Remove Marker?");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference().child("Locations").child(postLocationId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("Locations");
                                                reference3.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        List<String> contryRank =new ArrayList<>();
                                                        int counter =0;
                                                        for (DataSnapshot s : dataSnapshot.getChildren()){
                                                            MyLocation L = s.getValue(MyLocation.class);
                                                            if (L.getPublisher().equals(profileId)) {
                                                                if (!contryRank.contains(L.getCountyName())){
                                                                    counter++;
                                                                    contryRank.add(L.getCountyName());
                                                                }

                                                            }
                                                        }
                                                        HashMap<String,Object> hashMaplocation = new HashMap<>();
                                                        hashMaplocation.put("rank",counter);
                                                        FirebaseDatabase.getInstance().getReference("Users").child(profileId).updateChildren(hashMaplocation);

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                                //Toast.makeText(getContext(), "Deleted Location! "+marker.getTitle(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                    FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                //  Toast.makeText(getContext(), "Deleted Post ! "+marker.getTitle(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });



                                    dialog.dismiss();
                                    ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                                }
                            });
                            alertDialog.show();



                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Locations");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()){
                    final MyLocation user = s.getValue(MyLocation.class);

                    if (profileId.equals(user.getPublisher())) {

                        final String postId = user.getPostid();
                        String locationIdlo = user.getLocatinId();
                        // Toast.makeText(getContext(), ""+user.getLocatinId(), Toast.LENGTH_SHORT).show();
                        //
                        if (postId.equals("")){
                            FirebaseDatabase.getInstance().getReference("Locations").child(locationIdlo).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.i("Alaa2","Deleted الحمدلله");
                                    if (task.isSuccessful()){
                                        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("Locations");
                                        reference3.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                List<String> contryRank =new ArrayList<>();
                                                int counter =0;
                                                for (DataSnapshot s : dataSnapshot.getChildren()){
                                                    MyLocation L = s.getValue(MyLocation.class);
                                                    if (L.getPublisher().equals(profileId)) {
                                                        if (!contryRank.contains(L.getCountyName())){
                                                            counter++;
                                                            contryRank.add(L.getCountyName());
                                                        }

                                                    }
                                                }
                                                HashMap<String,Object> hashMaplocation = new HashMap<>();
                                                hashMaplocation.put("rank",counter);
                                                FirebaseDatabase.getInstance().getReference("Users").child(profileId).updateChildren(hashMaplocation);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        //Toast.makeText(getContext(), "Deleted Location! "+marker.getTitle(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                    /*
                                    // Toast.makeText(getContext(), "Deleted الحمدلله", Toast.LENGTH_SHORT).show();
                                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Users").child(user.getPublisher());
                                    reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            User userRank = dataSnapshot.getValue(User.class);
                                            int rankUser =userRank.getRank()-1;
                                            HashMap<String,Object> hashMaplocation = new HashMap<>();
                                            hashMaplocation.put("rank",rankUser);
                                            FirebaseDatabase.getInstance().getReference("Users").child(user.getPublisher()).updateChildren(hashMaplocation);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }*/
                            });


                        }else {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getPublisher());
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user1 = dataSnapshot.getValue(User.class);
                                    String username = user1.getUsername();
                                    Log.i("Alaa2",""+user1.getRank());
                                    // Glide.with(getContext()).load(user1.getImageurl())
                                    LatLng location = new LatLng(user.getLatitude(), user.getLongitude());
                                    Marker marker = googleMap.addMarker(new MarkerOptions().position(location).title(username).snippet(user.getDescription()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                    marker.setTag(postId);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
                    }else{
                        //   Toast.makeText(getContext(), "not equal", Toast.LENGTH_SHORT).show();
                    }

                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    ///

    ///
}
