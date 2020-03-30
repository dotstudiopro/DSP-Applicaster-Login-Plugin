package com.dotstudioz.services;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddHeaderInterceptor implements Interceptor {

    String accessToken = "";
    String clientToken = "";

    public AddHeaderInterceptor(String xAccessToken, String xClientToken) {
        this.accessToken = xAccessToken;
        if(xClientToken != null)
            this.clientToken = xClientToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request().newBuilder();
        /*builder.addHeader(Constants.KEY_ACCESS_TOKEN,Constants.VALUE_ACCESS_TOKEN);
        builder.addHeader(Constants.KEY_CLIENT_TOKEN,Constants.VALUE_CLIENT_TOKEN);*/
        builder.addHeader(Constants.KEY_ACCESS_TOKEN,accessToken);
        //builder.addHeader("Content-Type","application/json");
        if(clientToken != null)
            builder.addHeader(Constants.KEY_CLIENT_TOKEN,clientToken);
        return chain.proceed(builder.build());
    }
}