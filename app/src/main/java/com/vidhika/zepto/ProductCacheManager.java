package com.vidhika.zepto;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductCacheManager {

    private static final String TAG = "ProductCacheManager";
    private static final String PREF_NAME = "zepto_offline_cache";
    private static final String KEY_CACHED_PRODUCTS = "cached_products";
    private static final String KEY_CACHE_TIMESTAMP = "cache_timestamp";

    public interface OnCatalogUpdatedListener {
        void onCatalogUpdated(List<Product> products);
    }

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

    /**
     * Fetch products from Cloud Firestore. If Firestore is empty, it automatically seeds
     * the initial catalog to Firestore so products exist in the cloud for free.
     */
    public static void syncProductsFromFirestore(Context context, OnCatalogUpdatedListener listener) {
        if (context == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        List<Product> cloudProducts = new ArrayList<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            String name = doc.getString("name");
                            if (name == null || name.isEmpty()) continue;

                            String price = doc.getString("price");
                            String mrp = doc.getString("mrp");
                            String quantity = doc.getString("quantity");
                            String description = doc.getString("description");
                            String imageKey = doc.getString("imageKey");

                            if (price == null) price = "₹0";
                            if (mrp == null) mrp = "MRP ₹0";
                            if (quantity == null) quantity = "1 unit";
                            if (description == null) description = Product.getDefaultDescription(name);

                            int imageRes = resolveDrawableResId(context, imageKey, name);
                            cloudProducts.add(new Product(name, imageRes, price, mrp, quantity, description));
                        }

                        if (!cloudProducts.isEmpty()) {
                            saveProducts(context, cloudProducts);
                            if (listener != null) {
                                listener.onCatalogUpdated(cloudProducts);
                            }
                            return;
                        }
                    }

                    // Collection empty, seed catalog to Firestore in cloud
                    seedProductsToFirestore(context, listener);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Firestore product fetch failed, fallback to local cache: " + e.getMessage());
                    // Fallback to local cache
                    if (listener != null) {
                        listener.onCatalogUpdated(loadProducts(context));
                    }
                });
    }

    /**
     * Seeds initial products into Cloud Firestore.
     */
    public static void seedProductsToFirestore(Context context, OnCatalogUpdatedListener listener) {
        List<Product> catalog = getDefaultCatalog();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (Product product : catalog) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", product.getName());
            data.put("price", product.getPrice());
            data.put("mrp", product.getMrp());
            data.put("quantity", product.getQuantity());
            data.put("description", product.getDescription());
            data.put("imageKey", mapProductToImageKey(product.getName()));

            String docId = product.getName().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
            db.collection("products").document(docId).set(data, SetOptions.merge());
        }

        saveProducts(context, catalog);
        if (listener != null) {
            listener.onCatalogUpdated(catalog);
        }
    }

    private static String mapProductToImageKey(String name) {
        if (name == null) return "fortune";
        String lower = name.toLowerCase();
        if (lower.contains("sunflower") || lower.contains("fortune")) return "fortune";
        if (lower.contains("milk") || lower.contains("amul taaza")) return "amul_milk";
        if (lower.contains("onion")) return "onion";
        if (lower.contains("coconut")) return "coconut";
        if (lower.contains("butter")) return "amul_butter";
        if (lower.contains("coriander")) return "corianderc";
        return "fortune";
    }

    private static int resolveDrawableResId(Context context, String imageKey, String productName) {
        if (imageKey != null && !imageKey.isEmpty()) {
            int resId = context.getResources().getIdentifier(imageKey, "drawable", context.getPackageName());
            if (resId != 0) return resId;
        }

        String fallbackKey = mapProductToImageKey(productName);
        int fallbackResId = context.getResources().getIdentifier(fallbackKey, "drawable", context.getPackageName());
        return (fallbackResId != 0) ? fallbackResId : R.drawable.fortune;
    }
}
