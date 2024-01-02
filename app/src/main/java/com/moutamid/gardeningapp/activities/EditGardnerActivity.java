package com.moutamid.gardeningapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.moutamid.gardeningapp.utilis.Constants;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.databinding.ActivityEditGardnerBinding;
import com.moutamid.gardeningapp.models.UserModel;

import java.util.UUID;

public class EditGardnerActivity extends AppCompatActivity {
    ActivityEditGardnerBinding binding;
    private static final int PICK_FROM_GALLERY = 1001;
    UserModel userModel;
    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditGardnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.toolbar.title.setText("Edit Profile");

        Constants.initDialog(this);
        Constants.showDialog();

        Constants.databaseReference().child(Constants.USERS).child(Constants.auth().getCurrentUser().getUid())
                .get().addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        userModel = dataSnapshot.getValue(UserModel.class);
                        Stash.put(Constants.STASH_USER, userModel);
                        binding.name.getEditText().setText(userModel.getName());
                        binding.email.getEditText().setText(userModel.getEmail());
                        binding.address.getEditText().setText(userModel.getAddress());
                        binding.latitude.getEditText().setText(userModel.getLatitude() + "");
                        binding.longitude.getEditText().setText(userModel.getLongitude() + "");
                        Glide.with(this).load(userModel.getImage()).placeholder(R.drawable.profile_icon).into(binding.profile);
                    }
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(EditGardnerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        binding.update.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                if (imageUri != null) {
                    uploadImage();
                } else {
                    updateUser(userModel.getImage());
                }
            }
        });

        binding.profile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent, ""), PICK_FROM_GALLERY);
        });
    }

    private void updateUser(String image) {
        UserModel model = new UserModel(
                Constants.auth().getCurrentUser().getUid(),
                binding.name.getEditText().getText().toString(),
                binding.address.getEditText().getText().toString(),
                userModel.getEmail(), userModel.getPassword(),
                Double.parseDouble(binding.latitude.getEditText().getText().toString()),
                Double.parseDouble(binding.longitude.getEditText().getText().toString()),
                userModel.isGardener(), image, userModel.getList()
        );

        Stash.put(Constants.STASH_USER, model);
        Constants.databaseReference().child(Constants.USERS).child(Constants.auth().getCurrentUser().getUid()).setValue(model)
                .addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private void uploadImage() {
        Constants.storageReference(Constants.auth().getCurrentUser().getUid())
                .child("Images").child(UUID.randomUUID().toString()).putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        updateUser(uri.toString());
                    });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean valid() {
        if (binding.name.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Name is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.address.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Address is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.longitude.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Longitude is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.latitude.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Latitude is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).placeholder(R.drawable.profile_icon).into(binding.profile);
        }
    }

}