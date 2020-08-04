package com.example.memoriesbottle.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.memoriesbottle.Adapter.SaveAdapter;
import com.example.memoriesbottle.Model.Post;
import com.example.memoriesbottle.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaveFragment extends Fragment {

    List<String> mySaves;
    RecyclerView recyclerView_save;
    //Save
    SaveAdapter myFotoAdapter_save;
    List<Post> postList_save;

    public SaveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_save, container, false);
        recyclerView_save = view.findViewById(R.id.recycler_view_save);
        recyclerView_save.setHasFixedSize(true);
        LinearLayoutManager layoutManager_save = new LinearLayoutManager(getContext());
        recyclerView_save.setLayoutManager(layoutManager_save);
        postList_save = new ArrayList<>();
        myFotoAdapter_save = new SaveAdapter(getContext(),postList_save);
        recyclerView_save.setAdapter(myFotoAdapter_save);
        mysaves();
        return view;
    }

    private void mysaves(){
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
    }

}
