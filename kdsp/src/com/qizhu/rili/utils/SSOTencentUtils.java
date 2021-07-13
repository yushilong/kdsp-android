package com.qizhu.rili.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.qizhu.rili.ui.activity.BaseActivity;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * qq工具类
 */
public class SSOTencentUtils {
    private static final String APP_ID = "1104254810";
    private static final String SCOPE = "all";

    private static final String KEY_OPENID = "TENCENT_OPENID";
    private static final String KEY_TOKEN = "TENCENT_TOKEN";
    private static final String KEY_EXPIRES_IN = "TENCENT_EXPIRES_IN";

    private static Tencent mTencent;

    public static boolean isTencentValid() {
        return mTencent != null;
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mTencent != null) {
            Tencent.onActivityResultData(requestCode, resultCode, data, new IUiListener() {
                @Override
                public void onComplete(Object o) {

                }

                @Override
                public void onError(UiError uiError) {

                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    public static void doSSO(final Activity activity, final SSOTencentListener listener) {
        //初始化
        initTencent(activity);

        //取出token
        String openId = SPUtils.getStringValue(KEY_OPENID);
        String accessToken = SPUtils.getStringValue(KEY_TOKEN);
        String expiresIn = SPUtils.getStringValue(KEY_EXPIRES_IN);
        if (!TextUtils.isEmpty(accessToken)) {
            LogUtils.d("SSOTencentUtils config: openId = " + openId + ", accessToken = " + accessToken + ", expiresIn = " + expiresIn);
            mTencent.setOpenId(openId);
            mTencent.setAccessToken(accessToken, expiresIn);
        }
        LogUtils.d("SSOTencentUtils auth login!!!");
        IUiListener loginListener = new IUiListener() {

            @Override
            public void onComplete(Object response) {
                LogUtils.d("SSOTencentUtils onComplete: response = " + response);
                if (response != null) {
                    JSONObject jsonResponse = (JSONObject) response;
                    String openId = jsonResponse.optString(Constants.PARAM_OPEN_ID);
                    String accessToken = jsonResponse.optString(Constants.PARAM_ACCESS_TOKEN);
                    String expiresIn = jsonResponse.optString(Constants.PARAM_EXPIRES_IN);

                    LogUtils.d("SSOTencentUtils onComplete: openId = " + openId + ", accessToken = " + accessToken + ", expiresIn = " + expiresIn);

                    if (TextUtils.isEmpty(accessToken)) {
                        LogUtils.d("SSOTencent get openId null");
                        if (listener != null) {
                            listener.onSSOFail("get params null");
                        }
                    } else {
                        //保存token
                        SPUtils.putStringValue(KEY_OPENID, openId);
                        SPUtils.putStringValue(KEY_TOKEN, accessToken);
                        SPUtils.putStringValue(KEY_EXPIRES_IN, expiresIn);

                        //回调
                        if (listener != null) {
                            listener.onSSOSuccess(accessToken, expiresIn, openId);
                        }
                    }
                }
            }

            @Override
            public void onError(UiError uiError) {
                LogUtils.d("SSOTencentUtils: onError-->" + "code:" + uiError.errorCode + ", msg:"
                        + uiError.errorMessage + ", detail:" + uiError.errorDetail);
                if (listener != null) {
                    listener.onSSOFail("code:" + uiError.errorCode + ", msg:"
                            + uiError.errorMessage + ", detail:" + uiError.errorDetail);
                }
            }

            @Override
            public void onCancel() {
                LogUtils.d("SSOTencentUtils: onCancel!");
                if (listener != null) {
                    listener.onCancel();
                }
            }
        };
        mTencent.login(activity, SCOPE, loginListener);
    }

    /**
     * 初始化
     */
    private static void initTencent(Context context) {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(APP_ID, context.getApplicationContext());
        }
    }

    /**
     * sso的回调监听
     */
    public interface SSOTencentListener {
        void onSSOSuccess(String token, String expiresIn, String uid);

        void onSSOFail(String msg);

        void onCancel();
    }

    /**
     * sso的回调监听
     */
    public interface ShareTencentListener {
        void onComplete(Object response);

        void onError();

        void onCancel();
    }

    /**
     * 分享到qq空间
     */
    public static void shareToQZone(final BaseActivity baseActivity, Map<String, String> paramsMap, final ShareTencentListener listener) {
        final Tencent tencent = Tencent.createInstance(APP_ID, baseActivity);
        final Bundle params = new Bundle();
        String webUrl = ShareUtils.DEFAULT_URL;
        String iconUrl = ShareUtils.DEFAULT_IMAGE;
        if (paramsMap.containsKey(ShareUtils.Share_Link) && paramsMap.get(ShareUtils.Share_Link) != null) {
            webUrl = paramsMap.get(ShareUtils.Share_Link).length() > 4 ? paramsMap.get(ShareUtils.Share_Link) : webUrl;
        }

        //分享链接检测
//        webUrl = YSRLURLUtils.checkShareUrl(webUrl, ShareUtils.SHARE_QZONE_URL);

        String image;
        image = paramsMap.get(ShareUtils.Share_Image) != null ? paramsMap.get(ShareUtils.Share_Image) : "";
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        //标题,不超过200个字符,必填
        params.putString(QQShare.SHARE_TO_QQ_TITLE, StringUtils.cutString(TextUtils.isEmpty(paramsMap.get(ShareUtils.Share_Title)) ? "" : paramsMap.get(ShareUtils.Share_Title), 200));
        //内容,不超过600个字符,选填
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, StringUtils.cutString(TextUtils.isEmpty(paramsMap.get(ShareUtils.Share_Content)) ? "" : paramsMap.get(ShareUtils.Share_Content), 600));
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, webUrl);        //链接，必填
        ArrayList<String> imageUrls = new ArrayList<String>();
        if (!TextUtils.isEmpty(image)) {
            //传给qq需拼接图片地址
            imageUrls.add(UIUtils.getPicUrl(image, 600));
        }
        //如果已经有图片了，那么就不放入icon了，icon做为默认图片
        if (imageUrls.isEmpty()) {
            imageUrls.add(iconUrl);
        }

        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);

        //必须主线程启动分享
        LogUtils.d("SSOTencentUtils: mTencent shareToQZone -->");
        tencent.shareToQzone(baseActivity, params, new IUiListener() {
            @Override
            public void onCancel() {
                LogUtils.d("SSOTencentUtils: onCancel-->");
                listener.onCancel();
            }

            @Override
            public void onError(UiError e) {
                // TODO Auto-generated method stub
                LogUtils.d("SSOTencentUtils: onError-->" + e);
                listener.onError();
            }

            @Override
            public void onComplete(Object response) {
                // TODO Auto-generated method stub
                LogUtils.d("SSOTencentUtils: onError-->" + response);
                listener.onComplete(response);
            }

        });
    }

