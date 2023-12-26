package com.moutamid.gardeningapp.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.moutamid.gardeningapp.Constants;
import com.moutamid.gardeningapp.databinding.ActivityAddServicesBinding;
import com.moutamid.gardeningapp.models.ServiceModel;

import java.util.UUID;

public class AddServicesActivity extends AppCompatActivity {
    ActivityAddServicesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.toolbar.title.setText("Add Service");

        binding.next.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                ServiceModel model = new ServiceModel(UUID.randomUUID().toString(), Constants.auth().getCurrentUser().getUid(),
                        binding.name.getText().toString(),
                        Double.parseDouble(binding.price.getText().toString())
                );
                Constants.databaseReference().child(Constants.SERVICE).child(model.getUserID()).child(model.getId()).setValue(model)
                        .addOnSuccessListener(unused -> {
                            Constants.dismissDialog();
                            Toast.makeText(this, "Service Added Successfully", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }).addOnFailureListener(e -> {
                           Constants.dismissDialog();
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }

    private boolean valid() {
        if (binding.name.getText().toString().isEmpty()) {
            Toast.makeText(this, "Service is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.price.getText().toString().isEmpty()) {
            Toast.makeText(this, "Price is empty", Toast.LENGTH_SHORT).show();
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