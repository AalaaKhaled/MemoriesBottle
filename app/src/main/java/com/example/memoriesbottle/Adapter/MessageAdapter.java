package com.example.memoriesbottle.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.memoriesbottle.Model.Message;
import com.example.memoriesbottle.Model.User;
import com.example.memoriesbottle.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    List<Message> messages;
    Context mcontext;
    FirebaseUser firebaseUser;
    public static final int INCOMING_MESSAGE =1;
    public static final int OUTGOING_MESSAGE =2;

    public MessageAdapter(List<Message> messages, Context mcontext) {
        this.messages = messages;
        this.mcontext = mcontext;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            return OUTGOING_MESSAGE;
        }
        return INCOMING_MESSAGE;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == INCOMING_MESSAGE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_incoming,parent,false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_outcoming,parent,false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Message message = messages.get(position);
        holder.message.setText(message.getContent());
        //  if (!message.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
        //   int Id = message.getSenderId().to;
        // holder.name.setText(message.getSenderId());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(message.getSenderId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Log.i("Alaa",message.getSenderId());
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    Glide.with(mcontext).load(user.getImageurl()).into(holder.image_profile);
                    holder.name.setText(user.getUsername());
                }
                         /*
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            String username = ds.child("username").getValue(String.class);
                            String imageurl = ds.child("imageurl").getValue(String.class);
                            holder.name.setText(username);
                            //Toast.makeText(mcontext, username+" "+imageurl, Toast.LENGTH_SHORT).show();
                            Log.i("Alaa",username+" "+imageurl);
                            Glide.with(mcontext).load(imageurl).into(holder.image_profile);
*/


            }

            //}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //  }
        //holder.name.setText("Alaa");
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (message.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    AlertDialog alertDialog = new AlertDialog.Builder(mcontext).create();
                    alertDialog.setTitle("Do You Want TO Delete?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("Messages").child(message.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(mcontext, "Deleted Message !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        if (messages == null)return 0;
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,message;
        ImageView image_profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            image_profile = itemView.findViewById(R.id.image_profile);
        }
    }

}
