package com.qizhu.rili.controller;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.utils.LogUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by lindow on 10/9/16.
 * OKHttp请求的拦截器
 */
public class KDSPInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();

        // 添加新的参数
        HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()
                .newBuilder()
                .scheme(oldRequest.url().scheme())
                .host(oldRequest.url().host())
                .addQueryParameter("userId", AppContext.userId)
//                .addQueryParameter("userId", "f39cd2724e5227a0014e6102cebf09cb")
                .addQueryParameter("version", AppConfig.API_VERSION)
                .addQueryParameter("telephoneType", AppConfig.DEVICE_TYPE)
                .addQueryParameter("clientVersion", AppContext.version);

        // 新的请求
        Request newRequest = oldRequest.newBuilder()
                .method(oldRequest.method(), oldRequest.body())
                .url(authorizedUrlBuilder.build())
                .build();

        LogUtils.d("---> url " + newRequest.url().toString() + bodyToString(newRequest.body()));

        return chain.proceed(newRequest);
    }

    private static String bodyToString(RequestBody request) {
        try {
            RequestBody copy = request;
            Buffer buffer = new Buffer();
            if (copy != null && copy.contentLength() != 0) {
                copy.writeTo(buffer);
            } else {
                return "";
            }

            return "&" + buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
