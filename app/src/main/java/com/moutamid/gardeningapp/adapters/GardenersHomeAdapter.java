package com.moutamid.gardeningapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.moutamid.gardeningapp.Constants;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.activities.GardenerProfileActivity;
import com.moutamid.gardeningapp.models.GardenerModel;

import java.util.ArrayList;
import java.util.Collection;

public class GardenersHomeAdapter extends RecyclerView.Adapter<GardenersHomeAdapter.GardenerVH> implements Filterable {

    Context context;
    ArrayList<GardenerModel> list;
    ArrayList<GardenerModel> listAll;

    public GardenersHomeAdapter(Context context, ArrayList<GardenerModel> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public GardenerVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GardenerVH(LayoutInflater.from(context).inflate(R.layout.services_home, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GardenerVH holder, int position) {
        GardenerModel model = list.get(holder.getAdapterPosition());
        holder.name.setText(model.getServiceModel().getName());
        holder.price.setText("Price : $" + model.getServiceModel().getPrice());
        Glide.with(context).load(model.getUserModel().getImage()).placeholder(R.drawable.profile_icon).into(holder.profile);
        holder.itemView.setOnClickListener(v -> {
            Stash.put(Constants.PASS_MODEL, model);
            context.startActivity(new Intent(context, GardenerProfileActivity.class));
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<GardenerModel> filterList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filterList.addAll(listAll);
            } else {
                for (GardenerModel listModel : listAll) {
                    if (listModel.getServiceModel().getName().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            listModel.getUserModel().getName().toLowerCase().contains(constraint.toString().toLowerCase())
                    ) {
                        filterList.add(listModel);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends GardenerModel>) results.values);
            notifyDataSetChanged();
        }
    };

    public class GardenerVH extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView price, name;
        public GardenerVH(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
        }
    }

}
