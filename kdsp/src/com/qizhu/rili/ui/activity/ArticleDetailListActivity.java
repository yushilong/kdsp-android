package com.qizhu.rili.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.CommentAdapter;
import com.qizhu.rili.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.qizhu.rili.bean.Article;
import com.qizhu.rili.bean.Comment;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.data.ArticleCommentDataAccessor;
import com.qizhu.rili.listener.DataEmptyListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.service.PlayerService;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.JSONUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.utils.YSRLNavigationByUrlUtils;
import com.qizhu.rili.utils.YSRLURLUtils;
import com.qizhu.rili.widget.FitWidthImageView;
import com.qizhu.rili.widget.KDSPRecyclerView;
import com.qizhu.rili.widget.ListViewHead;

import org.json.JSONObject;

/**
 * Created by zhouyue on 08/28/2017.
 * 文章详情页
 */

public class ArticleDetailListActivity extends BaseListActivity {
    private static final int    SEND_SUCCESS         = 1;
    private static final int    SEND_FAILED          = 2;
    private static final int    MSG_DISMISS_PROGRESS = 3;     //链接加载完成
    private static final int    MSG_LOAD_URL         = 4;     //加载url
    private final        int    MUSICDURATION        = 5;     //获取歌曲播放时间标志
    private final        int    UPDATE               = 6;     //更新进度条标志
    private final static int    mMaxCount            = 500;   //文字最大数
    private final static String mURL                 = "http://h5.ishenpo.com/app/share/detail?articleId="; //分享url
    private String  mArticleId;                        //文章id
    public  WebView mWebview;                          //当前webview
    private View mView;

    private TextView          commentCountTv;          //评论数
    private TextView          mStartTimeTv;
    private TextView          mEndTimeTv;
    private FitWidthImageView mArticleImage;
    private TextView          mSeeOtherTv;              //阅读数
    private TextView          mSeeOtherVoiceTv;         //查看其它语音
    private TextView          mContentTv;               //语音内容
    private TextView          mTitleTv;                 //语音标题
    private TextView          mTitle;                   //语音标题
    private ImageView         mCollectImage;            //收藏
    private ImageView         mShareImage;              //收藏
    private ImageView         mVoiceImge;               //语音播放
    private SeekBar           mSeekBar;

    private LinearLayout commentLlayout;                //评论发送布局
    private LinearLayout commentBottomLlayout;          //评论收藏分享布局
    private LinearLayout mVoiceLlayout;                 //语音布局
    private EditText     contentEt;                     //文本编辑
    private int isCollect = 0;
    private boolean                   isCommentLayShow;
    private int                       mReadCount;        //评论数
    private String                    shareTitle;        //分享标题
    private String                    shareContent;      //分享内容
    private String                    picUrl;            //分享图片地址
    private String                    mFileUrl;          //语音地址
    private String                    mUrlTitle;          //语音地址
    private int                       articleType;       //1文章 2语音
    private LinearLayout.LayoutParams layoutParams;
    private int                       lastVisibleItemPosition;
    private PlayerService             playService;
    private boolean isPlay = false;
    private Article mArticle;
    private Intent  intent;

