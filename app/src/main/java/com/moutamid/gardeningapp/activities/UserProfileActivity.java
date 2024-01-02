package com.moutamid.gardeningapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.moutamid.gardeningapp.notification.FcmNotificationsSender;
import com.moutamid.gardeningapp.utilis.Constants;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.adapters.ReviewAdapter;
import com.moutamid.gardeningapp.databinding.ActivityUserProfileBinding;
import com.moutamid.gardeningapp.models.BookingModel;
import com.moutamid.gardeningapp.models.UserModel;

public class UserProfileActivity extends AppCompatActivity {
    ActivityUserProfileBinding binding;
    UserModel userModel;
    BookingModel bookingModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.initDialog(this);

        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.toolbar.title.setText("Profile");

        bookingModel = (BookingModel) Stash.getObject(Constants.PASS_MODEL, BookingModel.class);
        Constants.showDialog();

        Constants.databaseReference().child(Constants.USERS).child(bookingModel.getSenderID())
                .get().addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()){
                        userModel = dataSnapshot.getValue(UserModel.class);
                        setContent();
                    }
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        binding.openMap.setOnClickListener(v -> {
            String uri = "geo:" + userModel.getLatitude() + "," + userModel.getLongitude() + "?q=" + userModel.getLatitude() + "," + userModel.getLongitude() + "(" + userModel.getAddress() + ")";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
        });

        binding.accept.setOnClickListener(v -> {
            Constants.showDialog();
           removeValues();
        });

    }

    private void removeValues() {
        Constants.databaseReference().child(Constants.BOOKINGS).child(Constants.auth().getCurrentUser().getUid())
                .child(bookingModel.getID()).removeValue()
                .addOnSuccessListener(unused3 -> {
                    Constants.databaseReference().child(Constants.BOOKINGS).child(bookingModel.getSenderID())
                            .child(bookingModel.getID()).removeValue()
                            .addOnSuccessListener(unused4 -> {
                                setValue();
                            }).addOnFailureListener(e -> {
                                Constants.dismissDialog();
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setValue() {
        Constants.databaseReference().child(Constants.PENDING_PAYMENTS).child(bookingModel.getSenderID()).child(bookingModel.getID()).setValue(bookingModel)
                .addOnSuccessListener(unused2 -> {
                    new FcmNotificationsSender(
                            "/topics/" + bookingModel.getSenderID(),
                            "Request Accepted", "Your request is accepted by the gardener", this, this)
                            .SendNotifications();
                    new FcmNotificationsSender(
                            "/topics/" + bookingModel.getSenderID(),
                            "Pending Payment", "Please complete the payment process", this, this)
                            .SendNotifications();
                    Toast.makeText(this, "Request Accepted Successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                })
                .addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setContent() {
        binding.reviewRC.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.reviewRC.setHasFixedSize(false);

        if (userModel.getList() != null){
            if (userModel.getList().size() > 0) {
                binding.NoReview.setVisibility(View.GONE);
                binding.reviewRC.setVisibility(View.VISIBLE);
            }
            ReviewAdapter reviewAdapter = new ReviewAdapter(this, userModel.getList());
            binding.reviewRC.setAdapter(reviewAdapter);
        }

        binding.name.setText(userModel.getName());
        binding.email.setText(userModel.getEmail());
        binding.address.setText(userModel.getAddress());
        binding.cordinates.setText(userModel.getLatitude() + ", " + userModel.getLongitude());
        Glide.with(this).load(userModel.getImage()).placeholder(R.drawable.profile_icon).into(binding.profile);

    }
}