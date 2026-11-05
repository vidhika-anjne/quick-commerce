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

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Product> cartItems;
    private  CartManager cartManager;

    public  CartAdapter(Context context, List<Product> cartItems){
        this.context=context;
        this.cartItems=cartItems;
        this.cartManager=CartManager.getInstance();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent , int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent,false);
        return  new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position){
        Product product=cartItems.get(position);

        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(product.getPrice());
        holder.productImage.setImageResource(product.getImageResId());

        holder.btnRemove.setOnClickListener( v -> {
            cartManager.removeFromCart(product);
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
        });
    }

    @Override
    public int getItemCount(){
        return  cartItems.size();
    }

    public  static class CartViewHolder extends  RecyclerView.ViewHolder{
        ImageView productImage;
        TextView tvProductName, tvProductPrice;
        Button btnRemove;

        public  CartViewHolder(@NonNull View itemView){
            super((itemView));

            productImage= itemView.findViewById(R.id.cart_product_image);
            tvProductName=itemView.findViewById(R.id.cart_product_name);
            tvProductPrice=itemView.findViewById(R.id.cart_product_price);
            btnRemove= itemView.findViewById(R.id.remove_from_cart);
        }
    }


}
