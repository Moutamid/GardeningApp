package com.moutamid.gardeningapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moutamid.gardeningapp.Constants;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.databinding.ActivityGardenerBinding;
import com.moutamid.gardeningapp.fragments.gardener.BookingFragment;
import com.moutamid.gardeningapp.fragments.gardener.HomeFragment;
import com.moutamid.gardeningapp.fragments.gardener.ProfileFragment;

public class GardenerActivity extends AppCompatActivity {
    ActivityGardenerBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGardenerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);

        binding.bottomNav.setItemIconTintList(null);
        binding.bottomNav.setItemActiveIndicatorEnabled(false);

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
        binding.bottomNav.setSelectedItemId(R.id.nav_home);
        binding.bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
            } else if (itemId == R.id.nav_list) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new BookingFragment()).commit();
            } else if (itemId == R.id.nav_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProfileFragment()).commit();
            }
            return true;
        });

    }
}