package com.moutamid.gardeningapp.fragments.gardener;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.databinding.FragmentBookingBinding;

public class BookingFragment extends Fragment {
    FragmentBookingBinding binding;
    public BookingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookingBinding.inflate(getLayoutInflater(), container, false);



        return binding.getRoot();
    }
}