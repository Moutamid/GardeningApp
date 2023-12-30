package com.moutamid.gardeningapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.gardeningapp.Constants;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.activities.EditServiceActivity;
import com.moutamid.gardeningapp.models.ServiceModel;

import java.util.ArrayList;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServiceVH> {
    Context context;
    ArrayList<ServiceModel> list;

    public ServicesAdapter(Context context, ArrayList<ServiceModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ServiceVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ServiceVH(LayoutInflater.from(context).inflate(R.layout.services_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceVH holder, int position) {
        ServiceModel model = list.get(holder.getAdapterPosition());

        holder.price.setText("Price : $" + model.getPrice());
        holder.name.setText(model.getName());

        holder.delete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Delete Service")
                    .setMessage("Are you sure you want to delete this?")
                    .setNegativeButton("No", ((dialog, which) -> dialog.dismiss()))
                    .setPositiveButton("Yes", ((dialog, which) -> {
                        dialog.dismiss();
                        Constants.databaseReference().child(Constants.SERVICE).child(Constants.auth().getCurrentUser().getUid())
                                .child(model.getId()).removeValue();
                    })).show();
        });

        holder.edit.setOnClickListener(v -> {
            context.startActivity(new Intent(context, EditServiceActivity.class).putExtra(Constants.ID, model.getId()));
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ServiceVH extends RecyclerView.ViewHolder{
        TextView price, name;
        MaterialCardView delete, edit;
        public ServiceVH(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.price);
            name = itemView.findViewById(R.id.name);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
        }
    }

}
