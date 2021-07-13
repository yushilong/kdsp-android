package com.qizhu.rili.adapter;

import android.view.View;

/**
 * @创建者 Administrator
 * @创建时间 2016/7/5 14:30
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 2016/7/5$
 * @描述 ${TODO}
 */
public interface PagerItemAdapter {
    int getCount();
    View getView(int index);
}
