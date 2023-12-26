package com.moutamid.gardeningapp.fragments.gardener;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.gardeningapp.Constants;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.activities.AddServicesActivity;
import com.moutamid.gardeningapp.adapters.gardener.ServicesAdapter;
import com.moutamid.gardeningapp.databinding.FragmentHomeBinding;
import com.moutamid.gardeningapp.models.ServiceModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    ArrayList<ServiceModel> list;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);

        list = new ArrayList<>();

        binding.servicesRC.setHasFixedSize(false);
        binding.servicesRC.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.addNew.setOnClickListener(v -> openActivity());
        binding.add.setOnClickListener(v -> openActivity());

        return binding.getRoot();
    }

    private void openActivity() {
        startActivity(new Intent(requireContext(), AddServicesActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.initDialog(requireContext());
        getData();
    }

    private void getData() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.SERVICE).child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Constants.dismissDialog();
                        if (snapshot.exists()) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                ServiceModel model = dataSnapshot.getValue(ServiceModel.class);
                                list.add(model);
                            }
                            if (list.size() > 0){
                                binding.servicesRC.setVisibility(View.VISIBLE);
                                binding.nothingLayout.setVisibility(View.GONE);
                            } else {
                                binding.servicesRC.setVisibility(View.GONE);
                                binding.nothingLayout.setVisibility(View.VISIBLE);
                            }
                            ServicesAdapter adapter = new ServicesAdapter(requireContext(), list);
                            binding.servicesRC.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Constants.dismissDialog();
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}