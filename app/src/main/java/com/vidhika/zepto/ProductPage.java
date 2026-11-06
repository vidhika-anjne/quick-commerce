package com.vidhika.zepto;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ProductPage extends AppCompatActivity {

    private EditText searchBar;
    private ImageView profileIcon;

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> filteredList;

    private LinearLayout layoutOfflineBanner;
    private TextView tvOfflineMsg;
    private ConnectivityManager.NetworkCallback networkCallback;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);

        // Initialize persistent cart storage
        CartManager.getInstance(this);

        recyclerView = findViewById(R.id.recyclerview);
        searchBar = findViewById(R.id.search_bar);
        layoutOfflineBanner = findViewById(R.id.layout_offline_banner);
        tvOfflineMsg = findViewById(R.id.tv_offline_msg);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // Load products: from network if available, otherwise from offline persistent cache
        loadProductsWithOfflineSupport();

        filteredList = new ArrayList<>(productList);
        productAdapter = new ProductAdapter(this, filteredList);
        recyclerView.setAdapter(productAdapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_back) {
                    return true;
                } else if (itemId == R.id.nav_orders) {
                    startActivity(new Intent(ProductPage.this, OrdersActivity.class));
                    return true;
                } else if (itemId == R.id.nav_categories) {
                    startActivity(new Intent(ProductPage.this, CategoryActivity.class));
                    return true;
                } else if (itemId == R.id.nav_apparel) {
                    startActivity(new Intent(ProductPage.this, Apparel.class));
                    return true;
                } else if (itemId == R.id.nav_cart) {
                    startActivity(new Intent(ProductPage.this, CartActivity.class));
                    return true;
                }
                return false;
            }
        });

        profileIcon = findViewById(R.id.profile);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductPage.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // Register real-time network listener
        networkCallback = NetworkUtils.registerNetworkCallback(this, isConnected -> {
            runOnUiThread(() -> updateNetworkState(isConnected));
        });
    }

    private void loadProductsWithOfflineSupport() {
        boolean isOnline = NetworkUtils.isNetworkAvailable(this);

        if (isOnline) {
            // Online: Load products and cache them locally for offline access
            productList = ProductCacheManager.getDefaultCatalog();
            ProductCacheManager.saveProducts(this, productList);
            if (layoutOfflineBanner != null) {
                layoutOfflineBanner.setVisibility(View.GONE);
            }
        } else {
            // Offline: Retrieve previously cached products
            productList = ProductCacheManager.loadProducts(this);
            if (layoutOfflineBanner != null) {
                layoutOfflineBanner.setVisibility(View.VISIBLE);
                if (tvOfflineMsg != null) {
                    tvOfflineMsg.setText("Offline Mode • Browsing " + productList.size() + " cached products");
                }
            }
        }
    }

    private void updateNetworkState(boolean isConnected) {
        if (layoutOfflineBanner == null) return;

        if (isConnected) {
            layoutOfflineBanner.setVisibility(View.GONE);
            Toast.makeText(this, "⚡ You're back online!", Toast.LENGTH_SHORT).show();
            // Refresh and save fresh catalog
            productList = ProductCacheManager.getDefaultCatalog();
            ProductCacheManager.saveProducts(this, productList);
            filterProducts(searchBar.getText().toString());
        } else {
            layoutOfflineBanner.setVisibility(View.VISIBLE);
            if (tvOfflineMsg != null) {
                tvOfflineMsg.setText("Offline Mode • Browsing " + productList.size() + " cached products");
            }
            Toast.makeText(this, "No internet connection. Using cached products & cart.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check network state on resume
        boolean isConnected = NetworkUtils.isNetworkAvailable(this);
        if (layoutOfflineBanner != null) {
            layoutOfflineBanner.setVisibility(isConnected ? View.GONE : View.VISIBLE);
        }
        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkCallback != null) {
            NetworkUtils.unregisterNetworkCallback(this, networkCallback);
        }
    }

    private void filterProducts(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(productList);
        } else {
            for (Product product : productList) {
                if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(product);
                }
            }
        }
        productAdapter.notifyDataSetChanged();
    }
}
