package com.qizhu.rili.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.BroadcastUtils;

/**
 * Created by lindow on 15/9/14.
 * 本地的web页面，加载本地web内容
 */
public class LocalWebActivity extends BaseActivity {
    private static final int MSG_DISMISS_PROGRESS = 1;      //链接加载完成

    public WebView mWebview;                    // 当前webview
    private TextView titleTxt;                  // 标题
    private ImageView mLeftBtn;                 // 左边按钮
    private ImageView mRightBtn;                // 右边按钮
    private ProgressBar mProgress;              // 进度条

    private Handler handler;                    //当前handler
    private String mLoadUrl;                    //加载的url
    private String mTitle;                      //标题
    private String mShareUrl;                   //分享的链接
    private boolean mShowMore;                  //是否显示更多
    private boolean mIsCollect;                 //是否收藏
    private String mCollectId;                  //收藏的id

    //广播接收器
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BroadcastUtils.ACTION_COLLECT_DAILY_BROADCAST.equals(action)) {
                if (mCollectId.equals(intent.getStringExtra(IntentExtraConfig.EXTRA_ID))) {
                    mIsCollect = intent.getBooleanExtra(IntentExtraConfig.EXTRA_MODE, mIsCollect);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_lay);
        initView();
        getIntentData(getIntent());

        init();     //执行初始化函数
        initHandler();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastUtils.ACTION_COLLECT_DAILY_BROADCAST);
        BroadcastUtils.getInstance().registerReceiver(mReceiver, intentFilter);
    }

    private void initView() {
        mWebview = (WebView) findViewById(R.id.news_web);
        titleTxt = (TextView) findViewById(R.id.title_txt);
        mLeftBtn = (ImageView) findViewById(R.id.go_back);
        mRightBtn = (ImageView) findViewById(R.id.share_btn);
        mProgress = (ProgressBar) findViewById(R.id.progress);
    }

    private void init() {
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
//                urlTitle = title;
//                titleTxt.setText(urlTitle);
            }
        });
    }

    private void initHandler() {
        handler = new Handler() {
            public void handleMessage(Message msg) {//定义一个Handler，用于处理下载线程与UI间通讯
                if (!Thread.currentThread().isInterrupted()) {
                    switch (msg.what) {
                        case MSG_DISMISS_PROGRESS:
                            removeMessages(MSG_DISMISS_PROGRESS);
                            stopLoading();
                            break;
                    }
                }
            }
        };
    }

    private void getIntentData(Intent intent) {
        mLoadUrl = intent.getStringExtra(IntentExtraConfig.EXTRA_WEB_URL);
        mTitle = intent.getStringExtra(IntentExtraConfig.EXTRA_PAGE_TITLE);
        mShareUrl = intent.getStringExtra(IntentExtraConfig.EXTRA_SHARE_URL);
        mShowMore = intent.getBooleanExtra(IntentExtraConfig.EXTRA_MODE, false);
        mIsCollect = intent.getBooleanExtra(IntentExtraConfig.EXTRA_IS_MINE, false);
        mCollectId = intent.getStringExtra(IntentExtraConfig.EXTRA_ID);

        if (mShowMore) {
            mRightBtn.setVisibility(View.VISIBLE);
            mRightBtn.setImageResource(R.drawable.more_white);
        }

        mLeftBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                goBack();
            }
        });
        //分享
        mRightBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                ShareActivity.goToShareDaily(LocalWebActivity.this, mTitle, "", mShareUrl, mCollectId, mIsCollect);
            }
        });
        mWebview.loadDataWithBaseURL("about:blank", mLoadUrl, "text/html", "UTF-8", null);
        titleTxt.setText(mTitle);
    }

    private void loading() {
        mProgress.setVisibility(View.VISIBLE);
        mProgress.setProgress(0);
    }

    private void stopLoading() {
        mProgress.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        BroadcastUtils.getInstance().unregisterReceiver(mReceiver);
    }

    /**
     * 跳转到webview
     */
    public static void goToPage(Context context, String title, String url) {
        Intent intent = new Intent(context, LocalWebActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_PAGE_TITLE, title);
        intent.putExtra(IntentExtraConfig.EXTRA_WEB_URL, url);
        context.startActivity(intent);
    }

    /**
     * 跳转到webview
     */
    public static void goToPage(Context context, String title, String url, String shareUrl, boolean showMore, boolean isCollect, String dailyId) {
        Intent intent = new Intent(context, LocalWebActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_PAGE_TITLE, title);
        intent.putExtra(IntentExtraConfig.EXTRA_WEB_URL, url);
        intent.putExtra(IntentExtraConfig.EXTRA_SHARE_URL, shareUrl);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, showMore);
        intent.putExtra(IntentExtraConfig.EXTRA_IS_MINE, isCollect);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, dailyId);
        context.startActivity(intent);
    }
}
