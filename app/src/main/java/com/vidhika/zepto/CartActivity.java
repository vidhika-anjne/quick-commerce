package com.vidhika.zepto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvTotalPrice;
    private Button btnCheckout;
    private CartAdapter cartAdapter;
    private List<Product> cartItems;
    private LinearLayout layoutOfflineBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Ensure persistent cart storage is initialized
        CartManager.getInstance(this);

        recyclerView = findViewById(R.id.cart_list);
        tvTotalPrice = findViewById(R.id.total_price);
        btnCheckout = findViewById(R.id.btn_checkout);
        layoutOfflineBanner = findViewById(R.id.layout_offline_banner_cart);

        View btnBack = findViewById(R.id.btn_back_cart);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Check network state
        boolean isOnline = NetworkUtils.isNetworkAvailable(this);
        if (layoutOfflineBanner != null) {
            layoutOfflineBanner.setVisibility(isOnline ? View.GONE : View.VISIBLE);
        }

        cartItems = CartManager.getInstance().getCartItems();
        cartAdapter = new CartAdapter(this, cartItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cartAdapter);

        int totalAmount = calculateTotal();

        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(CartActivity.this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!NetworkUtils.isNetworkAvailable(CartActivity.this)) {
                Toast.makeText(CartActivity.this, "Offline Mode: Connect to internet for online payment or use COD", Toast.LENGTH_LONG).show();
            }

            Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
            intent.putExtra("TOTAL_AMOUNT", calculateTotal());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isOnline = NetworkUtils.isNetworkAvailable(this);
        if (layoutOfflineBanner != null) {
            layoutOfflineBanner.setVisibility(isOnline ? View.GONE : View.VISIBLE);
        }
        calculateTotal();
    }

    private int calculateTotal() {
        int total = 0;
        for (Product product : cartItems) {
            try {
                total += Integer.parseInt(product.getPrice().replace("₹", "").trim());
            } catch (Exception ignored) {}
        }
        tvTotalPrice.setText("Total: ₹" + total);
        return total;
    }
}
