package com.qizhu.rili.controller;

import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by lindow on 10/10/16.
 * json解析器,默认解析成json对象
 * 开源库https://github.com/brokge/Retrofit2.0-JSONCoverter
 */
public class KDSPJsonConverterFactory extends Converter.Factory {

    public static KDSPJsonConverterFactory create() {
        return new KDSPJsonConverterFactory();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new KDSPJsonResponseBodyConverter<JSONObject>();
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new KDSPJsonRequestBodyConverter<JSONObject>();
    }

}
