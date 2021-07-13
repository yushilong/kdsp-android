package com.qizhu.rili.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.lang.reflect.Field;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * ListFragment基类
 */
public abstract class BaseListFragment extends BaseFragment {
    protected PtrFrameLayout mPtrFrameLayout;   //下拉刷新整体
    protected KDSPRecyclerView mKDSPRecyclerView;               //列表
    private AppBarLayout mAppBarLayout;             //滚动控件
    protected RelativeLayout mScrollEnterView;      //滑动隐藏布局
    protected RelativeLayout mTitleView;
    protected RelativeLayout mBodyLay;
    protected View mProgressLay;
    protected ImageView mScrollToTop;           //列表滚动到头部的按钮
    protected View mRequestBad;                 //加载用户失败的默认界面
    protected RelativeLayout mEmptyLay;         //列表为空的提示布局
    private TextView mPullUpdateCountTxt;       //下拉更新条数的提示布局

    public BaseRecyclerAdapter mAdapter;            //列表适配器
    protected boolean isFirstGet = true;        //判断是否是第一次获取数据
    protected int mPage = 1;                    //列表数据的页码

    public static final int LAY_TYPE_NORMAL = 0;        //正常显示列表的布局类型
    public static final int LAY_TYPE_BAD = 1;           //请求失败的布局类型
    public static final int LAY_TYPE_LOADING = 2;       //加载中的布局类型
    public static final int LAY_TYPE_EMPTY = 3;         //数据为空的布局
    protected int mCurLayType = LAY_TYPE_NORMAL;

    private boolean hasInitialized = false;       //是否已经初始化过
    private boolean needRefreshHead = true;       //判断是否需要刷新头部布局
    protected boolean isOnPause = false;          //判断当前界面是否失去焦点

    protected boolean                            mCanPullDownRefresh                 = true;
    protected boolean                            hasNoNextData                       = false;        //判断是否还有下一页数据
    protected boolean                            isRequesting                        = true;          //是否正在请求数据
    protected HeaderAndFooterRecyclerViewAdapter mHeaderAndFooterRecyclerViewAdapter = null;

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
     *
     * @param hasNoNextData 是否还有下一页数据
     */
    public void setHasNoNextData(boolean hasNoNextData) {
        this.hasNoNextData = hasNoNextData;
    }

    public boolean isHasNoNextData() {
        return this.hasNoNextData;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.list_frag_base, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    public boolean isDataEmpty() {
        return mAdapter == null || mAdapter.isDataEmpty();
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.d("YSRL LIST FRAGMENT onStart, frag = " + this);
        if (!hasInitialized || (mAdapter == null || mAdapter.isDataEmpty())) {
            LogUtils.d("YSRL LIST FRAGMENT 第一次获取数据！this = " + this);
            refreshViewByType(LAY_TYPE_LOADING);
            startGetData();
        } else {
            LogUtils.d("YSRL LIST FRAGMENT 第二次更新界面！this = " + this + ", needRefreshHead = " + needRefreshHead);
            //刷新头部信息

            //非第一次初始化，子类重写
            repeatInit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        isOnPause = false;
        if (System.currentTimeMillis() - getLastUpdateDataTime() > AppConfig.INTERVAL_REFRESH_TIME) {
            LogUtils.d("YSRL list fragment 30分钟自动下拉刷新! ");
            pullDownToRefresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d("YSRL LIST FRAGMENT onPause, frag = " + this);

        isOnPause = true;
        needRefreshHead = true;
        AppContext.getAppHandler().removeMessages(AppContext.LOAD_DATA_MESSAGE);
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.d("YSRL LIST onStop in Fragment, frag = " + this);
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
        mPtrFrameLayout = (PtrFrameLayout) mMainLay.findViewById(R.id.ptr_frame);
        mKDSPRecyclerView = (KDSPRecyclerView) mMainLay.findViewById(R.id.list);
        mAppBarLayout = (AppBarLayout) mMainLay.findViewById(R.id.appbar);
        mScrollEnterView = (RelativeLayout) mMainLay.findViewById(R.id.scroll_enter);
        mTitleView = (RelativeLayout) mMainLay.findViewById(R.id.my_title);
        mBodyLay = (RelativeLayout) mMainLay.findViewById(R.id.body_lay);
        mRequestBad = mMainLay.findViewById(R.id.request_bad); //加载用户失败的默认界面
        mProgressLay = mMainLay.findViewById(R.id.progress_lay);
        mScrollToTop = (ImageView) mMainLay.findViewById(R.id.scroll_to_top);       //列表滚动到头部的按钮
        //初始化标题栏布局
        if (mTitleView.getChildCount() == 0) {
            initTitleView(mTitleView);
        }

        //初始化列表为空时的布局
        mEmptyLay = (RelativeLayout) mMainLay.findViewById(R.id.empty_lay);
        initEmptyView();

        //初始化下拉更新条数的布局
        if (mPullUpdateCountTxt == null) {
            View updateLay = mMainLay.findViewById(R.id.pull_update_lay);
            mPullUpdateCountTxt = (TextView) updateLay.findViewById(R.id.update_count_data);
        }

        mKDSPRecyclerView.instanceForListView(LinearLayoutManager.VERTICAL, false);

        //添加自定义滑动隐藏布局
        addScrollEnterView();

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

        mKDSPRecyclerView.addOnScrollListener(getScrollListener());

        //将列表滚动到头部
        mScrollToTop.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                mKDSPRecyclerView.scrollToPosition(0);
            }
        });
        //初始化适配器
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
        //在网络请求失败的布局上添加点击事件，重新获取数据
        mRequestBad.findViewById(R.id.reload).setOnClickListener(new OnSingleClickListener() {

            @Override
            public void onSingleClick(View view) {
                refreshViewByType(LAY_TYPE_LOADING);
                getData();
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                onAppBarOffsetChanged(verticalOffset);
            }
        });
    }

