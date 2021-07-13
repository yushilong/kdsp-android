package com.qizhu.rili.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.ui.activity.BaseActivity;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;


/**
 * 微信工具类
 */
public class WeixinUtils {
    //分享微信的APP_ID
    private static final String WECHAT_APP_ID = "wxca9fc66d377dab7b";
    private static final String SCOPE = "snsapi_userinfo";
    //第三方app和微信通信的openapi接口
    private IWXAPI iwxapi;
    //分享微信相关参数
    private static final int WX_MSG_SEND_START = 1;
    private static final int WX_MSG_SEND_END = 2;
    private Handler mWxHandler;
    private BaseActivity mWxActivity;   //用于分享微信时，显示进度条

    private SSOMMListener ssommListener;

    private static WeixinUtils mInstance = new WeixinUtils();

    private WeixinUtils() {
    }

    public static WeixinUtils getInstance() {
        if (mInstance == null) {
            mInstance = new WeixinUtils();
        }
        return mInstance;
    }

    /**
     * 将应用的appId注册到微信
     */
    public void regToWx(AppContext ctx) {
        iwxapi = WXAPIFactory.createWXAPI(ctx, WECHAT_APP_ID, true);
        iwxapi.registerApp(WECHAT_APP_ID);
        LogUtils.d("微信 regToWx iwxapi = " + iwxapi + ", APPID = " + WECHAT_APP_ID);
        mWxHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WX_MSG_SEND_START:
                        if (mWxActivity != null && !mWxActivity.isFinishing()) {
                            mWxActivity.showLoadingDialog();
                        }
                        break;
                    case WX_MSG_SEND_END:
                        boolean successed = (msg.obj != null && (Boolean) msg.obj);
                        if (!successed) {
                            UIUtils.toastMsgByStringResource(R.string.wechat_invaild);
                        }
                        if (mWxActivity != null && !mWxActivity.isFinishing()) {
                            mWxActivity.dismissLoadingDialog();
                        }
                        break;
                }
            }
        };
    }

    /**
     * 发送分享微信请求（网页链接）
     *
     * @param webUrl
     * @param title
     * @param desc
     * @param picAddress
     * @param scene
     * @param baseActivity
     */
    public void sendWxMiniReq(final String webUrl, final String title, final String desc, final String picAddress, final int scene, BaseActivity baseActivity, final boolean mergeTitle,final String path) {
        LogUtils.d("发送微信请求 webUrl = %1$s, title = %2$s, desc = %3$s, picAddress = %4$s, scene = %5$s", webUrl, title, desc, picAddress, scene);
        this.mWxActivity = baseActivity;
        AppContext.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mWxHandler.sendEmptyMessage(WX_MSG_SEND_START);
                boolean successed = innerSendMini(webUrl, title, desc, picAddress, scene, mergeTitle,path);
                mWxHandler.sendMessage(mWxHandler.obtainMessage(WX_MSG_SEND_END, successed));
            }
        });
    }

    /**
     * 发送分享微信请求（网页链接）
     *
     * @param webUrl
     * @param title
     * @param desc
     * @param picAddress
     * @param scene
     * @param baseActivity
     */
    public void sendWxReq(final String webUrl, final String title, final String desc, final String picAddress, final int scene, BaseActivity baseActivity, final boolean mergeTitle) {
        LogUtils.d("发送微信请求 webUrl = %1$s, title = %2$s, desc = %3$s, picAddress = %4$s, scene = %5$s", webUrl, title, desc, picAddress, scene);
        this.mWxActivity = baseActivity;
        AppContext.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mWxHandler.sendEmptyMessage(WX_MSG_SEND_START);
                boolean successed = innerSendWebLinkWx(webUrl, title, desc, picAddress, scene, mergeTitle);
                mWxHandler.sendMessage(mWxHandler.obtainMessage(WX_MSG_SEND_END, successed));
            }
        });
    }

    /**
     * v4.4 微信分享
     */
    public void sendWxUrlReq(final String webUrl, final String title, final String content, final int scene, BaseActivity baseActivity, final boolean mergeTitle) {
        this.mWxActivity = baseActivity;
        AppContext.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //不需要显示loading
                //mWxHandler.sendEmptyMessage(WX_MSG_SEND_START);
                boolean successed = innerSendWebUrl(webUrl, title, content, scene, mergeTitle);
                mWxHandler.sendMessage(mWxHandler.obtainMessage(WX_MSG_SEND_END, successed));

            }
        });
    }

    /**
     * 微信分享图片
     */
    public void sendWxBitmapReq(final String title, final String content, final int scene, BaseActivity baseActivity, final Bitmap bitmap, final boolean mergeTitle) {
        this.mWxActivity = baseActivity;
        AppContext.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                boolean successed = innerSendBitmap(bitmap, title, content, scene, mergeTitle);
                mWxHandler.sendMessage(mWxHandler.obtainMessage(WX_MSG_SEND_END, successed));
            }
        });
    }

    /**
     * 发送分享固定图片的微信请求（网页链接）
     *
     * @param webUrl
     * @param title
     * @param desc
     * @param bitmap
     * @param scene
     * @param baseActivity
     */
    public void sendWxBitmapReq(final String webUrl, final String title, final String desc, final Bitmap bitmap, final int scene, BaseActivity baseActivity, final boolean mergeTitle) {
        LogUtils.d("发送微信请求 webUrl = %1$s, title = %2$s, desc = %3$s, picAddress = %4$s, scene = %5$s", webUrl, title, desc, bitmap, scene);
        this.mWxActivity = baseActivity;
        AppContext.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
//                mWxHandler.sendEmptyMessage(WX_MSG_SEND_START);
                boolean successed = innerSendBmpWebLinkWx(webUrl, title, desc, bitmap, scene, mergeTitle);
                mWxHandler.sendMessage(mWxHandler.obtainMessage(WX_MSG_SEND_END, successed));
            }
        });
    }


    /**
     * 判断是否安装有微信，当前的微信版本是否是4.0
     */
    private boolean isValidForWx() {
        return iwxapi.isWXAppInstalled() ;
    }

    /**
     * 发送网页分享
     */
    private boolean innerSendWebUrl(String webUrl, String title, String desc, int scene, boolean mergeTitle) {
        if (!isValidForWx()) {
            return false;
        }

        LogUtils.d("微信 START iwxapi = " + iwxapi + ", sendWebLink webUrl = " + webUrl + ", title = " + title + ", desc = " + desc);
        WXWebpageObject webpage = new WXWebpageObject();

        webpage.webpageUrl = webUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        if (TextUtils.isEmpty(title)) {
            title = UIUtils.getString(R.string.app_name);
        }
        if (scene == SendMessageToWX.Req.WXSceneTimeline) {
            if (mergeTitle) {
                msg.title = desc + "[" + title + "]";
            } else {
                msg.title = title;
            }
        } else {
            msg.title = title;
        }
        //微信规定，title长度不可超过215KB，即Unicode字符的256长度，description的长度不可超过1KB
        msg.description = StringUtils.cutString(desc, 512);
        msg.title = StringUtils.cutString(msg.title, 256);

        //微信规定，图片的大小不可超过32KB
        msg.setThumbImage(ImageUtils.getResourceBitMap(this.mWxActivity, R.drawable.icon_rect));

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = scene;
        iwxapi.sendReq(req);
        LogUtils.d("微信 END iwxapi = " + iwxapi + ", sendWebLink webUrl = " + webUrl + ", title = " + title + ", desc = " + desc);
        return true;
    }

    private boolean innerSendMini(String webUrl, String title, String desc, String picAddress, int scene, boolean mergeTitle,String path) {
        if (!isValidForWx()) {
            return false;
        }

        LogUtils.d("微信 START iwxapi = " + iwxapi + ", SendMini webUrl = " + webUrl + ", title = " + title + ", desc = " + desc+",scene:"+scene);
        WXMiniProgramObject webpage = new WXMiniProgramObject();

        webpage.webpageUrl = webUrl;
        webpage.userName = "gh_ec3a481ff9c5";
        webpage.path = path;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        if (TextUtils.isEmpty(title)) {
            title = UIUtils.getString(R.string.app_name);
        }
        if (scene == SendMessageToWX.Req.WXSceneTimeline) {
            if (mergeTitle) {
                msg.title = desc + "[" + title + "]";
            } else {
                msg.title = title;
            }
        } else {
            msg.title = title;
        }
        //微信规定，title长度不可超过215KB，即Unicode字符的256长度，description的长度不可超过1KB
        msg.description = StringUtils.cutString(desc, 512);
        msg.title = StringUtils.cutString(msg.title, 256);

        //微信规定，图片的大小不可超过32KB
        String picUrl = UIUtils.getPicWebUrl(picAddress, 400);
        Bitmap thumbBmp = ImageUtils.getBitmapFromUrl(picUrl,300,300);
        if (thumbBmp == null) {
            thumbBmp = ImageUtils.getResourceBitMap(this.mWxActivity, R.drawable.icon_rect);
        }
        msg.setThumbImage(thumbBmp);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = scene;
        iwxapi.sendReq(req);
        LogUtils.d("微信 END iwxapi = " + iwxapi + ", SendMini webUrl = " + webUrl + ", title = " + title + ", desc = " + desc + ", picUrl = " + picUrl +", path =" +path);
        return true;
    }


    /**
     * 分享固定图片的网页链接到微信
     *
     * @param webUrl
     * @param title
     * @param desc
     * @param bitmap
     * @param scene
     */
    private boolean innerSendBmpWebLinkWx(String webUrl, String title, String desc, Bitmap bitmap, int scene, boolean mergeTitle) {
        if (!isValidForWx()) {
            return false;
        }

        LogUtils.d("微信 START iwxapi = " + iwxapi + ", sendWebLink webUrl = " + webUrl + ", title = " + title + ", desc = " + desc);
        WXWebpageObject webpage = new WXWebpageObject();

        webpage.webpageUrl = webUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        if (TextUtils.isEmpty(title)) {
            title = UIUtils.getString(R.string.app_name);
        }
        if (scene == SendMessageToWX.Req.WXSceneTimeline) {
            if (mergeTitle) {
                msg.title = desc + "[" + title + "]";
            } else {
                msg.title = title;
            }
        } else {
            msg.title = title;
        }
        //微信规定，title长度不可超过215KB，即Unicode字符的256长度，description的长度不可超过1KB
        msg.description = StringUtils.cutString(desc, 512);
        msg.title = StringUtils.cutString(msg.title, 256);

        //微信规定，图片的大小不可超过32KB，将传入的图片压缩到150*150，大小为22.5K
        Bitmap thumbBmp = bitmap;
        if (thumbBmp == null) {
            thumbBmp = ImageUtils.getResourceBitMap(this.mWxActivity, R.drawable.icon_rect);
        }
        msg.setThumbImage(thumbBmp);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = scene;
        iwxapi.sendReq(req);
        LogUtils.d("微信 END iwxapi = " + iwxapi + ", sendWebLink webUrl = " + webUrl + ", title = " + title + ", desc = " + desc);
        return true;
    }


    /**
     * 分享网页链接到微信
     *
     * @param webUrl
     * @param title
     * @param desc
     * @param picAddress
     * @param scene
     */
    private boolean innerSendWebLinkWx(String webUrl, String title, String desc, String picAddress, int scene, boolean mergeTitle) {
        if (!isValidForWx()) {
            return false;
        }

        LogUtils.d("微信 START iwxapi = " + iwxapi + ", sendWebLink webUrl = " + webUrl + ", title = " + title + ", desc = " + desc + ", picAddress = " + picAddress);
        WXWebpageObject webpage = new WXWebpageObject();

        webpage.webpageUrl = webUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        if (TextUtils.isEmpty(title)) {
            title = UIUtils.getString(R.string.app_name);
        }
        if (scene == SendMessageToWX.Req.WXSceneTimeline) {
            if (mergeTitle) {
                msg.title = desc + "[" + title + "]";
            } else {
                msg.title = title;
            }
        } else {
            msg.title = title;
        }
        //微信规定，title长度不可超过215KB，即Unicode字符的256长度，description的长度不可超过1KB
        msg.description = StringUtils.cutString(desc, 512);
        msg.title = StringUtils.cutString(msg.title, 256);

        //微信规定，图片的大小不可超过32KB，将传入的图片压缩到150*150，大小为22.5K
        String picUrl = UIUtils.getPicWebUrl(picAddress, 400);
        Bitmap thumbBmp = ImageUtils.getBitmapFromUrl(picUrl);
        if (thumbBmp == null) {
            thumbBmp = ImageUtils.getResourceBitMap(this.mWxActivity, R.drawable.icon_rect);
        }
        msg.setThumbImage(thumbBmp);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = scene;
        iwxapi.sendReq(req);
        LogUtils.d("微信 END iwxapi = " + iwxapi + ", sendWebLink webUrl = " + webUrl + ", title = " + title + ", desc = " + desc + ", picAddress = " + picAddress);
        return true;
    }

    /**
     * 发送图片分享
     */
    private boolean innerSendBitmap(Bitmap bitmap, String title, String desc, int scene, boolean mergeTitle) {
        if (!isValidForWx()) {
            return false;
        }

        LogUtils.d("微信 START iwxapi = " + iwxapi + ", title = " + title + ", desc = " + desc);
        WXImageObject bitmapPage = new WXImageObject(bitmap);

        WXMediaMessage msg = new WXMediaMessage(bitmapPage);
        if (TextUtils.isEmpty(title)) {
            title = UIUtils.getString(R.string.app_name);
        }
        if (scene == SendMessageToWX.Req.WXSceneTimeline) {
            if (mergeTitle) {
                msg.title = desc + "[" + title + "]";
            } else {
                msg.title = title;
            }
        } else {
            msg.title = title;
        }
        //微信规定，title长度不可超过215KB，即Unicode字符的256长度，description的长度不可超过1KB
        msg.description = StringUtils.cutString(desc, 512);
        msg.title = StringUtils.cutString(msg.title, 256);

        //微信规定，图片的大小不可超过32KB
        msg.setThumbImage(ImageUtils.zoomBitmap(bitmap, ImageUtils.THUMB_SIZE, ImageUtils.THUMB_SIZE, false));

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = scene;
        iwxapi.sendReq(req);
        return true;
    }

    /**
     * 微信支付
     *
     * @param context 上下文
     * @param data    数据
     */
    public void startPayByMM(BaseActivity context, JSONObject data) {
        if (!isValidForWx()) {
            UIUtils.toastMsg("您没有安装微信或微信版本过低~");
            return;
        }
        mWxActivity = context;
        if (data != null) {
            String appId = data.optString("appid");
            String partnerId = data.optString("partnerid");
            String prepayId = data.optString("prepayid");
            String nonceStr = data.optString("noncestr");
            String timeStamp = data.optString("timestamp");
            String packageValue = data.optString("package");
            String sign = data.optString("sign");

            PayReq req = new PayReq();
            req.appId = appId;
            req.partnerId = partnerId;
            req.prepayId = prepayId;
            req.nonceStr = nonceStr;
            req.timeStamp = timeStamp;
            req.packageValue = packageValue;
            req.sign = sign;

            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            IWXAPI api = WXAPIFactory.createWXAPI(context, appId);
            api.sendReq(req);
        }
    }

    public void doSSO(SSOMMListener ssommListener) {
        if (!isValidForWx()) {
            UIUtils.toastMsg("您没有安装微信或微信版本过低~");
            return;
        }
        this.ssommListener = ssommListener;

        SendAuth.Req req = new SendAuth.Req();
        req.scope = SCOPE;
        req.state = "wechat_qizhu";
        if (iwxapi == null) {
            regToWx(AppContext.baseContext);
            LogUtils.d("---->iwxapi" );

        }
        LogUtils.d("---->sendReq" );
        iwxapi.sendReq(req);
    }

    public void authSuccess(String code, String state) {
        if (state.equals("wechat_qizhu") && ssommListener != null) {
            ssommListener.onSSOSuccess(code);
            ssommListener = null;
        }
    }

    public void authDenied(String msg) {
        if (ssommListener != null) {
            ssommListener.onSSOFail(msg);
            ssommListener = null;
        }
    }

    /**
     * 分享微信相关
     *
     * @param type 类型
     */
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public void handleIntent(Intent intent, IWXAPIEventHandler handler) {
        if (iwxapi == null) {
            regToWx(AppContext.baseContext);
        }
        iwxapi.handleIntent(intent, handler);
    }

    public void shareWXRespSucceed() {
        if (mWxActivity != null) {
            UIUtils.toastMsgByStringResource(R.string.shared_succeed);
            mWxActivity.shareWxSucceed();
        }
    }

    public void shareWXRespFailed(String errorStr) {
        if (mWxActivity != null) {
            UIUtils.toastMsg(errorStr);
            mWxActivity.shareWxFailed();
        }
    }

    public void payWXRespSucceed(boolean isSuccess, String errStr) {
        if (mWxActivity != null) {
            mWxActivity.payWxSuccessd(isSuccess, errStr);
        }
        LogUtils.d("---->payWXRespSucceed();"+isSuccess );
    }

    /**
     * sso的回调监听
     */
    public interface SSOMMListener {
        void onSSOSuccess(String code);

        void onSSOFail(String msg);

        void onCancel();
    }
}
