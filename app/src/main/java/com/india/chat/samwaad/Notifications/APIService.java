package com.india.chat.samwaad.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({"Content-Type:application/json",
            "Authorization:key=AAAAKtcCBuI:APA91bEfogiNx6H4yR8rLmvRT2yX_nUoN_yqXV5k8UivAQEKxChLm6K5cZw06dnUXLoZfwYUNOo4r7N7QAd7Lv68Skyu7UHV6LiZYeEZxd55MVxncqwDJToRfOKdeNWvvkivfITyrJSZ"
            })

    @POST("fcm/send")
    Call<Response> sendNotification (@Body Sender body);
}
