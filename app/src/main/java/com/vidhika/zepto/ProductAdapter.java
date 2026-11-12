package com.vidhika.zepto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context mContext;
    private List<Product> mProductList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.mContext = context;
        this.mProductList = productList;
        CartManager.getInstance(context);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = mProductList.get(position);

        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(product.getPrice());
        holder.tvProductMrp.setText(product.getMrp());

        holder.tvProductMrp.setPaintFlags(holder.tvProductMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tvQuantity.setText(product.getQuantity());
        holder.imageView.setImageResource(product.getImageResId());

        // Read current quantity from CartManager to prevent recycled view bugs
        int currentCartQty = CartManager.getInstance(mContext).getProductQuantity(product);
        if (currentCartQty > 0) {
            holder.btnAddToCart.setVisibility(View.GONE);
            holder.cartContainer.setVisibility(View.VISIBLE);
            holder.tvItemCount.setText(String.valueOf(currentCartQty));
        } else {
            holder.btnAddToCart.setVisibility(View.VISIBLE);
            holder.cartContainer.setVisibility(View.GONE);
            holder.tvItemCount.setText("1");
        }

        holder.itemView.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION && currentPos < mProductList.size()) {
                Product target = mProductList.get(currentPos);
                Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                intent.putExtra("product_name", target.getName());
                intent.putExtra("product_image", target.getImageResId());
                intent.putExtra("product_price", target.getPrice());
                intent.putExtra("product_mrp", target.getMrp());
                intent.putExtra("product_quantity", target.getQuantity());
                intent.putExtra("product_description", target.getDescription());
                mContext.startActivity(intent);
            }
        });

        holder.btnAddToCart.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION && currentPos < mProductList.size()) {
                Product target = mProductList.get(currentPos);
                CartManager.getInstance(mContext).addToCart(target);
                int qty = CartManager.getInstance(mContext).getProductQuantity(target);
                holder.btnAddToCart.setVisibility(View.GONE);
                holder.cartContainer.setVisibility(View.VISIBLE);
                holder.tvItemCount.setText(String.valueOf(qty));
            }
        });

        holder.btnIncrease.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION && currentPos < mProductList.size()) {
                Product target = mProductList.get(currentPos);
                CartManager.getInstance(mContext).addToCart(target);
                int qty = CartManager.getInstance(mContext).getProductQuantity(target);
                holder.tvItemCount.setText(String.valueOf(qty));
            }
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION && currentPos < mProductList.size()) {
                Product target = mProductList.get(currentPos);
                CartManager.getInstance(mContext).removeFromCart(target);
                int qty = CartManager.getInstance(mContext).getProductQuantity(target);
                if (qty > 0) {
                    holder.tvItemCount.setText(String.valueOf(qty));
                } else {
                    holder.cartContainer.setVisibility(View.GONE);
                    holder.btnAddToCart.setVisibility(View.VISIBLE);
                    holder.tvItemCount.setText("1");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductMrp, tvQuantity, tvItemCount;
        ImageView imageView;
        Button btnAddToCart, btnIncrease, btnDecrease;
        LinearLayout cartContainer;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.product_name);
            tvProductPrice = itemView.findViewById(R.id.product_price);
            tvProductMrp = itemView.findViewById(R.id.product_mrp);
            tvQuantity = itemView.findViewById(R.id.quantity);
            imageView = itemView.findViewById(R.id.product_image);
            btnAddToCart = itemView.findViewById(R.id.add_to_cart);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            tvItemCount = itemView.findViewById(R.id.item_count);
            cartContainer = itemView.findViewById(R.id.cart_container);
        }
    }
}
