package com.moutamid.gardeningapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.databinding.ActivityPendingBinding;

public class PendingActivity extends AppCompatActivity {
    ActivityPendingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityPendingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }
}