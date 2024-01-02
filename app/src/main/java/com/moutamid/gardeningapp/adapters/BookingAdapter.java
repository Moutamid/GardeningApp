package com.moutamid.gardeningapp.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.stash.Stash;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.gardeningapp.notification.FcmNotificationsSender;
import com.moutamid.gardeningapp.utilis.Constants;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.models.BookingModel;
import com.moutamid.gardeningapp.models.FeedbackModel;
import com.moutamid.gardeningapp.models.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingVH> {

    Context context;
    Activity activity;
    ArrayList<BookingModel> list;

    public BookingAdapter(Context context, Activity activity, ArrayList<BookingModel> list) {
        this.context = context;
        this.activity = activity;
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
        holder.price.setText("Price : $" + model.getServiceModel().getPrice());
        String date = Constants.getFormattedDate(model.getStartDate()) + " - " + Constants.getFormattedDate(model.getEndDate());
        holder.date.setText(date);

        holder.itemView.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Complete Order")
                    .setMessage("Are you sure you want to Complete this order!")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        Constants.showDialog();
                        Constants.databaseReference().child(Constants.ONGOING_BOOKINGS).child(Constants.auth().getCurrentUser().getUid())
                                .child(model.getID()).removeValue()
                                .addOnSuccessListener(unused -> {
                                    Constants.dismissDialog();
                                    UserModel userModel = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
                                    String rec = userModel.isGardener() ? model.getSenderID() : model.getServiceModel().getUserID();
                                    new FcmNotificationsSender(
                                            "/topics/" + rec,
                                            "Order Completion", "Order Completion Successfully", context, activity)
                                            .SendNotifications();
                                    Toast.makeText(context, "Order Completion Successfully", Toast.LENGTH_SHORT).show();
                                    showReview(model);
                                }).addOnFailureListener(e -> {
                                    Constants.dismissDialog();
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

    }

    private void showReview(BookingModel model) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.feedback);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        TextInputLayout name = dialog.findViewById(R.id.name);
        TextInputLayout desc = dialog.findViewById(R.id.desc);
        MaterialButton button = dialog.findViewById(R.id.share);

        button.setOnClickListener(v -> {
            UserModel userModel = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
            String rec = userModel.isGardener() ? model.getSenderID() : model.getServiceModel().getUserID();
            FeedbackModel feedbackModel = new FeedbackModel(
                    Constants.auth().getCurrentUser().getUid(),
                    rec, name.getEditText().getText().toString(), desc.getEditText().getText().toString()
            );
            dialog.dismiss();
            Constants.showDialog();
            ArrayList<FeedbackModel> list = new ArrayList<>();
            Constants.databaseReference().child(Constants.USERS).child(feedbackModel.getReceiverID()).child("list")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // If the list already exists in the database, add new items to it
                                GenericTypeIndicator<ArrayList<FeedbackModel>> type = new GenericTypeIndicator<ArrayList<FeedbackModel>>() {};
                                ArrayList<FeedbackModel> existingList = dataSnapshot.getValue(type);
                                if (existingList != null) {
                                    list.addAll(existingList);
                                }
                            }

                            // Update the list in the database
                            list.add(feedbackModel);
                            Map<String, Object> map = new HashMap<>();
                            map.put("list", list);
                            Constants.databaseReference().child(Constants.USERS).child(feedbackModel.getReceiverID()).updateChildren(map)
                                    .addOnSuccessListener(unused -> {
                                        Constants.dismissDialog();
                                        Toast.makeText(context, "Thanks For your feedback!", Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(e -> {
                                        Constants.dismissDialog();
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle the error
                            Constants.dismissDialog();
                            Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
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
