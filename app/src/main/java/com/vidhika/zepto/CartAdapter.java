package com.vidhika.zepto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnCartChangedListener {
        void onCartChanged();
    }

    private Context context;
    private List<CartItem> cartItems;
    private CartManager cartManager;
    private OnCartChangedListener onCartChangedListener;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this(context, cartItems, null);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartChangedListener listener) {
        this.context = context;
        this.cartItems = (cartItems != null) ? cartItems : new ArrayList<>();
        this.cartManager = CartManager.getInstance(context);
        this.onCartChangedListener = listener;
    }

    public void updateCartItems(List<CartItem> items) {
        this.cartItems = (items != null) ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        Product product = item.getProduct();

        holder.tvProductName.setText(product.getName());
        holder.productImage.setImageResource(product.getImageResId());

        if (holder.tvUnitPrice != null) {
            holder.tvUnitPrice.setText(product.getQuantity() + " • " + product.getPrice() + " each");
        }

        holder.tvProductPrice.setText("₹" + item.getTotalPrice());
        holder.tvItemCount.setText(String.valueOf(item.getQuantity()));

        holder.btnIncrease.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION && currentPos < cartItems.size()) {
                CartItem targetItem = cartItems.get(currentPos);
                cartManager.addToCart(targetItem.getProduct());
                cartItems = cartManager.getCartItemList();
                notifyItemChanged(currentPos);
                if (onCartChangedListener != null) {
                    onCartChangedListener.onCartChanged();
                }
            }
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION && currentPos < cartItems.size()) {
                CartItem targetItem = cartItems.get(currentPos);
                cartManager.removeFromCart(targetItem.getProduct());
                cartItems = cartManager.getCartItemList();
                notifyDataSetChanged();
                if (onCartChangedListener != null) {
                    onCartChangedListener.onCartChanged();
                }
            }
        });

        if (holder.btnLegacyRemove != null) {
            holder.btnLegacyRemove.setOnClickListener(v -> {
                int currentPos = holder.getAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION && currentPos < cartItems.size()) {
                    CartItem targetItem = cartItems.get(currentPos);
                    cartManager.removeProductCompletely(targetItem.getProduct());
                    cartItems = cartManager.getCartItemList();
                    notifyDataSetChanged();
                    if (onCartChangedListener != null) {
                        onCartChangedListener.onCartChanged();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView tvProductName, tvProductPrice, tvUnitPrice, tvItemCount;
        View btnIncrease, btnDecrease;
        View btnLegacyRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.cart_product_image);
            tvProductName = itemView.findViewById(R.id.cart_product_name);
            tvUnitPrice = itemView.findViewById(R.id.cart_product_unit_price);
            tvProductPrice = itemView.findViewById(R.id.cart_product_price);
            tvItemCount = itemView.findViewById(R.id.tv_cart_item_count);
            btnIncrease = itemView.findViewById(R.id.btn_cart_increase);
            btnDecrease = itemView.findViewById(R.id.btn_cart_decrease);
            btnLegacyRemove = itemView.findViewById(R.id.remove_from_cart);
        }
    }
}
