package com.vidhika.zepto;

public class Product {
    private String name;
    private int imageResId;
    private String price;
    private String mrp;
    private String quantity;
    private String description;

    public Product(String name, int imageResId, String price, String mrp, String quantity) {
        this(name, imageResId, price, mrp, quantity, getDefaultDescription(name));
    }

    public Product(String name, int imageResId, String price, String mrp, String quantity, String description) {
        this.name = name;
        this.imageResId = imageResId;
        this.price = price;
        this.mrp = mrp;
        this.quantity = quantity;
        this.description = (description != null && !description.isEmpty()) ? description : getDefaultDescription(name);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static String getDefaultDescription(String productName) {
        if (productName == null) return "High quality fresh product delivered directly from dark stores in 10 minutes.";
        String lower = productName.toLowerCase();
        if (lower.contains("fortune") || lower.contains("oil")) {
            return "Fortune Sunlite Refined Sunflower Oil is enriched with Vitamin A and Vitamin D. It has a high smoke point, is light and easy to digest, making it ideal for daily healthy cooking and frying.";
        } else if (lower.contains("milk")) {
            return "Amul Taaza Homogenised Toned Milk is pasteurised, wholesome milk packed with essential calcium and vitamins. Great for tea, coffee, breakfast cereals, and daily family nutrition.";
        } else if (lower.contains("onion")) {
            return "Farm-fresh, hand-picked onions sourced directly from trusted growers. Rich in flavour, texture, and natural antioxidants, perfect for daily curries, gravies, and salads.";
        } else if (lower.contains("coconut")) {
            return "Fresh whole coconut with sweet natural water and thick nutritious flesh. Great for South Indian chutneys, curries, desserts, or direct grating.";
        } else if (lower.contains("butter")) {
            return "Amul Butter is India's favourite salted butter, crafted with fresh cream. Wonderfully smooth, rich, and delicious on warm toast, parathas, baking, and cooking.";
        } else if (lower.contains("coriander")) {
            return "Freshly harvested, aromatic coriander leaves (dhaniya). Crisp, green, and vibrant, ideal for garnishing Indian curries, soups, and preparing fresh chutneys.";
        }
        return "Guaranteed fresh and authentic product sourced with quality standards, packaged with utmost hygiene, and delivered to your doorstep in 10 minutes.";
    }
}
