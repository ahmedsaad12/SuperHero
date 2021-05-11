package com.zawraaadmin.models;

import java.io.Serializable;

public class SingleUserModel implements Serializable {
    private int id;
    private String name;
    private String phone;
    private String access_code;
    private double balance;
    private String email;
    private String logo;
    private String address;
    private String latitude;
    private String longitude;
    private String is_block;
    private String is_login;
    private String logout_time;
    private String notification_status;
    private String email_verification_code;
    private String email_verified_at;
    private String deleted_at;
    private String created_at;
    private String updated_at;

    public SingleUserModel(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAccess_code() {
        return access_code;
    }

    public double getBalance() {
        return balance;
    }

    public String getEmail() {
        return email;
    }

    public String getLogo() {
        return logo;
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

    public String getIs_block() {
        return is_block;
    }

    public String getIs_login() {
        return is_login;
    }

    public String getLogout_time() {
        return logout_time;
    }

    public String getNotification_status() {
        return notification_status;
    }

    public String getEmail_verification_code() {
        return email_verification_code;
    }

    public String getEmail_verified_at() {
        return email_verified_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
