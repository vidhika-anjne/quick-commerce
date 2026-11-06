package com.vidhika.zepto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class OrderManager {
    private static OrderManager instance;
    private List<Order> orders;

    private OrderManager() {
        orders = new ArrayList<>();
        seedInitialOrders();
    }

    public static synchronized OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    private void seedInitialOrders() {
        // Active Order (Live Tracking)
        List<OrderItem> activeItems = new ArrayList<>();
        activeItems.add(new OrderItem(
                "Fortune Sunflower Oil",
                R.drawable.fortune,
                "₹156",
                "1 l",
                "⚡ Out for Delivery",
                "Item picked & in transit with delivery partner"
        ));
        activeItems.add(new OrderItem(
                "Amul Taaza Milk (Pouch)",
                R.drawable.amul_milk,
                "₹28",
                "500ml",
                "⚡ Out for Delivery",
                "Stored in insulated delivery bag"
        ));
        activeItems.add(new OrderItem(
                "Amul Butter",
                R.drawable.amul_butter,
                "₹60",
                "50 gm",
                "⚡ Out for Delivery",
                "Dispatched from Indiranagar Dark Store"
        ));

        Order activeOrder = new Order(
                "#ZEP-94821",
                "Today, 12:40 PM",
                "₹244",
                "Flat 402, Green Glen Layout, Indiranagar",
                "Out for Delivery",
                3,
                "⚡ Arriving in 4 mins • Rider: Rajesh K.",
                activeItems
        );
        orders.add(activeOrder);

        // Past Delivered Order
        List<OrderItem> pastItems = new ArrayList<>();
        pastItems.add(new OrderItem(
                "Fresh Onion",
                R.drawable.onion,
                "₹83",
                "1 kg",
                "✓ Delivered",
                "Handed over to customer at doorstep"
        ));
        pastItems.add(new OrderItem(
                "Fresh Coconut",
                R.drawable.coconut,
                "₹89",
                "1 pc",
                "✓ Delivered",
                "Handed over to customer at doorstep"
        ));

        Order pastOrder = new Order(
                "#ZEP-88102",
                "Yesterday, 06:15 PM",
                "₹172",
                "Flat 402, Green Glen Layout, Indiranagar",
                "Delivered",
                4,
                "Delivered in 8 mins • OTP Verified",
                pastItems
        );
        orders.add(pastOrder);
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }

    public void addOrder(Order order) {
        orders.add(0, order);
    }

    public Order createOrderFromCart(List<Product> cartProducts, int totalAmount, String paymentMethod) {
        String orderId = "#ZEP-" + (10000 + new Random().nextInt(90000));
        SimpleDateFormat sdf = new SimpleDateFormat("Today, hh:mm a", Locale.getDefault());
        String orderDate = sdf.format(new Date());

        List<OrderItem> orderItems = new ArrayList<>();
        for (Product product : cartProducts) {
            orderItems.add(new OrderItem(
                    product.getName(),
                    product.getImageResId(),
                    product.getPrice(),
                    product.getQuantity(),
                    "📦 Packed at Dark Store",
                    "Item picked and verified by dark store associate"
            ));
        }

        Order newOrder = new Order(
                orderId,
                orderDate,
                "₹" + totalAmount,
                "Flat 402, Green Glen Layout, Indiranagar (" + paymentMethod + ")",
                "Order Placed",
                2,
                "⚡ Arriving in 10 mins • Preparing shipment",
                orderItems
        );

        orders.add(0, newOrder);
        return newOrder;
    }
}

