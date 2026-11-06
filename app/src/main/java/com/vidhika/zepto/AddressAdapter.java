package com.vidhika.zepto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    public interface AddressActionListener {
        void onEdit(Address address);
        void onDelete(Address address);
        void onSetDefault(Address address);
    }

    private Context context;
    private List<Address> addressList;
    private AddressActionListener listener;

    public AddressAdapter(Context context, List<Address> addressList, AddressActionListener listener) {
        this.context = context;
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address_card, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);

        String labelIcon = "📍 ";
        if ("Home".equalsIgnoreCase(address.getLabel())) {
            labelIcon = "🏠 ";
        } else if ("Work".equalsIgnoreCase(address.getLabel())) {
            labelIcon = "💼 ";
        }
        holder.tvLabel.setText(labelIcon + address.getLabel());

        holder.tvReceiverName.setText(address.getReceiverName());
        holder.tvPhoneNumber.setText("• +91 " + address.getPhoneNumber());
        holder.tvFullAddress.setText(address.getFormattedAddress());

        if (address.isDefault()) {
            holder.tvDefaultBadge.setVisibility(View.VISIBLE);
            holder.btnSetDefault.setVisibility(View.GONE);
        } else {
            holder.tvDefaultBadge.setVisibility(View.GONE);
            holder.btnSetDefault.setVisibility(View.VISIBLE);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(address);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(address);
        });

        holder.btnSetDefault.setOnClickListener(v -> {
            if (listener != null) listener.onSetDefault(address);
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvDefaultBadge, tvReceiverName, tvPhoneNumber, tvFullAddress, btnSetDefault;
        ImageView btnEdit, btnDelete;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tv_address_label);
            tvDefaultBadge = itemView.findViewById(R.id.tv_default_badge);
            tvReceiverName = itemView.findViewById(R.id.tv_receiver_name);
            tvPhoneNumber = itemView.findViewById(R.id.tv_phone_number);
            tvFullAddress = itemView.findViewById(R.id.tv_full_address);
            btnSetDefault = itemView.findViewById(R.id.btn_set_default);
            btnEdit = itemView.findViewById(R.id.btn_edit_address);
            btnDelete = itemView.findViewById(R.id.btn_delete_address);
        }
    }
}

