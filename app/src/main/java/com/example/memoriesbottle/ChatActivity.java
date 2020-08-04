package com.example.memoriesbottle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.memoriesbottle.Adapter.MessageAdapter;
import com.example.memoriesbottle.Model.Message;
import com.example.memoriesbottle.Model.Room;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    EditText message;
    ImageView send;
    RecyclerView recyclerView;
    String roomId;
    MessageAdapter adapter;
    List<Message> messages;
    LinearLayoutManager layoutManager;
    ImageView room_img,back;
    TextView room_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        //layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages,this);
        recyclerView.setAdapter(adapter);
        room_img = findViewById(R.id.image_room);
        room_name = findViewById(R.id.room_name);


        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Rooms").child(roomId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Room room = snapshot.getValue(Room.class);
                room_name.setText(room.getName());
                Glide.with(ChatActivity.this).load(room.getRoom_img()).into(room_img);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       // this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageContent = message.getText().toString();
                if (messageContent.trim().isEmpty()) {
                    return;
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages");
                String messageId = reference.push().getKey();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", messageId);
                hashMap.put("senderId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("senderName", "");
                hashMap.put("roomId",roomId);
                hashMap.put("content", messageContent);
                reference.child(messageId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            message.setText("");

                        }
                    }
                });
            }
        });

        readMessages();


    }
    private void readMessages() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message mess = snapshot.getValue(Message.class);
                    if (mess.getRoomId().equals(roomId)){
                        messages.add(mess);
                    }

                }
                adapter.notifyDataSetChanged();
                layoutManager.scrollToPosition(messages.size()-1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /*
    @Override
    protected void onResume() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();
    }

    @Override
    protected void onStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onStart();
    }
    */
}
