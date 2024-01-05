package com.moutamid.gardeningapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.databinding.ActivityGardenerPaymentBinding;
import com.moutamid.gardeningapp.models.UserModel;
import com.moutamid.gardeningapp.utilis.Constants;

import java.util.HashMap;
import java.util.Map;

public class GardenerPaymentActivity extends AppCompatActivity {
    ActivityGardenerPaymentBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGardenerPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.toolbar.title.setText("PayPal Setup");

        binding.paypal.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                Map<String, Object> map = new HashMap<>();
                map.put("clientID", binding.clientID.getEditText().getText().toString());
                map.put("paypalEmail", binding.email.getEditText().getText().toString());

                Constants.databaseReference().child(Constants.USERS).child(Constants.auth().getCurrentUser().getUid())
                        .updateChildren(map).addOnSuccessListener(unused -> {
                            Constants.dismissDialog();
                            Toast.makeText(this, "Paypal Setup Completed", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }).addOnFailureListener(e -> {
                            Constants.dismissDialog();
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private boolean valid() {
        if (binding.email.getEditText().getText().toString().isEmpty()){
            Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show();
            return false;
        } if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getEditText().getText().toString()).matches()){
            Toast.makeText(this, "Email is not valid", Toast.LENGTH_SHORT).show();
            return false;
        } if (binding.clientID.getEditText().getText().toString().isEmpty()){
            Toast.makeText(this, "clientID is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        Constants.showDialog();
        Constants.databaseReference().child(Constants.USERS).child(Constants.auth().getCurrentUser().getUid())
                .get().addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        UserModel user = dataSnapshot.getValue(UserModel.class);
                        if (user.getPaypalEmail() != null && user.getClientID() != null) {
                            if (!user.getPaypalEmail().isEmpty() && !user.getClientID().isEmpty()) {
                                binding.email.getEditText().setText(user.getPaypalEmail());
                                binding.clientID.getEditText().setText(user.getClientID());
                            }
                        }
                    }
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}