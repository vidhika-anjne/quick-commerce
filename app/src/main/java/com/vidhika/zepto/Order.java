package com.vidhika.zepto;

import java.util.List;

public class Order {
    private String orderId;
    private String orderDate;
    private String totalAmount;
    private String deliveryAddress;
    private String status;
    private int progressStage; // 1: Confirmed, 2: Packed, 3: Out for Delivery, 4: Delivered
    private String etaText;
    private List<OrderItem> items;

    public Order(String orderId, String orderDate, String totalAmount, String deliveryAddress,
                 String status, int progressStage, String etaText, List<OrderItem> items) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.progressStage = progressStage;
        this.etaText = etaText;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProgressStage() {
        return progressStage;
    }

    public void setProgressStage(int progressStage) {
        this.progressStage = progressStage;
    }

    public String getEtaText() {
        return etaText;
    }

    public void setEtaText(String etaText) {
        this.etaText = etaText;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}

