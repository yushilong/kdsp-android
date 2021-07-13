package com.qizhu.rili.utils;

import android.text.TextUtils;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnAuthCallbackListener;
import com.qizhu.rili.ui.activity.BaseActivity;

import org.json.JSONObject;

/**
 * 授权工具类
 * 调用授权工具类的时候，注意微博和qq的授权需要重写OnActivityResult来接收回调
 */
public class AuthUtils {
    //第三方登陆的配置
    public static final int SINA_AUTH_CODE = 1;         //新浪
    public static final int QQ_AUTH_CODE = 2;           //qq
    public static final int MM_AUTH_CODE = 3;           //微信

    /**
     * 授权
     *
     * @param baseActivity         请求授权的activity
     * @param platform             请求授权的平台
     * @param authCallbackListener 授权回调监听(直接由当前页面发起的授权需要添加此参数)
     */
    public static void doAuth(BaseActivity baseActivity, int platform, OnAuthCallbackListener authCallbackListener) {
        switch (platform) {
            case AuthUtils.SINA_AUTH_CODE:
                doSinaSSO(baseActivity, platform, authCallbackListener);
                break;
            case AuthUtils.MM_AUTH_CODE:
                doMMSSO(baseActivity, platform, authCallbackListener);
                break;
            case AuthUtils.QQ_AUTH_CODE:
                doTencentSSO(baseActivity, platform, authCallbackListener);
                break;
            default:
                //跳转至应用内webView进行授权处理
//                doWebViewAuth();
                break;
        }
    }

    private static void doSinaSSO(final BaseActivity baseActivity, final int platform, final OnAuthCallbackListener authCallbackListener) {
        SSOSinaUtils.doSSO(baseActivity, new SSOSinaUtils.SSOSinaListener() {
            @Override
            public void onSSOSuccess(String token, String uid) {
                //调用服务器接口
                initByAccessToken(baseActivity, platform, authCallbackListener, token, "", uid, "");
            }

            @Override
            public void onSSOFail(String message) {
                authCallbackListener.onAuthFail(message);
            }

            @Override
            public void onCancel() {
                authCallbackListener.onCancel();
            }
        });
    }

    /**
     * 执行qq SSO
     */
    private static void doTencentSSO(final BaseActivity baseActivity, final int platform, final OnAuthCallbackListener authCallbackListener) {
        SSOTencentUtils.doSSO(baseActivity, new SSOTencentUtils.SSOTencentListener() {
            @Override
            public void onSSOSuccess(String token, String expiresIn, String uid) {
                //调用服务器接口
                initByAccessToken(baseActivity, platform, authCallbackListener, token, expiresIn, uid, "");
            }

            @Override
            public void onSSOFail(String msg) {
                authCallbackListener.onAuthFail(msg);
            }

            @Override
            public void onCancel() {
                authCallbackListener.onCancel();
            }
        });
    }

    /**
     * 执行微信 SSO
     */
    private static void doMMSSO(final BaseActivity baseActivity, final int platform, final OnAuthCallbackListener authCallbackListener) {
        WeixinUtils.getInstance().doSSO(new WeixinUtils.SSOMMListener() {
            @Override
            public void onSSOSuccess(String code) {
                //调用服务器接口
                initByAccessToken(baseActivity, platform, authCallbackListener, "", "", "", code);
            }

            @Override
            public void onSSOFail(String msg) {
                authCallbackListener.onAuthFail(msg);
            }

            @Override
            public void onCancel() {
                authCallbackListener.onCancel();
            }
        });
    }

    /**
     * SSO后，调用服务器接口
     */
    private static void initByAccessToken(final BaseActivity baseActivity, int platform, final OnAuthCallbackListener authCallbackListener, String token, String expiresIn, final String uid, String code) {
        LogUtils.d("initByAccessToken: token = " + token + ", expiresIn = " + expiresIn + ", uid = " + uid + ", code = " + code);
        //显示进度条
        baseActivity.showLoadingDialog();
        switch (platform) {
            case AuthUtils.SINA_AUTH_CODE:
                KDSPApiController.getInstance().webLogin(token, uid, new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(JSONObject response) {
                        AuthSuccess(response, authCallbackListener, baseActivity);
                    }

                    @Override
                    public void handleAPIFailureMessage(Throwable error, String reqCode) {
                        baseActivity.dismissLoadingDialog();
                        authCallbackListener.onAuthFail(error.getMessage());
                    }
                });
                break;
            case AuthUtils.MM_AUTH_CODE:
                KDSPApiController.getInstance().wxLogin(code, new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(JSONObject response) {
                        AuthSuccess(response, authCallbackListener, baseActivity);
                    }

                    @Override
                    public void handleAPIFailureMessage(Throwable error, String reqCode) {
                        baseActivity.dismissLoadingDialog();
                        authCallbackListener.onAuthFail(error.getMessage());
                    }
                });
                break;
            case AuthUtils.QQ_AUTH_CODE:
                KDSPApiController.getInstance().qqLogin(token, uid, new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(JSONObject response) {
                        AuthSuccess(response, authCallbackListener, baseActivity);
                    }

                    @Override
                    public void handleAPIFailureMessage(Throwable error, String reqCode) {
                        baseActivity.dismissLoadingDialog();
                        authCallbackListener.onAuthFail(error.getMessage());
                    }
                });
                break;
            default:
                //跳转至应用内webView进行授权处理
//                doWebViewAuth();
                break;
        }
    }

    private static void AuthSuccess(JSONObject response, OnAuthCallbackListener authCallbackListener, final BaseActivity baseActivity) {
        if (response != null) {
            User user = User.parseObjectFromJSON(response.optJSONObject("user"));
            String birthTime = response.optJSONObject("user").optString("birthTime");
            //如果授权并未返回生日，那么让用户输入生日
            if (TextUtils.isEmpty(birthTime) || "null".equals(birthTime)) {
                SPUtils.putBoolSync(YSRLConstants.HAS_ENTER_INFO, false);
            } else {
                SPUtils.putBoolSync(YSRLConstants.HAS_ENTER_INFO, true);
            }
            AppContext.doAfterLogin(user);
            authCallbackListener.onAuthSuccess();
        } else {
            authCallbackListener.onAuthFail("");
        }

        baseActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                baseActivity.dismissLoadingDialog();
            }
        });
    }
}
