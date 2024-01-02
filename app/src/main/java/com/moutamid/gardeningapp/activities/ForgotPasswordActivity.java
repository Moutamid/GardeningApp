package com.moutamid.gardeningapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.moutamid.gardeningapp.utilis.Constants;
import com.moutamid.gardeningapp.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {
    ActivityForgotPasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.reset.setOnClickListener(v -> {
            if (binding.email.getEditText().getText().toString().isEmpty()){
                Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show();
            } else {
                Constants.showDialog();
                Constants.auth().sendPasswordResetEmail(
                        binding.email.getEditText().getText().toString()
                ).addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, "Check your inbox for reset link", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
    }

}