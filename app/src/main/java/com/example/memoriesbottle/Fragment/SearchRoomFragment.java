package com.example.memoriesbottle.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.memoriesbottle.Adapter.RoomAdapter;
import com.example.memoriesbottle.Model.Room;
import com.example.memoriesbottle.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchRoomFragment extends Fragment {
    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;

    EditText search_bar;




    public SearchRoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_room, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        search_bar = view.findViewById(R.id.search_bar);
        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(roomList,getContext());
        recyclerView.setAdapter(roomAdapter);
        // readUsers();
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchRooms(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return  view;
    }
    private void searchRooms(String s){
        //
        /*
        if(s != null && s.length()>0){
            char[] letters=s.toCharArray();
            String firstLetter = String.valueOf(letters[0]).toUpperCase();
            String remainingLetters = s.substring(1);
            s=firstLetter+remainingLetters;
        }


        Query query = FirebaseDatabase.getInstance().getReference("Rooms").orderByChild("nameLow")
                .startAt(s)
                .endAt(s+"\uf8ff");*/

        //
        /*
        Query query = FirebaseDatabase.getInstance().getReference("Rooms").orderByChild("name")
                .startAt(s)
                .endAt(s+"\uf8ff");
       */
     //startAt(text.toUppercase) and endAt(text.toLowerCase+ "\uf8ff")
        /*
          .startAt(s)
                .endAt(s+"\uf8ff");
        */
        Query query = FirebaseDatabase.getInstance().getReference("Rooms").orderByChild("nameLow")
                .startAt(s)
                .endAt(s+"\uf8ff");


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (search_bar.getText().toString().equals("")) {
                    roomList.clear();
                    roomAdapter.notifyDataSetChanged();

                }else {
                    roomList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Room room = snapshot.getValue(Room.class);
                        roomList.add(room);
                    }

                    roomAdapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
