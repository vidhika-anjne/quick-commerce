package com.vidhika.zepto;

public class Address {
    private String id;
    private String label;
    private String receiverName;
    private String phoneNumber;
    private String houseFlat;
    private String areaStreet;
    private String cityPincode;
    private boolean isDefault;

    public Address(String id, String label, String receiverName, String phoneNumber,
                   String houseFlat, String areaStreet, String cityPincode, boolean isDefault) {
        this.id = id;
        this.label = label;
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
        this.houseFlat = houseFlat;
        this.areaStreet = areaStreet;
        this.cityPincode = cityPincode;
        this.isDefault = isDefault;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHouseFlat() {
        return houseFlat;
    }

    public void setHouseFlat(String houseFlat) {
        this.houseFlat = houseFlat;
    }

    public String getAreaStreet() {
        return areaStreet;
    }

    public void setAreaStreet(String areaStreet) {
        this.areaStreet = areaStreet;
    }

    public String getCityPincode() {
        return cityPincode;
    }

    public void setCityPincode(String cityPincode) {
        this.cityPincode = cityPincode;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getFormattedAddress() {
        return houseFlat + ", " + areaStreet + ", " + cityPincode;
    }
}

