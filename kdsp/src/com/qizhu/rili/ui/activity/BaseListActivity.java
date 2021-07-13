package com.qizhu.rili.ui.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.BaseRecyclerAdapter;
import com.qizhu.rili.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.qizhu.rili.data.AbstractDataAccessor;
import com.qizhu.rili.data.DataMessage;
import com.qizhu.rili.listener.DataEmptyListener;
import com.qizhu.rili.listener.OnDataGetListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.RecyclerViewUtils;
import com.qizhu.rili.utils.SPUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.KDSPRecyclerView;

import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 列表Activity基类
 */
public abstract class BaseListActivity extends BaseActivity {
    protected PtrFrameLayout   mPtrFrameLayout;   //下拉刷新整体
    public KDSPRecyclerView mKDSPRecyclerView;               //列表
    protected AppBarLayout mAppBarLayout;             //滚动控件
    protected RelativeLayout mScrollEnterView;      //滑动隐藏布局
    protected RelativeLayout   mTitleView;        //标题布局
    protected RelativeLayout   mTransparentTitleView;        //标题布局
    protected RelativeLayout   mBottomView;       //底部布局
    protected RelativeLayout mScrollFixedView;      //滑动固定布局
    private RelativeLayout mPopRlayout;            //pop
    protected View             mMaskView;                   //键盘弹起时用到的遮罩层
    protected RelativeLayout   mEmptyLay;         //列表为空的提示布局
    protected RelativeLayout   mBodyLay;          //主体布局
    protected View             mProgressLay;                //加载
    protected View             mOutProgressLay;                //加载
    protected ImageView        mScrollToTop;           //列表滚动到头部的按钮
    protected RelativeLayout   mRootLay;          //总体布局
    protected View             mRequestBad;                 //加载用户失败的默认界面
    protected TextView         mPullUpdateCountTxt;       //下拉更新条数的提示布局
    protected boolean mCanPullDownRefresh = true;
    protected boolean mHideRecyclerView = false;
    protected HeaderAndFooterRecyclerViewAdapter mHeaderAndFooterRecyclerViewAdapter = null;


    public BaseRecyclerAdapter mAdapter;            //列表适配器
    protected boolean isFirstGet = true;        //判断是否是第一次获取数据
    protected int     mPage      = 1;                    //列表数据的页码

    public static final int LAY_TYPE_NORMAL  = 0;        //正常显示列表的布局类型
    public static final int LAY_TYPE_BAD     = 1;           //请求失败的布局类型
    public static final int LAY_TYPE_LOADING = 2;       //加载中的布局类型
    public static final int LAY_TYPE_EMPTY   = 3;         //数据为空的布局
    protected           int mCurLayType      = LAY_TYPE_NORMAL;

    private                boolean hasInitialized     = false;             //是否已经初始化过
    protected              boolean needRefreshHead    = true;             //判断是否需要刷新头部布局
    protected              boolean isOnPause          = false;                //判断是否失去焦点
    protected              boolean hasNoNextData      = false;            //判断是否还有下一页数据
    protected static final int     DEFAULT_HEAD_COUNT = 1;  //列表默认的头部个数（addHead的个数）
    protected              boolean isRequesting       = true;              //是否正在请求数据
    protected              boolean isFirstLoaded      = false;            //是否首页数据加载完成


