package com.moutamid.gardeningapp.fragments.gardener;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.gardeningapp.utilis.Constants;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.activities.EditGardnerActivity;
import com.moutamid.gardeningapp.activities.SplashScreenActivity;
import com.moutamid.gardeningapp.databinding.FragmentProfileBinding;
import com.moutamid.gardeningapp.models.UserModel;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);

        binding.logout.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setNegativeButton("No", ((dialog, which) -> dialog.dismiss()))
                    .setPositiveButton("Yes", ((dialog, which) -> {
                        dialog.dismiss();
                        Constants.auth().signOut();
                        startActivity(new Intent(requireContext(), SplashScreenActivity.class));
                        requireActivity().finish();
                    }))
                    .show();
        });

        binding.edit.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), EditGardnerActivity.class));
        });

        binding.privacy.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            startActivity(intent);
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        UserModel userModel = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        if (userModel != null) {
            binding.name.setText(userModel.getName());
            binding.email.setText(userModel.getEmail());
            binding.location.setText(userModel.getLatitude() + ", " + userModel.getLongitude());
            Glide.with(requireContext()).load(userModel.getImage()).placeholder(R.drawable.profile_icon).into(binding.profile);
        }
    }
}