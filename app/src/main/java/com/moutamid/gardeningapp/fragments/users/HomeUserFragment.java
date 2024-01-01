package com.moutamid.gardeningapp.fragments.users;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.fxn.stash.Stash;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.moutamid.gardeningapp.Constants;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.activities.GardenerProfileActivity;
import com.moutamid.gardeningapp.databinding.FragmentHomeUserBinding;
import com.moutamid.gardeningapp.models.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeUserFragment extends Fragment {
    FragmentHomeUserBinding binding;
    ArrayList<UserModel> usersList;
    Map<String, Object> marker = new HashMap<>();
    public HomeUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeUserBinding.inflate(getLayoutInflater(), container, false);
        usersList = new ArrayList<>();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.initDialog(requireContext());
        getData();
    }

    private void getData() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.USERS)
                .get().addOnSuccessListener(snapshot -> {
                    Constants.dismissDialog();
                    if (snapshot.exists()) {
                        usersList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UserModel model = dataSnapshot.getValue(UserModel.class);
                            if (model.isGardener())
                                usersList.add(model);
                        }
                        showData();
                    } else {

                        Toast.makeText(requireContext(), "No Service Available", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showData() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    return;
                }
                for (UserModel model : usersList){
                    LatLng mark = new LatLng(model.getLatitude(), model.getLongitude());
                    MarkerOptions mo = new MarkerOptions().position(mark).flat(true)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.your_custom_marker_icon))
//                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .rotation(0).title( model.getAddress());
                    Marker mkr = googleMap.addMarker(mo);
                    marker.put(mkr.getId(), model);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mark, 10f));
                }

                googleMap.setOnMarkerClickListener(mark -> {
                    UserModel model1 = (UserModel) marker.get(mark.getId());
                    Stash.put(Constants.PASS_MODEL, model1);
                    startActivity(new Intent(requireContext(), GardenerProfileActivity.class));
                    return false;
                });

            });
        }
    }
}