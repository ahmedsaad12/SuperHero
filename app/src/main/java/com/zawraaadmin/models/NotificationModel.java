package com.zawraaadmin.models;

import java.io.Serializable;

public class NotificationModel implements Serializable {
   private int id;
   private String ar_title;
   private String en_title;
   private String ar_desc;
   private String en_desc;
   private String type;
   private int from_user_id;
   private int client_id;
   private String is_read;
   private String created_at;
   private String updated_at;
private UserModel.User client;
    public int getId() {
        return id;
    }

    public String getAr_title() {
        return ar_title;
    }

    public String getEn_title() {
        return en_title;
    }

    public String getAr_desc() {
        return ar_desc;
    }

    public String getEn_desc() {
        return en_desc;
    }

    public String getType() {
        return type;
    }

    public int getFrom_user_id() {
        return from_user_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public String getIs_read() {
        return is_read;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public UserModel.User getClient() {
        return client;
    }
}
