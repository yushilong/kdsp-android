package com.qizhu.rili.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.AppManager;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.RequestCodeConfig;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.controller.QiNiuUploadCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.listener.ShareListener;
import com.qizhu.rili.ui.dialog.PayChooseDialogFragment;
import com.qizhu.rili.utils.AliPayUtils;
import com.qizhu.rili.utils.FileUtils;
import com.qizhu.rili.utils.FrescoLoadUtil;
import com.qizhu.rili.utils.ImageUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.SPUtils;
import com.qizhu.rili.utils.ShareUtils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.utils.UmengUtils;
import com.qizhu.rili.utils.WeixinUtils;
import com.qizhu.rili.utils.YSRLNavigationByUrlUtils;
import com.qizhu.rili.utils.YSRLURLUtils;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * webview Activity
 */
public class YSRLWebActivity extends BaseActivity {
    private static final int MSG_DISMISS_PROGRESS = 1;      //链接加载完成
    private static final int MSG_LOAD_URL         = 2;              //加载url
    private static final int MSG_SHARE_URL        = 3;             //分享url
    private static final int MSG_WEBVIEW_SCROLL   = 4;        //自动滚动url
    private static final int MSG_JS_CALLBACK      = 5;           //JS回调
    private static final int MSG_UPDATE_UI        = 6;             //更新UI
    private static final int MSG_DO_SHARE         = 7;              //执行分享

    public  WebView     mWebview;                    // 当前webview
    private TextView    titleTxt;                  // 标题
    private ImageView   mLeftBtn;                 // 左边按钮
    private ImageView   mRightBtn;                // 右边按钮
    private ProgressBar mProgress;              // 进度条

    private Handler handler;                    //当前handler
    private String urlSource = "";              //网页源

    private String mLoadUrl;                 //当期加载的url

    private boolean mNeedClearHistory = false;   //判断是否需要清除历史记录
    private boolean mNeedClearCache   = false;     //判断是否需要清除历史记录

    private static String  mLastUrl;         //记录上次链接
    private static int     mLastScrollY;        //记录上次滑动距离
    private static boolean mShouldScroll;   //是否需要滑动webview

    private String title;                   //网页标题
    private String webLink     = "";            //网页分享链接,用作分享默认值
    private String webTitle    = "";           //网页分享标题,用作分享默认值
    private String webImageUrl = "";        //网页分享图片地址,用作分享默认值
    private String shareLink   = "";          //分享链接
    private String shareTitle  = "";         //分享标题
    private String content     = "";            //分享内容
    private String imageUrl    = "";           //网页分享图片地址
    private int    mShareType;                 //分享的类型
    private int    mSharePlatform;             //分享的平台
    private int    mStatisticType;             //统计的type
    private String mStatisticSubType;       //统计的subtype

    private boolean isReload = false;       //是否正在重新加载

    private static Activity mWebActivity;   //当前界面

    private String urlTitle = "口袋神婆";

    private ValueCallback<Uri>   mUploadMessage;      //上传选择的文件信息
    public  ValueCallback<Uri[]> mUploadMessageForAndroid5;      //上传选择的文件信息

    private int           mLeftStyle;                 //左边按钮形式，1-100，0为不显示
    private int           mRightStyle;                //右边按钮形式，101-200，0为不显示
    private String        mLeftClick;              //左边按钮点击的web端方法
    private String        mRightClick;             //右边按钮点击的web端方法
    private boolean       mLocalUploadFile;       //是否客户端本地上传图片，默认为false，我们自己的链接会在updateUI方法里传true
    private String        mPayData;
    private SensorManager manager;
    private Sensor        magneticSensor, accelerometerSensor;
    private float[] values, r, gravity, geomagnetic;
    private MySensorEventListener listener;
    private boolean isFirst    = true;
    private boolean isFirstWeb = false;
    private String mTempUrl;
    private Uri    mUri;

    public static Activity getInstance() {
        if (mWebActivity == null) {
            mWebActivity = new YSRLWebActivity();
        }
        return mWebActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_lay);

        initView();
        mWebActivity = this;

        init();//执行初始化函数
        initSensor();
        getIntentData(getIntent());

