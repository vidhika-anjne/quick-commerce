package com.vidhika.zepto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
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
    private NestedScrollView scrollCartContent;
    private LinearLayout layoutEmptyCart;
    private View bottomCheckoutBar;
    private Button btnEmptyAddProducts;

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
        scrollCartContent = findViewById(R.id.scroll_cart_content);
        layoutEmptyCart = findViewById(R.id.layout_empty_cart);
        bottomCheckoutBar = findViewById(R.id.bottom_checkout_bar);
        btnEmptyAddProducts = findViewById(R.id.btn_empty_add_products);

        View btnBack = findViewById(R.id.btn_back_cart);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnEmptyAddProducts != null) {
            btnEmptyAddProducts.setOnClickListener(v -> {
                Intent intent = new Intent(CartActivity.this, ProductPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        // Check network state
        boolean isOnline = NetworkUtils.isNetworkAvailable(this);
        if (layoutOfflineBanner != null) {
            layoutOfflineBanner.setVisibility(isOnline ? View.GONE : View.VISIBLE);
        }

        cartItems = CartManager.getInstance().getCartItems();
        cartAdapter = new CartAdapter(this, cartItems, () -> {
            calculateTotal();
            updateCartUi();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cartAdapter);

        calculateTotal();
        updateCartUi();

        btnCheckout.setOnClickListener(v -> {
            // Strictly prevent users from proceeding to checkout if the cart is empty
            if (cartItems == null || cartItems.isEmpty() || calculateTotal() <= 0) {
                Toast.makeText(CartActivity.this, "Your cart is empty. Add products to continue.", Toast.LENGTH_SHORT).show();
                updateCartUi();
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

    private void updateCartUi() {
        boolean isEmpty = (cartItems == null || cartItems.isEmpty());

        if (isEmpty) {
            if (scrollCartContent != null) scrollCartContent.setVisibility(View.GONE);
            if (layoutEmptyCart != null) layoutEmptyCart.setVisibility(View.VISIBLE);
            if (bottomCheckoutBar != null) bottomCheckoutBar.setVisibility(View.GONE);
            tvTotalPrice.setText("Total: ₹0");
        } else {
            if (scrollCartContent != null) scrollCartContent.setVisibility(View.VISIBLE);
            if (layoutEmptyCart != null) layoutEmptyCart.setVisibility(View.GONE);
            if (bottomCheckoutBar != null) bottomCheckoutBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isOnline = NetworkUtils.isNetworkAvailable(this);
        if (layoutOfflineBanner != null) {
            layoutOfflineBanner.setVisibility(isOnline ? View.GONE : View.VISIBLE);
        }

        cartItems = CartManager.getInstance().getCartItems();
        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
        }
        calculateTotal();
        updateCartUi();
    }

    private int calculateTotal() {
        int total = 0;
        if (cartItems != null) {
            for (Product product : cartItems) {
                try {
                    total += Integer.parseInt(product.getPrice().replace("₹", "").trim());
                } catch (Exception ignored) {}
            }
        }
        tvTotalPrice.setText("Total: ₹" + total);
        return total;
    }
}
