package com.example.memoriesbottle.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.base.BaseFragment;
import com.example.memoriesbottle.Model.MyLocation;
import com.example.memoriesbottle.Model.User;
import com.example.memoriesbottle.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllMapsFragment extends BaseFragment implements LocationListener, GoogleMap.OnMarkerClickListener , GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener {

    View view;
    public static final int ROUND = 10;
    private GoogleMap mMap;
    public GoogleApiClient googleApiClient;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mUsers;
    Marker marker;
    MapView mapView;
    Button onMapsearch;
    EditText locationSearch;
    public FusedLocationProviderClient fusedLocationProviderClient;
    int count = 0 ;
    ProgressBar progressBar;


    public AllMapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_all_maps, container, false);
        locationSearch =  view.findViewById(R.id.editText);
        mapView=view.findViewById(R.id.map_view);
        progressBar = view.findViewById(R.id.progress_circular);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                mMap = googleMap;
                // googleMap.setOnMarkerClickListener(this);
                if (mMap != null){
                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
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

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
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



                mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot s : dataSnapshot.getChildren()){
                            final MyLocation user = s.getValue(MyLocation.class);
                            final String description = user.getDescription();
                            final String postId = user.getPostid();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getPublisher());
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user1 = dataSnapshot.getValue(User.class);
                                    String username = user1.getUsername();
                                    // Glide.with(getContext()).load(user1.getImageurl())
                                    LatLng location = new LatLng(user.getLatitude(), user.getLongitude());
                                    Marker marker =  mMap.addMarker(new MarkerOptions().position(location).title(username).snippet(description).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                    marker.setTag(postId);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13));

                        }
                        progressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });
            }
        });
        ChildEventListener mChildEventListener;
        mUsers= FirebaseDatabase.getInstance().getReference("Locations");
        // mUsers.push().setValue(marker);
        onMapsearch = view.findViewById(R.id.search_button);

        onMapsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = locationSearch.getText().toString();
                //Toast.makeText(getContext(), ""+location, Toast.LENGTH_SHORT).show();
                //

                if (!location.trim().equals("")) {
                    List<Address> addressList = null;

                    if (location != null || !location.equals("")) {
                        Geocoder geocoder = new Geocoder(getContext());
                        try {
                            addressList = geocoder.getFromLocationName(location, 1);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (addressList.size() != 0){
                        Address address = addressList.get(0);

                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        // mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                    }else {
                        Toast.makeText(getContext(), "You must enter correct place", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    }
                }else{
                    return;
                }

            }
        });


        return view;
    }



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


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
