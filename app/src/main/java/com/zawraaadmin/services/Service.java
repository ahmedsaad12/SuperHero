package com.zawraaadmin.services;


import com.zawraaadmin.models.LogoutModel;
import com.zawraaadmin.models.NotificationDataModel;
import com.zawraaadmin.models.UserModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Service {


    @FormUrlEncoded
    @POST("api/admin-login")
    Call<UserModel> login(@Field("email") String email,
                          @Field("password") String password


    );

    @GET("api/admin-notifications")
    Call<NotificationDataModel> getNotification(@Header("Authorization") String Authorization,
                                                @Query("user_id") int user_id,
                                                @Query("pagination") String pagination,
                                                @Query("page") int page


    );


    @FormUrlEncoded
    @POST("api/admin-delete-notification")
    Call<ResponseBody> delteNotification(
            @Header("Authorization") String token,
            @Field("id") int notification_id);

    @FormUrlEncoded
    @POST("api/admin-update-firebase")
    Call<LogoutModel> updateFirebaseToken(@Header("Authorization") String token,
                                          @Field("user_id") int user_id,
                                          @Field("phone_token") String phone_token,
                                          @Field("software_type") String software_type

    );

    @FormUrlEncoded
    @POST("api/admin-logout")
    Call<LogoutModel> logout(@Header("Authorization") String token,
                             @Field("user_id") int user_id,
                             @Field("phone_token") String phone_token,
                             @Field("software_type") String software_type
    );

}
