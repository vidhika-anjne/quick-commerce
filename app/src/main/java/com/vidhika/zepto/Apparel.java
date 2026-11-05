package com.vidhika.zepto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class Apparel extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apparel);

        recyclerView =findViewById(R.id.recyclerView);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        categoryList = new ArrayList<>();
        categoryList.add(new Category("Men's Topwear", R.drawable.men_top));
        categoryList.add(new Category("Men's Bottomwear", R.drawable.mens_bottom));
        categoryList.add(new Category("Women's Topwear", R.drawable.women_top));
        categoryList.add(new Category("Women's Bottomwear", R.drawable.women_bottom));
        categoryList.add(new Category("Footwear", R.drawable.footwear));
        categoryList.add(new Category("Bags", R.drawable.bags));
        categoryList.add(new Category("Jewellery", R.drawable.jewellery));
        categoryList.add(new Category("Travel", R.drawable.travel));

        categoryAdapter = new CategoryAdapter(this, categoryList);

        recyclerView.setAdapter(categoryAdapter);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_back) {
                    Intent intent = new Intent(Apparel.this, ProductPage.class);
                    startActivity(intent);
                    return true;

                } else if(itemId == R.id.nav_categories) {
                    Intent intent = new Intent(Apparel.this, CategoryActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_apparel) {
                    Intent intent = new Intent(Apparel.this, Apparel.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_cart) {
                    Intent intent = new Intent(Apparel.this, CartActivity.class);
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }
}


