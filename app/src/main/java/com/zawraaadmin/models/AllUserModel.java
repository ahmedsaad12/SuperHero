package com.zawraaadmin.models;

import java.io.Serializable;
import java.util.List;

public class AllUserModel implements Serializable {
   private List<SingleUserModel> data;
   private int status;

    public List<SingleUserModel> getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }
}
