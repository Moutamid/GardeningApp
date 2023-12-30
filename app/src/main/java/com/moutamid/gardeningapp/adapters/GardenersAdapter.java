package com.moutamid.gardeningapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.models.ServiceModel;

import java.util.ArrayList;
import java.util.Collection;

public class GardenersAdapter extends RecyclerView.Adapter<GardenersAdapter.GardenerVH> implements Filterable{

    Context context;
    ArrayList<ServiceModel> list;
    ArrayList<ServiceModel> listAll;

    public GardenersAdapter(Context context, ArrayList<ServiceModel> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public GardenerVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GardenerVH(LayoutInflater.from(context).inflate(R.layout.services, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GardenerVH holder, int position) {
        ServiceModel model = list.get(holder.getAdapterPosition());
        holder.name.setText(model.getName());
        holder.price.setText("Price : $" + model.getPrice());

        holder.book.setOnClickListener(v -> {

        });
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ServiceModel> filterList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filterList.addAll(listAll);
            } else {
                for (ServiceModel listModel : listAll) {
                    if (listModel.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
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
            list.addAll((Collection<? extends ServiceModel>) results.values);
            notifyDataSetChanged();
        }
    };

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
