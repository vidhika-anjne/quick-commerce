package com.vidhika.zepto;

public class Product {
    private String name;
    private int imageResId;
    private String price;
    private String mrp;
    private String quantity;

    public Product(String name,int imageResId,String price,String mrp,String quantity){
        this.name=name;
        this.imageResId=imageResId;
        this.price=price;
        this.mrp=mrp;
        this.quantity=quantity;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getPrice() {
        return price;
    }

    public String getMrp() {
        return mrp;
    }

    public String getQuantity() {
        return quantity;
    }
}