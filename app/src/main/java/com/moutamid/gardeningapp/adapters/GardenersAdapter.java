package com.moutamid.gardeningapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.stash.Stash;
import com.google.android.material.button.MaterialButton;
import com.moutamid.gardeningapp.Constants;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.activities.GardenerProfileActivity;
import com.moutamid.gardeningapp.models.GardenerModel;

import java.util.ArrayList;
import java.util.Collection;

public class GardenersAdapter extends RecyclerView.Adapter<GardenersAdapter.GardenerVH> {

    Context context;
    ArrayList<GardenerModel> list;

    public GardenersAdapter(Context context, ArrayList<GardenerModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public GardenerVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GardenerVH(LayoutInflater.from(context).inflate(R.layout.services, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GardenerVH holder, int position) {
        GardenerModel model = list.get(holder.getAdapterPosition());
        holder.name.setText(model.getServiceModel().getName());
        holder.price.setText("Price : $" + model.getServiceModel().getPrice());

        holder.book.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class GardenerVH extends RecyclerView.ViewHolder {
        MaterialButton book;
        TextView price, name;

        public GardenerVH(@NonNull View itemView) {
            super(itemView);
            book = itemView.findViewById(R.id.book);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
        }
    }

}
