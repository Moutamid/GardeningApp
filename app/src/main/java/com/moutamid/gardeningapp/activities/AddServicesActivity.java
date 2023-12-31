package com.moutamid.gardeningapp.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.utilis.Constants;
import com.moutamid.gardeningapp.databinding.ActivityAddServicesBinding;
import com.moutamid.gardeningapp.models.ServiceModel;

import java.util.UUID;

public class AddServicesActivity extends AppCompatActivity {
    ActivityAddServicesBinding binding;
    ArrayAdapter<CharSequence> servicesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.toolbar.title.setText("Add Service");

        servicesAdapter = ArrayAdapter.createFromResource(this, R.array.services, R.layout.dropdown_layout);
        servicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.serviceList.setAdapter(servicesAdapter);

        binding.next.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                ServiceModel model = new ServiceModel(UUID.randomUUID().toString(), Constants.auth().getCurrentUser().getUid(),
                        binding.name.getEditText().getText().toString(),
                        binding.service.getEditText().getText().toString(),
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
        if (binding.name.getEditText().getText().toString().isEmpty()) {
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