package com.vidhika.zepto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    private int totalAmount;
    private TextView tvTotalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        tvTotalAmount = findViewById(R.id.total_amount_textview);

        totalAmount = getIntent().getIntExtra("TOTAL_AMOUNT", 0);
        tvTotalAmount.setText("To Pay: ₹" + totalAmount);

        View btnBack = findViewById(R.id.btn_back_payment);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        View payCod = findViewById(R.id.card_pay_cod);
        if (payCod != null) {
            payCod.setOnClickListener(v -> placeOrderAndTrack("Cash on Delivery"));
        }

        View payCards = findViewById(R.id.card_pay_cards);
        if (payCards != null) {
            payCards.setOnClickListener(v -> placeOrderAndTrack("Credit / Debit Card"));
        }

        View payPluxee = findViewById(R.id.card_pay_pluxee);
        if (payPluxee != null) {
            payPluxee.setOnClickListener(v -> placeOrderAndTrack("Pluxee Meal Card"));
        }

        View btnAddUpi = findViewById(R.id.btn_add_upi);
        if (btnAddUpi != null) {
            btnAddUpi.setOnClickListener(v -> placeOrderAndTrack("UPI Payment"));
        }
    }

    public void payWithGPay(View view) {
        startUPIPayment("your_upi_id@okicici", "Google Pay");
    }

    public void payWithGpay(View view) {
        payWithGPay(view);
    }

    public void payWithPhonePe(View view) {
        startUPIPayment("your_upi_id@ybl", "PhonePe");
    }

    public void payWithPhonepe(View view) {
        payWithPhonePe(view);
    }

    public void payWithPaytm(View view) {
        startUPIPayment("your_upi@ptsbi", "Paytm");
    }

    private void startUPIPayment(String upiId, String appName) {
        try {
            Uri uri = Uri.parse("upi://pay?pa=" + upiId +
                    "&pn=Zepto" +
                    "&mc=0000" +
                    "&tid=021254" +
                    "&tr=123456789" +
                    "&tn=Payment" +
                    "&am=" + totalAmount +
                    "&cu=INR");

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, appName + " selected. Processing order...", Toast.LENGTH_SHORT).show();
            placeOrderAndTrack(appName);
        }
    }

    private void placeOrderAndTrack(String paymentMethod) {
        List<Product> cartProducts = CartManager.getInstance().getCartItems();

        if (cartProducts != null && !cartProducts.isEmpty()) {
            OrderManager.getInstance().createOrderFromCart(cartProducts, totalAmount, paymentMethod);
            CartManager.getInstance().clearCart();
        }

        Toast.makeText(this, "⚡ Order Placed Successfully via " + paymentMethod + "!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(PaymentActivity.this, OrdersActivity.class);
        startActivity(intent);
        finish();
    }
}