        mShouldScroll = false;
        initHandler();
        if (!TextUtils.isEmpty(mLoadUrl)) {

            if (YSRLURLUtils.isHttpHost(mLoadUrl)) {
                loadUrl(mLoadUrl);
            } else if (mLoadUrl.contains("tel:")) {
                Uri uri = Uri.parse(mLoadUrl);
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);    //此处不能返回FALSE
            } else {
                if (mLoadUrl.contains("toPay")) {

                    getPayData();

                    showDialogFragment(PayChooseDialogFragment.newInstance(), "选择支付方式");
                }
//                loadUrl(mLoadUrl);
                YSRLNavigationByUrlUtils.navigate(mLoadUrl, YSRLWebActivity.this, false);
                goBack();
            }

            //启动一个任务，5s后关闭loading对话框，防止对话框无法关闭的情况
            handler.sendEmptyMessageDelayed(MSG_DISMISS_PROGRESS, 5000);
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFirst = true;
//        mWebview.onResume();
//        mWebview.resumeTimers();
        magneticSensor = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(listener, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(listener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterListener(listener);
        isFirst = false;
//        mWebview.pauseTimers();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (this.isFinishing()) {   //已经finished重新启动Acitvity
            this.startActivity(intent);
        } else {                    //没有finish，重新设置intent
            setIntent(intent);
            getIntentData(intent);
            loadUrl(mLoadUrl);
        }
        LogUtils.d("---> onNewIntent load url : " + mLoadUrl);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLastScrollY = mWebview.getScrollY();
        LogUtils.d("---> webview scroll : " + mLastScrollY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (mWebview != null) {
//            mWebview.removeAllViews();
            mWebview.destroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RequestCodeConfig.REQUEST_CODE_LOGIN) {
                String json = data.getStringExtra(IntentExtraConfig.EXTRA_JSON);
                String jumpLink = "";
                try {
                    jumpLink = new JSONObject(json).optString("jumpLink");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(jumpLink)) {
                    LogUtils.d(" ----- before jumpLink: " + jumpLink);
                    if (AppContext.mUser != null) {
                        jumpLink = YSRLURLUtils.addUserIdUrl(jumpLink, AppContext.userId);
                    }
//                    jumpLink = YSRLURLUtils.modifyQiZhuUrl(jumpLink);
                    LogUtils.d(" ----- jumpLink: " + jumpLink);
                    mWebview.loadUrl(jumpLink);
                } else if (!TextUtils.isEmpty(mLastUrl)) {
                    LogUtils.d(" ----- before mLastUrl: " + mLastUrl);
                    if (AppContext.mUser != null) {
                        mLastUrl = YSRLURLUtils.addUserIdUrl(mLastUrl, AppContext.userId);
                    }
                    mLastUrl = YSRLURLUtils.modifyQiZhuUrl(mLastUrl);
                    LogUtils.d(" ----- mLastUrl: " + mLastUrl);
                    mWebview.loadUrl(mLastUrl);
                }
            } else if (requestCode == RequestCodeConfig.REQUEST_CODE_WEBVIEW_CHOOSE) {
                if (mUploadMessage != null) {
                    //对上传的图片进行压缩处理
                    compressImage(data.getData());
                }
            } else if (requestCode == RequestCodeConfig.REQUEST_CODE_WEBVIEW_CHOOSE_LOLLIPOP) {
                if (mUploadMessageForAndroid5 != null) {
                    //对上传的图片进行压缩处理
                    compressImage(data.getData());
                }
            }
        }

        //这里很重要，如果弹出对话框，用户选择一个图片或者进行拍照，但是进行到一半的时候，用户cancel了，这个时候再去点击“选择文件”按钮时，网页会失去响应。
        //原因是：点击“选择文件”按钮时，网页会缓存一个ValueCallback对象，必须触发了该对象的onReceiveValue()方法，WebView才会释放，进而才能再一次的选择文件。
        if (data == null) {
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }
            if (mUploadMessageForAndroid5 != null) {
                mUploadMessageForAndroid5.onReceiveValue(null);
                mUploadMessageForAndroid5 = null;
            }
        }
    }

    private void initView() {
        mWebview = (WebView) findViewById(R.id.news_web);
        titleTxt = (TextView) findViewById(R.id.title_txt);
        mLeftBtn = (ImageView) findViewById(R.id.go_back);
        mRightBtn = (ImageView) findViewById(R.id.share_btn);
        mProgress = (ProgressBar) findViewById(R.id.progress);
    }

    private void initSensor() {

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        values = new float[3];//用来保存最终的结果
        gravity = new float[3];//用来保存加速度传感器的值
        r = new float[9];//
        geomagnetic = new float[3];//用来保存地磁传感器的值
        listener = new MySensorEventListener();
    }

    private void initHandler() {
        handler = new Handler() {
            public void handleMessage(Message msg) {//定义一个Handler，用于处理下载线程与UI间通讯
                if (!Thread.currentThread().isInterrupted()) {
                    switch (msg.what) {
                        case MSG_DISMISS_PROGRESS:
                            removeMessages(MSG_DISMISS_PROGRESS);
                            String tempUrl = msg.obj != null ? msg.obj.toString() : "";
                            if (!TextUtils.isEmpty(tempUrl)) {
                                if (mNeedClearHistory) {
                                    mWebview.clearHistory();
                                    mNeedClearHistory = false;
                                    SPUtils.putBoolleanValue(YSRLConstants.NEED_CLEAR_WEBVIEW_HISTORY, false);
                                }
                                if (mShouldScroll) {
                                    mShouldScroll = false;
                                    sendEmptyMessageDelayed(MSG_WEBVIEW_SCROLL, 30);
                                }
                            }
                            stopLoading();
//                            getUrlSource();
                            break;
                        case MSG_LOAD_URL:
                            //加载url
                            String url = msg.obj != null ? msg.obj.toString() : "";
                            LogUtils.d("---->webview加载的url: " + url);
                            if (mLastUrl != null && mLastUrl.equals(url)) {
                                mShouldScroll = true;
                            }
                            if (mNeedClearCache) {
                                mWebview.clearCache(true);
                                mNeedClearCache = false;
                                SPUtils.putBoolleanValue(YSRLConstants.NEED_CLEAR_WEBVIEW_CACHE, false);
                            }

                            mLastUrl = url;
                            mWebview.loadUrl(url);
                            loading();
                            break;
                        case MSG_SHARE_URL:
                            webLink = mWebview.getUrl();
                            webTitle = urlTitle;
                            webImageUrl = YSRLURLUtils.getImageByUrl(urlSource);
                            break;
                        case MSG_WEBVIEW_SCROLL:
                            LogUtils.d("---> webview scroll " + mLastScrollY);
                            mWebview.scrollTo(0, mLastScrollY);
                            break;
                        case MSG_JS_CALLBACK:
                            String jsCallback = msg.obj != null ? msg.obj.toString() : "";
                            if (!TextUtils.isEmpty(jsCallback)) {
//                                MethodCompat.loadJS(mWebview, jsCallback);
                                mWebview.evaluateJavascript(jsCallback, new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String s) {

                                    }
                                });
                            }
                            break;
                        case MSG_UPDATE_UI:
                            updateUI();
                            break;
                        case MSG_DO_SHARE:
                            JSONObject jsonObject = (JSONObject) msg.obj;
                            doShare(jsonObject);
                            break;
                    }
                }
            }
        };
    }

    private void getIntentData(Intent intent) {
        mLoadUrl = intent.getStringExtra(IntentExtraConfig.EXTRA_WEB_URL);
        LogUtils.d("---> getIntentData : " + mLoadUrl);
        if (AppContext.mUser != null) {
            mLoadUrl = YSRLURLUtils.addUserIdUrl(mLoadUrl, AppContext.userId);
        }

        imageUrl = intent.getStringExtra(IntentExtraConfig.EXTRA_SHARE_IMAGE);
        title = intent.getStringExtra(IntentExtraConfig.EXTRA_SHARE_TITLE);
        content = intent.getStringExtra(IntentExtraConfig.EXTRA_SHARE_CONTENT);
//        mShareType = intent.getStringExtra(IntentExtraConfig.EXTRA_TYPE);
        mNeedClearHistory = SPUtils.getBoolleanValue(YSRLConstants.NEED_CLEAR_WEBVIEW_HISTORY, false);
        mNeedClearCache = SPUtils.getBoolleanValue(YSRLConstants.NEED_CLEAR_WEBVIEW_CACHE, false);

        if (!TextUtils.isEmpty(title)) {
            titleTxt.setText(title);
        }

        //左边按钮
        mLeftBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (TextUtils.isEmpty(mLeftClick)) {
                    goBack();
                } else {
//                    MethodCompat.loadJS(mWebview, "javascript:kdsp." + mLeftClick + "()");
                    mWebview.evaluateJavascript("javascript:kdsp." + mLeftClick + "()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {

                        }
                    });
                }
            }
        });

        //右边按钮
        mRightBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (TextUtils.isEmpty(mRightClick)) {
                    checkShare();
                    if (mShareType == 1) {
                        if (!TextUtils.isEmpty(imageUrl)) {
                            FrescoLoadUtil.getInstance().loadImageBitmap(imageUrl, new FrescoLoadUtil.FrescoBitmapCallback<Bitmap>() {
                                @Override
                                public void onSuccess(Uri uri, Bitmap result) {
                                    ShareActivity.mShareBitmap = result;
                                    ShareActivity.goToShareImage(YSRLWebActivity.this, "", "", "", "", ShareUtils.Share_Type_APP, "");
                                }

                                @Override
                                public void onFailure(Uri uri, Throwable throwable) {

                                }

                                @Override
                                public void onCancel(Uri uri) {

                                }
                            });
                        }


                    } else {

                        ShareActivity.goToShare(YSRLWebActivity.this, TextUtils.isEmpty(shareTitle) ? urlTitle : shareTitle, content, shareLink, imageUrl, ShareUtils.Share_Type_APP, mSharePlatform, mStatisticType, mStatisticSubType);
                    }

                } else {
                    mWebview.evaluateJavascript("javascript:kdsp." + mRightClick + "()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {

                        }
                    });
