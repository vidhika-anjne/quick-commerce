package com.vidhika.zepto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private OrdersAdapter ordersAdapter;
    private List<Order> orderList;
    private LinearLayout layoutEmpty;
    private TextView tvOrdersCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        rvOrders = findViewById(R.id.rv_orders);
        layoutEmpty = findViewById(R.id.layout_empty_orders);
        tvOrdersCount = findViewById(R.id.tv_orders_count_badge);
        ImageView btnBack = findViewById(R.id.btn_back_orders);
        View btnBrowse = findViewById(R.id.btn_browse_products);

        btnBack.setOnClickListener(v -> finish());

        if (btnBrowse != null) {
            btnBrowse.setOnClickListener(v -> {
                Intent intent = new Intent(OrdersActivity.this, ProductPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        loadOrders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        orderList = OrderManager.getInstance().getOrders();

        if (orderList == null || orderList.isEmpty()) {
            rvOrders.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            tvOrdersCount.setText("0 Orders");
        } else {
            rvOrders.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            tvOrdersCount.setText(orderList.size() + (orderList.size() == 1 ? " Order" : " Orders"));

            ordersAdapter = new OrdersAdapter(this, orderList);
            rvOrders.setLayoutManager(new LinearLayoutManager(this));
            rvOrders.setAdapter(ordersAdapter);
        }
    }
}

