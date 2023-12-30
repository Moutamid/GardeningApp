package com.moutamid.gardeningapp.fragments.users;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.gardeningapp.Constants;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.adapters.GardenersAdapter;
import com.moutamid.gardeningapp.adapters.GardenersHomeAdapter;
import com.moutamid.gardeningapp.databinding.FragmentHomeUserBinding;
import com.moutamid.gardeningapp.models.GardenerModel;
import com.moutamid.gardeningapp.models.ServiceModel;
import com.moutamid.gardeningapp.models.UserModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;


public class HomeUserFragment extends Fragment {
    FragmentHomeUserBinding binding;
    ArrayList<ServiceModel> list;
    ArrayList<UserModel> usersList;
    ArrayList<GardenerModel> gardenersList;
    GardenersHomeAdapter adapter;

    final double[] max = new double[1];
    final double[] min = new double[1];

    public HomeUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeUserBinding.inflate(getLayoutInflater(), container, false);
        list = new ArrayList<>();
        usersList = new ArrayList<>();
        gardenersList = new ArrayList<>();
        binding.gardenerRC.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.gardenerRC.setHasFixedSize(false);

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

        return binding.getRoot();
    }

    private void showFilterDialog() {
        Dialog dialog = new Dialog(requireContext());
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
            adapter = new GardenersHomeAdapter(requireContext(), gardenersList);
            binding.gardenerRC.setAdapter(adapter);
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
            List<GardenerModel> list = filterByPriceRange(gardenersList, min[0], max[0]);
            ArrayList<GardenerModel> ll = new ArrayList<>(list);
            if (ll.size() == 0){
                Toast.makeText(requireContext(), "Nothing Found", Toast.LENGTH_SHORT).show();
            }
            adapter = new GardenersHomeAdapter(requireContext(), ll);
            binding.gardenerRC.setAdapter(adapter);
        });
    }

    public static List<GardenerModel> filterByPriceRange(ArrayList<GardenerModel> gardenerList, double min, double max) {
        return gardenerList.stream()
                .filter(gardenerModel -> {
                    ServiceModel serviceModel = gardenerModel.getServiceModel();
                    return serviceModel != null && serviceModel.getPrice() >= min && serviceModel.getPrice() <= max;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.initDialog(requireContext());
        getData();
    }

    private void getData() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.SERVICE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                                    ServiceModel model = dataSnapshot2.getValue(ServiceModel.class);
                                    list.add(model);
                                }
                            }
                            getUsers();
                        } else {
                            Constants.dismissDialog();
                            Toast.makeText(requireContext(), "No Service Available", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Constants.dismissDialog();
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getUsers() {
        Constants.databaseReference().child(Constants.USERS)
                .get().addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        usersList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UserModel model = dataSnapshot.getValue(UserModel.class);
                            usersList.add(model);
                        }
                        showData();
                    } else {
                        Constants.dismissDialog();
                        Toast.makeText(requireContext(), "No Service Available", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showData() {
        Constants.dismissDialog();
        gardenersList.clear();
        for (ServiceModel serviceModel : list) {
            for (UserModel userModel : usersList) {
                if (serviceModel.getUserID().equals(userModel.getId()))
                    gardenersList.add(new GardenerModel(userModel, serviceModel));
            }
        }
        adapter = new GardenersHomeAdapter(requireContext(), gardenersList);
        binding.gardenerRC.setAdapter(adapter);
    }
}