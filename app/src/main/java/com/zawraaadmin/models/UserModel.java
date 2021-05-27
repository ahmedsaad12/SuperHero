package com.zawraaadmin.models;

import java.io.Serializable;

public class UserModel implements Serializable {

    private User data;
    private int status;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    public static class User implements Serializable {
        private int id;
        private String name;
        private String phone;
        private String email;
        private String email_verified_at;
        private String image;
        private int admin_type;
        private String lang;
        private String deleted_at;
        private String created_at;
        private String updated_at;
        private String token;
        private String fireBaseToken;
        private double latitude;
        private double longitude;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        public String getEmail_verified_at() {
            return email_verified_at;
        }

        public String getImage() {
            return image;
        }

        public int getAdmin_type() {
            return admin_type;
        }

        public String getLang() {
            return lang;
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

        public String getToken() {
            return token;
        }

        public String getFireBaseToken() {
            return fireBaseToken;
        }

        public void setFireBaseToken(String fireBaseToken) {
            this.fireBaseToken = fireBaseToken;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    public int getStatus() {
        return status;
    }
}
