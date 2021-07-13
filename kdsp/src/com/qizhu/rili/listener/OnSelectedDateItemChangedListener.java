package com.qizhu.rili.listener;

import com.qizhu.rili.bean.DateTime;

/**
 * Created by lindow on 15/9/8.
 * 选择日期发生更改的监听器
 */
public interface OnSelectedDateItemChangedListener {
    void onSelectedDateItemChanged(DateTime oldDate, DateTime newDate);
}
