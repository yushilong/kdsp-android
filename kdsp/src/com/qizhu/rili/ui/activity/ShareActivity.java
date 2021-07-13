package com.qizhu.rili.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.listener.ShareListener;
import com.qizhu.rili.utils.FileUtils;
import com.qizhu.rili.utils.OperUtils;
import com.qizhu.rili.utils.SSOSinaUtils;
import com.qizhu.rili.utils.SSOTencentUtils;
import com.qizhu.rili.utils.ShareUtils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import java.util.HashMap;

/**
 * Created by lindow on 15/7/28.
 * 分享的activity
 */
public class ShareActivity extends ChooserActivity implements IWeiboHandler.Response {
    private HashMap<String, String> paramsMap = new HashMap<String, String>();      //分享的内容map
    private int mShareType;             //分享的类型
    private int mSharePlatform;         //分享的平台
    private int mStatisticType;         //统计的type
    private String mStatisticSubType;   //统计的subtype
    private boolean mIsShareImage = false;      //是否分享图片

    private View mWeixinTimelineLay;    //微信朋友圈
    private View mWeixinLay;            //微信好友
    private View mWeixinMiniLay;            //小程序
    private View mQZoneLay;             //qq空间
    private View mQQLay;                //qq好友
    private View mWeiboLay;             //微博

