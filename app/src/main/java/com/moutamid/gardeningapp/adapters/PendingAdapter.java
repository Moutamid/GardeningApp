package com.moutamid.gardeningapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.stash.Stash;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.activities.PaymentActivity;
import com.moutamid.gardeningapp.models.BookingModel;
import com.moutamid.gardeningapp.utilis.Constants;

import java.util.ArrayList;

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.BookingVH> {

    Context context;
    ArrayList<BookingModel> list;

    public PendingAdapter(Context context, ArrayList<BookingModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BookingVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookingVH(LayoutInflater.from(context).inflate(R.layout.bookings, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookingVH holder, int position) {
        BookingModel model = list.get(holder.getAdapterPosition());
        holder.name.setText(model.getServiceModel().getName());
        holder.price.setText("Price : AED" + model.getServiceModel().getPrice());
        String date = Constants.getFormattedDate(model.getStartDate()) + " - " + Constants.getFormattedDate(model.getEndDate());
        holder.date.setText(date);

        holder.itemView.setOnClickListener(v -> {
            Stash.put(Constants.PASS_MODEL, model);
            context.startActivity(new Intent(context, PaymentActivity.class));
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BookingVH extends RecyclerView.ViewHolder {
        TextView date, price, name;

        public BookingVH(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            price = itemView.findViewById(R.id.price);
            name = itemView.findViewById(R.id.name);
        }
    }

}
