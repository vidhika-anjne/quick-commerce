package com.vidhika.zepto;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    public static final String CHANNEL_ORDERS_ID = "zepto_order_updates";
    public static final String CHANNEL_ORDERS_NAME = "Order Updates";

    public static final String CHANNEL_PROMOS_ID = "zepto_promotions";
    public static final String CHANNEL_PROMOS_NAME = "Promotions & Offers";

    private static final int NOTIF_ORDER_BASE_ID = 1001;
    private static final int NOTIF_PROMO_ID = 2001;

    public static void initChannels(Context context) {
        if (context == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager == null) return;

        // Channel 1: Order Updates (High Priority - heads up banner & sound)
        NotificationChannel orderChannel = new NotificationChannel(
                CHANNEL_ORDERS_ID,
                CHANNEL_ORDERS_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );
        orderChannel.setDescription("Live tracking updates for your Zepto grocery orders");
        orderChannel.enableLights(true);
        orderChannel.setLightColor(Color.parseColor("#FF3269"));
        orderChannel.enableVibration(true);
        orderChannel.setVibrationPattern(new long[]{0, 250, 200, 250});
        manager.createNotificationChannel(orderChannel);

        // Channel 2: Promotions & Offers (Default Priority)
        NotificationChannel promoChannel = new NotificationChannel(
                CHANNEL_PROMOS_ID,
                CHANNEL_PROMOS_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        promoChannel.setDescription("Exclusive discounts, flash sales and daily grocery deals");
        promoChannel.enableLights(true);
        promoChannel.setLightColor(Color.parseColor("#0C831F"));
        manager.createNotificationChannel(promoChannel);
    }

    public static void showOrderConfirmedNotification(Context context, String orderId, String amount, String etaText) {
        if (context == null) return;
        initChannels(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent intent = new Intent(context, OrdersActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                orderId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ORDERS_ID)
                .setSmallIcon(R.drawable.order_1)
                .setColor(Color.parseColor("#FF3269"))
                .setContentTitle("📦 Order Confirmed • " + orderId)
                .setContentText("Paid " + amount + " • Packing at dark store. ETA: " + etaText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Your order " + orderId + " (" + amount + ") has been verified and is being picked at the nearest dark store. Delivery in " + etaText + "!"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat.from(context).notify(NOTIF_ORDER_BASE_ID, builder.build());
    }

    public static void showOutForDeliveryNotification(Context context, String orderId, String riderName, String etaText) {
        if (context == null) return;
        initChannels(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent intent = new Intent(context, OrdersActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (orderId + "_transit").hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ORDERS_ID)
                .setSmallIcon(R.drawable.order_1)
                .setColor(Color.parseColor("#FF3269"))
                .setContentTitle("⚡ Out for Delivery • " + etaText)
                .setContentText("Rider " + riderName + " is arriving with your order " + orderId)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("⚡ Rider " + riderName + " has picked up order " + orderId + " and is en route! Arriving at your doorstep in " + etaText + "."))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat.from(context).notify(NOTIF_ORDER_BASE_ID + 1, builder.build());
    }

    public static void showPromotionalNotification(Context context, String title, String message, String promoCode) {
        if (context == null) return;
        initChannels(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent intent = new Intent(context, ProductPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (promoCode != null) {
            intent.putExtra("PROMO_CODE", promoCode);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                NOTIF_PROMO_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        String displayTitle = (title != null && !title.isEmpty()) ? title : "🎉 Exclusive Zepto Offer!";
        String displayMsg = (message != null && !message.isEmpty()) ? message : "Flat ₹75 OFF on fresh groceries today. Order now!";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_PROMOS_ID)
                .setSmallIcon(R.drawable.order_1)
                .setColor(Color.parseColor("#0C831F"))
                .setContentTitle(displayTitle)
                .setContentText(displayMsg)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(displayMsg))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat.from(context).notify(NOTIF_PROMO_ID, builder.build());
    }

    /**
     * Triggers immediate Order Confirmation and automatically schedules a realistic
     * "Rider Out For Delivery" dispatch update after 8 seconds for live demonstration.
     */
    public static void triggerOrderLifecycleNotifications(Context context, String orderId, String amount) {
        if (context == null) return;

        // Step 1: Immediate Order Confirmed
        showOrderConfirmedNotification(context, orderId, amount, "10 mins");

        // Step 2: Simulated Live Dispatch after 8 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            showOutForDeliveryNotification(context, orderId, "Rajesh K.", "4 mins");
        }, 8000);
    }
}

