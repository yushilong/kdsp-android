package com.qizhu.rili.controller;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by lindow on 10/10/16.
 * 响应body
 */
public class KDSPJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    KDSPJsonResponseBodyConverter() {

    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(value.string());
            return (T) jsonObj;
        } catch (Throwable e) {
            return null;
        }
    }
}
