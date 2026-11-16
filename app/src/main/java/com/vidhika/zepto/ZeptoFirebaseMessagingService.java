package com.vidhika.zepto;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class ZeptoFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "ZeptoFCM";
    private static final String PREFS_FCM = "zepto_fcm_prefs";
    private static final String KEY_FCM_TOKEN = "fcm_token";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM Registration Token: " + token);

        // Store token in SharedPreferences for easy retrieval
        SharedPreferences prefs = getSharedPreferences(PREFS_FCM, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = null;
        String body = null;

        // 1. Check Notification Payload
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        // 2. Check Data Payload
        Map<String, String> data = remoteMessage.getData();
        if (data != null && !data.isEmpty()) {
            if (title == null && data.containsKey("title")) {
                title = data.get("title");
            }
            if (body == null && data.containsKey("body")) {
                body = data.get("body");
            }

            String type = data.get("type");
            if ("order".equalsIgnoreCase(type)) {
                String orderId = data.get("order_id");
                String amount = data.get("amount");
                String eta = data.get("eta");
                if (orderId == null) orderId = "#ZEP-LIVE";
                if (amount == null) amount = "₹0";
                if (eta == null) eta = "8 mins";

                NotificationHelper.showOrderConfirmedNotification(this, orderId, amount, eta);
                return;
            }
        }

        // Default to Promotional Notification
        String promoCode = (data != null) ? data.get("promo_code") : null;
        NotificationHelper.showPromotionalNotification(this, title, body, promoCode);
    }
}

