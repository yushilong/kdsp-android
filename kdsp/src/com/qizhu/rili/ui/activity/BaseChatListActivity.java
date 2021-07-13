package com.qizhu.rili.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.RequestCodeConfig;
import com.qizhu.rili.adapter.BaseListAdapter;
import com.qizhu.rili.data.AbstractDataAccessor;
import com.qizhu.rili.data.DataMessage;
import com.qizhu.rili.listener.DataEmptyListener;
import com.qizhu.rili.listener.OnDataGetListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.SPUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.ResizeRelativeLayout;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by Lindow on 2015/11/16.
 * 对话列表的基类，所有带有评论输入框的列表的基类
 * 此基类大致内容与BaseListActivity相同
 */
public abstract class BaseChatListActivity extends BaseActivity {
    protected PtrFrameLayout mPtrFrameLayout;   //下拉刷新整体
    protected ListView mListView;               //列表
    protected RelativeLayout mTitleView;        //标题布局
    protected RelativeLayout mBottomView;       //底部布局，包括键盘和表情的显示区域
    protected RelativeLayout mChatView;         //聊天输入布局，包括表情键盘切换以及输入等
    protected View mContentLay;                 //包括列表和键盘遮罩层的布局
    protected View mMaskView;                   //键盘弹起时用到的遮罩层
    protected RelativeLayout mEmptyLay;         //列表为空的提示布局
    protected RelativeLayout mBodyLay;
    protected View mProgressLay;
    protected ResizeRelativeLayout mRootLay;
    protected View mRequestBad;                 //加载用户失败的默认界面
    private TextView mPullUpdateCountTxt;       //下拉更新条数的提示布局
    protected TextView mUnread;                 //未读消息

    public BaseListAdapter mAdapter;            //列表适配器
    protected boolean isFirstGet = true;        //判断是否是第一次获取数据
    protected int mPage = 1;                    //列表数据的页码

    public static final int LAY_TYPE_NORMAL = 0;        //正常显示列表的布局类型
    public static final int LAY_TYPE_BAD = 1;           //请求失败的布局类型
    public static final int LAY_TYPE_LOADING = 2;       //加载中的布局类型
    public static final int LAY_TYPE_EMPTY = 3;         //数据为空的布局
    protected int mCurLayType = LAY_TYPE_NORMAL;

    private boolean hasInitialized = false;             //是否已经初始化过
    private boolean needRefreshHead = true;             //判断是否需要刷新头部布局
    protected boolean isOnPause = false;                //判断是否失去焦点
    protected boolean hasNoNextData = false;            //判断是否还有下一页数据
    protected static final int DEFAULT_HEAD_COUNT = 1;  //列表默认的头部个数（addHead的个数）
    protected boolean isRequesting = true;              //是否正在请求数据
    protected int mContentHeight;                       //列表的高度

    protected String picPath;                     //选择图片的返回路径
    protected String cameraFilePath = "";         //相机拍摄时存储的路径

