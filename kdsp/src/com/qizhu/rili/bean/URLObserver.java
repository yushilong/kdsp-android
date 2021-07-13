package com.qizhu.rili.bean;


import com.qizhu.rili.listener.URLChangeListener;
import com.qizhu.rili.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by Lindow on 2014/12/20.
 * URL变更的观察器
 */
public class URLObserver {
    private String mUrl;                                                //监听的url
    private ArrayList<URLChangeListener> mListeners;     //监听器列表，用弱引用防止OOM

    public URLObserver() {
        mListeners = new ArrayList<URLChangeListener>();
    }

    /**
     * 设置URL，若url发生变化，那么激发监听器
     */
    public void setUrl(String url) {
        if (!url.equals(mUrl)) {
            LogUtils.d("URLObserver setUrl old url = " + mUrl + ",new url = " + url);
            for(URLChangeListener listener : mListeners) {
                if (listener != null) {
                    LogUtils.d("URLObserver listener onChange old url = " + mUrl + ",new url = " + url);
                    listener.onChange(mUrl, url);
                }
            }
            mUrl = url;
        }
    }

    /**
     * 获取当前观察器的url
     */
    public String getUrl() {
        return mUrl;
    }

    public void addListener(URLChangeListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(URLChangeListener listener) {
        mListeners.remove(listener);
    }

    public void clear() {
        mListeners.clear();
    }
}
