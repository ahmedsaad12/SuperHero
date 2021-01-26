package com.zawraaadmin.mvp.activity_notification_mvp;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.zawraaadmin.R;
import com.zawraaadmin.models.LogoutModel;
import com.zawraaadmin.models.NotificationDataModel;
import com.zawraaadmin.models.UserModel;
import com.zawraaadmin.preferences.Preferences;
import com.zawraaadmin.remote.Api;
import com.zawraaadmin.share.Common;
import com.zawraaadmin.tags.Tags;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityNotificationPresenter {
    private ActivityNotificationView view;
    private Context context;
    private UserModel userModel;
    private Preferences preference;
    private Call<NotificationDataModel> call;

    public ActivityNotificationPresenter(ActivityNotificationView view, Context context) {
        this.view = view;
        this.context = context;
        preference = Preferences.getInstance();
        userModel = preference.getUserData(context);
        updateTokenFireBase();

    }

    public void getNotifications(int page) {
        if (userModel == null) {
            return;
        }

        view.showProgressBar();
        if (call != null) {
            call.cancel();

        }
        call = Api.getService(Tags.base_url).
                getNotification("Bearer " + userModel.getData().getToken(), userModel.getData().getId(), "on", page);

        call.enqueue(new Callback<NotificationDataModel>() {
            @Override
            public void onResponse(Call<NotificationDataModel> call, Response<NotificationDataModel> response) {

                if (response.isSuccessful()) {
                    view.hideProgressBar();
                    if (response.body() != null) {
                        view.onSuccess(response.body());

                    }

                } else {

                    try {
                        view.onFailed(context.getString(R.string.failed));
                        Log.e("error", response.code() + "_" + response.errorBody().string());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationDataModel> call, Throwable t) {
                try {
                    view.hideProgressBar();
                    if (t.getMessage() != null) {
                        Log.e("error", t.getMessage() + "__");

                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                            view.onFailed(context.getString(R.string.failed));

                        } else if (t.getMessage().toLowerCase().equals("Canceled".toLowerCase()) || t.getMessage().toLowerCase().contains("Socket closed".toLowerCase())) {

                        } else {
                            view.onFailed(context.getString(R.string.failed));

                        }
                    }


                } catch (Exception e) {
                }

            }
        });
    }

    public void deltenotification(int notification) {
        view.onLoad();
        Api.getService(Tags.base_url)
                .delteNotification("Bearer " + userModel.getData().getToken(), notification)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        view.onFinishload();
                        if (response.isSuccessful() && response.body() != null) {
                            //  Log.e("eeeeee", response.body().getUser().getName());
                            // view.onUserFound(response.body());
                            //  Log.e("eeeeee", response.body().getUser().getName());
                            String re = null;
                            String status = null;
                            try {
                                re = response.body().string();
                                JSONObject obj = new JSONObject(re);
                                status = (String) obj.get("status");
                            } catch (Exception e) {
                            }
                            Log.e("data", re);
                            if (status.equals("200")) {
                                view.onSuccessDelete();
                            } else {
                                view.onFailed(context.getResources().getString(R.string.failed));

                            }
                        } else {
                            try {
                                Log.e("mmmmmmmmmm", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            if (response.code() == 500) {
                                // view.onServer();
                            } else {
                                view.onFailed(response.message());
                                //  Toast.makeText(VerificationCodeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            view.onFinishload();
                            if (t.getMessage() != null) {
                                Log.e("msg_category_error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //   view.onnotconnect(t.getMessage().toLowerCase());
                                    //  Toast.makeText(VerificationCodeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    view.onFailed(t.getMessage());
                                    // Toast.makeText(VerificationCodeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });

    }

    private void updateTokenFireBase() {
        Log.e("1", "d");
        if (userModel != null) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (task.isSuccessful()) {
                        String token = task.getResult().getToken();
                        Log.e("token", token);
                        Api.getService(Tags.base_url)
                                .updateFirebaseToken(userModel.getData().getToken(), userModel.getData().getId(), token, "android")
                                .enqueue(new Callback<LogoutModel>() {
                                    @Override
                                    public void onResponse(Call<LogoutModel> call, Response<LogoutModel> response) {
                                        if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                                            userModel.getData().setFireBaseToken(token);
                                            preference.create_update_userdata(context, userModel);

                                        } else {
                                            try {

                                                Log.e("errorToken", response.code() + "_" + response.errorBody().string());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<LogoutModel> call, Throwable t) {
                                        try {

                                            if (t.getMessage() != null) {
                                                Log.e("errorToken2", t.getMessage());
                                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                                    Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                        } catch (Exception e) {
                                        }
                                    }
                                });
                    }
                }
            });

        }
    }

    public void logout() {
        if (userModel == null) {
            view.onNavigateToLoginActivity();
            return;
        }
        ProgressDialog dialog = Common.createProgressDialog(context, context.getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .logout(userModel.getData().getToken(), userModel.getData().getId(), userModel.getData().getFireBaseToken(), "android")
                .enqueue(new Callback<LogoutModel>() {
                    @Override
                    public void onResponse(Call<LogoutModel> call, Response<LogoutModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    preference.clear(context);
                                    view.onLogoutSuccess();
                                } else {
                                    view.onFailed(context.getString(R.string.failed));
                                }
                            }


                        } else {

                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            if (response.code() == 500) {
                                view.onFailed("Server Error");
                            } else {
                                view.onFailed(context.getString(R.string.failed));

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LogoutModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    view.onFailed(context.getString(R.string.failed));
                                } else {
                                    view.onFailed(context.getString(R.string.failed));

                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });


    }
}