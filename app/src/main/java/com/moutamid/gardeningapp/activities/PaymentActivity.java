package com.moutamid.gardeningapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.moutamid.gardeningapp.R;
import com.moutamid.gardeningapp.databinding.ActivityPaymentBinding;
import com.moutamid.gardeningapp.models.BookingModel;
import com.moutamid.gardeningapp.models.UserModel;
import com.moutamid.gardeningapp.utilis.Constants;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;

public class PaymentActivity extends AppCompatActivity {
    ActivityPaymentBinding binding;
    private static final int PAYPAL_REQUEST_CODE = 1001;
    PayPalConfiguration config;
    UserModel userModel;
    BookingModel bookingModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.toolbar.title.setText("Pay");

        bookingModel = (BookingModel) Stash.getObject(Constants.PASS_MODEL, BookingModel.class);

        binding.money.getEditText().setText(bookingModel.getServiceModel().getPrice() + "");

        config = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) // Use ENVIRONMENT_PRODUCTION for production
                .clientId(getString(R.string.clientID));

        binding.paypal.setOnClickListener(v -> {
            if (valid()) {
                PayPalPayment payment = new PayPalPayment(new BigDecimal(bookingModel.getServiceModel().getPrice()), "USD", "Payment for " + bookingModel.getServiceModel().getName(), PayPalPayment.PAYMENT_INTENT_SALE);
                payment.custom("receiver_client_id=" + userModel.getClientID());
                payment.custom("receiver_email=" + userModel.getPaypalEmail());
                Intent intent = new Intent(this, com.paypal.android.sdk.payments.PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payment);
                startActivityForResult(intent, PAYPAL_REQUEST_CODE);
            }
        });
    }

    private boolean valid() {
        if (binding.email.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getEditText().getText().toString()).matches()) {
            Toast.makeText(this, "Email is not valid", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.money.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Price is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        getUser();
    }

    private void getUser() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.USERS).child(bookingModel.getServiceModel().getUserID())
                .get().addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        userModel = dataSnapshot.getValue(UserModel.class);
                        if (userModel.getPaypalEmail() != null) {
                            if (!userModel.getPaypalEmail().isEmpty()) {
                                binding.email.getEditText().setText(userModel.getPaypalEmail());
                            }
                        }
                    }
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    String paymentId = confirmation.getProofOfPayment().getPaymentId();
                    Constants.showDialog();
                    Constants.databaseReference().child(Constants.ONGOING_BOOKINGS).child(Constants.auth().getCurrentUser().getUid()).child(bookingModel.getID()).setValue(bookingModel)
                            .addOnSuccessListener(unused -> {
                                Constants.databaseReference().child(Constants.ONGOING_BOOKINGS).child(bookingModel.getSenderID()).child(bookingModel.getID()).setValue(bookingModel)
                                        .addOnSuccessListener(unused2 -> {
                                            Toast.makeText(this, "Payment Sent Successfully", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Constants.dismissDialog();
                                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Constants.dismissDialog();
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Payment Canceled", Toast.LENGTH_SHORT).show();
            } else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID) {
                // Invalid payment, handle accordingly
                Toast.makeText(this, "Invalid Payment", Toast.LENGTH_SHORT).show();
            }
        }
    }


}