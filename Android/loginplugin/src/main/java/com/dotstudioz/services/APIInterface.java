package com.dotstudioz.services;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface APIInterface {

    @GET("/subscriptions/users/active_subscriptions")
    Call<Object> activeSubscriptions();

    @GET("/subscriptions/check/{id}")
    Call<Object> checkSubscriptions(@Path("id") String id);

    @GET("/subscriptions/summary")
    Call<Object> subscriptionSummary();

//    @GET("subscriptions/users/cancel")
//    Call<Object> cancelSubscription();

    @POST("/subscriptions/users/cancel")
    Call<Object> cancelSubscription();

    @POST("/subscriptions/users/subscribe_to/{subscription_id}")
    Call<Object> changeSubscriptionPlan(@Path("subscription_id") String id, @Query("platform") String platform);

    @POST("/subscriptions/users/import/subscribe_to/{id}")
    Call<Object> createChargifyCustomerUsingSubscriptionID(@Path("id") String id);

    @POST("/subscriptions/users/create_from_nonce")
    Call<Object> createBraintreeCustomerUsingNonce(@Body JsonObject jsonObject);

    @POST("/subscriptions/firetv/create/subscribe_to/{id}")
    Call<Object> createBraintreeAndChargifyCustomer(@Path("id") String id, @Body JsonObject jsonObject);

    @GET
    Call<Object> getVMAPAdTag(@Url String url);
}
