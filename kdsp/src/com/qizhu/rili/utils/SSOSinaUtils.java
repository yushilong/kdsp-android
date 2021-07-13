package com.qizhu.rili.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.ui.activity.BaseActivity;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

/**
 * Created by lindow on 15/9/1.
 * 微博工具类
 */
public class SSOSinaUtils {
    private static final String APP_KEY = "2248777232";
    private static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    // 应用申请的高级权限
    private static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";

    private static final String KEY_UID = "uid";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_EXPIRES_IN = "expires_in";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    private static AuthInfo mAuthInfo;
    //注意：SsoHandler 仅当 SDK 支持 SSO 时有效
    private static SsoHandler mSsoHandler;
    // 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
    private static Oauth2AccessToken mAccessToken;
    private IWeiboShareAPI mWeiboShareAPI;

    private static SSOSinaUtils mInstance = new SSOSinaUtils();

    private SSOSinaUtils() {
    }

    public static SSOSinaUtils getInstance() {
        if (mInstance == null) {
            mInstance = new SSOSinaUtils();
        }
        return mInstance;
    }

    /**
     * 将应用的appId注册到微博
     */
    public void regToSina(AppContext ctx) {
        // 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(ctx, APP_KEY);
        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        mWeiboShareAPI.registerApp();
        initAuthInfo(ctx);
    }

