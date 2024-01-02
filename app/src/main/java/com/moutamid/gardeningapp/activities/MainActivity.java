package com.moutamid.gardeningapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.google.firebase.messaging.FirebaseMessaging;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.databinding.ActivityMainBinding;
import com.moutamid.gardeningapp.fragments.users.BookingUserFragment;
import com.moutamid.gardeningapp.fragments.users.HomeUserFragment;
import com.moutamid.gardeningapp.fragments.users.ProfileUserFragment;
import com.moutamid.gardeningapp.utilis.Constants;

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

        Constants.databaseReference().child("server_key").get()
                .addOnSuccessListener(dataSnapshot -> {
                    String key = dataSnapshot.getValue(String.class);
                    Stash.put(Constants.KEY, key);
                });

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.auth().getCurrentUser().getUid());
    }

}