package com.vidhika.zepto;

public class OrderItem {
    private String productName;
    private int imageResId;
    private String price;
    private String quantity;
    private String trackingStatus;
    private String trackingDescription;

    public OrderItem(String productName, int imageResId, String price, String quantity, String trackingStatus, String trackingDescription) {
        this.productName = productName;
        this.imageResId = imageResId;
        this.price = price;
        this.quantity = quantity;
        this.trackingStatus = trackingStatus;
        this.trackingDescription = trackingDescription;
    }

    public String getProductName() {
        return productName;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getTrackingStatus() {
        return trackingStatus;
    }

    public void setTrackingStatus(String trackingStatus) {
        this.trackingStatus = trackingStatus;
    }

    public String getTrackingDescription() {
        return trackingDescription;
    }

    public void setTrackingDescription(String trackingDescription) {
        this.trackingDescription = trackingDescription;
    }
}

