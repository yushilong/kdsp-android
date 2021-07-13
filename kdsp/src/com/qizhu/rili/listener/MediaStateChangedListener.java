package com.qizhu.rili.listener;

import android.media.MediaPlayer;

/**
 * Created by lindow on 4/5/16.
 * 媒体文件播放状态变更监听器,播放结束的接口为onCompletion
 */
public interface MediaStateChangedListener extends MediaPlayer.OnCompletionListener{
    /**
     * @param url 开始的url
     */
    void onStart(String url);
    /**
     * @param url 暂停的url
     */
    void onPause(String url);
    /**
     * @param url 停止播放的url
     */
    void onStop(String url);
}