    public static void doSSO(final Activity activity, final SSOSinaListener listener) {
        //初始化
        initAuthInfo(activity);
        mSsoHandler = new SsoHandler(activity, mAuthInfo);
        mSsoHandler.authorize(new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle values) {
                // 从 Bundle 中解析 Token
                mAccessToken = Oauth2AccessToken.parseAccessToken(values);
                //从这里获取用户输入的 电话号码信息
//                String phoneNum = mAccessToken.getPhoneNum();
                if (mAccessToken.isSessionValid()) {
                    // 保存 Token 到 SharedPreferences
                    writeAccessToken(mAccessToken);
                    listener.onSSOSuccess(mAccessToken.getToken(), mAccessToken.getUid());
                } else {
                    // 以下几种情况，您会收到 Code：
                    // 1. 当您未在平台上注册的应用程序的包名与签名时；
                    // 2. 当您注册的应用程序包名与签名不正确时；
                    // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                    String code = values.getString("code");
                    String message = "授权失败";
                    if (!TextUtils.isEmpty(code)) {
                        message = message + "\nObtained the code: " + code;
                    }
                    LogUtils.d(message);
                    listener.onSSOFail(message);
                }
            }

            @Override
            public void onCancel() {
                listener.onCancel();
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                listener.onSSOFail(e.getMessage());
            }
        });
    }

    /**
     * sso的回调监听
     */
    public interface SSOSinaListener {
        void onSSOSuccess(String token, String uid);

        void onSSOFail(String message);

        void onCancel();
    }

    /**
     * 初始化
     */
    private static void initAuthInfo(Context context) {
        if (mAuthInfo == null) {
            mAuthInfo = new AuthInfo(context, APP_KEY, REDIRECT_URL, SCOPE);
        }
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 保存 Token 对象到 SharedPreferences。
     *
     * @param token Token 对象
     */
    public static void writeAccessToken(Oauth2AccessToken token) {
        if (null == token) {
            return;
        }

        SPUtils.putStringValue(KEY_UID, token.getUid());
        SPUtils.putStringValue(KEY_ACCESS_TOKEN, token.getToken());
        SPUtils.putStringValue(KEY_REFRESH_TOKEN, token.getRefreshToken());
        SPUtils.putLongValue(KEY_EXPIRES_IN, token.getExpiresTime());
    }

    /**
     * 从 SharedPreferences 读取 Token 信息。
     *
     * @return 返回 Token 对象
     */
    public static Oauth2AccessToken getAccessToken() {
        Oauth2AccessToken token = new Oauth2AccessToken();
        token.setUid(SPUtils.getStringValue(KEY_UID));
        token.setToken(SPUtils.getStringValue(KEY_ACCESS_TOKEN));
        token.setRefreshToken(SPUtils.getStringValue(KEY_REFRESH_TOKEN));
        token.setExpiresTime(SPUtils.getLongValue(KEY_EXPIRES_IN));

        return token;
    }

    public void handleWeiboResponse(Intent intent, IWeiboHandler.Response response) {
        mWeiboShareAPI.handleWeiboResponse(intent, response);
    }

    public void sendMessage(final BaseActivity activity, final String title, final String content, final String imageUrl, final String shareUrl) {
        AppContext.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //由于新浪微博SDK做的烂，vivo渠道老是crash，因此catch下
                try {
                    sendMultiMessage(activity, title, content, imageUrl, shareUrl, false, false, false);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     * 如果只分享单文本，那么把文本传入content
     *
     * @param title    分享的文本标题
     * @param content  分享的文本内容
     * @param imageUrl 分享的图片地址
     * @param shareUrl 分享的链接
     * @param hasMusic 分享的内容是否有音乐
     * @param hasVideo 分享的内容是否有视频
     * @param hasVoice 分享的内容是否有声音
     */
    public void sendMultiMessage(final BaseActivity activity, String title, String content, String imageUrl, String shareUrl, boolean hasMusic, boolean hasVideo, boolean hasVoice) {
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

        if (!TextUtils.isEmpty(content)) {
            weiboMessage.textObject = getTextObj(content);
        }

        if (!TextUtils.isEmpty(imageUrl)) {
            weiboMessage.imageObject = getImageObj(activity, imageUrl);
        }

        // 2. 初始化从第三方到微博的消息请求
        final SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        Oauth2AccessToken accessToken = getAccessToken();
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        if (TextUtils.isEmpty(token)) {
            doSSO(activity, new SSOSinaListener() {
                @Override
                public void onSSOSuccess(String token, String uid) {
                    Oauth2AccessToken oauth2AccessToken = getAccessToken();
                    String mToken = "";
                    if (oauth2AccessToken != null) {
                        mToken = oauth2AccessToken.getToken();
                    }

                    if (!TextUtils.isEmpty(mToken)) {
                        mWeiboShareAPI.sendRequest(activity, request, mAuthInfo, mToken, new WeiboAuthListener() {
                            @Override
                            public void onComplete(Bundle bundle) {
                                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                                writeAccessToken(newToken);
                            }

                            @Override
                            public void onCancel() {
                            }

                            @Override
                            public void onWeiboException(WeiboException arg0) {
                            }
                        });
                    }
                }

                @Override
                public void onSSOFail(String message) {

                }

                @Override
                public void onCancel() {

                }
            });
        } else {
            mWeiboShareAPI.sendRequest(activity, request, mAuthInfo, token, new WeiboAuthListener() {
                @Override
                public void onComplete(Bundle bundle) {
                    Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                    writeAccessToken(newToken);
                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onWeiboException(WeiboException arg0) {
                }
            });
        }
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj(String content) {
        TextObject textObject = new TextObject();
        textObject.text = content;
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj(BaseActivity activity, String imageUrl) {
        ImageObject imageObject = new ImageObject();
        //设置缩略图,缩略图为ThumbImage不是ImageObject。注意：最终压缩过的缩略图大小不得超过 32kb。
        String picUrl = UIUtils.getPicUrl(imageUrl, 400);
        //得到原图
        Bitmap bitmap = ImageUtils.getBitmapFromUrl(picUrl, 0, 0);
        Bitmap thumbBmp = ImageUtils.getBitmapFromUrl(picUrl);
        if (thumbBmp == null) {
            thumbBmp = ImageUtils.getResourceBitMap(activity, R.drawable.icon_rect);
        }
        imageObject.setImageObject(bitmap);
        imageObject.setThumbImage(thumbBmp);
        return imageObject;
    }
}
