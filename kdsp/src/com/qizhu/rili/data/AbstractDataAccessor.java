package com.qizhu.rili.data;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.listener.OnDataGetListener;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.SPUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 获取数据的抽象类，该类负责整合数据库和服务端的数据
 * 该类不是线程安全的
 */
public abstract class AbstractDataAccessor<T> {
    protected int mLimit = 20;                  //每次获取数据数目的限制
    public List<T> mData = new ArrayList<T>();  //当前数据列表

    public static final String MSG_DATA_PARSE_ERROR = "数据解析出错！";
    public static final String MSG_API_FAILURE_ERROR = "请求失败了，请重试~";
    public static final String MSG_NETWORK_ERROR = "你的网络不太给力哦~";
    public static final String MSG_NOT_LOGIN_ERROR = "您还没有登录！";

    public int mLoadNum = 0;                        //实际加载过的数据，不是totalNum，因为可能会获取重复数据，依据totalNum计算page的时候可能会出错，导致不断的获取同一页的数据
    protected boolean mLoadDBSuccess = false;       //加载数据库数据是否成功,默认为false
    protected String mDataSPKey = "";               //此页面数据更新时间的key值

    protected UpdateListener mUpdateListener;       //更新数据的监听器
    protected int mPage = 1;                        //当前的请求页，每次请求成功之后默认请求下一页数据
    protected boolean mShouldPullDownToRefresh = false;       //是否应该下拉刷新

    /**
     * 设置更新数据的监听器
     */
    public void setUpdateListener(UpdateListener listener) {
        this.mUpdateListener = listener;
    }

    /**
     * 初次获取数据接口，逻辑如下：
     * 1.如果mData为空，则直接从服务器获取数据
     * 2.如果mData不为空，则直接返回数据
     */
    public void getData(final OnDataGetListener<T> listener) {
        LogUtils.d("now begin getData");
        //默认请求首页的时候，是可以重复请求的
        mShouldPullDownToRefresh = true;
        //网络可用，则直接从服务器获取数据
        if (AppContext.isNetworkConnected()) {
            if (shouldUpdateData()) {
                LogUtils.d("should update data,so get data from server");
                mShouldPullDownToRefresh = false;
                getDataFromDb(listener);
                getAllDataFromServer(listener);
            } else {
                LogUtils.d("should not update data,so get data from db");
                mLoadDBSuccess = getDataFromDb(listener);
                if (!mLoadDBSuccess) {
                    LogUtils.d("load db failed,so get data from server");
                    getDataFromServerAndAppend(listener);
                }
            }
        } else {
            //网络不可用，从数据库中获取
            LogUtils.d("no network,so get data from db");
            mLoadDBSuccess = getDataFromDb(listener);
            if (!mLoadDBSuccess) {
                //数据库返回数据失败，返回网络错误
                listener.onGetData(DataMessage.<T>buildFailDataMessage(MSG_NETWORK_ERROR));
            }
        }
    }

    /**
     * 该页面是否需要刷新数据,当前页面上一次更新数据时与当前时间间隔超过15分钟时进行数据刷新
     */
    private boolean shouldUpdateData() {
        return System.currentTimeMillis() - SPUtils.getLongValue(mDataSPKey) > AppConfig.UODATE_DATA_TIME_INTERVAL;
    }

    /**
     * 该页面是否应该下拉刷新
     * 此处主要是避免重复的进行首页的数据请求，避免流程上的纰漏
     * 纰漏如下：在15分钟的机制之下，若大于15分钟，同时回调中设置了下拉刷新，那么是会重复请求首页的，因此设置变量值避免这种情况
     */
    public boolean shouldPullDownToRefresh() {
        return mShouldPullDownToRefresh;
    }

    /**
     * 从服务端获取全部数据，并且使用获取的数据替换掉当前数据
     */
    public void getAllDataFromServer(final OnDataGetListener<T> listener) {
        mLoadDBSuccess = false;     //下拉刷新之后，不再从服务器获取数据了，因为数据库也会相应的清空来保证数据的正确性
        SPUtils.putLongValue(mDataSPKey, System.currentTimeMillis());
        getDataFromServerAndReset(listener);
    }

    /**
     * 从服务器获取数据，之后reset当前数据
     */
    private void getDataFromServerAndReset(OnDataGetListener<T> listener) {
        getDataFromServer(listener, false);
    }

    /**
     * 从服务器获取数据，之后append到当前数据
     */
    private void getDataFromServerAndAppend(OnDataGetListener<T> listener) {
        SPUtils.putLongValue(mDataSPKey, System.currentTimeMillis());
        getDataFromServer(listener, true);
    }

    /**
     * 从服务端获取数据
     *
     * @param useAppend true 使用appendNewData方法，false 使用resetData方法，用户在获取数据后，直接调用handleNewData方法即可
     */
    protected abstract void getDataFromServer(OnDataGetListener<T> listener, boolean useAppend);

    /**
     * 从数据库获取数据，仅返回成功信息，失败信息由网络返回
     */
    protected abstract boolean getDataFromDb(OnDataGetListener<T> listener);