    /**
     * 是否可以下拉刷新
     */
    protected boolean canPullDownRefresh() {
        return mCanPullDownRefresh;
    }

    /**
     * AppBar滚动监听
     */
    protected void onAppBarOffsetChanged(int verticalOffset) {
    }

    /**
     * 获取滑动监听（子类需重写）
     */
    protected RecyclerView.OnScrollListener getScrollListener() {
        return null;
    }

    /**
     * 下拉刷新
     */
    public void pullDownToRefresh() {
        mPage = 1;
        getData();
    }

    /**
     * 上拉刷新数据
     */
    public void pullUpToRefresh() {
        getNextData();
    }

    /**
     * 根据相应布局类型，刷新布局，在刷新加载失败布局时，失败信息从服务器返回异常中读取显示
     */
    protected void refreshViewByType(int type) {
        switch (type) {
            case LAY_TYPE_NORMAL:
                mScrollEnterView.setVisibility(View.VISIBLE);
                mRequestBad.setVisibility(View.GONE);
                mProgressLay.setVisibility(View.GONE);
                mKDSPRecyclerView.setVisibility(View.VISIBLE);
                refreshEmptyView(false);
                break;
            case LAY_TYPE_BAD:

                if (mAdapter != null && mAdapter.isDataEmpty()) {
                    mRequestBad.setVisibility(View.VISIBLE);
                    mKDSPRecyclerView.setVisibility(View.GONE);
                    mProgressLay.setVisibility(View.GONE);
                    mScrollEnterView.setVisibility(View.GONE);
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
                mScrollEnterView.setVisibility(View.VISIBLE);
                mProgressLay.setVisibility(View.GONE);
                mRequestBad.setVisibility(View.GONE);
                refreshEmptyView(true);
        }
        mCurLayType = type;
    }

    /**
     * 刷新列表为空的布局
     *
     * @param isShow 是否显示
     */
    private void refreshEmptyView(boolean isShow) {
        if (!isShow) {
            mEmptyLay.setVisibility(View.GONE);
        } else {
            mEmptyLay.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 开始获取列表数据
     */
    public boolean startGetData() {
        LogUtils.d("YSRL LIST FRAGMENT startGetData, frag = " + this + ", hasInitialized = " + hasInitialized);
        if (hasInitialized) {
            refreshViewByType(LAY_TYPE_NORMAL);
            return false;
        }
        isFirstGet = true;
        mPage = 1;
        hasInitialized = true;
        getData();
        return true;
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
     * 设置是否已经初始化的标志位
     *
     * @param hasInitialized 是否被初始化
     */
    public void setHasInitialized(boolean hasInitialized) {
        this.hasInitialized = hasInitialized;
    }

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
     * @param needAutoPullDown 是否自动下拉刷新
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
                doOnGetData(dataMessage, accessor, needAutoPullDown, listener);
            }
        };
    }

    protected void doOnGetData(final DataMessage dataMessage, final AbstractDataAccessor accessor, final boolean needAutoPullDown, final DataEmptyListener listener) {
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
            autoPullDownRefresh();
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

    /**
     * 非第一次加载时执行的方法
     */
    public void repeatInit() {
    }

    /**
     * 自动刷新一次数据
     */
    public void autoPullDownRefresh() {
        if (mKDSPRecyclerView != null) {
            mPtrFrameLayout.autoRefresh();
            pullDownToRefresh();
            mKDSPRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtils.d("PULL_DOWN_REFRESH postDelay, refreshComplete!");
                    mPtrFrameLayout.refreshComplete();
                }
            }, 3000);
        }
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

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 添加自定义滑动隐藏布局
     */
    protected void addScrollEnterView() {
    }

    /**
     * 初始化列表为空时的布局
     */
    protected void initEmptyView() {
    }


    protected void refreshListViewComplete() {
        mPtrFrameLayout.refreshComplete();
    }

    /**
     * 初始化适配器
     */
    protected abstract void initAdapter();

    /**
     * 初始化头部标题栏布局
     *
     * @param titleView 标题view
     */
    protected abstract void initTitleView(RelativeLayout titleView);

    /**
     * 获取列表数据
     */
    protected abstract void getData();

    /**
     * 获取下一页数据
     */
    protected abstract void getNextData();

    /**
     * 设置是否显示滚动图标
     */
    protected void setShowScrollIcon(boolean show) {
        if (show && mScrollToTop.getVisibility() == View.VISIBLE) {
            return;
        }
        if (!show && mScrollToTop.getVisibility() == View.GONE) {
            return;
        }
        mScrollToTop.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 是否显示回到top的icon,默认是显示的,你可以重构return false则不显示
     */
    protected boolean isShowScrollIcon() {
        return true;
    }
}
