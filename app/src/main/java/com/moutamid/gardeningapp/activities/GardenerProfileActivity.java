package com.moutamid.gardeningapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.fxn.stash.Stash;
import com.moutamid.gardeningapp.Constants;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.databinding.ActivityGardenerProfileBinding;
import com.moutamid.gardeningapp.models.GardenerModel;

public class GardenerProfileActivity extends AppCompatActivity {
    ActivityGardenerProfileBinding binding;
    GardenerModel gardenerModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGardenerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gardenerModel = (GardenerModel) Stash.getObject(Constants.PASS_MODEL, GardenerModel.class);



    }
}