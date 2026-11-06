package com.vidhika.zepto;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final String PREF_NAME = "zepto_cart_cache";
    private static final String KEY_CART_ITEMS = "cached_cart_items";

    private static CartManager instance;
    private List<Product> cartItems;
    private Context appContext;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager();
        }
        if (context != null && instance.appContext == null) {
            instance.init(context);
        }
        return instance;
    }

    public void init(Context context) {
        if (context != null) {
            this.appContext = context.getApplicationContext();
            loadCartFromStorage();
        }
    }

    public void addToCart(Product product) {
        cartItems.add(product);
        persistCartToStorage();
    }

    public void removeFromCart(Product product) {
        cartItems.remove(product);
        persistCartToStorage();
    }

    public List<Product> getCartItems() {
        if (cartItems.isEmpty() && appContext != null) {
            loadCartFromStorage();
        }
        return new ArrayList<>(cartItems);
    }

    public void clearCart() {
        cartItems.clear();
        persistCartToStorage();
    }

    private void persistCartToStorage() {
        if (appContext == null) return;

        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();

        for (Product product : cartItems) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", product.getName());
                jsonObject.put("imageResId", product.getImageResId());
                jsonObject.put("price", product.getPrice());
                jsonObject.put("mrp", product.getMrp());
                jsonObject.put("quantity", product.getQuantity());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        prefs.edit().putString(KEY_CART_ITEMS, jsonArray.toString()).apply();
    }

    private void loadCartFromStorage() {
        if (appContext == null) return;

        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String jsonString = prefs.getString(KEY_CART_ITEMS, null);

        if (jsonString != null && !jsonString.isEmpty()) {
            cartItems.clear();
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String name = obj.getString("name");
                    int imageResId = obj.getInt("imageResId");
                    String price = obj.getString("price");
                    String mrp = obj.getString("mrp");
                    String quantity = obj.getString("quantity");

                    cartItems.add(new Product(name, imageResId, price, mrp, quantity));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
