package com.vidhika.zepto;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddressManager {
    private static final String PREF_NAME = "zepto_address_pref";
    private static final String KEY_ADDRESSES = "saved_addresses";

    private static AddressManager instance;

    private AddressManager() {}

    public static synchronized AddressManager getInstance() {
        if (instance == null) {
            instance = new AddressManager();
        }
        return instance;
    }

    public List<Address> getAddresses(Context context) {
        if (context == null) return new ArrayList<>();

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String jsonString = prefs.getString(KEY_ADDRESSES, null);

        if (jsonString == null || jsonString.isEmpty()) {
            List<Address> defaultList = getDefaultAddresses();
            saveAllAddresses(context, defaultList);
            return defaultList;
        }

        List<Address> addressList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String id = obj.optString("id", UUID.randomUUID().toString());
                String label = obj.optString("label", "Home");
                String receiverName = obj.optString("receiverName", "ABC XYZ");
                String phoneNumber = obj.optString("phoneNumber", "8669425109");
                String houseFlat = obj.optString("houseFlat", "");
                String areaStreet = obj.optString("areaStreet", "");
                String cityPincode = obj.optString("cityPincode", "");
                boolean isDefault = obj.optBoolean("isDefault", false);

                addressList.add(new Address(id, label, receiverName, phoneNumber, houseFlat, areaStreet, cityPincode, isDefault));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return getDefaultAddresses();
        }

        return addressList;
    }

    public void saveAddress(Context context, Address address) {
        if (context == null || address == null) return;

        List<Address> list = getAddresses(context);
        boolean updated = false;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(address.getId())) {
                list.set(i, address);
                updated = true;
                break;
            }
        }

        if (!updated) {
            if (list.isEmpty()) {
                address.setDefault(true);
            }
            list.add(0, address);
        }

        if (address.isDefault()) {
            for (Address a : list) {
                if (!a.getId().equals(address.getId())) {
                    a.setDefault(false);
                }
            }
        }

        saveAllAddresses(context, list);
    }

    public void deleteAddress(Context context, String addressId) {
        if (context == null || addressId == null) return;

        List<Address> list = getAddresses(context);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(addressId)) {
                boolean wasDefault = list.get(i).isDefault();
                list.remove(i);
                if (wasDefault && !list.isEmpty()) {
                    list.get(0).setDefault(true);
                }
                break;
            }
        }

        saveAllAddresses(context, list);
    }

    public void setDefaultAddress(Context context, String addressId) {
        if (context == null || addressId == null) return;

        List<Address> list = getAddresses(context);
        for (Address a : list) {
            a.setDefault(a.getId().equals(addressId));
        }

        saveAllAddresses(context, list);
    }

    private void saveAllAddresses(Context context, List<Address> list) {
        if (context == null || list == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();

        for (Address a : list) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("id", a.getId());
                obj.put("label", a.getLabel());
                obj.put("receiverName", a.getReceiverName());
                obj.put("phoneNumber", a.getPhoneNumber());
                obj.put("houseFlat", a.getHouseFlat());
                obj.put("areaStreet", a.getAreaStreet());
                obj.put("cityPincode", a.getCityPincode());
                obj.put("isDefault", a.isDefault());
                jsonArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        prefs.edit().putString(KEY_ADDRESSES, jsonArray.toString()).apply();
    }

    private List<Address> getDefaultAddresses() {
        List<Address> list = new ArrayList<>();
        list.add(new Address(
                "addr_1",
                "Home",
                "ABC XYZ",
                "8669425109",
                "Flat 402, Green Glen Layout",
                "100ft Road, Indiranagar",
                "Bengaluru - 560038",
                true
        ));
        list.add(new Address(
                "addr_2",
                "Work",
                "ABC XYZ",
                "8669425109",
                "Tower B, 5th Floor, Ecospace Tech Park",
                "Outer Ring Road, Bellandur",
                "Bengaluru - 560103",
                false
        ));
        return list;
    }
}

