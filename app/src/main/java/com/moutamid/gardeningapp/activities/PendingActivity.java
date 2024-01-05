package com.moutamid.gardeningapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.adapters.BookingAdapter;
import com.moutamid.gardeningapp.adapters.PendingAdapter;
import com.moutamid.gardeningapp.databinding.ActivityPendingBinding;
import com.moutamid.gardeningapp.models.BookingModel;
import com.moutamid.gardeningapp.utilis.Constants;

import java.util.ArrayList;

public class PendingActivity extends AppCompatActivity {
    ActivityPendingBinding binding;
    ArrayList<BookingModel> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityPendingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.toolbar.title.setText("Pending Payments");

        binding.bookingsRC.setHasFixedSize(false);
        binding.bookingsRC.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.initDialog(this);
        Constants.showDialog();
        getData();
    }

    private void getData() {
        Constants.databaseReference().child(Constants.ONGOING_BOOKINGS).child(Constants.auth().getCurrentUser().getUid())
                .get().addOnSuccessListener(snapshot -> {
                    Constants.dismissDialog();
                    if (snapshot.exists()) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            BookingModel bookingModel = dataSnapshot.getValue(BookingModel.class);
                            list.add(bookingModel);
                        }

                        if (list.size() > 0) {
                            binding.noLayout.setVisibility(View.GONE);
                            binding.bookingsRC.setVisibility(View.VISIBLE);
                        }

                        PendingAdapter adapter = new PendingAdapter(this, list);
                        binding.bookingsRC.setAdapter(adapter);
                    }
                }).addOnFailureListener(error -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



}