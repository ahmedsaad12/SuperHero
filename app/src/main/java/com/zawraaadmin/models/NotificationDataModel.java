package com.zawraaadmin.models;

import java.io.Serializable;
import java.util.List;

public class NotificationDataModel implements Serializable {
    private List<NotificationModel> data;
    private int current_page;

    public List<NotificationModel> getData() {
        return data;
    }

    public int getCurrent_page() {
        return current_page;
    }
}
