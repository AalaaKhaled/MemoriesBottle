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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.base.BaseActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class AddRoomFormActivity extends BaseActivity {
    MaterialEditText title;
    MaterialEditText description;
    Button add;
    ImageView room_img;
    /*
    TextView tv_change;
    FirebaseUser firebaseUser;
    Uri imageUri;
    StorageTask uploadTask;
    StorageReference storageRef;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room_form);
        title=findViewById(R.id.name);
        description=findViewById(R.id.description);
        room_img = findViewById(R.id.room_img);
        //tv_change = findViewById(R.id.tv_change);
        add=findViewById(R.id.add);
        /*
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("UploadsRooms");

        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).setCropShape(CropImageView.CropShape.OVAL).start(AddRoomFormActivity.this);
            }
        });
       */
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRoom(title.getText().toString(),description.getText().toString());

            }

        });

    }

    private void addRoom(String title, String description) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Rooms");
        String roomId = reference.push().getKey();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",roomId);
        hashMap.put("name",title);
        hashMap.put("nameLow",title.toLowerCase());
        hashMap.put("description",description);
        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("room_img","https://firebasestorage.googleapis.com/v0/b/memories-bottle.appspot.com/o/travel.png?alt=media&token=5323b342-7a51-4910-b4c0-90b67aba282b");
        reference.child(roomId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    hideProgressDialog();
                    Toast.makeText(AddRoomFormActivity.this, "room added successfully", Toast.LENGTH_SHORT).show();
                    // startActivity(new Intent(PostActivity.this,MainActivity.class));
                    finish();
                }
            }
        });
    }
    /*
    private String getFileExtention(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){
        showProgressDialog(R.string.upload);
        if (imageUri != null){
            final StorageReference  filereference = storageRef.child(System.currentTimeMillis()+"."+getFileExtention(imageUri));
            Toast.makeText(activity, "extension  :  "+getFileExtention(imageUri), Toast.LENGTH_SHORT).show();
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
    */
}
