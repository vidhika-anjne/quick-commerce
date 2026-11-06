package com.vidhika.zepto;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.UUID;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.AddressActionListener {

    private RecyclerView rvAddresses;
    private AddressAdapter adapter;
    private List<Address> addressList;
    private LinearLayout layoutEmpty;
    private TextView tvAddressCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        rvAddresses = findViewById(R.id.rv_addresses);
        layoutEmpty = findViewById(R.id.layout_empty_addresses);
        tvAddressCount = findViewById(R.id.tv_address_count_badge);
        ImageView btnBack = findViewById(R.id.btn_back_address);
        Button btnAddNew = findViewById(R.id.btn_add_new_address);

        btnBack.setOnClickListener(v -> finish());
        btnAddNew.setOnClickListener(v -> showAddressFormDialog(null));

        loadAddresses();
    }

    private void loadAddresses() {
        addressList = AddressManager.getInstance().getAddresses(this);

        if (addressList == null || addressList.isEmpty()) {
            rvAddresses.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            tvAddressCount.setText("0 Saved");
        } else {
            rvAddresses.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            tvAddressCount.setText(addressList.size() + " Saved");

            adapter = new AddressAdapter(this, addressList, this);
            rvAddresses.setLayoutManager(new LinearLayoutManager(this));
            rvAddresses.setAdapter(adapter);
        }
    }

    @Override
    public void onEdit(Address address) {
        showAddressFormDialog(address);
    }

    @Override
    public void onDelete(Address address) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Address")
                .setMessage("Are you sure you want to delete this address (" + address.getLabel() + ")?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    AddressManager.getInstance().deleteAddress(AddressActivity.this, address.getId());
                    Toast.makeText(AddressActivity.this, "Address deleted", Toast.LENGTH_SHORT).show();
                    loadAddresses();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onSetDefault(Address address) {
        AddressManager.getInstance().setDefaultAddress(this, address.getId());
        Toast.makeText(this, "Default address set to " + address.getLabel(), Toast.LENGTH_SHORT).show();
        loadAddresses();
    }

    private void showAddressFormDialog(Address addressToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_address_form, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView tvTitle = dialogView.findViewById(R.id.tv_form_title);
        ImageView btnClose = dialogView.findViewById(R.id.btn_close_form);
        RadioGroup rgType = dialogView.findViewById(R.id.rg_address_type);
        RadioButton rbHome = dialogView.findViewById(R.id.rb_home);
        RadioButton rbWork = dialogView.findViewById(R.id.rb_work);
        RadioButton rbOther = dialogView.findViewById(R.id.rb_other);

        EditText etName = dialogView.findViewById(R.id.et_receiver_name);
        EditText etPhone = dialogView.findViewById(R.id.et_phone_number);
        EditText etHouse = dialogView.findViewById(R.id.et_house_flat);
        EditText etArea = dialogView.findViewById(R.id.et_area_street);
        EditText etCity = dialogView.findViewById(R.id.et_city_pincode);
        CheckBox cbDefault = dialogView.findViewById(R.id.cb_is_default);
        Button btnSave = dialogView.findViewById(R.id.btn_save_address);

        boolean isEditing = addressToEdit != null;
        tvTitle.setText(isEditing ? "Edit Address" : "Add New Address");
        btnSave.setText(isEditing ? "Update Address" : "Save Address");

        if (isEditing) {
            etName.setText(addressToEdit.getReceiverName());
            etPhone.setText(addressToEdit.getPhoneNumber());
            etHouse.setText(addressToEdit.getHouseFlat());
            etArea.setText(addressToEdit.getAreaStreet());
            etCity.setText(addressToEdit.getCityPincode());
            cbDefault.setChecked(addressToEdit.isDefault());

            if ("Home".equalsIgnoreCase(addressToEdit.getLabel())) {
                rbHome.setChecked(true);
            } else if ("Work".equalsIgnoreCase(addressToEdit.getLabel())) {
                rbWork.setChecked(true);
            } else {
                rbOther.setChecked(true);
            }
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String house = etHouse.getText().toString().trim();
            String area = etArea.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            boolean isDef = cbDefault.isChecked();

            if (TextUtils.isEmpty(name)) {
                etName.setError("Receiver name is required");
                etName.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(phone) || phone.length() < 10) {
                etPhone.setError("Enter a valid 10-digit phone number");
                etPhone.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(house)) {
                etHouse.setError("House / Flat number is required");
                etHouse.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(area)) {
                etArea.setError("Area / Street is required");
                etArea.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(city)) {
                etCity.setError("City and pincode are required");
                etCity.requestFocus();
                return;
            }

            String selectedLabel = "Home";
            int checkedId = rgType.getCheckedRadioButtonId();
            if (checkedId == R.id.rb_work) {
                selectedLabel = "Work";
            } else if (checkedId == R.id.rb_other) {
                selectedLabel = "Other";
            }

            String addressId = isEditing ? addressToEdit.getId() : UUID.randomUUID().toString();
            Address address = new Address(addressId, selectedLabel, name, phone, house, area, city, isDef);

            AddressManager.getInstance().saveAddress(AddressActivity.this, address);
            Toast.makeText(AddressActivity.this, isEditing ? "Address updated!" : "New address saved!", Toast.LENGTH_SHORT).show();

            dialog.dismiss();
            loadAddresses();
        });

        dialog.show();
    }
}

