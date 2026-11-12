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

public class CartManager {
    private static final String TAG = "CartManager";
    private static final String PREF_NAME = "zepto_cart_cache";

    private static CartManager instance;
    private List<CartItem> cartItemList;
    private Context appContext;
    private String currentLoadedUserKey;

    private CartManager() {
        cartItemList = new ArrayList<>();
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
        if (context != null) {
            if (instance.appContext == null) {
                instance.init(context);
            } else {
                instance.ensureLoaded();
            }
        }
        return instance;
    }

    public void init(Context context) {
        if (context != null) {
            this.appContext = context.getApplicationContext();
            loadCartFromStorage();
        }
    }

    private String getCurrentUserCartKey() {
        String phone = (appContext != null) ? AuthManager.getPhoneNumber(appContext) : "guest";
        if (phone == null || phone.trim().isEmpty()) {
            phone = "guest";
        }
        // Normalize: e.g. "+91 8103126630" -> "918103126630"
        String sanitized = phone.replaceAll("[^0-9a-zA-Z]", "");
        if (sanitized.isEmpty()) {
            sanitized = "guest";
        }
        return "cart_items_user_" + sanitized;
    }

    public synchronized void switchUser() {
        if (appContext != null) {
            persistCartToStorage();
            loadCartFromStorage();
        }
    }

    public synchronized void onUserLoggedOut() {
        if (appContext != null) {
            persistCartToStorage();
        }
        cartItemList.clear();
        currentLoadedUserKey = null;
    }

