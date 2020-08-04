package com.example.memoriesbottle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.base.BaseActivity;
import com.example.memoriesbottle.Model.Room;
import com.example.memoriesbottle.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class EditRoomActivity extends BaseActivity {
    ImageView image_room;
    TextView tv_change;
    MaterialEditText name,description;
    FirebaseUser firebaseUser;
    Uri imageUri;
    StorageTask uploadTask;
    StorageReference storageRef;
    Button save;
    String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);
        image_room = findViewById(R.id.image_room);
        save = findViewById(R.id.save);
        tv_change = findViewById(R.id.tv_change);
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);

        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("UploadsRoom");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Rooms").child(roomId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Room room = dataSnapshot.getValue(Room.class);
                if (room != null){

                    name.setText(room.getName());
                    description.setText(room.getDescription());
                    Glide.with(getApplicationContext()).load(room.getRoom_img()).into(image_room);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).setCropShape(CropImageView.CropShape.OVAL).start(EditRoomActivity.this);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(name.getText().toString(),description.getText().toString());

            }

        });
    }

    private void updateProfile(String name, String description) {
        showProgressDialog(R.string.wait);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Rooms").child(roomId);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("name",name);
        hashMap.put("description",description);
        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressDialog();
                //Toast.makeText(EditRoomActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    private String getFileExtention(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){
        showProgressDialog(R.string.upload);
        if (imageUri != null){
            final StorageReference  filereference = storageRef.child(System.currentTimeMillis()+"."+getFileExtention(imageUri));
           // Toast.makeText(activity, "extension  :  "+getFileExtention(imageUri), Toast.LENGTH_SHORT).show();
            uploadTask = filereference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String myURL = downloadUri.toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Rooms").child(roomId);
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("room_img",""+myURL);
                        reference.updateChildren(hashMap);
                        hideProgressDialog();
                    }else {
                        Toast.makeText(activity, "Failed...", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(activity, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(activity, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            uploadImage();
        }else{
            Toast.makeText(activity, "Something gone wrong!", Toast.LENGTH_SHORT).show();
        }
    }
}
