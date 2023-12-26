package com.moutamid.gardeningapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.moutamid.gardeningapp.Constants;
import com.moutamid.gardeningapp.MainActivity;
import com.moutamid.gardeningapp.databinding.ActivityRegisterBinding;
import com.moutamid.gardeningapp.models.UserModel;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.login.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        binding.register.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                Constants.auth().createUserWithEmailAndPassword(
                        binding.email.getEditText().getText().toString(),
                        binding.password.getEditText().getText().toString()
                ).addOnSuccessListener(authResult -> {
                    UserModel model = new UserModel(
                            Constants.auth().getCurrentUser().getUid(),
                            binding.name.getEditText().getText().toString(),
                            binding.address.getEditText().getText().toString(),
                            binding.email.getEditText().getText().toString(),
                            binding.password.getEditText().getText().toString(),
                            Double.parseDouble(binding.latitude.getEditText().getText().toString()),
                            Double.parseDouble(binding.longitude.getEditText().getText().toString()),
                            binding.isGardener.isChecked(), "", new ArrayList<>()
                    );
                    Stash.put(Constants.STASH_USER, model);
                    Constants.databaseReference().child(Constants.USERS).child(Constants.auth().getCurrentUser().getUid()).setValue(model)
                            .addOnSuccessListener(unused -> {
                                Constants.dismissDialog();
                                if (model.isGardener()) {
                                    startActivity(new Intent(this, GardenerActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(this, MainActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(e -> {
                                Constants.dismissDialog();
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
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
        if (binding.email.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Email is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getEditText().getText().toString()).matches()) {
            Toast.makeText(this, "Email is not valid", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.password.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Password is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
    }
}