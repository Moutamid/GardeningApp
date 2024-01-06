package com.moutamid.gardeningapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.stash.Stash;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.activities.UserProfileActivity;
import com.moutamid.gardeningapp.models.BookingModel;
import com.moutamid.gardeningapp.notification.FcmNotificationsSender;
import com.moutamid.gardeningapp.utilis.Constants;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestVH> {

    Context context;
    Activity activiy;
    ArrayList<BookingModel> list;

    public RequestAdapter(Context context, Activity activiy, ArrayList<BookingModel> list) {
        this.context = context;
        this.activiy = activiy;
        this.list = list;
    }

    @NonNull
    @Override
    public RequestVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestVH(LayoutInflater.from(context).inflate(R.layout.booking_requests, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestVH holder, int position) {
        BookingModel model = list.get(holder.getAdapterPosition());
        holder.name.setText(model.getServiceModel().getName());
        holder.service.setText(model.getServiceModel().getService());
        holder.price.setText("Price : AED" + model.getServiceModel().getPrice());
        String date = Constants.getFormattedDate(model.getStartDate()) + " - " + Constants.getFormattedDate(model.getEndDate());
        holder.date.setText(date);

        holder.accept.setOnClickListener(v -> {
            Stash.put(Constants.PASS_MODEL, model);
            context.startActivity(new Intent(context, UserProfileActivity.class));
        });

        holder.decline.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Decline Request")
                    .setMessage("Are you sure you want to decline this request!")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        Constants.showDialog();
                        Constants.databaseReference().child(Constants.BOOKINGS).child(Constants.auth().getCurrentUser().getUid())
                                .child(model.getID()).removeValue()
                                .addOnSuccessListener(unused -> {
                                    Constants.databaseReference().child(Constants.BOOKINGS).child(model.getSenderID())
                                            .child(model.getID()).removeValue()
                                            .addOnSuccessListener(unused2 -> {
                                                Constants.dismissDialog();
                                                new FcmNotificationsSender(
                                                        "/topics/" + model.getSenderID(),
                                                        "Request Decline", "Your request is decline by the gardener", context, activiy)
                                                        .SendNotifications();
                                                Toast.makeText(context, "Request Decline Successfully", Toast.LENGTH_SHORT).show();
                                            }).addOnFailureListener(e -> {
                                                Constants.dismissDialog();
                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                }).addOnFailureListener(e -> {
                                    Constants.dismissDialog();
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RequestVH extends RecyclerView.ViewHolder {
        MaterialCardView accept, decline;
        TextView date, price,service, name;

        public RequestVH(@NonNull View itemView) {
            super(itemView);
            accept = itemView.findViewById(R.id.accept);
            decline = itemView.findViewById(R.id.decline);
            date = itemView.findViewById(R.id.date);
            service = itemView.findViewById(R.id.service);
            price = itemView.findViewById(R.id.price);
            name = itemView.findViewById(R.id.name);
        }
    }

}
