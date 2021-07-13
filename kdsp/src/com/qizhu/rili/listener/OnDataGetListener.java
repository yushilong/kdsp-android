package com.qizhu.rili.listener;

import com.qizhu.rili.data.DataMessage;

/**
 * 获取数据后的回调接口
 */
public interface OnDataGetListener<T> {
    void onGetData(DataMessage<T> dataMessage);
}