    /**
     * 分享到qq
     */
    public static void shareToQQ(final BaseActivity baseActivity, Map<String, String> paramsMap, final ShareTencentListener listener) {
        final Tencent tencent = Tencent.createInstance(APP_ID, baseActivity);
        LogUtils.d("SSOTencentUtils: shareToQQ -->");
        final Bundle params = new Bundle();
        String webUrl = ShareUtils.DEFAULT_URL;
        String iconUrl = ShareUtils.DEFAULT_IMAGE;
        if (paramsMap.containsKey(ShareUtils.Share_Link) && paramsMap.get(ShareUtils.Share_Link) != null) {
            webUrl = paramsMap.get(ShareUtils.Share_Link).length() > 4 ? paramsMap.get(ShareUtils.Share_Link) : webUrl;
        }
        //分享链接检测
//        webUrl = YSRLURLUtils.checkShareUrl(webUrl, ShareUtils.SHARE_QQ_URL);

        String image;
        image = paramsMap.get(ShareUtils.Share_Image) != null ? paramsMap.get(ShareUtils.Share_Image) : "";
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "口袋神婆");
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        //标题,不超过30个字符,必填
        params.putString(QQShare.SHARE_TO_QQ_TITLE, StringUtils.cutString(TextUtils.isEmpty(paramsMap.get(ShareUtils.Share_Title)) ? "神婆推荐" : paramsMap.get(ShareUtils.Share_Title), 30));
        //内容,不超过40个字符,选填
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, StringUtils.cutString(TextUtils.isEmpty(paramsMap.get(ShareUtils.Share_Content)) ? "神婆推荐" : paramsMap.get(ShareUtils.Share_Content), 40));
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, webUrl);        //链接，必填
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);     //是否在好友选择列表,会自动打开分享到qzone的弹窗
        if (!TextUtils.isEmpty(image)) {
            //传给qq需拼接图片地址
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, UIUtils.getPicUrl(image, 600));
        } else {
            //将icon至于最后，有图片的话就使用传入的图
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, iconUrl);
        }

        //必须主线程启动分享
        LogUtils.d("SSOTencentUtils: mTencent shareToQZone -->");
        tencent.shareToQQ(baseActivity, params, new IUiListener() {
            @Override
            public void onCancel() {
                LogUtils.d("SSOTencentUtils: onCancel-->");
                listener.onCancel();
            }

            @Override
            public void onError(UiError e) {
                // TODO Auto-generated method stub
                LogUtils.d("SSOTencentUtils: onError-->" + e);
                listener.onError();
            }

            @Override
            public void onComplete(Object response) {
                // TODO Auto-generated method stub
                LogUtils.d("SSOTencentUtils: onError-->" + response);
                listener.onComplete(response);
            }

        });
    }

    /**
     * 分享图片到qq
     *
     * @param imagePath 本地图片路径
     */
    public static void shareToQQImage(final BaseActivity baseActivity, String imagePath, boolean isQZone, final ShareTencentListener listener) {
        final Tencent tencent = Tencent.createInstance(APP_ID, baseActivity);
        LogUtils.d("SSOTencentUtils: shareToQQImage -->");
        final Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imagePath);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "口袋神婆");
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        if (isQZone) {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        }

        //必须主线程启动分享
        LogUtils.d("SSOTencentUtils: mTencent shareToQZone -->");
        tencent.shareToQQ(baseActivity, params, new IUiListener() {
            @Override
            public void onCancel() {
                LogUtils.d("SSOTencentUtils: onCancel-->");
                listener.onCancel();
            }

            @Override
            public void onError(UiError e) {
                // TODO Auto-generated method stub
                LogUtils.d("SSOTencentUtils: onError-->" + e);
                listener.onError();
            }

            @Override
            public void onComplete(Object response) {
                // TODO Auto-generated method stub
                LogUtils.d("SSOTencentUtils: onError-->" + response);
                listener.onComplete(response);
            }

        });
    }
}
