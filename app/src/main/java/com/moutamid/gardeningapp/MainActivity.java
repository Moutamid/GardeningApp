package com.moutamid.gardeningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.moutamid.gardeningapp.databinding.ActivityMainBinding;
import com.moutamid.gardeningapp.fragments.gardener.BookingFragment;
import com.moutamid.gardeningapp.fragments.gardener.HomeFragment;
import com.moutamid.gardeningapp.fragments.gardener.ProfileFragment;
import com.moutamid.gardeningapp.fragments.users.BookingUserFragment;
import com.moutamid.gardeningapp.fragments.users.HomeUserFragment;
import com.moutamid.gardeningapp.fragments.users.ProfileUserFragment;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);

        binding.bottomNav.setItemIconTintList(null);
        binding.bottomNav.setItemActiveIndicatorEnabled(false);

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeUserFragment()).commit();
        binding.bottomNav.setSelectedItemId(R.id.nav_home);
        binding.bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeUserFragment()).commit();
            } else if (itemId == R.id.nav_list) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new BookingUserFragment()).commit();
            } else if (itemId == R.id.nav_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProfileUserFragment()).commit();
            }
            return true;
        });

    }
}