//                    MethodCompat.loadJS(mWebview, "javascript:kdsp." + mRightClick + "()");
                }
            }
        });
    }

    private void loading() {
        mProgress.setVisibility(View.VISIBLE);
        mProgress.setProgress(0);
    }

    private void getPayData() {
        Uri uri = Uri.parse(mLoadUrl);
        String json = uri.getQueryParameter("json");
        JSONObject data = new JSONObject();
        try {
            if (!TextUtils.isEmpty(json)) {
                data = new JSONObject(json);
            }
            mPayData = data.optString("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopLoading() {
        mProgress.setVisibility(View.GONE);
    }

    /**
     * 初始化浏览器前进后退刷新的图标
     */
    private void initWebNavBtn() {
        if (isReload) {
            stopLoading();
            isReload = false;
        }
    }

    /**
     * 返回按钮的点击事件
     */
    @Override
    public void onBackPressed() {
        //如果左侧的mLeftStyle为1，那么回退事件与左上角相同
        if (1 == mLeftStyle) {
            if (TextUtils.isEmpty(mLeftClick)) {
                goBack();
            } else {
//                MethodCompat.loadJS(mWebview, "javascript:kdsp." + mLeftClick + "()");
                mWebview.evaluateJavascript("javascript:kdsp." + mLeftClick + "()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                    }
                });
            }
        } else {
            goBack();
        }
    }

    @Override
    public boolean onClickBackBtnEvent() {
        if (mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        }

        return super.onClickBackBtnEvent();
    }

    public void init() {//初始化
        CookieSyncManager.createInstance(YSRLWebActivity.this);
        CookieManager mCookieManager = CookieManager.getInstance();
        mCookieManager.setAcceptCookie(true);

        WebSettings mWebSettings = mWebview.getSettings();
        mWebSettings.setJavaScriptEnabled(true);      //可用JS
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);    //设置js可以直接打开窗口，如window.open()
        mWebSettings.setDomStorageEnabled(true);          //支持DOM Storage
        mWebSettings.setUseWideViewPort(true);            //使用推荐窗口
        mWebSettings.setLoadWithOverviewMode(true);       //设置加载模式


//         mWebSettings.setAllowContentAccess(false);
//        mWebSettings.setAppCacheEnabled(true);
//        mWebSettings.setDatabaseEnabled(true);
//        mWebSettings.setSupportZoom(true);//是否可以缩放，默认true
//        mWebSettings.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
//        mWebSettings.setLoadWithOverviewMode(true);
//        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        mWebSettings.setAllowFileAccess(true);
//        mWebSettings.setPluginState(WebSettings.PluginState.ON);
//        mWebSettings.setAllowFileAccessFromFileURLs(true);
//        mWebSettings.setBlockNetworkImage(false);
//        mWebSettings.setBlockNetworkLoads(false);
//        mWebSettings.setAllowUniversalAccessFromFileURLs(true);
//        mWebSettings.setLoadsImagesAutomatically(true);
//        mWebSettings.setDefaultTextEncodingName("utf-8");

        if (MethodCompat.isCompatible(Build.VERSION_CODES.LOLLIPOP)) {
//            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        }

        mWebview.addJavascriptInterface(new JIFace(), "kdspNative");
//        mWebview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mWebSettings.setUserAgentString(mWebSettings.getUserAgentString() + " App/YSRL platform/Android AppVersion/" + AppContext.version);
        LogUtils.d("loadUrl userAgent is %s", mWebSettings.getUserAgentString());

        mWebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);     //滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
//        mWebview.clearCache(true);
//        mWebview.clearHistory();

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtils.d("onPageStarted load url =  " + url);
                super.onPageStarted(view, url, favicon);

            }

            //webview内部新加载url时才会处理走该方法
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {

                LogUtils.d("shouldOverrideUrlLoading load url = " + url + ",\n" + mTempUrl);
                //如果没有跳转处理
                if (YSRLURLUtils.isHttpHost(url)) {

//                    LogUtils.d("YSRLURLUtils.isHttpHost(urll) load url = " + url);
                    //替换口袋神婆的链接,先更换链接，若更换的链接与原链接不相等，则替换
                    if (url != null && !url.equals(YSRLURLUtils.modifyQiZhuUrl(url))) {
                        LogUtils.d("-------------- load url = " + url);
                        loadUrl(url);
                    }
                } else if (url.contains("tel:")) {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                    startActivity(intent);    //此处不能返回FALSE
                    return true;
                } else {
                    if (url.contains("toPay")) {
                        getPayData();
                        LogUtils.d("---> url : " + mPayData);
                        showDialogFragment(PayChooseDialogFragment.newInstance(), "选择支付方式");
                        return true;
                    }
                    mUri = Uri.parse(url);
                    String scheme = mUri.getScheme();
                    if (scheme.equals("ysrl")) {
                        YSRLNavigationByUrlUtils.navigate(url, YSRLWebActivity.this, false);
                    } else {

                        if (isFirst) {
                            if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {
                                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
                                    if (mUri.toString().startsWith("intent"))
                                        mUri = Uri.parse(mUri.toString().replace("intent", "tbopen"));
                                    LogUtils.d("startsWith(\"intent\") = " + mUri.toString());

                                } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {

                                }

                                try {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent("android.intent.action.VIEW", mUri);
                                            intent.setData(mUri);
                                            List<ResolveInfo> activities = getPackageManager().queryIntentActivities(intent,0);
                                            if(!activities.isEmpty()){
                                                startActivity(intent);
                                            }
                                        }
                                    }, 1000);

//                                       mWebview.pauseTimers();
//                                       mWebview.goBack();


                                } catch (Exception e) {

                                }

                            }
                            isFirst = false;
                            LogUtils.d("android.intent.action.VIEW load url = " + Build.VERSION.SDK_INT + url + ",\n" + mUri.toString() + ",package:" + mUri.getQueryParameter("package") + scheme);


                        }

                        return true;
                    }

                    return true;
                }
                //自行处理url加载

                return super.shouldOverrideUrlLoading(view, url);
            }


            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                return super.shouldInterceptRequest(view, request);
            }


            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, com.tencent.smtt.export.external.interfaces.SslError sslError) {
                super.onReceivedSslError(webView, sslErrorHandler, sslError);
                sslErrorHandler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, final String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                LogUtils.d("YSRL WEBACTIVITY onPageFinished load url = " + url);
                //重新设定web导航按钮
                urlTitle = view.getTitle();
                titleTxt.setText(urlTitle);
                initWebNavBtn();
//
            }
        });

        mWebview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {//载入进度改变而触发
                if (progress == 100) {
                    view.requestFocus();
                    Message msg = handler.obtainMessage(MSG_DISMISS_PROGRESS);
                    msg.obj = view.getUrl();
                    handler.sendMessage(msg);
                } else {
                    mProgress.setVisibility(View.VISIBLE);
                    mProgress.setProgress(progress);
                }
                super.onProgressChanged(view, progress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                urlTitle = title;
                titleTxt.setText(urlTitle);
                LogUtils.d("---->title:" + title);
            }

            // js上传文件的<input type="file" name="fileField" id="fileField" />事件捕获
            // For Android > 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                LogUtils.d("openFileChooser 5.0 = ");
                chooseLocalFileForAndroid5(filePathCallback);
                return true;
            }

            // Android > 4.1.1 调用这个方法
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                LogUtils.d("openFileChooser 4.1.1 = ");
                chooseLocalFile(uploadMsg);
            }

            // 3.0 + 调用这个方法
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                LogUtils.d("openFileChooser 3.0 = ");
                chooseLocalFile(uploadMsg);
            }

            // Android < 3.0 调用这个方法
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                LogUtils.d("openFileChooser < 3.0 = ");
                chooseLocalFile(uploadMsg);
            }
        });

        //开启webview download模式
        mWebview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                //调用系统默认的浏览器进行下载
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    /**
     * 选择本地文件
     */
    private void chooseLocalFile(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, RequestCodeConfig.REQUEST_CODE_WEBVIEW_CHOOSE);
    }

    /**
     * 选择多个本地文件，支持android 5.0以上
     */
    private void chooseLocalFileForAndroid5(ValueCallback<Uri[]> filePathCallback) {
        if (mUploadMessageForAndroid5 != null) {
            mUploadMessageForAndroid5.onReceiveValue(null);
            mUploadMessageForAndroid5 = null;
        }
        mUploadMessageForAndroid5 = filePathCallback;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, RequestCodeConfig.REQUEST_CODE_WEBVIEW_CHOOSE_LOLLIPOP);
    }

    public void loadUrl(String url) {
        LogUtils.d("loadUrl url is " + url);
        if (!TextUtils.isEmpty(url)) {
            Message msg = handler.obtainMessage(MSG_LOAD_URL);
            msg.obj = url;
            handler.sendMessage(msg);
        }
    }

    /**
     * 压缩图片，将输入流传值webview
     */
    private void compressImage(final Uri uri) {
        try {

            if (uri != null) {
                //图片的路径
                String path = FileUtils.getFilePath(YSRLWebActivity.this, uri);

                final String mCompressPath = ImageUtils.compressImage(path);
                File file = new File(mCompressPath);
                if (!TextUtils.isEmpty(mCompressPath)) {
                    //客户端本地上传图片
                    if (mLocalUploadFile) {
                        //客户端本地上传，所以将上传本身回调取消
                        if (mUploadMessage != null) {
                            mUploadMessage.onReceiveValue(null);
                            mUploadMessage = null;
                        }
                        if (mUploadMessageForAndroid5 != null) {
                            mUploadMessageForAndroid5.onReceiveValue(null);
                            mUploadMessageForAndroid5 = null;
                        }
                        final String key = KDSPApiController.getInstance().generateUploadKey(mCompressPath);
                        KDSPApiController.getInstance().uploadImageToQiNiu(key, file, new QiNiuUploadCallBack() {

                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("rotate", 0);
                                    jsonObject.put("key", key);
//                                    MethodCompat.loadJS(mWebview, "javascript:kdsp.uploadImage('" + jsonObject + "')");
                                    mWebview.evaluateJavascript("javascript:kdsp.uploadImage('" + jsonObject + "')", new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String s) {

                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("rotate", 0);
                                    jsonObject.put("base64", "data:image/jpeg;base64," + FileUtils.encodeBase64File(mCompressPath));
//                                    MethodCompat.loadJS(mWebview, "javascript:kdsp.uploadImage('" + jsonObject + "')");
                                    mWebview.evaluateJavascript("javascript:kdsp.uploadImage('" + jsonObject + "')", new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String s) {

                                        }
                                    });

                                } catch (Exception e) {

                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        //客户端本地上传
                        if (mUploadMessage != null) {
                            mUploadMessage.onReceiveValue(Uri.fromFile(file));
                            mUploadMessage = null;
                        }
                        if (mUploadMessageForAndroid5 != null) {
                            mUploadMessageForAndroid5.onReceiveValue(new Uri[]{Uri.fromFile(file)});
                            mUploadMessageForAndroid5 = null;
                        }
                    }
                } else {
                    UmengUtils.reportError(this, "webview上传图片获取为空");
                }
            } else {
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                    mUploadMessage = null;
                }
                if (mUploadMessageForAndroid5 != null) {
                    mUploadMessageForAndroid5.onReceiveValue(null);
                    mUploadMessageForAndroid5 = null;
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * 更新UI
     */
    private void updateUI() {
        switch (mLeftStyle) {
            case 0:
                mLeftBtn.setVisibility(View.GONE);
                break;
            case 1:
                mLeftBtn.setImageResource(R.drawable.back);
                break;
            default:
                break;
        }
        switch (mRightStyle) {
            case 0:
                mRightBtn.setVisibility(View.GONE);
                break;
            case 101:
                mRightBtn.setVisibility(View.VISIBLE);
                mRightBtn.setImageResource(R.drawable.share_purple);
                break;
            default:
                mRightBtn.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 执行分享
     */
    private void doShare(JSONObject jsonObject) {
        int sharePlatForm = jsonObject.optInt("sharePlatform");
        HashMap<String, String> paramsMap = new HashMap<String, String>();      //分享的内容map
        paramsMap.put(ShareUtils.Share_Type, jsonObject.optInt("shareType") + "");

        paramsMap.put(ShareUtils.Share_Link, jsonObject.optString("link"));

        paramsMap.put(ShareUtils.Share_Title, jsonObject.optString("title"));

        paramsMap.put(ShareUtils.Share_Content, jsonObject.optString("content"));

        paramsMap.put(ShareUtils.Share_Image, jsonObject.optString("imageUrl"));

        int statisticsType = jsonObject.optInt("shareStatisticsType");
        String statisticSubType = jsonObject.optString("shareStatisticsSubType");
        //微信朋友圈
        if (StringUtils.getBoolOfInt(sharePlatForm, 1)) {
            //分享到微信朋友圈
            ShareUtils.shareToWeixin(this, paramsMap, SendMessageToWX.Req.WXSceneTimeline, false, statisticsType, statisticSubType);
        } else if (StringUtils.getBoolOfInt(sharePlatForm, 2)) { //微信好友
            //分享到微信
            ShareUtils.shareToWeixin(this, paramsMap, SendMessageToWX.Req.WXSceneSession, false, statisticsType, statisticSubType);
        } else if (StringUtils.getBoolOfInt(sharePlatForm, 3)) {      //qq空间
            ShareUtils.shareToQZone(this, paramsMap, new ShareListener() {
                @Override
                public void shareSuccess() {

                }

                @Override
                public void shareFailed() {

                }
            }, statisticsType, statisticSubType);
        } else if (StringUtils.getBoolOfInt(sharePlatForm, 4)) {      //qq好友
            //分享到QQ
            ShareUtils.shareToQQ(this, paramsMap, new ShareListener() {
                @Override
                public void shareSuccess() {

                }

                @Override
                public void shareFailed() {

                }
            }, statisticsType, statisticSubType);
        } else if (StringUtils.getBoolOfInt(sharePlatForm, 5)) {
            //微博
            ShareUtils.shareToWeiBo(this, paramsMap, statisticsType, statisticSubType);
        }
    }

    @Override
    public void payWxSuccessd(boolean isSuccess, String errStr) {
        if (isSuccess) {
            UIUtils.toastMsg("支付成功");
//            MethodCompat.loadJS(mWebview, "javascript:kdsp.success()");
            mWebview.evaluateJavascript("javascript:kdsp.success()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {

                }
            });
        }
    }

    @Override
    public void payAliSuccessd(boolean isSuccess, String errStr) {
        if (isSuccess) {
            UIUtils.toastMsg("支付成功");
//            MethodCompat.loadJS(mWebview, "javascript:kdsp.success()");
            mWebview.evaluateJavascript("javascript:kdsp.success()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {

                }
            });
        }
    }

    @Override
    public <T> void setExtraData(T t) {
        if (YSRLConstants.WEIXIN_PAY.equals(t)) {
            showLoadingDialog();
            KDSPApiController.getInstance().wxpay(mPayData, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(final JSONObject response) {
                    YSRLWebActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoadingDialog();
                            WeixinUtils.getInstance().startPayByMM(YSRLWebActivity.this, response);
                        }
                    });
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    dismissLoadingDialog();
                    showFailureMessage(error);
                }
            });
        } else if (YSRLConstants.ALIPAY.equals(t)) {
            showLoadingDialog();
            KDSPApiController.getInstance().alipay(mPayData, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(final JSONObject response) {
                    YSRLWebActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoadingDialog();
                            AliPayUtils.getInstance().startPay(YSRLWebActivity.this, response);
                        }
                    });
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    dismissLoadingDialog();
                    showFailureMessage(error);
                }
            });
        }
    }

    private class MySensorEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                geomagnetic = event.values;
            }
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                gravity = event.values;
                getValue();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    public void getValue() {
        // r从这里返回
        SensorManager.getRotationMatrix(r, null, gravity, geomagnetic);
        //values从这里返回
        SensorManager.getOrientation(r, values);
        //提取数据
        values[0] = (float) Math.toDegrees(values[0]);


    }

    /**
     * 跳转到webview
     */
    public static void goToPage(Context context, String url) {
        Intent intent = new Intent(context, YSRLWebActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_WEB_URL, url);
        context.startActivity(intent);
    }

    /**
     * 跳转到webview
     *
     * @param url        webview的链接
     * @param imageUrl   分享的图片地址
     * @param shareTitle 分享的标题
     * @param content    分享的内容
     */
    public static void goToPage(Context context, String url, String imageUrl, String shareTitle, String content) {
        Intent intent = new Intent(context, YSRLWebActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_WEB_URL, url);
        intent.putExtra(IntentExtraConfig.EXTRA_SHARE_IMAGE, imageUrl);
        intent.putExtra(IntentExtraConfig.EXTRA_SHARE_TITLE, shareTitle);
        intent.putExtra(IntentExtraConfig.EXTRA_SHARE_CONTENT, content);
        context.startActivity(intent);
    }

    public static void closeWebView() {
        YSRLWebActivity.getInstance().finish();
        //如果从通知进入，主页面不在堆栈，则回退到主页面
        if (AppManager.getAppManager().backToMain()) {
            MainActivity.goToPage(YSRLWebActivity.getInstance());
        }
    }

    /**
     * 调用js得到网页的title
     */
    private void getUrlSource() {
        String ht = "javascript:window.kdspNative.print(document.documentElement.outerHTML);";
//        MethodCompat.loadJS(mWebview, ht);
        mWebview.evaluateJavascript(ht, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {

            }
        });
    }


    /**
     * 检测分享值
     */
    private void checkShare() {
        //先用默认值，确保不为空
        shareTitle = TextUtils.isEmpty(shareTitle) ? webTitle : shareTitle;
        shareLink = TextUtils.isEmpty(shareLink) ? webLink : shareLink;
        imageUrl = TextUtils.isEmpty(imageUrl) ? webImageUrl : imageUrl;
    }

    /**
     * Javascript接口类
     * js方法全部执行在子线程，因此用handler
     */
    class JIFace {
        /**
         * JS回调打印页面源文件
         */
        @JavascriptInterface
        public void print(String data) {
            urlSource = data;
            handler.sendEmptyMessage(MSG_SHARE_URL);
        }
        @JavascriptInterface
        public float getOrientation(){
            return  values[0];
        }

        @JavascriptInterface
        public void invoke(String data) {
            LogUtils.d("---> invoke data = " + data);
            try {
                JSONObject jsonObject = new JSONObject(data);
                String method = jsonObject.optString("method");
                if (!TextUtils.isEmpty(method)) {
                    if ("doShare".equals(method)) {
                        Message msg = handler.obtainMessage(MSG_DO_SHARE);
                        msg.obj = jsonObject;
                        handler.sendMessage(msg);
                    } else if ("doMultiShare".equals(method)) {
                        shareTitle = jsonObject.optString("title");
                        content = jsonObject.optString("content");
                        shareLink = jsonObject.optString("link");
                        imageUrl = jsonObject.optString("imageUrl");
                        mShareType = jsonObject.optInt("shareType");
                        mSharePlatform = jsonObject.optInt("sharePlatform");
                        mStatisticType = jsonObject.optInt("shareStatisticsType");
                        mStatisticSubType = jsonObject.optString("shareStatisticsSubType");
                        checkShare();
                        ShareActivity.goToShare(YSRLWebActivity.this, TextUtils.isEmpty(shareTitle) ? urlTitle : shareTitle, content, shareLink, imageUrl, mShareType, mSharePlatform, mStatisticType, mStatisticSubType);
                        LogUtils.d("------" + mShareType);
                    } else if ("closeWebView".equals(method)) {
                        closeWebView();
                    } else if ("updateUI".equals(method)) {
                        mLeftStyle = jsonObject.optInt("leftButtonStyle");
                        mRightStyle = jsonObject.optInt("rightButtonStyle");
                        mLeftClick = jsonObject.optString("leftButtonClick");
                        mRightClick = jsonObject.optString("rightButtonClick");
                        mLocalUploadFile = jsonObject.optBoolean("localUploadFile");
                        handler.sendEmptyMessage(MSG_UPDATE_UI);
                    } else if ("setShare".equals(method)) {
                        shareTitle = jsonObject.optString("title");
                        content = jsonObject.optString("content");
                        shareLink = jsonObject.optString("link");
                        imageUrl = jsonObject.optString("imageUrl");
                        mShareType = jsonObject.optInt("shareType");
                        mSharePlatform = jsonObject.optInt("sharePlatform");
                        mStatisticType = jsonObject.optInt("shareStatisticsType");
                        mStatisticSubType = jsonObject.optString("shareStatisticsSubType");
                    }else if("getOrientation".equals(method)){

                    }
                }

                String callback = jsonObject.optString("callback");
                if (!TextUtils.isEmpty(callback)) {
                    Message msg = handler.obtainMessage(MSG_JS_CALLBACK);
                    msg.obj = "javascript:kdsp." + callback + "('" + jsonObject + "')";
                    handler.sendMessage(msg);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
