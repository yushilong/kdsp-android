package com.qizhu.rili.controller;

import com.qizhu.rili.utils.LogUtils;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lindow on 19/10/2016.
 * 七牛上传的回调
 */
public abstract class QiNiuUploadCallBack implements Callback<JSONObject> {

    @Override
    public void onResponse(Call<JSONObject> call, final Response<JSONObject> response) {
        //七牛上传成功则直接回调，此时为同步回调
        JSONObject json = response.body();
        //json获取为body，基本不可能为空
        if (json != null) {
            handleAPISuccessMessage(json);
        } else {
            handleAPIFailureMessage(new Exception("上传图片到七牛出错~"), "上传图片到七牛出错~");
        }
    }

    @Override
    public void onFailure(Call<JSONObject> call, Throwable t) {
        LogUtils.d("http request throwable => " + t);
        handleAPIFailureMessage(new Exception("你的网络不太给力哦~"), "你的网络不太给力哦~");
    }

    /**
     * 处理成功返回的消息,注意此函数为异步执行，若需要更新UI，则需要post到主线程
     * 由于大数据处理可能需要延迟，所以post操作由子类自己实现
     */
    abstract public void handleAPISuccessMessage(org.json.JSONObject response);

    /**
     * 延时或者服务器返回失败的结果
     *
     * @param error   其中message为错误的信息
     * @param reqCode result的错误码
     */
    abstract public void handleAPIFailureMessage(Throwable error, String reqCode);
}
