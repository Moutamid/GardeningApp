package com.moutamid.gardeningapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.moutamid.gardeningapp.databinding.ActivitySplashScreenBinding;
import com.moutamid.gardeningapp.utilis.Constants;
import com.moutamid.gardeningapp.models.UserModel;

public class SplashScreenActivity extends AppCompatActivity {
    ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        new Handler().postDelayed(() -> {
            if (Constants.auth().getCurrentUser() != null) {
                binding.progressLayout.setVisibility(View.VISIBLE);
                Constants.databaseReference().child(Constants.USERS).child(Constants.auth().getCurrentUser().getUid())
                        .get().addOnSuccessListener(dataSnapshot -> {
                            if (dataSnapshot.exists()){
                                UserModel model = dataSnapshot.getValue(UserModel.class);
                                Stash.put(Constants.STASH_USER, model);
                                if (model.isGardener()){
                                    startActivity(new Intent(SplashScreenActivity.this, GardenerActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        }).addOnFailureListener(e -> {
                            runOnUiThread(() -> Toast.makeText(SplashScreenActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                        });
            } else {
                startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
                finish();
            }
        }, 2000);
    }
}