    private ArticleCommentDataAccessor mDataAccessor;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BroadcastUtils.ACTION_VOICE_TIME.equals(action)) {

                int time = intent.getIntExtra(IntentExtraConfig.EXTRA_ID, 0);
                mHandler.sendEmptyMessage(MUSICDURATION);
                mEndTimeTv.setText(DateUtils.second2String(time));

            } else if (BroadcastUtils.ACTION_VOICE_POSITION.equals(action)) {
                isPlay = true;
                mVoiceImge.setImageResource(R.drawable.voice_pause);
                mHandler.sendEmptyMessage(MUSICDURATION);
                mHandler.sendEmptyMessage(UPDATE);
            } else if (BroadcastUtils.ACTION_VOICE_STOP.equals(action)) {
                isPlay = false;
                mVoiceImge.setImageResource(R.drawable.voice_start);
            }
        }
    };


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissLoadingDialog();
            switch (msg.what) {
                case SEND_SUCCESS:
                    Comment newComment = new Comment();
                    if (msg.obj != null) {
                        JSONObject jsonObject = JSONUtils.parseJSONObject(msg.obj.toString());
                        newComment = Comment.parseObjectFromJSON(jsonObject.optJSONObject("comment"));
                    }
                    mDataAccessor.mData.add(0, newComment);
                    commentLlayout.setVisibility(View.GONE);
                    refreshCommentLayoutUi();
                    isCommentLayShow = false;
                    commentBottomLlayout.setVisibility(View.VISIBLE);
                    commentCountTv.setText(getString(R.string.comment) + ++mReadCount);
                    contentEt.setText("");
                    mMaskView.setVisibility(View.GONE);
                    refreshListView();
                    mKDSPRecyclerView.scrollToPosition(1);
                    break;
                case SEND_FAILED:
                    UIUtils.toastMsg("发送失败，请重试或者检查您的网络！");
                    break;

                case MSG_DISMISS_PROGRESS:
                    removeMessages(MSG_DISMISS_PROGRESS);
//                    stopLoading();
                    break;
                case MSG_LOAD_URL:
                    //加载url
                    String url = msg.obj != null ? msg.obj.toString() : "";
                    LogUtils.d("---->webview加载的url: " + url);
                    mWebview.loadUrl(url);
                    break;

                case MUSICDURATION:
                    mSeekBar.setMax(playService.getDuration());
                    LogUtils.d("----MUSICDURATION" + playService.getDuration());

                    break;
                case UPDATE:
                    LogUtils.d("----UPDATE" + playService.getMediaPlayer().getCurrentPosition());

                    int position = playService.getMediaPlayer().getCurrentPosition();
                    try {

                        mSeekBar.setProgress(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mHandler.sendEmptyMessageDelayed(UPDATE, 500);


                    break;

            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getIntentExtra();
        registerBroadcast();
        mHideRecyclerView = true;
        super.onCreate(savedInstanceState);
        init();
    }


    private void getIntentExtra() {
        mArticleId = getIntent().getStringExtra(IntentExtraConfig.EXTRA_ID);
        shareTitle = getIntent().getStringExtra(IntentExtraConfig.EXTRA_SHARE_TITLE);
        shareContent = getIntent().getStringExtra(IntentExtraConfig.EXTRA_SHARE_CONTENT);
        picUrl = getIntent().getStringExtra(IntentExtraConfig.EXTRA_SHARE_IMAGE);
        mFileUrl = getIntent().getStringExtra(IntentExtraConfig.EXTRA_PARCEL);
        mReadCount = getIntent().getIntExtra(IntentExtraConfig.EXTRA_MODE, 0);
        isCollect = getIntent().getIntExtra(IntentExtraConfig.EXTRA_GROUP_ID, 0);
        articleType = getIntent().getIntExtra(IntentExtraConfig.EXTRA_TYPE, 0);
    }

    private void registerBroadcast() {
        if (articleType == 2) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BroadcastUtils.ACTION_VOICE_TIME);
            filter.addAction(BroadcastUtils.ACTION_VOICE_POSITION);
            filter.addAction(BroadcastUtils.ACTION_VOICE_STOP);
            BroadcastUtils.getInstance().registerReceiver(mReceiver, filter);
        }
    }

    private void init() {
        if (articleType == 1) {
            initWebView();     //执行初始化函数
            loadUrl(mURL + mArticleId);
        } else if (articleType == 2) {
            mSeekBar.setPadding(0, 0, 0, 0);
            intent = new Intent(ArticleDetailListActivity.this, PlayerService.class);
            intent.putExtra("url", mFileUrl);
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
            startService(intent);
            getVoiceData();
            mHideRecyclerView = false;
        }

        if (isCollect == 0) {
            mCollectImage.setImageResource(R.drawable.collect_selected);
        } else if (isCollect == 1) {
            mCollectImage.setImageResource(R.drawable.collect_unselected);
        }
        mKDSPRecyclerView.requestFocus();
        mKDSPRecyclerView.setFocusableInTouchMode(false);
    }


    private void refreshCommentLayoutUi() {
        if (mDataAccessor.mData.isEmpty()) {
            layoutParams.setMargins(DisplayUtils.dip2px(26), DisplayUtils.dip2px(30), DisplayUtils.dip2px(28), DisplayUtils.dip2px(60));
        } else {
            layoutParams.setMargins(DisplayUtils.dip2px(26), DisplayUtils.dip2px(30), DisplayUtils.dip2px(28), DisplayUtils.dip2px(10));
        }
        commentCountTv.setLayoutParams(layoutParams);
    }

    public void loadUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            Message msg = mHandler.obtainMessage(MSG_LOAD_URL);
            msg.obj = url;
            mHandler.sendMessage(msg);
        }
    }


