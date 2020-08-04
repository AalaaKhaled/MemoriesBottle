package com.example.memoriesbottle.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.memoriesbottle.ChatActivity;
import com.example.memoriesbottle.EditRoomActivity;
import com.example.memoriesbottle.FollowersActivity;
import com.example.memoriesbottle.MainActivity;
import com.example.memoriesbottle.Model.Room;
import com.example.memoriesbottle.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    List<Room> rooms;
    public Context mContext;
    FirebaseUser firebaseUser;

    public RoomAdapter(List<Room> rooms, Context mContext) {
        this.rooms = rooms;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.room_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Room item=rooms.get(position);
        holder.name.setText(item.getName());
        holder.desc.setText(item.getDescription());
        Glide.with(mContext).load(item.getRoom_img()).into(holder.room_img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("roomId", item.getId());
                mContext.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (item.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Do You Want TO Update?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(mContext, EditRoomActivity.class);
                            intent.putExtra("roomId", item.getId());
                            mContext.startActivity(intent);
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
        if(rooms==null)return 0;
        return rooms.size();
    }
    public Room getRoom(int adapterPosition) {
        return rooms.get(adapterPosition);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,desc;
        ImageView room_img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            desc = itemView.findViewById(R.id.description);
            room_img = itemView.findViewById(R.id.room_img);
        }
    }
}