    public synchronized void addToCart(Product product) {
        if (product == null) return;
        ensureLoaded();

        CartItem existing = findCartItem(product.getName());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + 1);
        } else {
            cartItemList.add(new CartItem(product, 1));
        }

        persistCartToStorage();
    }

    public synchronized void removeFromCart(Product product) {
        if (product == null) return;
        ensureLoaded();

        CartItem existing = findCartItem(product.getName());
        if (existing != null) {
            if (existing.getQuantity() > 1) {
                existing.setQuantity(existing.getQuantity() - 1);
            } else {
                cartItemList.remove(existing);
            }
            persistCartToStorage();
        }
    }

    public synchronized void removeProductCompletely(Product product) {
        if (product == null) return;
        ensureLoaded();

        CartItem existing = findCartItem(product.getName());
        if (existing != null) {
            cartItemList.remove(existing);
            persistCartToStorage();
        }
    }

    public synchronized int getProductQuantity(Product product) {
        if (product == null) return 0;
        return getProductQuantity(product.getName());
    }

    public synchronized int getProductQuantity(String productName) {
        if (productName == null) return 0;
        ensureLoaded();

        CartItem item = findCartItem(productName);
        return item != null ? item.getQuantity() : 0;
    }

    public synchronized List<CartItem> getCartItemList() {
        ensureLoaded();
        return new ArrayList<>(cartItemList);
    }

    /**
     * Backward-compatible accessor returning a list of Products currently in the cart.
     */
    public synchronized List<Product> getCartItems() {
        ensureLoaded();
        List<Product> products = new ArrayList<>();
        for (CartItem item : cartItemList) {
            products.add(item.getProduct());
        }
        return products;
    }

    public synchronized int getTotalCartAmount() {
        ensureLoaded();
        int total = 0;
        for (CartItem item : cartItemList) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public synchronized int getTotalItemCount() {
        ensureLoaded();
        int total = 0;
        for (CartItem item : cartItemList) {
            total += item.getQuantity();
        }
        return total;
    }

    public synchronized boolean isCartEmpty() {
        ensureLoaded();
        return cartItemList.isEmpty();
    }

    public synchronized void clearCart() {
        ensureLoaded();
        cartItemList.clear();
        persistCartToStorage();
    }

    private CartItem findCartItem(String productName) {
        if (productName == null) return null;
        for (CartItem item : cartItemList) {
            if (item.getProduct() != null && productName.trim().equalsIgnoreCase(item.getProduct().getName().trim())) {
                return item;
            }
        }
        return null;
    }

    private void ensureLoaded() {
        if (appContext == null) return;
        String expectedKey = getCurrentUserCartKey();
        if (currentLoadedUserKey == null || !currentLoadedUserKey.equals(expectedKey)) {
            loadCartFromStorage();
        }
    }

    private void persistCartToStorage() {
        if (appContext == null) return;

        String userKey = getCurrentUserCartKey();
        currentLoadedUserKey = userKey;
        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();

        for (CartItem item : cartItemList) {
            try {
                Product product = item.getProduct();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", product.getName());
                jsonObject.put("imageResId", product.getImageResId());
                jsonObject.put("price", product.getPrice());
                jsonObject.put("mrp", product.getMrp());
                jsonObject.put("quantity", product.getQuantity());
                jsonObject.put("description", product.getDescription());
                jsonObject.put("cartQuantity", item.getQuantity());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        prefs.edit().putString(userKey, jsonArray.toString()).apply();

        // Sync to Cloud Firestore in background
        syncCartToCloudFirestore();
    }

    private void syncCartToCloudFirestore() {
        if (appContext == null) return;
        String phone = AuthManager.getPhoneNumber(appContext);
        if (phone == null || phone.trim().isEmpty() || "guest".equalsIgnoreCase(phone)) return;

        String sanitizedPhone = phone.replaceAll("[^0-9a-zA-Z]", "");
        if (sanitizedPhone.isEmpty()) return;

        List<Map<String, Object>> itemsList = new ArrayList<>();
        for (CartItem item : cartItemList) {
            Product p = item.getProduct();
            Map<String, Object> map = new HashMap<>();
            map.put("name", p.getName());
            map.put("price", p.getPrice());
            map.put("mrp", p.getMrp());
            map.put("quantity", p.getQuantity());
            map.put("description", p.getDescription());
            map.put("imageResId", p.getImageResId());
            map.put("cartQuantity", item.getQuantity());
            itemsList.add(map);
        }

        Map<String, Object> docData = new HashMap<>();
        docData.put("phoneNumber", phone);
        docData.put("updatedAt", System.currentTimeMillis());
        docData.put("items", itemsList);

        FirebaseFirestore.getInstance()
                .collection("user_carts")
                .document(sanitizedPhone)
                .set(docData, SetOptions.merge())
                .addOnFailureListener(e -> Log.w(TAG, "Cloud cart sync error: " + e.getMessage()));
    }

    public synchronized void loadCartFromStorage() {
        if (appContext == null) return;

        currentLoadedUserKey = getCurrentUserCartKey();
        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String jsonString = prefs.getString(currentLoadedUserKey, null);

        cartItemList.clear();
        if (jsonString != null && !jsonString.isEmpty()) {
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
                    int cartQty = obj.optInt("cartQuantity", 1);

                    Product product = new Product(name, imageResId, price, mrp, quantity, description);
                    cartItemList.add(new CartItem(product, cartQty));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // Local is empty, attempt to restore from Cloud Firestore in the background
            restoreCartFromCloudFirestore();
        }
    }

    private void restoreCartFromCloudFirestore() {
        if (appContext == null) return;
        String phone = AuthManager.getPhoneNumber(appContext);
        if (phone == null || phone.trim().isEmpty() || "guest".equalsIgnoreCase(phone)) return;

        String sanitizedPhone = phone.replaceAll("[^0-9a-zA-Z]", "");
        if (sanitizedPhone.isEmpty()) return;

        FirebaseFirestore.getInstance()
                .collection("user_carts")
                .document(sanitizedPhone)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc != null && doc.exists()) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");
                        if (items != null && !items.isEmpty() && cartItemList.isEmpty()) {
                            for (Map<String, Object> map : items) {
                                String name = (String) map.get("name");
                                String price = (String) map.get("price");
                                String mrp = (String) map.get("mrp");
                                String quantity = (String) map.get("quantity");
                                String description = (String) map.get("description");
                                Long cartQtyL = (Long) map.get("cartQuantity");
                                Long imgResL = (Long) map.get("imageResId");

                                int cartQty = (cartQtyL != null) ? cartQtyL.intValue() : 1;
                                int imageRes = (imgResL != null) ? imgResL.intValue() : R.drawable.fortune;

                                Product p = new Product(name, imageRes, price, mrp, quantity, description);
                                cartItemList.add(new CartItem(p, cartQty));
                            }
                            persistCartToStorage();
                        }
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Cloud cart restore error: " + e.getMessage()));
    }
}
