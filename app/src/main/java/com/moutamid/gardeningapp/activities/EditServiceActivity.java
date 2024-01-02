package com.moutamid.gardeningapp.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.moutamid.gardeningapp.utilis.Constants;
import com.moutamid.gardeningapp.databinding.ActivityEditServiceBinding;
import com.moutamid.gardeningapp.models.ServiceModel;

public class EditServiceActivity extends AppCompatActivity {
    ActivityEditServiceBinding binding;
    ServiceModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.toolbar.title.setText("Edit Service");

        String id = getIntent().getStringExtra(Constants.ID);
        Constants.initDialog(this);
        Constants.showDialog();

        Constants.databaseReference().child(Constants.SERVICE).child(Constants.auth().getCurrentUser().getUid()).child(id)
                .get().addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        model = dataSnapshot.getValue(ServiceModel.class);
                        binding.name.setText(model.getName());
                        binding.price.setText("" + model.getPrice());
                    }
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        binding.next.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                model.setName(binding.name.getText().toString());
                model.setPrice(Double.parseDouble(binding.price.getText().toString()));
                Constants.databaseReference().child(Constants.SERVICE).child(model.getUserID()).child(model.getId()).setValue(model)
                        .addOnSuccessListener(unused -> {
                            Constants.dismissDialog();
                            Toast.makeText(this, "Service Updated Successfully", Toast.LENGTH_SHORT).show();
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
}