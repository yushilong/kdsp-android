package com.qizhu.rili.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.FileUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.MD5Utils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.VoiceUtils;

import java.io.File;
import java.io.IOException;

/***
 * 2017/9/25
 * @author zhouyue
 * 音乐播放服务
 */

public class PlayerService extends Service {
    private MediaPlayer mediaPlayer; // 媒体播放器对象
    private String      path;            // 音乐文件路径
    private boolean     isPause;        // 暂停状态
    private int duration;            //播放长度
    public         boolean isPlaying   = false;
    private static String  mCurrentUrl = "";        //当前正在播放的url


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("service", "service created");
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    if (mediaPlayer != null) {
                        mediaPlayer.reset();
                    }

                    return false;
                }
            });
        }

    }


    public class PlayBinder extends Binder {
        public PlayerService getPlayService() {
            return PlayerService.this;
        }
    }


    public void start() {

        LogUtils.d("---->mediaPlayer.start()");
        try {

//            if (path.equals(mCurrentUrl)) {
            mediaPlayer.start();
//            } else {
//                mCurrentUrl = path;
//                startMediaVoice(path);
//            }

            isPlaying = true;
        } catch (Exception e) {

        }

    }

    public void setTo(int msec) {
        mediaPlayer.seekTo(msec);
    }

    public int getDuration() {
        return duration;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void startMediaVoice(String path) throws IOException {

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

    }


    @Override
    public IBinder onBind(Intent arg0) {
        LogUtils.d("---->onBind");

        return new PlayBinder();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d("---->onStartCommand");


//        if (path.equals(mCurrentUrl)) {
//            if(mediaPlayer.isPlaying()){
//                LogUtils.d("---> ACTION_VOICE_POSITION service " );
//                BroadcastUtils.sendVoiceTimeBroadcast(BroadcastUtils.ACTION_VOICE_POSITION,mediaPlayer.getCurrentPosition());
//                mediaPlayer.start();
//            }
//            LogUtils.d("---> ACTION_VOICE_TIME service "+mediaPlayer.getDuration() );
//            BroadcastUtils.sendVoiceTimeBroadcast(BroadcastUtils.ACTION_VOICE_TIME,mediaPlayer.getDuration());
//            return super.onStartCommand(intent, flags, startId);
//        } else {
//            mCurrentUrl = path;
//            mediaPlayer.stop();
//        }

        //先找到对应的文件
        try {
            path = intent.getStringExtra("url");        //歌曲路径
            VoiceUtils.loadVoice(path);
            mediaPlayer.reset();
            String mMD5path = MD5Utils.MD5(path);
            String mFileName = FileUtils.getVoiceCacheDirPath() + "/" + mMD5path + "." + StringUtils.getFileFormat(path);
            File file = new File(mFileName);
            //本地有则从本地播放，本地无则直接网络播放
            if (file.exists() && file.length() != 0) {
                LogUtils.d("voice file exits");
                mediaPlayer.setDataSource(mFileName);
                LogUtils.d("---> startMedia mFileName " + mFileName);
            } else {
                String mUrl = path;
                LogUtils.d("---> startMedia url " + mUrl);
                mediaPlayer.setDataSource(AppContext.baseContext, Uri.parse(mUrl));
            }
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    duration = mediaPlayer.getDuration();
//                    if(mediaPlayer.isPlaying()){
//                        LogUtils.d("---> ACTION_VOICE_POSITION service " );
//                        BroadcastUtils.sendVoiceTimeBroadcast(BroadcastUtils.ACTION_VOICE_POSITION,mediaPlayer.getCurrentPosition());
//                    }
                    LogUtils.d("---> ACTION_VOICE_TIME service " + mediaPlayer.getDuration());
                    BroadcastUtils.sendVoiceTimeBroadcast(BroadcastUtils.ACTION_VOICE_TIME, mediaPlayer.getDuration());
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    LogUtils.d("---> setOnCompletionListener service ");
                    BroadcastUtils.sendVoiceTimeBroadcast(BroadcastUtils.ACTION_VOICE_STOP, 0);
                }
            });

        } catch (Exception e) {

        }


        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 播放音乐
     *
     * @param currentTime
     */
    public void play(int currentTime) {
        try {
            mediaPlayer.reset();// 把各项参数恢复到初始状态
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare(); // 进行缓冲
            mediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));// 注册一个监听器
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停音乐
     */
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    private void resume() {
        if (isPause) {
            mediaPlayer.start();
            isPause = false;
        }
    }


    /**
     * 停止音乐
     */
    private void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * 实现一个OnPrepareLister接口,当音乐准备好的时候开始播放
     */
    private final class PreparedListener implements OnPreparedListener {
        private int currentTime;

        public PreparedListener(int currentTime) {
            this.currentTime = currentTime;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            if (isPlaying) {
                mediaPlayer.start(); // 开始播放
            }


            if (currentTime > 0) { // 如果音乐不是从头播放
                mediaPlayer.seekTo(currentTime);
            }

        }
    }


}
