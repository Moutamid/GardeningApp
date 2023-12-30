package com.moutamid.gardeningapp.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.gardeningapp.BookingClickListeners;
import com.moutamid.gardeningapp.Constants;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.adapters.GardenersAdapter;
import com.moutamid.gardeningapp.databinding.ActivityGardenerProfileBinding;
import com.moutamid.gardeningapp.models.BookingModel;
import com.moutamid.gardeningapp.models.ServiceModel;
import com.moutamid.gardeningapp.models.UserModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GardenerProfileActivity extends AppCompatActivity {
    ActivityGardenerProfileBinding binding;
    ArrayList<ServiceModel> list;
    GardenersAdapter adapter;
    UserModel model;
    final double[] max = new double[1];
    final double[] min = new double[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGardenerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.initDialog(this);

        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.toolbar.title.setText("Gardeners Profile");

        list = new ArrayList<>();

        model = (UserModel) Stash.getObject(Constants.PASS_MODEL, UserModel.class);

        max[0] = 1000.0;
        min[0] = 0.0;

        binding.filter.setOnClickListener(v -> {
            showFilterDialog();
        });

        binding.search.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.serviceRC.setLayoutManager(new LinearLayoutManager(this));
        binding.serviceRC.setHasFixedSize(false);

        binding.openMap.setOnClickListener(v -> {
            String uri = "geo:" + model.getLatitude() + "," + model.getLongitude() + "?q=" + model.getLatitude() + "," + model.getLongitude() + "(" + model.getAddress() + ")";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
        });
        setContent();
    }

    private void setContent() {
        binding.name.setText(model.getName());
        binding.email.setText(model.getEmail());
        binding.address.setText(model.getAddress());
        binding.cordinates.setText(model.getLatitude() + ", " + model.getLongitude());
        Glide.with(this).load(model.getImage()).placeholder(R.drawable.profile_icon).into(binding.profile);
        getServices();
    }

    private void getServices() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.SERVICE).child(model.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Constants.dismissDialog();
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ServiceModel model1 = dataSnapshot.getValue(ServiceModel.class);
                        list.add(model1);
                    }
                    adapter = new GardenersAdapter(GardenerProfileActivity.this, list, clickListeners);
                    binding.serviceRC.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Constants.dismissDialog();
                Toast.makeText(GardenerProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFilterDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filter_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();


        MaterialButton filter = dialog.findViewById(R.id.filter);
        MaterialButton clear = dialog.findViewById(R.id.clear);
        TextView maximum = dialog.findViewById(R.id.maximum);
        TextView minimum = dialog.findViewById(R.id.minimum);
        RangeSlider range = dialog.findViewById(R.id.range);


        List<Float> values = new ArrayList<>();
        values.add((float) min[0]);
        values.add((float) max[0]);
        range.setValues(values);

        minimum.setText(String.format("%.2f", min[0]));
        maximum.setText(String.format("%.2f", max[0]));

        clear.setOnClickListener(v -> {
            dialog.dismiss();
            max[0] = 1000.0;
            min[0] = 0.0;
            adapter = new GardenersAdapter(this, list, clickListeners);
            binding.serviceRC.setAdapter(adapter);
        });

        range.setLabelFormatter(value -> "$" + String.format("%.2f", value));
        range.addOnChangeListener((slider, value, fromUser) -> {
            float minValue = slider.getValues().get(0);
            float maxValue = slider.getValues().get(1);
            min[0] = minValue;
            max[0] = maxValue;
            minimum.setText(String.format("%.2f", minValue));
            maximum.setText(String.format("%.2f", maxValue));
        });
        filter.setOnClickListener(v -> {
            dialog.dismiss();
            List<ServiceModel> list2 = filterByPriceRange(list, min[0], max[0]);
            ArrayList<ServiceModel> ll = new ArrayList<>(list2);
            if (ll.size() == 0) {
                Toast.makeText(this, "Nothing Found", Toast.LENGTH_SHORT).show();
            }
            adapter = new GardenersAdapter(this, ll, clickListeners);
            binding.serviceRC.setAdapter(adapter);
        });

    }

    public static List<ServiceModel> filterByPriceRange(ArrayList<ServiceModel> gardenerList, double min, double max) {
        return gardenerList.stream()
                .filter(gardenerModel -> gardenerModel != null && gardenerModel.getPrice() >= min && gardenerModel.getPrice() <= max)
                .collect(Collectors.toList());
    }

    private void openDateRangePicker(ServiceModel model) {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setStart(System.currentTimeMillis() - 1000); // Set the start date if needed
        constraintsBuilder.setEnd(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365); // Set the end date if needed

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setCalendarConstraints(constraintsBuilder.build());
        MaterialDatePicker<Pair<Long, Long>> picker = builder.build();
        picker.addOnPositiveButtonClickListener(selection -> {
            sendBookingRequest(model, selection.first, selection.second);
        });

        picker.show(getSupportFragmentManager(), picker.toString());
    }

    private void sendBookingRequest(ServiceModel model, long startDate, long endDate) {
        Constants.showDialog();
        BookingModel bookingModel = new BookingModel(
                UUID.randomUUID().toString(),
                Constants.auth().getCurrentUser().getUid(),
                model, new Date().getTime(), startDate, endDate, false);
        Constants.databaseReference().child(Constants.BOOKINGS).child(Constants.auth().getCurrentUser().getUid()).child(bookingModel.getID()).setValue(bookingModel)
                .addOnSuccessListener(unused -> {
                    Constants.databaseReference().child(Constants.BOOKINGS).child(bookingModel.getServiceModel().getUserID()).child(bookingModel.getID()).setValue(bookingModel)
                            .addOnSuccessListener(unused2 -> {
                                Constants.dismissDialog();
                                Toast.makeText(this, "Booking Request Sent", Toast.LENGTH_LONG).show();
                            })
                            .addOnFailureListener(e -> {
                                Constants.dismissDialog();
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    BookingClickListeners clickListeners = model -> openDateRangePicker(model);

}