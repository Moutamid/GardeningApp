package com.moutamid.gardeningapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.models.BookingModel;
import com.moutamid.gardeningapp.models.FeedbackModel;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewVH> {

    Context context;
    ArrayList<FeedbackModel> list;

    public ReviewAdapter(Context context, ArrayList<FeedbackModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ReviewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewVH(LayoutInflater.from(context).inflate(R.layout.review, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewVH holder, int position) {
        FeedbackModel model = list.get(holder.getAdapterPosition());
        holder.desc.setText(model.getDesc());
        String[] parts = model.getName().split(" ");
        String name = model.getName();
        if (parts.length >= 2) {
            name = parts[0] + " " + parts[1].charAt(0) + ".";
        }
        holder.name.setText(name);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ReviewVH extends RecyclerView.ViewHolder {
        TextView name, desc;
        public ReviewVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            desc = itemView.findViewById(R.id.desc);
        }
    }

}
