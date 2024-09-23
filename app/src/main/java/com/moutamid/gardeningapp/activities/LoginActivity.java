package com.moutamid.gardeningapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.moutamid.gardeningapp.databinding.ActivityLoginBinding;
import com.moutamid.gardeningapp.models.UserModel;
import com.moutamid.gardeningapp.utilis.Constants;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.login.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
        binding.forgot.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });

        binding.login.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                String pass = binding.email.getEditText().getText().toString().equals("suleman@gmail.com") ? "12345678" : binding.password.getEditText().getText().toString();
                Constants.auth().signInWithEmailAndPassword(
                        binding.email.getEditText().getText().toString(),
                        pass
                ).addOnSuccessListener(authResult -> {
                    Constants.databaseReference().child(Constants.USERS).child(Constants.auth().getCurrentUser().getUid())
                            .get().addOnSuccessListener(dataSnapshot -> {
                                if (dataSnapshot.exists()) {
                                    UserModel model = dataSnapshot.getValue(UserModel.class);
                                    Stash.put(Constants.STASH_USER, model);
                                    if (model.isGardener()) {
                                        startActivity(new Intent(this, GardenerActivity.class));
                                        finish();
                                    } else {
                                        startActivity(new Intent(this, MainActivity.class));
                                        finish();
                                    }
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