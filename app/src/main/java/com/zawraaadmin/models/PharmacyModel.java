package com.zawraaadmin.models;

import java.io.Serializable;

public class PharmacyModel implements Serializable {
   private int id;
   private String title;
   private String category_title;
   private double balance;
   private String address;
   private String latitude;
   private String longitude;
   private String logo;
   private String code;
   private String bills_count;
   private String created_at;
   private String updated_at;

    public PharmacyModel(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory_title() {
        return category_title;
    }

    public double getBalance() {
        return balance;
    }

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLogo() {
        return logo;
    }

    public String getCode() {
        return code;
    }

    public String getBills_count() {
        return bills_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

}
