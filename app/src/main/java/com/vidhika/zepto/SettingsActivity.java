package com.vidhika.zepto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        View btnBack = findViewById(R.id.btn_back_settings);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        View btnOrders = findViewById(R.id.btn_action_orders);
        if (btnOrders != null) {
            btnOrders.setOnClickListener(v -> {
                startActivity(new Intent(SettingsActivity.this, OrdersActivity.class));
            });
        }

        View rowOrders = findViewById(R.id.row_orders_reordering);
        if (rowOrders != null) {
            rowOrders.setOnClickListener(v -> {
                startActivity(new Intent(SettingsActivity.this, OrdersActivity.class));
            });
        }

        View rowAddresses = findViewById(R.id.row_saved_addresses);
        if (rowAddresses != null) {
            rowAddresses.setOnClickListener(v -> {
                startActivity(new Intent(SettingsActivity.this, AddressActivity.class));
            });
        }
    }

    public void zeptoCash(View view) {
        Toast.makeText(this, "Zepto Cash Balance: ₹75", Toast.LENGTH_SHORT).show();
    }
}
