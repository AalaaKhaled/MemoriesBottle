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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class PostActivity extends BaseActivity {
    Uri imageUri;
    String myURL = "";
    StorageTask uploadTask;
    StorageReference storageReference;
    ImageView close,image_added;
    TextView post;
    EditText description;
    String locationid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        init();
        Intent intent = getIntent();
        locationid = intent.getStringExtra("locationid");
        Toast.makeText(activity, ""+locationid, Toast.LENGTH_SHORT).show();

        //
        HashMap<String,Object> hashMaplocation = new HashMap<>();
        hashMaplocation.put("postid","");
        hashMaplocation.put("description","");
        FirebaseDatabase.getInstance().getReference("Locations").child(locationid).updateChildren(hashMaplocation);

        //
        storageReference = FirebaseStorage.getInstance().getReference("Posts");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this,MainActivity.class));
                finish();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog(R.string.post);
                uploadImge();
            }
        });
       // image_added.setOnClickListener(new View.OnClickListener() {
          //  @Override
            //public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).setCropShape(CropImageView.CropShape.OVAL).start(PostActivity.this);
            //}
        //});
    }

    private void uploadImge() {
       // showProgressDialog(R.string.post);
        if (imageUri != null){
            //final StorageReference filerefrence = storageReference.child(System.currentTimeMillis()+"."+getFileExtention(imageUri));
            final StorageReference filerefrence = storageReference.child(System.currentTimeMillis()+".jpg");
            uploadTask = filerefrence.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filerefrence.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myURL =downloadUri.toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                        String postId = reference.push().getKey();
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("postId",postId);
                        hashMap.put("postImage",myURL);
                        hashMap.put("locationId",locationid);
                        hashMap.put("description",description.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.child(postId).setValue(hashMap);
                        //
                        HashMap<String,Object> hashMaplocation = new HashMap<>();
                        hashMaplocation.put("postid",postId);
                        hashMaplocation.put("description",description.getText().toString());
                        FirebaseDatabase.getInstance().getReference("Locations").child(locationid).updateChildren(hashMaplocation);

                        //
                        hideProgressDialog();
                        startActivity(new Intent(PostActivity.this,MainActivity.class));
                        finish();
                        /*
                        Intent intent = new Intent();
                        intent.putExtra("locationnid", locationid);
                        intent.putExtra("posttid",postId);
                        startActivity(intent);
                        finish();*/
                        /*
                        finish();
                        Intent intent = new Intent(PostActivity.this , MainActivity.class);
                        intent.putExtra("locationnid", locationid);
                        intent.putExtra("posttid",postId);
                        //startActivity(intent);
*/
                         /*
                        Intent intent=new Intent(PostActivity.this,MainActivity.class);
                        intent.putExtra("locationnid", locationid);
                        intent.putExtra("posttid",postId);
                        setResult(2,intent);
                        //startActivityForResult(intent, 2);
                        finish();*/

                    }else {
                        Toast.makeText(activity, "Failed...", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(activity, "No Image Selected", Toast.LENGTH_SHORT).show();
        }

    }

    private String getFileExtention(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            image_added.setImageURI(imageUri);
        }else{
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this,MainActivity.class));
            finish();
        }

    }

    private void init() {
        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
    }
}
