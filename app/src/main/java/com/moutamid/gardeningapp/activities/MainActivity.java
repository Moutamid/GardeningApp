package com.moutamid.gardeningapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
        binding.bottomNav.setSelectedItemId(R.id.nav_home);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            ) {
                shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS);
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {
            Log.d("NotificationHelper", "getToken: " + s);
            Constants.databaseReference().child(Constants.USERS).child(Constants.auth().getCurrentUser().getUid()).child("fcmToken").setValue(s);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        });
    }

}