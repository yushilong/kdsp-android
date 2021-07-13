package com.qizhu.rili.listener;

import android.view.View;

import com.qizhu.rili.AppConfig;

/**
 * Created by Lindow on 2015/2/5.
 * 封装点击事件，防止重复点击
 */
public abstract class OnSingleClickListener implements View.OnClickListener {
    private static long lastClickTime = 0;      //上次点击的时间

    @Override
    public void onClick(View view) {
        if (System.currentTimeMillis() - lastClickTime > AppConfig.INTERVAL_CLICK_TIME) {
            lastClickTime = System.currentTimeMillis();
            onSingleClick(view);
        }
    }

    abstract public void onSingleClick(View v);
}
