package com.qizhu.rili.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.qizhu.rili.R;
import com.qizhu.rili.listener.LoginSuccessListener;
import com.qizhu.rili.listener.OnAuthCallbackListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.AuthUtils;
import com.qizhu.rili.utils.SSOSinaUtils;
import com.qizhu.rili.utils.SSOTencentUtils;
import com.qizhu.rili.utils.UIUtils;

/**
 * Created by lindow on 11/10/15.
 * 登陆的选择activity
 */
public class LoginChooserActivity extends ChooserActivity {
    private static LoginSuccessListener mLoginSuccessListener;          //登陆成功回调

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_lay);
        initView();
    }

    private void initView() {
        findViewById(R.id.close).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        //微信授权
        findViewById(R.id.auth_weixin).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AuthUtils.doAuth(LoginChooserActivity.this, AuthUtils.MM_AUTH_CODE, new OnAuthCallbackListener() {
                    @Override
                    public void onAuthSuccess() {
                        UIUtils.toastMsgByStringResource(R.string.auth_success);
                        //登录成功之后记得调用init接口更新用户配置
                        setInitFlag(true);
                        finish();
                        if (mLoginSuccessListener != null) {
                            mLoginSuccessListener.success();
                            mLoginSuccessListener = null;
                        }
                    }

                    @Override
                    public void onAuthFail(String msg) {
                        if (TextUtils.isEmpty(msg)) {
                            UIUtils.toastMsgByStringResource(R.string.auth_failed);
                        } else {
                            UIUtils.toastMsg(msg);
                        }
                    }

                    @Override
                    public void onCancel() {
                        UIUtils.toastMsgByStringResource(R.string.auth_cancel);
                    }
                });
            }
        });

        //qq授权
        findViewById(R.id.auth_qq).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AuthUtils.doAuth(LoginChooserActivity.this, AuthUtils.QQ_AUTH_CODE, new OnAuthCallbackListener() {
                    @Override
                    public void onAuthSuccess() {
                        UIUtils.toastMsgByStringResource(R.string.auth_success);
                        //登录成功之后记得调用init接口更新用户配置
                        setInitFlag(true);
                        finish();
                        if (mLoginSuccessListener != null) {
                            mLoginSuccessListener.success();
                            mLoginSuccessListener = null;
                        }
                    }

                    @Override
                    public void onAuthFail(String msg) {
                        if (TextUtils.isEmpty(msg)) {
                            UIUtils.toastMsgByStringResource(R.string.auth_failed);
                        } else {
                            UIUtils.toastMsg(msg);
                        }
                    }

                    @Override
                    public void onCancel() {
                        UIUtils.toastMsgByStringResource(R.string.auth_cancel);
                    }
                });
            }
        });

        //微博授权
        findViewById(R.id.auth_weibo).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AuthUtils.doAuth(LoginChooserActivity.this, AuthUtils.SINA_AUTH_CODE, new OnAuthCallbackListener() {
                    @Override
                    public void onAuthSuccess() {
                        UIUtils.toastMsgByStringResource(R.string.auth_success);
                        //登录成功之后记得调用init接口更新用户配置
                        setInitFlag(true);
                        finish();
                        if (mLoginSuccessListener != null) {
                            mLoginSuccessListener.success();
                            mLoginSuccessListener = null;
                        }
                    }

                    @Override
                    public void onAuthFail(String msg) {
                        if (TextUtils.isEmpty(msg)) {
                            UIUtils.toastMsgByStringResource(R.string.auth_failed);
                        } else {
                            UIUtils.toastMsg(msg);
                        }
                    }

                    @Override
                    public void onCancel() {
                        UIUtils.toastMsgByStringResource(R.string.auth_cancel);
                    }
                });
            }
        });
    }

    @Override
    public boolean onClickBackBtnEvent() {
        //直接返回false，因为mainActivity未启动会造成重复的登陆界面
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, R.anim.push_bottom_out);
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     *
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        SSOSinaUtils.onActivityResult(requestCode, resultCode, data);
        // qq 授权回调
        SSOTencentUtils.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 跳转至登录页面
     */
    public static void goToPage(Context context) {
        Intent intent = new Intent(context, LoginChooserActivity.class);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.push_bottom_in, android.R.anim.fade_out);
        }
    }

    /**
     * 跳转至登录页面
     */
    public static void goToPage(Context context, LoginSuccessListener loginSuccessListener) {
        Intent intent = new Intent(context, LoginChooserActivity.class);
        mLoginSuccessListener = loginSuccessListener;
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.push_bottom_in, android.R.anim.fade_out);
        }
    }
}