    //滑动监听
    protected RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                int visibleItemCount = recyclerView.getChildCount();
                //默认滚动两屏之后显示
                setShowScrollIcon(isShowScrollIcon() && lastVisibleItemPosition > 6);
                if (visibleItemCount + lastVisibleItemPosition >= totalItemCount - 6) {
                    LogUtils.d("滑到底部, onScroll--> firstVisibleItem = " + lastVisibleItemPosition + ", visibleItemCount = " + visibleItemCount + ", totalItemCount = " + totalItemCount
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

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };


    /**
     * 设置是否还有下一页的数据
     */
    public void setHasNoNextData(boolean hasNoNextData) {
        this.hasNoNextData = hasNoNextData;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d("YSRL LIST ACTIVITY onCreate！this = " + this.getClass().getName());
        setContentView(R.layout.list_base);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();

        LogUtils.d("YSRL LIST ACTIVITY onStart, this = " + this.getClass().getName());
        //当不需要在onStart时获取数据时，直接返回
        if (!needGetDataOnStart()) {
            return;
        }

        //如果已经初始化过了，则不再更新
        if (!hasInitialized || (mAdapter == null || mAdapter.isDataEmpty())) {
            LogUtils.d("YSRL LIST ACTIVITY 第一次获取数据！this = " + this.getClass().getName());
            refreshViewByType(LAY_TYPE_LOADING);
            startGetData();
            hasInitialized = true;
        } else {
            LogUtils.d("YSRL LIST ACTIVITY 第二次更新界面！this = " + this.getClass().getName());
            //刷新头部信息
            refreshHeadView();

            repeatInit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d("YSRL LIST ACTIVITY onResume, this = " + this.getClass().getName());

        isOnPause = false;
        //刷新头部布局
        refreshHeadView();
        if (System.currentTimeMillis() - getLastUpdateDataTime() > AppConfig.INTERVAL_REFRESH_TIME) {
            LogUtils.d("YSRL list ACTIVITY 30分钟自动下拉刷新! ");
            pullDownToRefresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d("YSRL LIST ACTIVITY onPause, this = " + this.getClass().getName());

        isOnPause = true;
        needRefreshHead = true;
        AppContext.getAppHandler().removeMessages(AppContext.LOAD_DATA_MESSAGE);
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.d("YSRL LIST onStop in ACTIVITY, this = " + this.getClass().getName());
    }

    /**
     * 是否需要在onStart时开始获取数据
     */
    protected boolean needGetDataOnStart() {
        return true;
    }

    /**
     * 获取上次更新数据的时间,若上次更新的时间未记录，则设置为系统当前时间
     */
    protected long getLastUpdateDataTime() {
        long mLastGetDateTime = SPUtils.getLongValue(getDataSp());
        if (0 == mLastGetDateTime) {
            mLastGetDateTime = System.currentTimeMillis();
        }
        return mLastGetDateTime;
    }

    /**
     * 获取数据更新的键值，默认返回为空，则不记录当前界面
     */
    protected String getDataSp() {
        return "";
    }

    /**
     * 初始化布局
     */
    protected void initView() {
        findView();
        initEmptyView(mEmptyLay);

        initTitleView(mTitleView);
        initTransparentTitleView(mTransparentTitleView);
        initScrollFixedView();
        initBottomView(mBottomView);
        initPopView(mPopRlayout);
        //添加自定义滑动隐藏布局
        addScrollEnterView();
        mMaskView.setOnClickListener(initMaskOnClickListener());

        //初始化下拉更新条数的布局
        if (mPullUpdateCountTxt == null) {
            View updateLay = findViewById(R.id.pull_update_lay);
            mPullUpdateCountTxt = (TextView) updateLay.findViewById(R.id.update_count_data);
        }


        //设置列表布局的刷新事件
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //上拉刷新数据
                pullDownToRefresh();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                //是否可以下拉刷新
                return canPullDownRefresh() && PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });

        mKDSPRecyclerView.instanceForListView(LinearLayoutManager.VERTICAL, false);
        mKDSPRecyclerView.addOnScrollListener(getScrollListener());

        //将列表滚动到头部
        mScrollToTop.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                mKDSPRecyclerView.scrollToPosition(0);
            }
        });

        //初始化listview的数据
        initAdapter();
        if (mAdapter != null) {
            if (mHeaderAndFooterRecyclerViewAdapter == null) {
                mKDSPRecyclerView.setAdapter(mAdapter);
            } else {
                mKDSPRecyclerView.setAdapter(mHeaderAndFooterRecyclerViewAdapter);
            }

        }

        //在列表中添加自定义头部布局
        addHeadView(mKDSPRecyclerView, null);
        //在列表中添加自定义底部布局
        addFooterView(mKDSPRecyclerView, null);

        //在网络请求失败的布局上添加点击事件，重新获取数据
        mRequestBad.findViewById(R.id.reload).setOnClickListener(new OnSingleClickListener() {

            @Override
            public void onSingleClick(View view) {
                refreshViewByType(LAY_TYPE_LOADING);
                startGetData();
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                onAppBarOffsetChanged(verticalOffset);
            }
        });
    }

    protected  void initPopView(RelativeLayout popRlayout){

    }

    /**
     * 是否可以下拉刷新
     */
    protected boolean canPullDownRefresh() {
        return mCanPullDownRefresh;
    }


    /**
     * 下拉刷新
     */
    public void pullDownToRefresh() {
        startGetData();
    }

    /**
     * 上拉刷新数据
     */
    public void pullUpToRefresh() {
        getNextData();
    }

    /**
     * findViewById
     */
    private void findView() {
        mRootLay = (RelativeLayout) findViewById(R.id.main_lay);
        mPtrFrameLayout = (PtrFrameLayout) findViewById(R.id.ptr_frame);
        mKDSPRecyclerView = (KDSPRecyclerView) findViewById(R.id.list);
        mTitleView = (RelativeLayout) findViewById(R.id.my_title);
        mTransparentTitleView = (RelativeLayout) findViewById(R.id.transparent_title);
        mScrollFixedView = (RelativeLayout) findViewById(R.id.scroll_fixed);
        mBottomView = (RelativeLayout) findViewById(R.id.my_bottom);
        mPopRlayout = (RelativeLayout) findViewById(R.id.pop_rlayout);
        mBodyLay = (RelativeLayout) findViewById(R.id.body_lay);
        mRequestBad = findViewById(R.id.request_bad);           //加载用户失败的默认界面
        mMaskView = findViewById(R.id.mask);
        mProgressLay = findViewById(R.id.progress_lay);
        mOutProgressLay = findViewById(R.id.out_progress_lay);
        //初始化列表为空时的布局
        mEmptyLay = (RelativeLayout) findViewById(R.id.empty_lay);
        mScrollToTop = (ImageView) findViewById(R.id.scroll_to_top);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        mScrollEnterView = (RelativeLayout) findViewById(R.id.scroll_enter);
    }

    /**
     * 根据相应布局类型，刷新布局，在刷新加载失败布局时，失败信息从服务器返回异常中读取显示
     *
     * @param type 类别
     */
    protected void refreshViewByType(int type) {
        switch (type) {
            case LAY_TYPE_NORMAL:
                mScrollEnterView.setVisibility(View.VISIBLE);
                mRequestBad.setVisibility(View.GONE);
                mProgressLay.setVisibility(View.GONE);
                if(mHideRecyclerView){
                    mKDSPRecyclerView.setVisibility(View.GONE);
                }else {
                    mKDSPRecyclerView.setVisibility(View.VISIBLE);
                }
                mBottomView.setVisibility(View.VISIBLE);
                mEmptyLay.setVisibility(View.GONE);
                break;
            case LAY_TYPE_BAD:
                if (mAdapter != null && mAdapter.isDataEmpty()) {
                    mRequestBad.setVisibility(View.VISIBLE);
                    mKDSPRecyclerView.setVisibility(View.GONE);
                    mProgressLay.setVisibility(View.GONE);
                    mScrollEnterView.setVisibility(View.GONE);
                    mBottomView.setVisibility(View.GONE);
                    refreshEmptyView(false);
                }
                break;
            case LAY_TYPE_LOADING:
                if (mCurLayType == LAY_TYPE_LOADING) {  //如果当前正处于加载状态，则不再加载进度条
                    return;
                }
                mProgressLay.setVisibility(View.VISIBLE);
                mRequestBad.setVisibility(View.GONE);
                mKDSPRecyclerView.setVisibility(View.GONE);
                mScrollEnterView.setVisibility(View.GONE);
                refreshEmptyView(false);
                break;
            case LAY_TYPE_EMPTY:
                mProgressLay.setVisibility(View.GONE);
                mRequestBad.setVisibility(View.GONE);
                mKDSPRecyclerView.setVisibility(View.GONE);
                mScrollEnterView.setVisibility(View.VISIBLE);
                refreshEmptyView(true);
        }
        mCurLayType = type;
    }

    /**
     * 刷新列表头部布局
     */
    private void refreshHeadView() {
        if (mAdapter != null && needRefreshHead) {
            needRefreshHead = false;
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 开始获取列表数据
     */
    public void startGetData() {
        LogUtils.d("YSRL LIST ACTIVITY startGetData, this = " + this.getClass().getName());
        isFirstGet = true;
        mPage = 1;
        getData();
    }

    public boolean isDataEmpty() {
        return mAdapter == null || mAdapter.isDataEmpty();
    }

    /**
     * 刷新列表布局
     */
    protected void refreshListView() {

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 滚动到指定位置
     */
//    public void refreshListPos(int position) {
//        mKDSPRecyclerView.setSelection(position + mListView.getHeaderViewsCount());
//    }

    /**
     * 构建默认的数据返回后的回调接口
     *
     * @param accessor 数据读取器
     */
    protected OnDataGetListener buildDefaultDataGetListener(final AbstractDataAccessor accessor) {
        return buildDefaultDataGetListener(accessor, false);
    }

    /**
     * 构建默认的数据返回后的回调接口
     *
     * @param needAutoPullDown 是否需要自动下拉刷新
     */
    protected OnDataGetListener buildDefaultDataGetListener(final AbstractDataAccessor accessor, final boolean needAutoPullDown) {
        return buildDefaultDataGetListener(accessor, needAutoPullDown, null);
    }

    /**
     * 构建默认的数据返回后的回调接口
     *
     * @param listener 回调监听
     */
    protected OnDataGetListener buildDefaultDataGetListener(final AbstractDataAccessor accessor, final boolean needAutoPullDown, final DataEmptyListener listener) {
        return new OnDataGetListener() {
            @Override
            public void onGetData(DataMessage dataMessage) {
                doOnGetData(dataMessage, accessor, listener, needAutoPullDown);
            }
        };
    }

    protected void doOnGetData(final DataMessage dataMessage, final AbstractDataAccessor accessor, final DataEmptyListener listener, final boolean needAutoPullDown) {
        LogUtils.d("ONDATAGETLISTENER dataMsg = " + dataMessage);
        AppContext.getAppHandler().removeMessages(AppContext.LOAD_DATA_MESSAGE);
        if (dataMessage.isFail()) {
            String msg = dataMessage.msg;
            if (TextUtils.isEmpty(msg)) {
                UIUtils.toastMsgByStringResource(R.string.http_request_failure);
            } else {
                UIUtils.toastMsg(msg);
            }
            if (isDataEmpty()) {
                refreshViewByType(LAY_TYPE_BAD);
            } else {
                refreshViewByType(LAY_TYPE_NORMAL);
            }
        } else {
            if (listener == null) {
                refreshViewByType(LAY_TYPE_NORMAL);
            }
        }

        if (needAutoPullDown && dataMessage.isDataFromDb() && accessor.shouldPullDownToRefresh()) {
            //手动触发下拉刷新
            mPtrFrameLayout.autoRefresh();
            pullDownToRefresh();
            mPtrFrameLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtils.d("PULL_DOWN_REFRESH postDelay, refreshComplete!");
                    mPtrFrameLayout.refreshComplete();
                }
            }, 3000);
        } else {
            refreshListViewComplete();
        }

        refreshListView();

        //设置没有更多数据的标志位
        setHasNoNextData(dataMessage.hasNoNextData());

        //回调通知
        if (listener != null) {
            listener.onDataGet(dataMessage.isDataEmpty());
        }
        isRequesting = false;
    }

    protected void refreshListViewComplete() {
        mPtrFrameLayout.refreshComplete();
    }

    /**
     * 刷新列表为空的布局
     *
     * @param isShow 是否显示空布局
     */
    private void refreshEmptyView(boolean isShow) {
        if (mEmptyLay != null) {
            mEmptyLay.setVisibility(isShow ? View.VISIBLE : View.GONE);
            //回调
            onEmptyViewShowed(isShow);
        }
    }

    /**
     * 重复加载时执行的方法
     */
    public void repeatInit() {
    }

    /**
     * 向ListView中添加自定义的头部布局
     *
     * @param refreshableView 当前listview
     */
    protected void addHeadView(KDSPRecyclerView refreshableView, View view) {
        if (view != null && mAdapter != null) {
            RecyclerViewUtils.setHeaderView(refreshableView, view);
        }

    }

    /**
     * 向ListView中添加自定义的底部布局
     *
     * @param refreshableView 当前listview
     */
    protected void addFooterView(KDSPRecyclerView refreshableView, View view) {
        if (view != null && mAdapter != null) {
            RecyclerViewUtils.setFooterView(refreshableView, view);
        }
    }

    /**
     * 添加自定义滑动隐藏布局
     */
    protected void addScrollEnterView() {
    }

    /**
     * AppBar滚动监听
     */
    protected void onAppBarOffsetChanged(int verticalOffset) {
    }
    /**
     * mask单击事件
     */
    protected OnSingleClickListener initMaskOnClickListener() {
        return null;
    }

    /**
     * 当空布局显示或隐藏时的回调
     *
     * @param isShow 是否显示
     */
    protected void onEmptyViewShowed(boolean isShow) {
    }

    /**
     * 初始化适配器
     */
    protected abstract void initAdapter();

    /**
     * 初始化列表为空时的布局
     *
     * @param emptyLay 空布局
     */
    protected void initEmptyView(RelativeLayout emptyLay) {
    }

    /**
     * 初始化头部布局
     *
     * @param titleView 标题view
     */
    protected void initTitleView(RelativeLayout titleView) {
    }

 /**
     * 初始化头部布局
     *
     * @param titleView 标题view
     */
    protected void initTransparentTitleView(RelativeLayout titleView) {
    }
    /**
     *
     */
    protected View createScrollFixedView() {
        return null;
    }

    /**
     * 初始化头部布局
     */
    protected final void initScrollFixedView() {
        if (mScrollFixedView.getChildCount() == 0) {
            View tmp = createScrollFixedView();
            if (tmp != null) {
                mScrollFixedView.addView(tmp);
            }
        }
    }

    /**
     * 初始化底部布局
     *
     * @param bottomView 底部view
     */
    protected void initBottomView(RelativeLayout bottomView) {
    }

    /**
     * 设置是否显示滚动图标
     */
    protected void setShowScrollIcon(boolean show) {
        mScrollToTop.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 获取列表数据
     */
    protected abstract void getData();

    /**
     * 获取下一页数据
     */
    protected abstract void getNextData();

    /**
     * 是否显示回到top的icon,默认是显示的,你可以重构return false则不显示
     */
    protected boolean isShowScrollIcon() {
        return true;
    }

    /**
     * 获取滑动监听（子类需重写）
     */
    protected RecyclerView.OnScrollListener getScrollListener() {
        return null;
    }
}
