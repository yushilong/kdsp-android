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
    private static final int MSG_DISMISS_PROGRESS = 1;      //??????????????????
    private static final int MSG_LOAD_URL         = 2;              //??????url
    private static final int MSG_SHARE_URL        = 3;             //??????url
    private static final int MSG_WEBVIEW_SCROLL   = 4;        //????????????url
    private static final int MSG_JS_CALLBACK      = 5;           //JS??????
    private static final int MSG_UPDATE_UI        = 6;             //??????UI
    private static final int MSG_DO_SHARE         = 7;              //????????????

    public  WebView     mWebview;                    // ??????webview
    private TextView    titleTxt;                  // ??????
    private ImageView   mLeftBtn;                 // ????????????
    private ImageView   mRightBtn;                // ????????????
    private ProgressBar mProgress;              // ?????????

    private Handler handler;                    //??????handler
    private String urlSource = "";              //?????????

    private String mLoadUrl;                 //???????????????url

    private boolean mNeedClearHistory = false;   //????????????????????????????????????
    private boolean mNeedClearCache   = false;     //????????????????????????????????????

    private static String  mLastUrl;         //??????????????????
    private static int     mLastScrollY;        //????????????????????????
    private static boolean mShouldScroll;   //??????????????????webview

    private String title;                   //????????????
    private String webLink     = "";            //??????????????????,?????????????????????
    private String webTitle    = "";           //??????????????????,?????????????????????
    private String webImageUrl = "";        //????????????????????????,?????????????????????
    private String shareLink   = "";          //????????????
    private String shareTitle  = "";         //????????????
    private String content     = "";            //????????????
    private String imageUrl    = "";           //????????????????????????
    private int    mShareType;                 //???????????????
    private int    mSharePlatform;             //???????????????
    private int    mStatisticType;             //?????????type
    private String mStatisticSubType;       //?????????subtype

    private boolean isReload = false;       //????????????????????????

    private static Activity mWebActivity;   //????????????

    private String urlTitle = "????????????";

    private ValueCallback<Uri>   mUploadMessage;      //???????????????????????????
    public  ValueCallback<Uri[]> mUploadMessageForAndroid5;      //???????????????????????????

    private int           mLeftStyle;                 //?????????????????????1-100???0????????????
    private int           mRightStyle;                //?????????????????????101-200???0????????????
    private String        mLeftClick;              //?????????????????????web?????????
    private String        mRightClick;             //?????????????????????web?????????
    private boolean       mLocalUploadFile;       //?????????????????????????????????????????????false??????????????????????????????updateUI????????????true
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

        init();//?????????????????????
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
                startActivity(intent);    //??????????????????FALSE
            } else {
                if (mLoadUrl.contains("toPay")) {

                    getPayData();

                    showDialogFragment(PayChooseDialogFragment.newInstance(), "??????????????????");
                }
//                loadUrl(mLoadUrl);
                YSRLNavigationByUrlUtils.navigate(mLoadUrl, YSRLWebActivity.this, false);
                goBack();
            }

            //?????????????????????5s?????????loading????????????????????????????????????????????????
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
        if (this.isFinishing()) {   //??????finished????????????Acitvity
            this.startActivity(intent);
        } else {                    //??????finish???????????????intent
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
                    //????????????????????????????????????
                    compressImage(data.getData());
                }
            } else if (requestCode == RequestCodeConfig.REQUEST_CODE_WEBVIEW_CHOOSE_LOLLIPOP) {
                if (mUploadMessageForAndroid5 != null) {
                    //????????????????????????????????????
                    compressImage(data.getData());
                }
            }
        }

        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????cancel????????????????????????????????????????????????????????????????????????????????????
        //?????????????????????????????????????????????????????????????????????ValueCallback????????????????????????????????????onReceiveValue()?????????WebView??????????????????????????????????????????????????????
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
        values = new float[3];//???????????????????????????
        gravity = new float[3];//????????????????????????????????????
        r = new float[9];//
        geomagnetic = new float[3];//?????????????????????????????????
        listener = new MySensorEventListener();
    }

    private void initHandler() {
        handler = new Handler() {
            public void handleMessage(Message msg) {//????????????Handler??????????????????????????????UI?????????
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
                            //??????url
                            String url = msg.obj != null ? msg.obj.toString() : "";
                            LogUtils.d("---->webview?????????url: " + url);
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

        //????????????
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

        //????????????
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
     * ?????????????????????????????????????????????
     */
    private void initWebNavBtn() {
        if (isReload) {
            stopLoading();
            isReload = false;
        }
    }

    /**
     * ???????????????????????????
     */
    @Override
    public void onBackPressed() {
        //???????????????mLeftStyle???1???????????????????????????????????????
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

    public void init() {//?????????
        CookieSyncManager.createInstance(YSRLWebActivity.this);
        CookieManager mCookieManager = CookieManager.getInstance();
        mCookieManager.setAcceptCookie(true);

        WebSettings mWebSettings = mWebview.getSettings();
        mWebSettings.setJavaScriptEnabled(true);      //??????JS
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);    //??????js??????????????????????????????window.open()
        mWebSettings.setDomStorageEnabled(true);          //??????DOM Storage
        mWebSettings.setUseWideViewPort(true);            //??????????????????
        mWebSettings.setLoadWithOverviewMode(true);       //??????????????????


//         mWebSettings.setAllowContentAccess(false);
//        mWebSettings.setAppCacheEnabled(true);
//        mWebSettings.setDatabaseEnabled(true);
//        mWebSettings.setSupportZoom(true);//???????????????????????????true
//        mWebSettings.setBuiltInZoomControls(true);//?????????????????????????????????false
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

        mWebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);     //?????????????????????0????????????????????????????????????????????????????????????
//        mWebview.clearCache(true);
//        mWebview.clearHistory();

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtils.d("onPageStarted load url =  " + url);
                super.onPageStarted(view, url, favicon);

            }

            //webview???????????????url???????????????????????????
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {

                LogUtils.d("shouldOverrideUrlLoading load url = " + url + ",\n" + mTempUrl);
                //????????????????????????
                if (YSRLURLUtils.isHttpHost(url)) {

//                    LogUtils.d("YSRLURLUtils.isHttpHost(urll) load url = " + url);
                    //???????????????????????????,?????????????????????????????????????????????????????????????????????
                    if (url != null && !url.equals(YSRLURLUtils.modifyQiZhuUrl(url))) {
                        LogUtils.d("-------------- load url = " + url);
                        loadUrl(url);
                    }
                } else if (url.contains("tel:")) {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                    startActivity(intent);    //??????????????????FALSE
                    return true;
                } else {
                    if (url.contains("toPay")) {
                        getPayData();
                        LogUtils.d("---> url : " + mPayData);
                        showDialogFragment(PayChooseDialogFragment.newInstance(), "??????????????????");
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
                //????????????url??????

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
                //????????????web????????????
                urlTitle = view.getTitle();
                titleTxt.setText(urlTitle);
                initWebNavBtn();
//
            }
        });

        mWebview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {//???????????????????????????
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

            // js???????????????<input type="file" name="fileField" id="fileField" />????????????
            // For Android > 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                LogUtils.d("openFileChooser 5.0 = ");
                chooseLocalFileForAndroid5(filePathCallback);
                return true;
            }

            // Android > 4.1.1 ??????????????????
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                LogUtils.d("openFileChooser 4.1.1 = ");
                chooseLocalFile(uploadMsg);
            }

            // 3.0 + ??????????????????
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                LogUtils.d("openFileChooser 3.0 = ");
                chooseLocalFile(uploadMsg);
            }

            // Android < 3.0 ??????????????????
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                LogUtils.d("openFileChooser < 3.0 = ");
                chooseLocalFile(uploadMsg);
            }
        });

        //??????webview download??????
        mWebview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                //??????????????????????????????????????????
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    /**
     * ??????????????????
     */
    private void chooseLocalFile(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, RequestCodeConfig.REQUEST_CODE_WEBVIEW_CHOOSE);
    }

    /**
     * ?????????????????????????????????android 5.0??????
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
     * ?????????????????????????????????webview
     */
    private void compressImage(final Uri uri) {
        try {

            if (uri != null) {
                //???????????????
                String path = FileUtils.getFilePath(YSRLWebActivity.this, uri);

                final String mCompressPath = ImageUtils.compressImage(path);
                File file = new File(mCompressPath);
                if (!TextUtils.isEmpty(mCompressPath)) {
                    //???????????????????????????
                    if (mLocalUploadFile) {
                        //?????????????????????????????????????????????????????????
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
                        //?????????????????????
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
                    UmengUtils.reportError(this, "webview????????????????????????");
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
     * ??????UI
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
     * ????????????
     */
    private void doShare(JSONObject jsonObject) {
        int sharePlatForm = jsonObject.optInt("sharePlatform");
        HashMap<String, String> paramsMap = new HashMap<String, String>();      //???????????????map
        paramsMap.put(ShareUtils.Share_Type, jsonObject.optInt("shareType") + "");

        paramsMap.put(ShareUtils.Share_Link, jsonObject.optString("link"));

        paramsMap.put(ShareUtils.Share_Title, jsonObject.optString("title"));

        paramsMap.put(ShareUtils.Share_Content, jsonObject.optString("content"));

        paramsMap.put(ShareUtils.Share_Image, jsonObject.optString("imageUrl"));

        int statisticsType = jsonObject.optInt("shareStatisticsType");
        String statisticSubType = jsonObject.optString("shareStatisticsSubType");
        //???????????????
        if (StringUtils.getBoolOfInt(sharePlatForm, 1)) {
            //????????????????????????
            ShareUtils.shareToWeixin(this, paramsMap, SendMessageToWX.Req.WXSceneTimeline, false, statisticsType, statisticSubType);
        } else if (StringUtils.getBoolOfInt(sharePlatForm, 2)) { //????????????
            //???????????????
            ShareUtils.shareToWeixin(this, paramsMap, SendMessageToWX.Req.WXSceneSession, false, statisticsType, statisticSubType);
        } else if (StringUtils.getBoolOfInt(sharePlatForm, 3)) {      //qq??????
            ShareUtils.shareToQZone(this, paramsMap, new ShareListener() {
                @Override
                public void shareSuccess() {

                }

                @Override
                public void shareFailed() {

                }
            }, statisticsType, statisticSubType);
        } else if (StringUtils.getBoolOfInt(sharePlatForm, 4)) {      //qq??????
            //?????????QQ
            ShareUtils.shareToQQ(this, paramsMap, new ShareListener() {
                @Override
                public void shareSuccess() {

                }

                @Override
                public void shareFailed() {

                }
            }, statisticsType, statisticSubType);
        } else if (StringUtils.getBoolOfInt(sharePlatForm, 5)) {
            //??????
            ShareUtils.shareToWeiBo(this, paramsMap, statisticsType, statisticSubType);
        }
    }

    @Override
    public void payWxSuccessd(boolean isSuccess, String errStr) {
        if (isSuccess) {
            UIUtils.toastMsg("????????????");
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
            UIUtils.toastMsg("????????????");
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
        // r???????????????
        SensorManager.getRotationMatrix(r, null, gravity, geomagnetic);
        //values???????????????
        SensorManager.getOrientation(r, values);
        //????????????
        values[0] = (float) Math.toDegrees(values[0]);


    }

    /**
     * ?????????webview
     */
    public static void goToPage(Context context, String url) {
        Intent intent = new Intent(context, YSRLWebActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_WEB_URL, url);
        context.startActivity(intent);
    }

    /**
     * ?????????webview
     *
     * @param url        webview?????????
     * @param imageUrl   ?????????????????????
     * @param shareTitle ???????????????
     * @param content    ???????????????
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
        //?????????????????????????????????????????????????????????????????????
        if (AppManager.getAppManager().backToMain()) {
            MainActivity.goToPage(YSRLWebActivity.getInstance());
        }
    }

    /**
     * ??????js???????????????title
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
     * ???????????????
     */
    private void checkShare() {
        //?????????????????????????????????
        shareTitle = TextUtils.isEmpty(shareTitle) ? webTitle : shareTitle;
        shareLink = TextUtils.isEmpty(shareLink) ? webLink : shareLink;
        imageUrl = TextUtils.isEmpty(imageUrl) ? webImageUrl : imageUrl;
    }

    /**
     * Javascript?????????
     * js??????????????????????????????????????????handler
     */
    class JIFace {
        /**
         * JS???????????????????????????
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