    public static Bitmap mShareBitmap;              //分享的图片,分享完毕进行回收
    private String mPicPath = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.share_lay);
        initParams();
        initView();

        if (savedInstanceState != null) {
            SSOSinaUtils.getInstance().handleWeiboResponse(getIntent(), this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        SSOSinaUtils.getInstance().handleWeiboResponse(intent, this);
    }

    private void initParams() {
        Intent mIntent = getIntent();
        mShareType = mIntent.getIntExtra(ShareUtils.Share_Type, 0);
        mSharePlatform = mIntent.getIntExtra(ShareUtils.Share_Platform, 0);
        mStatisticType = mIntent.getIntExtra(ShareUtils.Share_Statistics_Type, StatisticsConstant.TYPE_SHARE);
        mStatisticSubType = mIntent.getStringExtra(ShareUtils.Share_Statistics_SubType);

        paramsMap.put(ShareUtils.Share_Type, mShareType + "");

        //是否为纯图片分享
        mIsShareImage = mIntent.getBooleanExtra(IntentExtraConfig.EXTRA_MODE, false);

        if (mIntent.hasExtra(ShareUtils.Share_Link)) {
            paramsMap.put(ShareUtils.Share_Link, mIntent.getStringExtra(ShareUtils.Share_Link));
        }

        if (mIntent.hasExtra(ShareUtils.Share_Title)) {
            paramsMap.put(ShareUtils.Share_Title, mIntent.getStringExtra(ShareUtils.Share_Title));
        }

        if (mIntent.hasExtra(ShareUtils.Share_Content)) {
            paramsMap.put(ShareUtils.Share_Content, mIntent.getStringExtra(ShareUtils.Share_Content));
        }

        if (mIntent.hasExtra(ShareUtils.Share_Image)) {
            paramsMap.put(ShareUtils.Share_Image, mIntent.getStringExtra(ShareUtils.Share_Image));
        }
        if (mIntent.hasExtra(ShareUtils.Share_Path)) {
            paramsMap.put(ShareUtils.Share_Path, mIntent.getStringExtra(ShareUtils.Share_Path));
        }
    }

    /**
     * 根据分享平台决定内容,二进制的1位决定
     * 0 全平台分享
     * 01 微信朋友圈
     * 10 微信好友
     * 100 qq空间
     * 1000 qq好友
     * 10000 微博
     */
    private void initView() {
        mWeixinTimelineLay = findViewById(R.id.weixin_timeline_lay);
        mWeixinMiniLay = findViewById(R.id.weixin_mini_lay);
        mWeixinLay = findViewById(R.id.weixin_lay);
        mQZoneLay = findViewById(R.id.qzone_lay);
        mQQLay = findViewById(R.id.qq_lay);
        mWeiboLay = findViewById(R.id.weibo_lay);

        if (mSharePlatform != 0) {
            //微信朋友圈
            if (!StringUtils.getBoolOfInt(mSharePlatform, 1)) {
                mWeixinTimelineLay.setVisibility(View.GONE);
            }
            //微信好友
            if (!StringUtils.getBoolOfInt(mSharePlatform, 2)) {
                mWeixinLay.setVisibility(View.GONE);
            }
            //qq空间
            if (!StringUtils.getBoolOfInt(mSharePlatform, 3)) {
                mQZoneLay.setVisibility(View.GONE);
            }
            //qq好友
            if (!StringUtils.getBoolOfInt(mSharePlatform, 4)) {
                mQQLay.setVisibility(View.GONE);
            }
            //微博
            if (!StringUtils.getBoolOfInt(mSharePlatform, 5)) {
                mWeiboLay.setVisibility(View.GONE);
            }

        }
        mWeixinMiniLay.setVisibility(View.VISIBLE);
        mWeixinLay.setVisibility(View.GONE);
        if(mShareType == ShareUtils.Share_Type_APP){
            mWeixinMiniLay.setVisibility(View.GONE);
            mWeixinLay.setVisibility(View.VISIBLE);
        }

        //小程序
        if(mShareType == ShareUtils.Share_Type_Goods ){
            mWeixinMiniLay.setVisibility(View.VISIBLE);
            mWeixinLay.setVisibility(View.GONE);
            mWeixinTimelineLay.setVisibility(View.GONE);
        }
    }

    /**
     * 分享操作
     */
    public void doShareClick(View view) {
        OperUtils.mShouldOper = true;
        if (view.getId() == R.id.weixinButton) {
            //分享到微信
            if (mIsShareImage) {
                ShareUtils.shareToWeixin(this, paramsMap, SendMessageToWX.Req.WXSceneSession, mShareBitmap, false, mStatisticType, mStatisticSubType);
            } else {
                ShareUtils.shareToWeixin(this, paramsMap, SendMessageToWX.Req.WXSceneSession, false, mStatisticType, mStatisticSubType);
            }
        } else if (view.getId() == R.id.timelineButton) {
            //分享到微信朋友圈
            if (mIsShareImage) {
                ShareUtils.shareToWeixin(this, paramsMap, SendMessageToWX.Req.WXSceneTimeline, mShareBitmap, false, mStatisticType, mStatisticSubType);
            } else {
                ShareUtils.shareToWeixin(this, paramsMap, SendMessageToWX.Req.WXSceneTimeline, false, mStatisticType, mStatisticSubType);
            }
        } else if (view.getId() == R.id.sinaButton) {
            //分享到新浪
            if (mIsShareImage) {
                if (TextUtils.isEmpty(mPicPath)) {
                    mPicPath = FileUtils.saveImage(mShareBitmap, true, false);
                }
                paramsMap.put(ShareUtils.Share_Image, mPicPath);
                ShareUtils.shareToWeiBoImage(this, paramsMap, mStatisticType, mStatisticSubType);
            } else {
                ShareUtils.shareToWeiBo(this, paramsMap, mStatisticType, mStatisticSubType);
            }
        } else if (view.getId() == R.id.qZoneButton) {
            //分享到QQ空间
            if (mIsShareImage) {
                if (TextUtils.isEmpty(mPicPath)) {
                    mPicPath = FileUtils.saveImage(mShareBitmap, true, false);
                }
                ShareUtils.shareToQQImage(ShareActivity.this, mPicPath, true, new ShareListener() {
                    @Override
                    public void shareSuccess() {
                        shareQQSucceed();
                    }

                    @Override
                    public void shareFailed() {
                        finish();
                    }
                }, mStatisticType, mStatisticSubType);
            } else {
                ShareUtils.shareToQZone(this, paramsMap, new ShareListener() {
                    @Override
                    public void shareSuccess() {
                        shareQQSucceed();
                    }

                    @Override
                    public void shareFailed() {
                        finish();
                    }
                }, mStatisticType, mStatisticSubType);
            }
        } else if (view.getId() == R.id.qqButton) {
            //分享到QQ
            if (mIsShareImage) {
                if (TextUtils.isEmpty(mPicPath)) {
                    mPicPath = FileUtils.saveImage(mShareBitmap, true, false);
                }
                ShareUtils.shareToQQImage(ShareActivity.this, mPicPath, false, new ShareListener() {
                    @Override
                    public void shareSuccess() {
                        shareQQSucceed();
                    }

                    @Override
                    public void shareFailed() {
                        finish();
                    }
                }, mStatisticType, mStatisticSubType);
            } else {
                ShareUtils.shareToQQ(this, paramsMap, new ShareListener() {
                    @Override
                    public void shareSuccess() {
                        shareQQSucceed();
                    }

                    @Override
                    public void shareFailed() {
                        finish();
                    }
                }, mStatisticType, mStatisticSubType);
            }
        }else if(view.getId() == R.id.weixin_mini_Button){
            ShareUtils.shareToWeixinMini(this, paramsMap, SendMessageToWX.Req.WXSceneSession, false, mStatisticType, mStatisticSubType);
        }
    }

    /**
     * 调用分享页
     */
    public static void goToShare(Context context, String title, String content, String shareLink, String image, int shareType, String mStatisticSubType) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(ShareUtils.Share_Type, shareType);
        intent.putExtra(ShareUtils.Share_Link, shareLink);
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(ShareUtils.Share_Title, title);
        }
        if (!TextUtils.isEmpty(content)) {
            intent.putExtra(ShareUtils.Share_Content, content);
        }
        if (!TextUtils.isEmpty(image)) {
            intent.putExtra(ShareUtils.Share_Image, image);
        }
        intent.putExtra(ShareUtils.Share_Statistics_Type, StatisticsConstant.TYPE_SHARE);
        intent.putExtra(ShareUtils.Share_Statistics_SubType, mStatisticSubType);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.push_bottom_in, android.R.anim.fade_out);
        }
    }

    /**
     * 调用分享页,指定分享平台
     */
    public static void goToShare(Context context, String title, String content, String shareLink, String image, int shareType, int sharePlatform, int mStatisticsType, String mStatisticSubType) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(ShareUtils.Share_Type, shareType);
        intent.putExtra(ShareUtils.Share_Platform, sharePlatform);
        intent.putExtra(ShareUtils.Share_Link, shareLink);
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(ShareUtils.Share_Title, title);
        }
        if (!TextUtils.isEmpty(content)) {
            intent.putExtra(ShareUtils.Share_Content, content);
        }
        if (!TextUtils.isEmpty(image)) {
            intent.putExtra(ShareUtils.Share_Image, image);
        }
        intent.putExtra(ShareUtils.Share_Statistics_Type, mStatisticsType);
        intent.putExtra(ShareUtils.Share_Statistics_SubType, mStatisticSubType);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.push_bottom_in, android.R.anim.fade_out);
        }
    }

    /**
     * 调用分享页,指定分享平台
     */
    public static void goToMiniShare(Context context, String title, String content, String shareLink, String image, int shareType, String mStatisticSubType,String path) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(ShareUtils.Share_Type, shareType);
        intent.putExtra(ShareUtils.Share_Link, shareLink);
        intent.putExtra(ShareUtils.Share_Path, path);
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(ShareUtils.Share_Title, title);
        }
        if (!TextUtils.isEmpty(content)) {
            intent.putExtra(ShareUtils.Share_Content, content);
        }
        if (!TextUtils.isEmpty(image)) {
            intent.putExtra(ShareUtils.Share_Image, image);
        }
        intent.putExtra(ShareUtils.Share_Statistics_Type, StatisticsConstant.TYPE_SHARE);
        intent.putExtra(ShareUtils.Share_Statistics_SubType, mStatisticSubType);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.push_bottom_in, android.R.anim.fade_out);
        }
    }

    /**
     * 分享口袋神婆给好友
     * shareType = 7
     */
    public static void goToShareApp(Context context, String title, String content, String shareLink, int mStatisticsType, String mStatisticSubType) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(ShareUtils.Share_Type, ShareUtils.Share_Type_APP);
        intent.putExtra(ShareUtils.Share_Link, shareLink);
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(ShareUtils.Share_Title, title);
        }
        if (!TextUtils.isEmpty(content)) {
            intent.putExtra(ShareUtils.Share_Content, content);
        }
        intent.putExtra(ShareUtils.Share_Statistics_Type, mStatisticsType);
        intent.putExtra(ShareUtils.Share_Statistics_SubType, mStatisticSubType);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.push_bottom_in, android.R.anim.fade_out);
        }
    }

    /**
     * 分享口袋神婆给好友
     * shareType = 8
     */
    public static void goToShareDaily(Context context, String title, String content, String shareLink, String dailyId, boolean isCollect) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(ShareUtils.Share_Type, ShareUtils.Share_Type_DAILY);
        intent.putExtra(ShareUtils.Share_Link, shareLink);
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(ShareUtils.Share_Title, title);
        }
        if (!TextUtils.isEmpty(content)) {
            intent.putExtra(ShareUtils.Share_Content, content);
        }
        intent.putExtra(IntentExtraConfig.EXTRA_ID, dailyId);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, isCollect);
        intent.putExtra(ShareUtils.Share_Statistics_Type, StatisticsConstant.TYPE_DAILY_SHARE);
        intent.putExtra(ShareUtils.Share_Statistics_SubType, dailyId);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.push_bottom_in, android.R.anim.fade_out);
        }
    }

    /**
     * 调用分享页分享图片
     */
    public static void goToShareImage(Context context, String title, String content, String shareLink, String image, int shareType, String mStatisticSubType) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(ShareUtils.Share_Type, shareType);
        intent.putExtra(ShareUtils.Share_Link, shareLink);
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(ShareUtils.Share_Title, title);
        }
        if (!TextUtils.isEmpty(content)) {
            intent.putExtra(ShareUtils.Share_Content, content);
        }
        if (!TextUtils.isEmpty(image)) {
            intent.putExtra(ShareUtils.Share_Image, image);
        }
        intent.putExtra(ShareUtils.Share_Statistics_Type, StatisticsConstant.TYPE_SHARE);
        intent.putExtra(ShareUtils.Share_Statistics_SubType, mStatisticSubType);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, true);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.push_bottom_in, android.R.anim.fade_out);
        }
    }

    @Override
    public void shareWxSucceed() {
        super.shareWxSucceed();
        finish();
    }

    /**
     * 分享qq成功
     */
    private void shareQQSucceed() {
        finish();
    }

    /**
     * 微博分享回调
     * 接收微博客户端请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                UIUtils.toastMsgByStringResource(R.string.shared_succeed);
                finish();
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                UIUtils.toastMsgByStringResource(R.string.shared_cancel);
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                UIUtils.toastMsgByStringResource(R.string.shared_failed);
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (mShareBitmap != null && !mShareBitmap.isRecycled()) {
            mShareBitmap.recycle();
            mShareBitmap = null;
        }
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
}
