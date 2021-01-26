package com.zawraaadmin.mvp.activity_notification_mvp;

import com.zawraaadmin.models.NotificationDataModel;
import com.zawraaadmin.models.NotificationModel;

import java.util.List;

public interface ActivityNotificationView {
    void onSuccess(NotificationDataModel data);
    void onFailed(String msg);
    void showProgressBar();
    void hideProgressBar();

    void onLoad();

    void onFinishload();

    void onSuccessDelete();

    void onNavigateToLoginActivity();

    void onLogoutSuccess();
}
