package com.vidhika.zepto;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class ProductPage extends AppCompatActivity {

    private EditText searchBar;
    private ImageView profileIcon;

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> filteredList;

    @SuppressLint("MissingInflatedId")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Explicit call to super
        setContentView(R.layout.activity_product_page);

        recyclerView = findViewById(R.id.recyclerview);
        searchBar = findViewById(R.id.search_bar);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        productList = new ArrayList<>();
        productList.add(new Product("Fortune Sunflower", R.drawable.fortune, "₹156", "MRP ₹190", "1 l"));
        productList.add(new Product("Amul taaza Milk(Pouch)", R.drawable.amul_milk, "₹28", "MRP ₹30", "500ml"));
        productList.add(new Product("Onion", R.drawable.onion, "₹83", "MRP ₹111", "1 kg"));
        productList.add(new Product("Coconut", R.drawable.coconut, "₹89", "MRP ₹119", "1 "));
        productList.add(new Product("Amul Butter", R.drawable.amul_butter, "₹60", "MRP ₹90", "50 gm"));
        productList.add(new Product("Coriander", R.drawable.corianderc, "₹18", "MRP ₹24", "1 "));

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