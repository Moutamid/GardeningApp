package com.moutamid.gardeningapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.fxn.stash.Stash;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.gardeningapp.utilis.Constants;
import com.moutamid.gardeningapp.databinding.ActivityRegisterBinding;
import com.moutamid.gardeningapp.models.UserModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.login.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        binding.autoDetect.setOnClickListener(v -> {
            if (checkGPSStatus()) {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Location Services Disabled")
                        .setMessage("Please enable GPS to access location-based features. You can turn on location services in your device settings")
                        .setCancelable(true)
                        .setPositiveButton("Open Settings", ((dialog, which) -> {
                            dialog.dismiss();
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        })).show();
            } else {
                autoDetect();
            }
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
                            binding.isGardener.isChecked(), "", new ArrayList<>(), "", ""
                    );
                    Constants.databaseReference().child(Constants.USERS).child(Constants.auth().getCurrentUser().getUid()).setValue(model)
                            .addOnSuccessListener(unused -> {
                                Constants.dismissDialog();
                                Stash.put(Constants.STASH_USER, model);
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

    private void autoDetect() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
        } else Constants.showDialog();
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            new Handler().postDelayed(Constants::dismissDialog, 2000);
            if (location != null) {
                currentLocation = location;
                binding.latitude.getEditText().setText(String.valueOf(location.getLatitude()));
                binding.longitude.getEditText().setText(String.valueOf(location.getLongitude()));
                Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        StringBuilder addressStringBuilder = new StringBuilder();
                        if (address.getLocality() != null) {
                            addressStringBuilder.append(address.getLocality()).append(", ");
                        }
                        if (address.getSubLocality() != null) {
                            addressStringBuilder.append(address.getSubLocality()).append(", ");
                        }
                        if (address.getAdminArea() != null) {
                            addressStringBuilder.append(address.getAdminArea()).append(", ");
                        }
                        if (address.getCountryName() != null) {
                            addressStringBuilder.append(address.getCountryName());
                        }
                        binding.address.getEditText().setText(addressStringBuilder.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Unable to retrieve location", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Constants.dismissDialog();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private boolean checkGPSStatus() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return !gps_enabled && !network_enabled;
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