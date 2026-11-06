package com.vidhika.zepto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public OrdersAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_card, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderId.setText(order.getOrderId());
        holder.tvOrderDate.setText(order.getOrderDate());
        holder.tvOrderTotal.setText(order.getTotalAmount());
        holder.tvOrderEta.setText(order.getEtaText());

        int stage = order.getProgressStage();

        // Configure Status Pill & Timeline according to stage
        if (stage >= 4) {
            holder.tvStatusPill.setText("✓ Delivered");
            holder.tvStatusPill.setBackgroundResource(R.drawable.bg_tracking_badge_delivered);
            holder.tvStatusPill.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));

            holder.iconStep1.setBackgroundResource(R.drawable.bg_pill_green);
            holder.lineStep1_2.setBackgroundColor(ContextCompat.getColor(context, R.color.zepto_green));
            holder.iconStep2.setBackgroundResource(R.drawable.bg_pill_green);
            holder.lineStep2_3.setBackgroundColor(ContextCompat.getColor(context, R.color.zepto_green));
            holder.iconStep3.setBackgroundResource(R.drawable.bg_pill_green);
            holder.iconStep3.setImageResource(R.drawable.ic_check_small);
            holder.lineStep3_4.setBackgroundColor(ContextCompat.getColor(context, R.color.zepto_green));

            holder.tvStep3.setText("Delivered");
            holder.tvStep3.setTextColor(ContextCompat.getColor(context, R.color.zepto_green));
            holder.tvStep4.setTextColor(ContextCompat.getColor(context, R.color.zepto_green));
            holder.btnTrackLive.setText("Reorder");
        } else if (stage == 3) {
            holder.tvStatusPill.setText("⚡ Out for Delivery");
            holder.tvStatusPill.setBackgroundResource(R.drawable.bg_pill_green);
            holder.tvStatusPill.setTextColor(ContextCompat.getColor(context, R.color.zepto_green));

            holder.iconStep1.setBackgroundResource(R.drawable.bg_pill_green);
            holder.lineStep1_2.setBackgroundColor(ContextCompat.getColor(context, R.color.zepto_green));
            holder.iconStep2.setBackgroundResource(R.drawable.bg_pill_green);
            holder.lineStep2_3.setBackgroundColor(ContextCompat.getColor(context, R.color.zepto_green));
            holder.iconStep3.setBackgroundResource(R.drawable.bg_pill_pink);
            holder.iconStep3.setImageResource(R.drawable.ic_delivery_bike);
            holder.lineStep3_4.setBackgroundColor(ContextCompat.getColor(context, R.color.divider));

            holder.tvStep3.setTextColor(ContextCompat.getColor(context, R.color.zepto_pink));
            holder.btnTrackLive.setText("Track Live");
        } else {
            holder.tvStatusPill.setText("📦 Order Packing");
            holder.tvStatusPill.setBackgroundResource(R.drawable.bg_pill_purple);
            holder.tvStatusPill.setTextColor(ContextCompat.getColor(context, R.color.zepto_primary));

            holder.iconStep1.setBackgroundResource(R.drawable.bg_pill_green);
            holder.lineStep1_2.setBackgroundColor(ContextCompat.getColor(context, R.color.zepto_green));
            holder.iconStep2.setBackgroundResource(R.drawable.bg_pill_pink);
            holder.lineStep2_3.setBackgroundColor(ContextCompat.getColor(context, R.color.divider));
            holder.iconStep3.setBackgroundResource(R.drawable.bg_pill_purple);
            holder.lineStep3_4.setBackgroundColor(ContextCompat.getColor(context, R.color.divider));

            holder.btnTrackLive.setText("Track Live");
        }

        // Dynamically inflate each product item with tracking details
        holder.containerItems.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(context);

        List<OrderItem> items = order.getItems();
        holder.tvItemsCountHeader.setText("Product Details & Tracking (" + items.size() + ")");

        for (OrderItem item : items) {
            View itemView = inflater.inflate(R.layout.item_order_product, holder.containerItems, false);

            ImageView imgProduct = itemView.findViewById(R.id.img_order_product);
            TextView tvName = itemView.findViewById(R.id.tv_order_product_name);
            TextView tvQty = itemView.findViewById(R.id.tv_order_product_qty);
            TextView tvPrice = itemView.findViewById(R.id.tv_order_product_price);
            TextView tvTrackingStatus = itemView.findViewById(R.id.tv_product_tracking_status);
            TextView tvTrackingDesc = itemView.findViewById(R.id.tv_product_tracking_desc);
            View layoutTracking = itemView.findViewById(R.id.layout_product_tracking);

            imgProduct.setImageResource(item.getImageResId());
            tvName.setText(item.getProductName());
            tvQty.setText(item.getQuantity());
            tvPrice.setText(item.getPrice());
            tvTrackingStatus.setText(item.getTrackingStatus());
            tvTrackingDesc.setText(item.getTrackingDescription());

            if (stage >= 4) {
                layoutTracking.setBackgroundResource(R.drawable.bg_tracking_badge_delivered);
                tvTrackingStatus.setTextColor(ContextCompat.getColor(context, R.color.zepto_green));
            } else {
                layoutTracking.setBackgroundResource(R.drawable.bg_tracking_badge_active);
                tvTrackingStatus.setTextColor(ContextCompat.getColor(context, R.color.zepto_green));
            }

            holder.containerItems.addView(itemView);
        }

        holder.btnTrackLive.setOnClickListener(v -> {
            if (stage >= 4) {
                Toast.makeText(context, "Items added to cart for reorder!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Delivery partner Rajesh is 4 minutes away from your location!", Toast.LENGTH_LONG).show();
            }
        });

        holder.btnHelp.setOnClickListener(v -> {
            Toast.makeText(context, "Connecting to Zepto 24x7 Customer Support for " + order.getOrderId(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvStatusPill, tvOrderEta, tvOrderTotal, tvItemsCountHeader;
        ImageView iconStep1, iconStep2, iconStep3;
        View iconStep4, lineStep1_2, lineStep2_3, lineStep3_4;
        TextView tvStep1, tvStep2, tvStep3, tvStep4;
        LinearLayout containerItems;
        Button btnTrackLive, btnHelp;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvStatusPill = itemView.findViewById(R.id.tv_order_status_pill);
            tvOrderEta = itemView.findViewById(R.id.tv_order_eta);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            tvItemsCountHeader = itemView.findViewById(R.id.tv_items_count_header);

            iconStep1 = itemView.findViewById(R.id.icon_step1);
            iconStep2 = itemView.findViewById(R.id.icon_step2);
            iconStep3 = itemView.findViewById(R.id.icon_step3);
            iconStep4 = itemView.findViewById(R.id.icon_step4);
            lineStep1_2 = itemView.findViewById(R.id.line_step1_2);
            lineStep2_3 = itemView.findViewById(R.id.line_step2_3);
            lineStep3_4 = itemView.findViewById(R.id.line_step3_4);

            tvStep1 = itemView.findViewById(R.id.tv_step1);
            tvStep2 = itemView.findViewById(R.id.tv_step2);
            tvStep3 = itemView.findViewById(R.id.tv_step3);
            tvStep4 = itemView.findViewById(R.id.tv_step4);

            containerItems = itemView.findViewById(R.id.container_order_items);
            btnTrackLive = itemView.findViewById(R.id.btn_track_rider);
            btnHelp = itemView.findViewById(R.id.btn_help);
        }
    }
}