    //滑动监听
    protected AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (visibleItemCount + firstVisibleItem >= totalItemCount - 6) {
                LogUtils.d("滑到底部, onScroll--> firstVisibleItem = " + firstVisibleItem + ", visibleItemCount = " + visibleItemCount + ", totalItemCount = " + totalItemCount
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
        LogUtils.d("CYZS LIST ACTIVITY onCreate！this = " + this.getClass().getName());
        setContentView(R.layout.list_chat_base);
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();

        LogUtils.d("CYZS LIST ACTIVITY onStart, this = " + this.getClass().getName());
        //当不需要在onStart时获取数据时，直接返回
        if (!needGetDataOnStart()) {
            return;
        }

        //如果已经初始化过了，则不再更新
        if (!hasInitialized || (mAdapter == null || mAdapter.isDataEmpty())) {
            LogUtils.d("CYZS LIST ACTIVITY 第一次获取数据！this = " + this.getClass().getName());
            refreshViewByType(LAY_TYPE_LOADING);
            startGetData();
            hasInitialized = true;
        } else {
            LogUtils.d("CYZS LIST ACTIVITY 第二次更新界面！this = " + this.getClass().getName());
            //刷新头部信息
            refreshHeadView();

            repeatInit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d("CYZS LIST ACTIVITY onResume, this = " + this.getClass().getName());

        isOnPause = false;
        //刷新头部布局
        refreshHeadView();
        if (System.currentTimeMillis() - getLastUpdateDataTime() > AppConfig.INTERVAL_REFRESH_TIME) {
            LogUtils.d("CYZS list ACTIVITY 30分钟自动下拉刷新! ");
            pullDownToRefresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d("CYZS LIST ACTIVITY onPause, this = " + this.getClass().getName());

        isOnPause = true;
        needRefreshHead = true;
        AppContext.getAppHandler().removeMessages(AppContext.LOAD_DATA_MESSAGE);
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.d("CYZS LIST onStop in ACTIVITY, this = " + this.getClass().getName());
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
        initBottomView(mBottomView);
        //initChat放在initBottom之后，将init的表情与输入框联合起来
        initChatView(mChatView);

        mRootLay.setOnResizeListener(initOnResizeListener());
        mMaskView.setOnClickListener(initMaskOnClickListener());

        //初始化下拉更新条数的布局
        if (mPullUpdateCountTxt == null) {
            View updateLay = findViewById(R.id.pull_update_lay);
            mPullUpdateCountTxt = (TextView) updateLay.findViewById(R.id.update_count_data);
        }

        //在列表中添加自定义底部布局
        addFooterView(mListView);
        //在列表中添加自定义头部布局
        addHeadView(mListView);

        //设置列表布局的刷新事件
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //上拉刷新数据
                pullDownToRefresh();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                //不可以下拉刷新
                return false;
            }
        });

        mListView.setOnScrollListener(getScrollListener());

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(parent, view, position, id);
            }
        });

        //初始化listview的数据
        initAdapter();
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
        }

        //在网络请求失败的布局上添加点击事件，重新获取数据
        mRequestBad.findViewById(R.id.reload).setOnClickListener(new OnSingleClickListener() {

            @Override
            public void onSingleClick(View view) {
                refreshViewByType(LAY_TYPE_LOADING);
                startGetData();
            }
        });
    }

    /**
     * 获取滑动监听（子类需重写）
     */
    protected AbsListView.OnScrollListener getScrollListener() {
        return null;
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
        mRootLay = (ResizeRelativeLayout) findViewById(R.id.main_lay);
        mPtrFrameLayout = (PtrFrameLayout) findViewById(R.id.ptr_frame);
        mListView = (ListView) findViewById(R.id.list);
        mTitleView = (RelativeLayout) findViewById(R.id.my_title);
        mBottomView = (RelativeLayout) findViewById(R.id.my_bottom);
        mChatView = (RelativeLayout) findViewById(R.id.chat_lay);
        mBodyLay = (RelativeLayout) findViewById(R.id.body_lay);
        mContentLay = findViewById(R.id.content_lay);
        mRequestBad = findViewById(R.id.request_bad);           //加载用户失败的默认界面
        mMaskView = findViewById(R.id.mask);
        mProgressLay = findViewById(R.id.progress_lay);
        //初始化列表为空时的布局
        mEmptyLay = (RelativeLayout) findViewById(R.id.empty_lay);
        mUnread = (TextView) findViewById(R.id.unread);
    }

    /**
     * 获取更新新数据的监听器
     */
    public AbstractDataAccessor.UpdateListener getUpdateListener() {
        return new AbstractDataAccessor.UpdateListener() {
            @Override
            public void onUpdateNewCount(int newCount) {
                refreshPullUpdateCount(newCount);
            }
        };
    }

    /**
     * 刷新下拉更新数据条数布局
     */
    protected void refreshPullUpdateCount(final int count) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPullUpdateCountTxt != null) {
                    String countTxt = (count == 0) ? "已经更新到最新" : String.format("更新%s条", count >= 20 ? "20+" : String.valueOf(count));
                    mPullUpdateCountTxt.setText(countTxt);
                    mPullUpdateCountTxt.setVisibility(View.VISIBLE);
                    mPullUpdateCountTxt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPullUpdateCountTxt.setVisibility(View.GONE);
                        }
                    }, 1500);
                }
            }
        });
    }


    /**
     * 根据相应布局类型，刷新布局，在刷新加载失败布局时，失败信息从服务器返回异常中读取显示
     *
     * @param type 类别
     */
    protected void refreshViewByType(int type) {
        switch (type) {
            case LAY_TYPE_NORMAL:
                mRequestBad.setVisibility(View.GONE);
                mProgressLay.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mEmptyLay.setVisibility(View.GONE);
                break;
            case LAY_TYPE_BAD:
                if (mAdapter != null && mAdapter.isDataEmpty()) {
                    mRequestBad.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                    mProgressLay.setVisibility(View.GONE);
                    refreshEmptyView(false);
                }
                break;
            case LAY_TYPE_LOADING:
                if (mCurLayType == LAY_TYPE_LOADING) {  //如果当前正处于加载状态，则不再加载进度条
                    return;
                }
                mProgressLay.setVisibility(View.VISIBLE);
                mRequestBad.setVisibility(View.GONE);
                mListView.setVisibility(View.GONE);
                refreshEmptyView(false);
                break;
            case LAY_TYPE_EMPTY:
                mProgressLay.setVisibility(View.GONE);
                mRequestBad.setVisibility(View.GONE);
                refreshEmptyView(true);
                break;
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
        LogUtils.d("CYZS LIST ACTIVITY startGetData, this = " + this.getClass().getName());
        isFirstGet = true;
        mPage = 1;
        getData();
    }

    /**
     * 获取下一页数据失败时的调整
     */
    protected void adjustPageForNext() {
        if (mPage > 1) {
            mPage--;
        }
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
     *
     * @param position
     */
    public void refreshListPos(int position) {
        mListView.setSelection(position + mListView.getHeaderViewsCount());
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
        if (dataMessage.isFail() && accessor.mData.isEmpty()) {
            String msg = dataMessage.msg;
            if (TextUtils.isEmpty(msg)) {
                UIUtils.toastMsgByStringResource(R.string.http_request_failure);
            } else {
                UIUtils.toastMsg(msg);
            }
            refreshViewByType(LAY_TYPE_BAD);
        } else {
            if (listener == null) {
                refreshViewByType(LAY_TYPE_NORMAL);
            }
        }

        if (needAutoPullDown && dataMessage.isDataFromDb() && accessor.shouldPullDownToRefresh()) {
            //手动触发下拉刷新
            mPtrFrameLayout.autoRefresh();
            pullDownToRefresh();
            mListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtils.d("PULL_DOWN_REFRESH postDelay, refreshComplete!");
                    mPtrFrameLayout.refreshComplete();
                }
            }, 3000);
        } else {
            refreshListViewComplete();
        }

        if (dataMessage.appendNum > 0) {
            refreshListView();
        }

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
    protected void addHeadView(ListView refreshableView) {
    }

    /**
     * 向ListView中添加自定义的底部布局
     *
     * @param refreshableView 当前listview
     */
    protected void addFooterView(ListView refreshableView) {
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
     * 初始化底部输入布局
     *
     * @param chatView 底部view
     */
    protected void initChatView(RelativeLayout chatView) {
    }

    /**
     * 初始化底部布局
     *
     * @param bottomView 底部view
     */
    protected void initBottomView(RelativeLayout bottomView) {
    }

    /**
     * 列表项点击事件
     *
     * @param parent   父view
     * @param view     点击view
     * @param position 点击位置
     * @param id       点击id
     */
    protected abstract void onListItemClick(AdapterView<?> parent, View view, int position, long id);

    /**
     * 设定界面布局变化的监听类
     */
    protected ResizeRelativeLayout.OnResizeListener initOnResizeListener() {
        return null;
    }

    /**
     * 获取列表数据
     */
    protected abstract void getData();

    /**
     * 获取下一页数据
     */
    protected abstract void getNextData();

    @Override
    public void setImagePath(String path) {
        cameraFilePath = path;
    }

    /**
     * 图片选择之后，执行的回调事件
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d("获取图片 onActivityResult -> this = " + this + ", requestCode = " + requestCode + ", data = " + data);
        //友盟报少量data返回为空的情况
        try {
            // 通过“图片浏览器”获得自己的头像图片
            if (requestCode == RequestCodeConfig.REQUEST_CODE_GETIMAGE_BYSDCARD) {
                Uri selectedImg = data.getData();
                //测试时发现moto的一款手机返回的居然是这样的uri  file:///mnt/sdcard/DCIM/Camera/1351684944195.jpg
                if (selectedImg.toString().contains("file://")) {
                    picPath = selectedImg.getPath();
                } else {
                    String[] filePathColumn = new String[]{MediaStore.Images.Media.DATA};
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(selectedImg, filePathColumn, null, null, null);
                        if (cursor == null) {
                            UIUtils.toastMsg("图片格式不合要求~");
                            picPath = "";
                        } else {
                            cursor.moveToFirst();
                            picPath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
                LogUtils.i("picPath:" + picPath + ":   ------" + selectedImg.toString());
                sendPic();
            } else if (requestCode == RequestCodeConfig.REQUEST_CODE_GETIMAGE_BYCAMERA) {
                // 通过手机的拍照功能，获得自己的头像图片
                picPath = cameraFilePath;

                LogUtils.i("picPath:" + picPath);
                sendPic();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送图片
     */
    protected abstract void sendPic();

}
