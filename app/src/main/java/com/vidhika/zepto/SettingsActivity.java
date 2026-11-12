package com.vidhika.zepto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
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

        // Display current authenticated phone number
        TextView tvPhone = findViewById(R.id.tv_settings_phone);
        if (tvPhone != null) {
            tvPhone.setText(AuthManager.getPhoneNumber(this));
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

        // Log out handler
        View btnLogout = findViewById(R.id.btn_logout_settings);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                CartManager.getInstance(SettingsActivity.this).onUserLoggedOut();
                AuthManager.signOut(SettingsActivity.this);
                Toast.makeText(SettingsActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    public void zeptoCash(View view) {
        Toast.makeText(this, "Zepto Cash Balance: ₹75", Toast.LENGTH_SHORT).show();
    }
}
