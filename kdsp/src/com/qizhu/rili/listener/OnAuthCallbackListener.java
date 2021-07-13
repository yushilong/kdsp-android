package com.qizhu.rili.listener;

/**
 * Created by lindow on 11/10/15.
 * 授权回调监听
 */
public interface OnAuthCallbackListener {

    void onAuthSuccess();

    void onAuthFail(String error);

    void onCancel();
}
