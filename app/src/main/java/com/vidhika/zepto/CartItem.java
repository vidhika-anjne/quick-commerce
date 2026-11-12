package com.vidhika.zepto;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUnitPrice() {
        if (product == null || product.getPrice() == null) return 0;
        try {
            return Integer.parseInt(product.getPrice().replace("₹", "").trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public int getTotalPrice() {
        return getUnitPrice() * quantity;
    }
}

