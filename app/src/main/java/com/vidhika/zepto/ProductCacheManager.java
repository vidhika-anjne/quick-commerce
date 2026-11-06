package com.vidhika.zepto;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductCacheManager {

    private static final String PREF_NAME = "zepto_offline_cache";
    private static final String KEY_CACHED_PRODUCTS = "cached_products";
    private static final String KEY_CACHE_TIMESTAMP = "cache_timestamp";

    public static void saveProducts(Context context, List<Product> productList) {
        if (context == null || productList == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();

        for (Product product : productList) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", product.getName());
                jsonObject.put("imageResId", product.getImageResId());
                jsonObject.put("price", product.getPrice());
                jsonObject.put("mrp", product.getMrp());
                jsonObject.put("quantity", product.getQuantity());
                jsonObject.put("description", product.getDescription());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        prefs.edit()
                .putString(KEY_CACHED_PRODUCTS, jsonArray.toString())
                .putLong(KEY_CACHE_TIMESTAMP, System.currentTimeMillis())
                .apply();
    }

    public static List<Product> loadProducts(Context context) {
        if (context == null) return getDefaultCatalog();

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String jsonString = prefs.getString(KEY_CACHED_PRODUCTS, null);

        if (jsonString == null || jsonString.isEmpty()) {
            List<Product> defaults = getDefaultCatalog();
            saveProducts(context, defaults);
            return defaults;
        }

        List<Product> productList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String name = obj.getString("name");
                int imageResId = obj.getInt("imageResId");
                String price = obj.getString("price");
                String mrp = obj.getString("mrp");
                String quantity = obj.getString("quantity");
                String description = obj.optString("description", Product.getDefaultDescription(name));

                productList.add(new Product(name, imageResId, price, mrp, quantity, description));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return getDefaultCatalog();
        }

        return productList.isEmpty() ? getDefaultCatalog() : productList;
    }

    public static boolean hasCachedProducts(Context context) {
        if (context == null) return false;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.contains(KEY_CACHED_PRODUCTS);
    }

    public static List<Product> getDefaultCatalog() {
        List<Product> list = new ArrayList<>();
        list.add(new Product("Fortune Sunflower", R.drawable.fortune, "₹156", "MRP ₹190", "1 l"));
        list.add(new Product("Amul taaza Milk(Pouch)", R.drawable.amul_milk, "₹28", "MRP ₹30", "500ml"));
        list.add(new Product("Onion", R.drawable.onion, "₹83", "MRP ₹111", "1 kg"));
        list.add(new Product("Coconut", R.drawable.coconut, "₹89", "MRP ₹119", "1 "));
        list.add(new Product("Amul Butter", R.drawable.amul_butter, "₹60", "MRP ₹90", "50 gm"));
        list.add(new Product("Coriander", R.drawable.corianderc, "₹18", "MRP ₹24", "1 "));
        return list;
    }
}