//    @Override
//    protected void addScrollEnterView() {
//        View view = mInflater.inflate(R.layout.head_comment, null);
//        if (view != null) {
//            view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        }
//        mWebview = (WebView) view.findViewById(R.id.news_web);
//        commentCountTv = (TextView) view.findViewById(R.id.comment_count_tv);
//        mSeeOtherTv = (TextView) view.findViewById(R.id.see_other_tv);
//        mSeeOtherTv.setVisibility(View.GONE);
//        commentCountTv.setVisibility(View.GONE);
//
//        mSeeOtherTv.setOnClickListener(new OnSingleClickListener() {
//            @Override
//            public void onSingleClick(View v) {
//                CollectListActivity.goToPage(ArticleDetailListActivity.this, 0);
//            }
//        });
//        mScrollEnterView.addView(view);
//    }

    @Override
    protected void addHeadView(KDSPRecyclerView refreshableView, View view) {

        view = new ListViewHead(this, R.layout.head_comment_voice);
        mStartTimeTv = (TextView) view.findViewById(R.id.start_time_tv);
        mEndTimeTv = (TextView) view.findViewById(R.id.end_time_tv);
        commentCountTv = (TextView) view.findViewById(R.id.comment_count_tv);
        mSeeOtherTv = (TextView) view.findViewById(R.id.see_other_tv);
        mSeeOtherVoiceTv = (TextView) view.findViewById(R.id.see_other_voice_tv);
        mTitleTv = (TextView) view.findViewById(R.id.title_tv);
        mContentTv = (TextView) view.findViewById(R.id.content_tv);
        mVoiceImge = (ImageView) view.findViewById(R.id.voice_image);
        mArticleImage = (FitWidthImageView) view.findViewById(R.id.article_image);
        mWebview = (WebView) view.findViewById(R.id.news_web);
        mView =  view.findViewById(R.id.web_progress_lay);
        mVoiceLlayout = (LinearLayout) view.findViewById(R.id.voice_llayout);

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(DisplayUtils.dip2px(26), DisplayUtils.dip2px(30), DisplayUtils.dip2px(28), DisplayUtils.dip2px(20));
        commentCountTv.setLayoutParams(layoutParams);

        mSeekBar = (SeekBar) view.findViewById(R.id.seek);


        mSeeOtherTv.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                CollectListActivity.goToPage(ArticleDetailListActivity.this, 1);
            }
        });

        mSeeOtherVoiceTv.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                CollectListActivity.goToPage(ArticleDetailListActivity.this, 2);
            }
        });


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int mprogress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mStartTimeTv.setText(DateUtils.second2String(progress));
                mprogress = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playService.play(mprogress);


                LogUtils.d("---->progress:" + mprogress);
            }
        });

        mVoiceImge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPlay) {
                    playService.pause();
                    mVoiceImge.setImageResource(R.drawable.voice_start);
                    isPlay = false;
                } else {
                    playService.start();
                    mVoiceImge.setImageResource(R.drawable.voice_pause);

                    isPlay = true;
                }
                mHandler.sendEmptyMessage(MUSICDURATION);
                mHandler.sendEmptyMessage(UPDATE);
            }
        });

        if (articleType == 1) {
            mWebview.setVisibility(View.VISIBLE);
            mVoiceLlayout.setVisibility(View.GONE);
            mSeeOtherTv.setVisibility(View.VISIBLE);
            mSeeOtherVoiceTv.setVisibility(View.GONE);
        } else if (articleType == 2) {
            mWebview.setVisibility(View.GONE);
            mVoiceLlayout.setVisibility(View.VISIBLE);
            mSeeOtherTv.setVisibility(View.GONE);
            mSeeOtherVoiceTv.setVisibility(View.VISIBLE);
        }

        super.addHeadView(refreshableView, view);
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.d("---ServiceConnection");
            PlayerService.PlayBinder playBinder = (PlayerService.PlayBinder) service;
            playService = playBinder.getPlayService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playService = null;
        }
    };

    @Override
    protected boolean canPullDownRefresh() {
        return false;
    }


    @Override
    protected void initTransparentTitleView(RelativeLayout titleView) {

        View view = mInflater.inflate(R.layout.title_transparent, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mResources.getDimension(R.dimen.header_height));
        if (view != null) {
            mTitle = (TextView) view.findViewById(R.id.title_txt);
            view.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    goBack();
                }
            });
            mCollectImage = (ImageView) view.findViewById(R.id.collect_image);
            mShareImage = (ImageView) view.findViewById(R.id.share_image);
            if (articleType == 1) {
                mTitle.setText(R.string.god_daily);
            } else if (articleType == 2) {
                mTitle.setText(R.string.god_voice);
            }

            titleView.addView(view, params);
        }

    }

    @Override
    protected void onAppBarOffsetChanged(int verticalOffset) {
        super.onAppBarOffsetChanged(verticalOffset);
        LogUtils.d("---->appBarLayout:" + verticalOffset);
    }

    @Override
    protected void initBottomView(RelativeLayout bottomView) {
        View view = mInflater.inflate(R.layout.item_article_comment_bottom, null);
        commentBottomLlayout = (LinearLayout) view.findViewById(R.id.comment_bottom_llayout);
        commentLlayout = (LinearLayout) view.findViewById(R.id.comment_llayout);
        TextView writeCommentTv = (TextView) view.findViewById(R.id.write_comment_tv);
        TextView sentTv = (TextView) view.findViewById(R.id.sent_tv);
        final TextView wordCountTv = (TextView) view.findViewById(R.id.word_count_tv);
        contentEt = (EditText) view.findViewById(R.id.content_et);

        writeCommentTv.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                commentBottomLlayout.setVisibility(View.GONE);
                commentLlayout.setVisibility(View.VISIBLE);
                isCommentLayShow = true;
                mMaskView.setVisibility(View.VISIBLE);
                mMaskView.setBackgroundColor(ContextCompat.getColor(ArticleDetailListActivity.this, R.color.black2_transparent_50));
                showSoftInput();

            }
        });

        contentEt.addTextChangedListener(new TextWatcher() {
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                selectionStart = contentEt.getSelectionStart();
                selectionEnd = contentEt.getSelectionEnd();
                if (s.length() > mMaxCount) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    contentEt.setText(s);
                    contentEt.setSelection(tempSelection);
                    UIUtils.toastMsg(getString(R.string.comment_word_count_toast));
                } else {
                    int wordCount = mMaxCount - s.length();
                    wordCountTv.setText("" + wordCount);
                }

            }
        });


        /*
         * 发消息
         */
        sentTv.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                final String content = contentEt.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    showLoadingDialog();

                    KDSPApiController.getInstance().addArticleComment(mArticleId, content, new KDSPHttpCallBack() {
                        @Override
                        public void handleAPISuccessMessage(JSONObject response) {
                            LogUtils.d("---->" + response.toString());
                            Message msg = Message.obtain();
                            msg.what = SEND_SUCCESS;
                            try {

                                msg.obj = response;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void handleAPIFailureMessage(Throwable error, String content) {
                            mHandler.sendEmptyMessage(SEND_FAILED);
                        }
                    });
                }
            }
        });


        /*
         * 收藏
         */
        mCollectImage.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showLoadingDialog();
                if (isCollect == 0) {
                    isCollect = 1;
                } else {
                    isCollect = 0;
                }
                KDSPApiController.getInstance().operArticleCollect(mArticleId, isCollect, new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(JSONObject response) {
                        dismissLoadingDialog();
                        if (isCollect == 0) {
                            UIUtils.toastMsg("收藏成功");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mCollectImage.setImageResource(R.drawable.collect_selected);

                                }
                            });

                        } else if (isCollect == 1) {
                            UIUtils.toastMsg("取消收藏");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mCollectImage.setImageResource(R.drawable.collect_unselected);
                                }
                            });

                        }
                        BroadcastUtils.sendBroadcast(BroadcastUtils.ACTION_COLLECT_REFRESH);
                    }

                    @Override
                    public void handleAPIFailureMessage(Throwable error, String content) {
                        dismissLoadingDialog();
                        showFailureMessage(error);

                    }
                });
            }
        });

        mShareImage.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String title = getString(R.string.god_daily);
                if (articleType == 1) {
                    title = getString(R.string.god_daily);
                } else if (articleType == 2) {
                    title = getString(R.string.god_voice);
                }
                ShareActivity.goToMiniShare(ArticleDetailListActivity.this, shareTitle, shareContent,
                        mURL + mArticleId, picUrl, 0, "","pages/zhi_yun/article?article_id="
                + mArticleId + "&articleType=" + articleType  +"&title=" + shareTitle);
            }
        });

        bottomView.addView(view);
    }


    /**
     * mWebview init
     */
    private void initWebView() {

        CookieSyncManager.createInstance(ArticleDetailListActivity.this);
        CookieManager mCookieManager = CookieManager.getInstance();
        mCookieManager.setAcceptCookie(true);

        WebSettings mWebSettings = mWebview.getSettings();
        mWebSettings.setJavaScriptEnabled(true);      //可用JS
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);    //设置js可以直接打开窗口，如window.open()
        mWebSettings.setDomStorageEnabled(true);          //支持DOM Storage
        mWebSettings.setUseWideViewPort(true);            //使用推荐窗口
        mWebSettings.setLoadWithOverviewMode(true);       //设置加载模式
        mWebSettings.setAllowContentAccess(true);
        if (MethodCompat.isCompatible(Build.VERSION_CODES.LOLLIPOP)) {
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        }


        mWebSettings.setUserAgentString(mWebSettings.getUserAgentString() + " App/YSRL platform/Android AppVersion/" + AppContext.version);
        LogUtils.d("loadUrl userAgent is %s", mWebSettings.getUserAgentString());

        mWebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtils.d("onPageStarted load url =  " + url);
                super.onPageStarted(view, url, favicon);
            }

            //webview内部新加载url时才会处理走该方法
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {


                //如果没有跳转处理
                if (YSRLURLUtils.isHttpHost(url)) {

                    //替换口袋神婆的链接,先更换链接，若更换的链接与原链接不相等，则替换
                    if (url != null && !url.equals(YSRLURLUtils.modifyQiZhuUrl(url))) {
                        LogUtils.d("----> loadUrl(url)1");
                        loadUrl(url);
                    }
                } else if (url.contains("tel:")) {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                    startActivity(intent);    //此处不能返回FALSE
                    return true;
                } else {
                    YSRLNavigationByUrlUtils.navigate(url, ArticleDetailListActivity.this, false);
                    return true;
                }
                //自行处理url加载
                return super.shouldOverrideUrlLoading(view, url);
            }

            //处理https的请求
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, final String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogUtils.d("YSRL WEBACTIVITY onPageFinished url = " + url);
                //重新设定web导航按钮
            }
        });


        mWebview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {//载入进度改变而触发
                if (progress == 100) {

                    mOutProgressLay.setVisibility(View.GONE);
                    LogUtils.d("---->webview100");
                    mHideRecyclerView = false;
                    commentCountTv.setVisibility(View.VISIBLE);
                    mSeeOtherTv.setVisibility(View.VISIBLE);
                    mKDSPRecyclerView.setVisibility(View.VISIBLE);
                    commentCountTv.setText(getString(R.string.comment) + mReadCount);

                    view.requestFocus();
                    Message msg = Message.obtain();
                    msg.obj = view.getUrl();
                    msg.what = MSG_DISMISS_PROGRESS;
                    mHandler.sendMessage(msg);
                } else {
                    mKDSPRecyclerView.setVisibility(View.GONE);
                    mOutProgressLay.setVisibility(View.VISIBLE);
//                    mProgress.setVisibility(View.VISIBLE);
//                    mProgress.setProgress(progress);
                }
                super.onProgressChanged(view, progress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mUrlTitle = title;
//                titleTxt.setText(urlTitle);
            }


        });

    }

    private void getVoiceData() {
        KDSPApiController.getInstance().getArticleByIdAndUserId(mArticleId, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {

                mArticle = Article.parseObjectFromJSON(response.optJSONObject("article"));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        LogUtils.d("---->getVoiceData" + mArticle.content);
                        mTitleTv.setText(mArticle.title);
                        mContentTv.setText(mArticle.content);
                        commentCountTv.setText(getString(R.string.comment) + mReadCount);
                        mArticleImage.setDefheight(AppContext.getScreenWidth(), 750, 600);
                        UIUtils.display600Image(mArticle.poster, mArticleImage, R.drawable.def_loading_img);
                        mEndTimeTv.setText(DateUtils.second2String(playService.getDuration()));

                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        needRefreshHead = false;
    }


    @Override
    protected void initAdapter() {
        if (mDataAccessor == null) {
            mDataAccessor = new ArticleCommentDataAccessor(mArticleId);
        }
        if (mAdapter == null) {
            mAdapter = new CommentAdapter(this, mDataAccessor.mData);
            mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        }
    }

    @Override
    protected void getData() {
//        showLoadingDialog();
        mProgressLay.setVisibility(View.VISIBLE);
        refreshViewByType(LAY_TYPE_LOADING);

        showLoadingDialog();
        mDataAccessor.getData(buildDefaultDataGetListener(mDataAccessor, true, new DataEmptyListener() {
            @Override
            public void onDataGet(boolean isEmpty) {
                dismissLoadingDialog();
                refreshViewByType(LAY_TYPE_NORMAL);
                refreshCommentLayoutUi();
            }
        }));

    }

    @Override
    protected void getNextData() {
        mDataAccessor.getNextData(buildDefaultDataGetListener(mDataAccessor));
    }

    @Override
    public void pullDownToRefresh() {
        mDataAccessor.getAllDataFromServer(buildDefaultDataGetListener(mDataAccessor, false, new DataEmptyListener() {
            @Override
            public void onDataGet(boolean isEmpty) {
                refreshViewByType(LAY_TYPE_NORMAL);
            }
        }));
    }


    @Override
    protected RecyclerView.OnScrollListener getScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int totalItemCount = recyclerView.getAdapter().getItemCount();
                    lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                    int lastVisibleItem = lm.findFirstVisibleItemPosition();
                    int visibleItemCount = recyclerView.getChildCount();
                    if (lastVisibleItemPosition > 0 || mDataAccessor.mData.isEmpty()) {
                        if (!isCommentLayShow) {
                            commentBottomLlayout.setVisibility(View.VISIBLE);
                        }


                    } else {
                        commentBottomLlayout.setVisibility(View.GONE);
                    }
                    //默认滚动两屏之后显示
                    setShowScrollIcon(isShowScrollIcon() && lastVisibleItemPosition > 6);
                    if (visibleItemCount + lastVisibleItemPosition >= totalItemCount - 6) {
                        LogUtils.d("滑到底部, onScroll--> firstVisibleItem = " + lastVisibleItemPosition + ":" + lastVisibleItem + ", visibleItemCount = " + visibleItemCount + ", totalItemCount = " + totalItemCount
                                + ", hasNoNextData = " + hasNoNextData);
                        if (!isRequesting) {
                            isRequesting = true;
                            if (!hasNoNextData) {
                                AppContext.getAppHandler().sendEmptyMessageDelayed(AppContext.LOAD_DATA_MESSAGE, AppConfig.DELAY_DISPLAY_LOADING_TOAST);
                                pullUpToRefresh();
                            }
                        }
                    }
                }

                    LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    View view = lm.findViewByPosition(0);
                    int top = 0;
                    if (view != null) {
                        top = view.getTop();
                        if (Math.abs(top) > DisplayUtils.dip2px(250)) {
                            mTitle.setVisibility(View.GONE);
                        } else {
                            mTitle.setVisibility(View.VISIBLE);
                        }
                    }



            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0 && mDataAccessor.mData.isEmpty()) {
                    isCommentLayShow = true;
                    commentBottomLlayout.setVisibility(View.GONE);
                }
                if (dy > 0) {
                    isCommentLayShow = false;
                }
            }
        };

    }

    @Override
    protected OnSingleClickListener initMaskOnClickListener() {
        return new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mMaskView.setVisibility(View.GONE);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        commentBottomLlayout.setVisibility(View.VISIBLE);
                    }
                }, 500);

                contentEt.setFocusableInTouchMode(true);
                contentEt.setFocusable(true);
                commentLlayout.setVisibility(View.GONE);
                mKDSPRecyclerView.scrollToPosition(lastVisibleItemPosition);
                hideSoftInput();
            }
        };
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (articleType == 2) {
            mHandler.removeMessages(UPDATE);
            BroadcastUtils.getInstance().unregisterReceiver(mReceiver);
            mSeekBar.setProgress(0);
            unbindService(conn);
            stopService(intent);
        }

        if (mWebview != null) {
//            mWebview.removeAllViews();
            mWebview.destroy();
        }
    }

    private void showSoftInput() {
        contentEt.setFocusable(true);
        contentEt.setFocusableInTouchMode(true);
        contentEt.requestFocus();
        InputMethodManager imm = (InputMethodManager) ArticleDetailListActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void hideSoftInput() {

        InputMethodManager imm = (InputMethodManager) ArticleDetailListActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(contentEt.getWindowToken(), 0);
    }

    public static void goToPage(Context context, String name, int commentCount, int isCollect, String title, String cotent, int position, String urlpic, int articleType, String fileUrl) {
        Intent intent = new Intent(context, ArticleDetailListActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, name);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, commentCount);
        intent.putExtra(IntentExtraConfig.EXTRA_GROUP_ID, isCollect);
        intent.putExtra(IntentExtraConfig.EXTRA_SHARE_TITLE, title);
        intent.putExtra(IntentExtraConfig.EXTRA_SHARE_CONTENT, cotent);
        intent.putExtra(IntentExtraConfig.EXTRA_SHARE_IMAGE, urlpic);
        intent.putExtra(IntentExtraConfig.EXTRA_POSITION, position);
        intent.putExtra(IntentExtraConfig.EXTRA_TYPE, articleType);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, fileUrl);
        context.startActivity(intent);
    }


}
