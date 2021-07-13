package com.qizhu.rili.controller;

import android.text.TextUtils;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lindow on 10/10/16.
 * 网络请求回调,默认都为json回调
 */
public abstract class KDSPHttpCallBack implements Callback<JSONObject> {

    @Override
    public void onResponse(Call<JSONObject> call, final Response<JSONObject> response) {
        AppContext.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                LogUtils.d("http request response => " + Thread.currentThread().getName());
                try {
                    //预检查服务器返回结果，如果result不等于1，表明操作不成功，转到handleAPIFailure操作
                    //json获取为body,若接口请求异常，是有可能得到null值的
                    JSONObject json = response.body();
                    //若json为null，此行代码会catch异常走失败流程，不影响整体流程
                    final int result = json.optInt("Result", 1);
                    if (result != 1) {
                        final String errMsg = json.optString("MessageInfo");
                        AppContext.getAppHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                handleAPIFailureMessage(new Exception(errMsg), String.valueOf(result));
                            }
                        });
                    } else {
                        //代码走到这里，json肯定不为空，因此handleAPISuccessMessage里无需判断
                        handleAPISuccessMessage(json);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.d("异步执行异常");
                    //异步执行异常的时候默认做请求失败处理
                    AppContext.getAppHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            handleAPIFailureMessage(new Exception("你的网络不太给力哦~"), "你的网络不太给力哦~");
                        }
                    });
                }
            }
        });
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
     * 延时或者服务器返回失败的结果,此函数默认全部放在主线程执行
     *
     * @param error   其中message为错误的信息
     * @param reqCode result的错误码
     */
    abstract public void handleAPIFailureMessage(Throwable error, String reqCode);

    protected void showFailureMessage(Throwable error) {
        String msg = error.getMessage();
        if (TextUtils.isEmpty(msg)) {
            UIUtils.toastMsgByStringResource(R.string.http_request_failure);
        } else {
            UIUtils.toastMsg(msg);
        }
    }
}