    /**
     * 获取下一页数据，与请求第一页一样，流程更改
     * 1.若上次load数据库成功，则说明需要从数据库获取，那么继续从数据库获取之
     * 2.若从数据库获取失败，那么还是从网络获取之
     * 3.若上次就load数据库失败了，那么就直接从网络获取了
     * 4.获取下一页数据，就无需在检测15分钟重复请求了，该机制只发生在初次获取数据之上
     */
    public void getNextData(final OnDataGetListener<T> listener) {
        if (!mLoadDBSuccess) {
            LogUtils.d("getNextData,get data from db failed,so get next data from server");
            getDataFromServerAndAppend(listener);
        } else {
            LogUtils.d("getNextData,get data from db success,so continue");
            mLoadDBSuccess = getDataFromDb(listener);
            if (!mLoadDBSuccess) {
                LogUtils.d("getNextData,load db failed,so get Next data from server");
                getDataFromServerAndAppend(listener);
            }
        }
    }

    /**
     * 处理获取到的数据
     *
     * @param useAppend true 使用appendNewData方法，false 使用resetData方法
     */
    public int handleNewData(boolean useAppend, List<T> newData, int msgStatus) {
        if (useAppend) {
            if (msgStatus == DataMessage.SUCCESS_FROM_SERVER) {
                mPage++;   //不管是否刷新，只要得到新数据，则刷新页数
                mLoadNum += newData.size();
                LogUtils.d("-------- now add  mLoadNum = " + mLoadNum);
            }
            return appendNewData(newData, msgStatus);
        } else {
            mPage++;   //不管是否刷新，只要得到新数据，则刷新页数
            mLoadNum = newData.size();//重新计算loadNum
            LogUtils.d("-------- now mLoadNum = " + mLoadNum);
            return resetData(newData, msgStatus);
        }
    }

    /**
     * 将返回的数据append到当前数据列表中
     */
    protected int appendNewData(List<T> newData, int msgStatus) {
        int newNum = 0;
        List<T> newDataLst = new ArrayList<T>();
        for (T tmp : newData) {
            if (!mData.contains(tmp)) {
                newDataLst.add(tmp);
                newNum++;
            }
        }

        //排序
        if (getSortComparator() != null) {
            Collections.sort(newDataLst, getSortComparator());
        }
        mData.addAll(newDataLst);

        //将新获取的数据插入传入的lst中，那么前台可以才能这里获取实际新获取的信息
        newData.clear();
        newData.addAll(newDataLst);

        afterAppendNewData(newDataLst, msgStatus);
        return newNum;
    }

    /**
     * 获取排序的Comparator
     */
    protected Comparator<T> getSortComparator() {
        return null;
    }

    /**
     * 获取服务器请求数据的页数
     */
    protected int getPage(boolean useAppend) {
        mPage = useAppend ? mPage : 1;
        return mPage;
    }

    /**
     * 获取当前的请求页数
     */
    public int getPage () {
        return mPage;
    }

    /**
     * 为每个返回的单个T对象生成服务器顺序，确保缓存显示顺序正确
     */
    protected int generateServerOrder(String key) {
        return Integer.parseInt(key) + mPage * 20;
    }

    /**
     * 添加完新数据后进行的操作，比如数据库存储等
     */
    abstract protected void afterAppendNewData(List<T> data, int msgStatus);


    /**
     * 用新数据替换当前的数据
     */
    protected int resetData(List<T> data, int msgStatus) {
        if (data != null) {
            //计算更新的条数
            updateNewData(data);
            //重新初始化数据
            mData.clear();
            mData.addAll(data);
        }

        //排序
        if (getSortComparator() != null) {
            Collections.sort(mData, getSortComparator());
        }
        afterResetData(mData, msgStatus);
        return mData.size();
    }

    /**
     * 计算更新的新数据
     */
    protected int updateNewData(List<T> data) {
        int updateCount = 0;
        if (data != null) {
            List<T> dataTmp = new ArrayList<T>(data);
            dataTmp.retainAll(mData);
            updateCount = data.size() - dataTmp.size();
        }
        //更新新数据后的回调
        if (mUpdateListener != null) {
            mUpdateListener.onUpdateNewCount(updateCount);
        }
        return updateCount;
    }

    /**
     * 更新完数据后进行的操作，必须将数据更新到数据库中等
     */
    abstract protected void afterResetData(List<T> data, int msgStatus);

    /**
     * 获取一次取数据的量
     */
    protected int getLimit() {
        return mLimit;
    }

    public int getTotalNum() {
        return mData.size();
    }

    /**
     * 将所有数据设置为默认值
     */
    public void makeDataDefault() {
        mLimit = 10;            //每次获取数据数目的限制
        mData = new ArrayList<T>();     //当前数据列表

        mLoadNum = 0;//实际加载过的数据，不是totalNum，因为可能会获取重复数据，依据totalNum计算page的时候可能会出错，导致不断的获取同一页的数据
        mPage = 1;  //请求页数重置为1
    }

    /**
     * 更新数据的监听器
     */
    public interface UpdateListener {
        void onUpdateNewCount(int newCount);
    }

    /**
     * 获得数据更新键值
     */
    public void setDataSpKey(String mKey) {
        mDataSPKey = mKey;
    }
}
