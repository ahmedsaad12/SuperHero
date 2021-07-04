package com.zawraaadmin.mvp.activity_notification_mvp;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.zawraaadmin.R;
import com.zawraaadmin.models.AllUserModel;
import com.zawraaadmin.models.LogoutModel;
import com.zawraaadmin.models.NotificationDataModel;
import com.zawraaadmin.models.PharmacyDataModel;
import com.zawraaadmin.models.UserModel;
import com.zawraaadmin.preferences.Preferences;
import com.zawraaadmin.remote.Api;
import com.zawraaadmin.share.Common;
import com.zawraaadmin.tags.Tags;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityNotificationPresenter implements DatePickerDialog.OnDateSetListener{
    private ActivityNotificationView view;
    private Context context;
    private UserModel userModel;
    private Preferences preference;
    private Call<NotificationDataModel> call;
    private ProgressDialog dialog;
    private DatePickerDialog datePickerDialog;

    public ActivityNotificationPresenter(ActivityNotificationView view, Context context) {
        this.view = view;
        this.context = context;
        preference = Preferences.getInstance();
        userModel = preference.getUserData(context);
        updateTokenFireBase();
        createDateDialog();
    }

    private void createDateDialog() {
        try {
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);

            datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setOkText(context.getString(R.string.select));
            datePickerDialog.setCancelText(context.getString(R.string.cancel));
            datePickerDialog.setAccentColor(ContextCompat.getColor(context, R.color.colorPrimary));
            datePickerDialog.setOkColor(ContextCompat.getColor(context, R.color.colorPrimary));
            datePickerDialog.setCancelColor(ContextCompat.getColor(context, R.color.gray4));
            datePickerDialog.setLocale(Locale.ENGLISH);
            datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_1);

        }catch (Exception e){}

    }

    public void showDateDialog(FragmentManager fragmentManager){
        try {
            datePickerDialog.show(fragmentManager,"");
        }catch (Exception e){}
    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendar.set(Calendar.MONTH, monthOfYear);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            String date = dateFormat.format(new Date(calendar.getTimeInMillis()));
            ActivityNotificationPresenter.this.view.onDateSelected(date);
        }catch (Exception e){}

    }

    public void getNotifications(int page, String client_id, String deleget_id, String date) {
        if (userModel == null) {
            return;
        }
        if (page == 1) {
            view.showProgressBar();
        }
        if (call != null) {
            call.cancel();

        }
        Log.e("client_id", client_id+"__________");
        Log.e("deleget_id", deleget_id+"________");
        Log.e("date", date+"_________");

        call = Api.getService(Tags.base_url).
                getNotification(userModel.getData().getToken(), userModel.getData().getId(),client_id,deleget_id,date, "on", page);

        call.enqueue(new Callback<NotificationDataModel>() {
            @Override
            public void onResponse(Call<NotificationDataModel> call, Response<NotificationDataModel> response) {

                if (response.isSuccessful()) {
                    if (page == 1) {

                        view.hideProgressBar();
                    }
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
                    if (page == 1) {

                        view.hideProgressBar();
                    }
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
                .delteNotification(userModel.getData().getToken(), notification)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        view.onFinishload();
                        if (response.isSuccessful() && response.body() != null) {
                            //  Log.e("eeeeee", response.body().getUser().getName());
                            // view.onUserFound(response.body());
                            //  Log.e("eeeeee", response.body().getUser().getName());

                            view.onSuccessDelete();

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

    public void getUsers() {
        if (userModel == null) {
            return;
        }
        dialog=Common.createProgressDialog(context, context.getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .getUser(userModel.getData().getToken())
                .enqueue(new Callback<AllUserModel>() {
                    @Override
                    public void onResponse(Call<AllUserModel> call, Response<AllUserModel> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200 && response.body().getData() != null) {
                                view.onUserSuccess(response.body().getData());
                              //  search();
                            }


                        } else {
                            //  view.onProgressHide();
                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                //  view.onFailed("Server Error");

                            } else {
                                //view.onFailed(context.getString(R.string.failed));

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AllUserModel> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //        view.onFailed(context.getString(R.string.something));
                                } else {
                                    //      view.onFailed(context.getString(R.string.failed));

                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    public void search(String query) {


        Api.getService(Tags.base_url)
                .search(query)
                .enqueue(new Callback<PharmacyDataModel>() {
                    @Override
                    public void onResponse(Call<PharmacyDataModel> call, Response<PharmacyDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200 && response.body().getData() != null) {
                                view.onClientSuccess(response.body().getData());

                            }


                        } else {
                            dialog.dismiss();
                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                //view.onFailed("Server Error");

                            } else {
                                // view.onFailed(context.getString(R.string.failed));

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PharmacyDataModel> call, Throwable t) {
                        try {
                            dialog.dismiss();


                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //   view.onFailed(context.getString(R.string.something));
                                } else {
                                    // view.onFailed(context.getString(R.string.failed));

                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }


}