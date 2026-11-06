package com.vidhika.zepto;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView imgProduct;
    private TextView tvName, tvPrice, tvMrp, tvQuantity, tvDescription;
    private TextView tvBottomPrice, tvBottomUnit, tvDetailCount, tvCartBadge;
    private Button btnAddToCart, btnIncrease, btnDecrease;
    private LinearLayout layoutStepper;
    private View btnCartShortcut, btnBack;

    private String productName;
    private int productImageRes;
    private String productPrice;
    private String productMrp;
    private String productQuantity;
    private String productDescription;
    private Product currentProduct;

    private int itemCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        CartManager.getInstance(this);

        // Read intent extras
        Intent intent = getIntent();
        productName = intent.getStringExtra("product_name");
        productImageRes = intent.getIntExtra("product_image", R.drawable.ic_launcher_foreground);
        productPrice = intent.getStringExtra("product_price");
        productMrp = intent.getStringExtra("product_mrp");
        productQuantity = intent.getStringExtra("product_quantity");
        productDescription = intent.getStringExtra("product_description");

        if (productName == null) productName = "Product Name";
        if (productPrice == null) productPrice = "₹0";
        if (productMrp == null) productMrp = "MRP ₹0";
        if (productQuantity == null) productQuantity = "1 unit";
        if (productDescription == null || productDescription.isEmpty()) {
            productDescription = Product.getDefaultDescription(productName);
        }

        currentProduct = new Product(productName, productImageRes, productPrice, productMrp, productQuantity, productDescription);

        initViews();
        bindData();
        setupListeners();
        updateCartBadge();
        checkIfProductInCart();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back_details);
        btnCartShortcut = findViewById(R.id.btn_cart_shortcut);
        tvCartBadge = findViewById(R.id.tv_cart_badge);

        imgProduct = findViewById(R.id.img_product_detail);
        tvName = findViewById(R.id.tv_detail_name);
        tvPrice = findViewById(R.id.tv_detail_price);
        tvMrp = findViewById(R.id.tv_detail_mrp);
        tvQuantity = findViewById(R.id.tv_detail_quantity);
        tvDescription = findViewById(R.id.tv_detail_description);

        tvBottomPrice = findViewById(R.id.tv_bottom_price);
        tvBottomUnit = findViewById(R.id.tv_bottom_unit);
        btnAddToCart = findViewById(R.id.btn_detail_add_to_cart);
        layoutStepper = findViewById(R.id.layout_detail_stepper);
        btnDecrease = findViewById(R.id.btn_detail_decrease);
        btnIncrease = findViewById(R.id.btn_detail_increase);
        tvDetailCount = findViewById(R.id.tv_detail_count);
    }

    private void bindData() {
        imgProduct.setImageResource(productImageRes);
        tvName.setText(productName);
        tvPrice.setText(productPrice);
        tvMrp.setText(productMrp);
        tvMrp.setPaintFlags(tvMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvQuantity.setText(productQuantity);
        tvDescription.setText(productDescription);

        tvBottomPrice.setText(productPrice);
        tvBottomUnit.setText(productQuantity);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCartShortcut.setOnClickListener(v -> {
            startActivity(new Intent(ProductDetailsActivity.this, CartActivity.class));
        });

        btnAddToCart.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(currentProduct);
            itemCount = 1;
            tvDetailCount.setText(String.valueOf(itemCount));
            btnAddToCart.setVisibility(View.GONE);
            layoutStepper.setVisibility(View.VISIBLE);
            updateCartBadge();
            Toast.makeText(ProductDetailsActivity.this, productName + " added to cart!", Toast.LENGTH_SHORT).show();
        });

        btnIncrease.setOnClickListener(v -> {
            itemCount++;
            tvDetailCount.setText(String.valueOf(itemCount));
            CartManager.getInstance().addToCart(currentProduct);
            updateCartBadge();
        });

        btnDecrease.setOnClickListener(v -> {
            if (itemCount > 1) {
                itemCount--;
                tvDetailCount.setText(String.valueOf(itemCount));
                CartManager.getInstance().removeFromCart(currentProduct);
                updateCartBadge();
            } else {
                itemCount = 0;
                CartManager.getInstance().removeFromCart(currentProduct);
                layoutStepper.setVisibility(View.GONE);
                btnAddToCart.setVisibility(View.VISIBLE);
                updateCartBadge();
                Toast.makeText(ProductDetailsActivity.this, productName + " removed from cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfProductInCart() {
        List<Product> cartItems = CartManager.getInstance().getCartItems();
        int count = 0;
        for (Product item : cartItems) {
            if (item.getName().equalsIgnoreCase(productName)) {
                count++;
            }
        }

        if (count > 0) {
            itemCount = count;
            tvDetailCount.setText(String.valueOf(itemCount));
            btnAddToCart.setVisibility(View.GONE);
            layoutStepper.setVisibility(View.VISIBLE);
        } else {
            btnAddToCart.setVisibility(View.VISIBLE);
            layoutStepper.setVisibility(View.GONE);
        }
    }

    private void updateCartBadge() {
        List<Product> items = CartManager.getInstance().getCartItems();
        if (items != null && !items.isEmpty()) {
            tvCartBadge.setVisibility(View.VISIBLE);
            tvCartBadge.setText(String.valueOf(items.size()));
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
        checkIfProductInCart();
    }
}

