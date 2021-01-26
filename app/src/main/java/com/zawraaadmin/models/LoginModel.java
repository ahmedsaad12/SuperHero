package com.zawraaadmin.models;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;


import com.zawraaadmin.BR;
import com.zawraaadmin.R;

import java.io.Serializable;

public class LoginModel extends BaseObservable implements Serializable {
    private String email;
    private String password;
    public ObservableField<String> error_email = new ObservableField<>();
    public ObservableField<String> error_password = new ObservableField<>();

    public LoginModel() {
        email ="";
        password = "";
    }

    public boolean isDataValid(Context context){
        if (!email.isEmpty()&&!password.isEmpty()&&password.length()>=6){
            error_email.set(null);
            error_password.set(null);
            return true;
        }else {

            if (email.isEmpty()){
                error_email.set(context.getString(R.string.field_req));

            }else {
                error_email.set(null);

            }

            if (password.isEmpty()){
                error_password.set(context.getString(R.string.field_req));
            }else if (password.length()<6){
                error_password.set(context.getString(R.string.password_short));
            }else {
                error_password.set(null);
            }
            return false;
        }
    }
    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);

    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }
}
