package com.zawraaadmin.mvp.activity_login_presenter;


import com.zawraaadmin.models.UserModel;

public interface ActivityLoginView {
    void onSuccess(UserModel userModel);
    void onFailed(String msg);

    void onUserNoFound();
}
