package com.moutamid.gardeningapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodeAsyncTask extends AsyncTask<Double, Void, String> {

    private Context context;
    private GeocodeListener listener;

    public GeocodeAsyncTask(Context context, GeocodeListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Double... params) {
        double latitude = params[0];
        double longitude = params[1];

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String addressText = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                // Concatenate address details
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressText += address.getAddressLine(i);
                    if (i < address.getMaxAddressLineIndex()) {
                        addressText += ", ";
                    }
                }
            }
        } catch (IOException e) {
            Log.e("GeocodeAsyncTask", "Error getting address", e);
        }

        return addressText;
    }

    @Override
    protected void onPostExecute(String address) {
        super.onPostExecute(address);
        listener.onGeocodeComplete(address);
    }

    public interface GeocodeListener {
        void onGeocodeComplete(String address);
    }
}

