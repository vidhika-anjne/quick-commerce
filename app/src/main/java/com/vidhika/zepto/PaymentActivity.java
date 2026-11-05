package com.vidhika.zepto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {
    private int totalAmount;
    private TextView tvTotalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        tvTotalAmount = findViewById(R.id.total_amount_textview);

        totalAmount = getIntent().getIntExtra("TOTAL_AMOUNT", 0);
        tvTotalAmount.setText("Total Amount: ₹" + totalAmount);
    }

    public void payWithGPay(View view) {
        startUPIPayment("your_upi_id@okicici", "Google Pay");
    }

    public void payWithPhonePe(View view) {
        startUPIPayment("your_upi_id@ybl", "PhonePe");
    }

    public void payWithPaytm(View view) {
        startUPIPayment("your_upi@ptsbi", "Paytm");
    }

        private void startUPIPayment(String upiId, String appName) {
        try {
            Uri uri = Uri.parse("upi://pay?pa=" + upiId +
                    "&pn=MerchantName" +
                    "&mc=0000" +
                    "&tid=021254" +
                    "&tr=123456789" +
                    "&tn=Payment" +
                    "&am=" + totalAmount +
                    "&cu=INR");

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, appName + " app not found", Toast.LENGTH_SHORT).show();
        }
    }
}
