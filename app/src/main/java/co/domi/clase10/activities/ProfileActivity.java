package co.domi.clase10.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import co.domi.clase10.R;
import co.domi.clase10.model.User;
import co.domi.clase10.util.UtilDomi;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private Button saveProfileBtn, editProfileBtn;
    private ImageView imageProfile;
    private User myUser;
    private static final int GALLERY_CALLBACK = 1;
    private String path;
    private FirebaseStorage storage;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        }, 1);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        myUser = (User) getIntent().getExtras().getSerializable("myUser");

        saveProfileBtn = findViewById(R.id.saveProfileBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        imageProfile = findViewById(R.id.imageProfile);


        editProfileBtn.setOnClickListener(
                v -> {
                    Intent i = new Intent(Intent.ACTION_PICK);
                    i.setType("image/*");
                    startActivityForResult(i, GALLERY_CALLBACK);
                }
        );


        saveProfileBtn.setOnClickListener(
                v -> {
                    if (path == null) return;
                    try {
                        String name = UUID.randomUUID().toString();
                        FileInputStream fis = new FileInputStream(new File(path));
                        storage.getReference().child("profiles").child(name).putStream(fis).addOnCompleteListener(
                                task -> {
                                    if(task.isSuccessful()){
                                        myUser.setPhotoId(name);
                                        db.collection("users").document(myUser.getId()).set(myUser);
                                        finish();
                                    }
                                }
                        );
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        );

        loadPhoto();

    }

    private void loadPhoto() {
        db.collection("users").document(myUser.getId()).get().addOnCompleteListener(
                task -> {
                    myUser = task.getResult().toObject(User.class);
                    if(myUser.getPhotoId() != null) {
                        storage.getReference().child("profiles").child(myUser.getPhotoId()).getDownloadUrl().addOnCompleteListener(
                                urlTask -> {
                                    String url = urlTask.getResult().toString();
                                    Glide.with(imageProfile).load(url).into(imageProfile);
                                }
                        );
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CALLBACK && resultCode == RESULT_OK) {
            Uri photoUri = data.getData();
            path = UtilDomi.getPath(this, photoUri);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            imageProfile.setImageBitmap(bitmap);
        }
    }
}