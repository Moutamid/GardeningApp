package com.moutamid.gardeningapp.fragments.gardener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.moutamid.gardeningapp.utilis.Constants;
import com.moutamid.gardeningapp.adapters.RequestAdapter;
import com.moutamid.gardeningapp.databinding.FragmentBookingRequestsBinding;
import com.moutamid.gardeningapp.models.BookingModel;

import java.util.ArrayList;

public class BookingRequestsFragment extends Fragment {
    FragmentBookingRequestsBinding binding;
    ArrayList<BookingModel> list;

    public BookingRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookingRequestsBinding.inflate(getLayoutInflater(), container, false);

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
        Constants.databaseReference().child(Constants.BOOKINGS).child(Constants.auth().getCurrentUser().getUid())
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

                        RequestAdapter adapter = new RequestAdapter(requireContext(), requireActivity(), list);
                        binding.bookingsRC.setAdapter(adapter);
                    }
                }).addOnFailureListener(error -> {
                    Constants.dismissDialog();
                    Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}