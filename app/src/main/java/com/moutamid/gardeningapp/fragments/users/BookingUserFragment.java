package com.moutamid.gardeningapp.fragments.users;

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
import com.moutamid.gardeningapp.adapters.BookingAdapter;
import com.moutamid.gardeningapp.databinding.FragmentBookingUserBinding;
import com.moutamid.gardeningapp.models.BookingModel;

import java.util.ArrayList;

public class BookingUserFragment extends Fragment {
    FragmentBookingUserBinding binding;
    ArrayList<BookingModel> list;
    public BookingUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookingUserBinding.inflate(getLayoutInflater(), container, false);
        binding.bookingsRC.setHasFixedSize(false);
        binding.bookingsRC.setLayoutManager(new LinearLayoutManager(requireContext()));

        list = new ArrayList<>();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.initDialog(requireContext());
        Constants.showDialog();
        getData();
    }

    private void getData() {
        Constants.databaseReference().child(Constants.ONGOING_BOOKINGS).child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
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

                            BookingAdapter adapter = new BookingAdapter(requireContext(), list);
                            binding.bookingsRC.setAdapter(adapter);
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