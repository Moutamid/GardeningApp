package com.moutamid.gardeningapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.RangeSlider;
import com.moutamid.gardeningapp.Constants;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.adapters.GardenersAdapter;

import com.moutamid.gardeningapp.databinding.ActivityGardenerProfileBinding;
import com.moutamid.gardeningapp.models.ServiceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GardenerProfileActivity extends AppCompatActivity {
    ActivityGardenerProfileBinding binding;
    ArrayList<ServiceModel> list;
    GardenersAdapter adapter;
    final double[] max = new double[1];
    final double[] min = new double[1];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGardenerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        list = new ArrayList<>();

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

        clear.setOnClickListener(v-> {
            dialog.dismiss();
            max[0] = 1000.0;
            min[0] = 0.0;
            adapter = new GardenersAdapter(this, list);
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
            if (ll.size() == 0){
                Toast.makeText(this, "Nothing Found", Toast.LENGTH_SHORT).show();
            }
            adapter = new GardenersAdapter(this, ll);
            binding.serviceRC.setAdapter(adapter);
        });
    }

    public static List<ServiceModel> filterByPriceRange(ArrayList<ServiceModel> gardenerList, double min, double max) {
        return gardenerList.stream()
                .filter(gardenerModel -> gardenerModel != null && gardenerModel.getPrice() >= min && gardenerModel.getPrice() <= max)
                .collect(Collectors.toList());
    }
    
}