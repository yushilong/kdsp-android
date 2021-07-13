package com.qizhu.rili.utils;

import android.media.MediaPlayer;
import android.net.Uri;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.listener.MediaStateChangedListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lindow on 2/29/16.
 * 录音工具类，控制全局语音播放
 */
public class VoiceUtils {
    private static MediaPlayer mMediaPlayer;       //用于控制全局播放
    private static String mCurrentUrl = "";        //当前正在播放的url
    private static MediaStateChangedListener mMediaStateChangedListener;       //存储一个监听器，监听器有且仅有一个足矣
    private static boolean mIsPause;                //是否处于暂停态

    /**
     * 加载语音，如果是网络路径，则下载加载，如果是本地路径，则直接读取
     * 将下载的录音文件保存在缓存图片的路径下，清除缓存的时候一并清除
     */
    public static void loadVoice(final String path) {
        AppContext.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtils.d("loadVoice " + path);
                    if (!path.isEmpty()) {
                        //先用文件名的MD5码拼接存储文件名，看文件是否已经下载
                        String mMD5path = MD5Utils.MD5(path);
                        String mFileName = FileUtils.getVoiceCacheDirPath() + "/" + mMD5path + "." + StringUtils.getFileFormat(path);
                        File file = new File(mFileName);
                        if (file.exists() && file.length() != 0) {
                            LogUtils.d("voice file exits");
                        } else {
                            if (file.exists()) {
                                file.delete();
                            }
                            //开始下载的时候先创建缓存文件
                            String mTempName = FileUtils.getVoiceCacheDirPath() + "/" + mMD5path + ".tmp";
                            File mTempFile = new File(mTempName);
                            //如果缓存文件已经存在了，那么删除之
                            if (mTempFile.exists()) {
                                mTempFile.delete();
                            }

                            String mUrl = AppConfig.API_IMG_URL + path;

                            //输出的文件流
                            FileOutputStream fos = new FileOutputStream(mTempName);

                            URL url = new URL(mUrl);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.connect();
                            //输入流
                            InputStream is = conn.getInputStream();
                            // 1K的数据缓冲
                            byte[] bs = new byte[1024];
                            // 开始读取
                            int len;
                            while ((len = is.read(bs)) != -1) {
                                fos.write(bs, 0, len);
                            }
                            //下载完毕，将缓存文件置为语音文件
                            mTempFile.renameTo(file);

                            //完毕，关闭所有链接
                            fos.close();
                            is.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 播放语音并且播放动画
     */
    public static void playVoice(final String path, MediaStateChangedListener listener) {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.reset();
                mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                        if (mediaPlayer != null) {
                            mediaPlayer.reset();
                        }

                        return false;
                    }
                });
            }
            //若当前没有监听器，则覆盖之
            if (mMediaStateChangedListener == null) {
                mMediaStateChangedListener = listener;
                mMediaPlayer.setOnCompletionListener(listener);
            }
            //如果当前正在播放，则对比url
            if (mMediaPlayer.isPlaying()) {
                //播放的是当前的url，则暂停
                if (path.equals(mCurrentUrl)) {
                    mIsPause = true;
                    mMediaPlayer.pause();
                    mMediaStateChangedListener.onPause(mCurrentUrl);
                } else {
                    //要播放另一首语音，则先关闭前面一首
                    mMediaPlayer.stop();
                    mMediaStateChangedListener.onStop(mCurrentUrl);
                    mIsPause = false;
                    //更新监听器与当前播放路径
                    mCurrentUrl = path;
                    mMediaStateChangedListener = null;
                    mMediaStateChangedListener = listener;
                    mMediaStateChangedListener.onStart(mCurrentUrl);
                    startMedia(path, listener);
                }
            } else {
                mMediaStateChangedListener = null;
                mMediaStateChangedListener = listener;
                mMediaStateChangedListener.onStart(mCurrentUrl);
                if (mIsPause) {
                    mMediaPlayer.start();
                } else if (!path.isEmpty()) {
                    startMedia(path, listener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放语音并且播放动画
     */
    public static void playVoice1(final String path, MediaStateChangedListener listener) {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.reset();
                mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                        if (mediaPlayer != null) {
                            mediaPlayer.reset();
                        }

                        return false;
                    }
                });
            }
            //若当前没有监听器，则覆盖之
            if (mMediaStateChangedListener == null) {
                mMediaStateChangedListener = listener;
                mMediaPlayer.setOnCompletionListener(listener);
            }
            //如果当前正在播放，则对比url
            if (mMediaPlayer.isPlaying()) {
                //播放的是当前的url，则暂停
                if (path.equals(mCurrentUrl)) {
                    mIsPause = true;
                    mMediaPlayer.pause();
                    mMediaStateChangedListener.onPause(mCurrentUrl);
                } else {
                    //要播放另一首语音，则先关闭前面一首
                    mMediaPlayer.stop();
                    mMediaStateChangedListener.onStop(mCurrentUrl);
                    mIsPause = false;
                    //更新监听器与当前播放路径
                    mCurrentUrl = path;
                    mMediaStateChangedListener = null;
                    mMediaStateChangedListener = listener;
                    mMediaStateChangedListener.onStart(mCurrentUrl);
                    startMediaVoice(path, listener);
                }
            } else {
                mMediaStateChangedListener = null;
                mMediaStateChangedListener = listener;
                mMediaStateChangedListener.onStart(mCurrentUrl);
                if (mIsPause) {
                    mMediaPlayer.start();
                } else if (!path.isEmpty()) {
                    startMediaVoice(path, listener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startMedia(String path, MediaStateChangedListener listener) throws IOException {
        mMediaPlayer.reset();
        //先找到对应的文件
        String mMD5path = MD5Utils.MD5(path);
        String mFileName = FileUtils.getVoiceCacheDirPath() + "/" + mMD5path + "." + StringUtils.getFileFormat(path);
        File file = new File(mFileName);
        //本地有则从本地播放，本地无则直接网络播放
        if (file.exists() && file.length() != 0) {
            LogUtils.d("voice file exits");
            mMediaPlayer.setDataSource(mFileName);
            LogUtils.d("---> startMedia mFileName " + mFileName);
        } else {
            String mUrl = AppConfig.API_IMG_URL + path;
            LogUtils.d("---> startMedia url " + mUrl);
            mMediaPlayer.setDataSource(AppContext.baseContext, Uri.parse(mUrl));
        }
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        mCurrentUrl = path;
        mMediaPlayer.setOnCompletionListener(listener);
    }

    public static void startMediaVoice(String path, MediaStateChangedListener listener) throws IOException {
        mMediaPlayer.reset();
        //先找到对应的文件
        String mMD5path = MD5Utils.MD5(path);
        String mFileName = FileUtils.getVoiceCacheDirPath() + "/" + mMD5path + "." + StringUtils.getFileFormat(path);
        File file = new File(mFileName);
        //本地有则从本地播放，本地无则直接网络播放
        if (file.exists() && file.length() != 0) {
            LogUtils.d("voice file exits");
            mMediaPlayer.setDataSource(mFileName);
            LogUtils.d("---> startMedia mFileName " + mFileName);
        } else {
            String mUrl = path;
            LogUtils.d("---> startMedia url " + mUrl);
            mMediaPlayer.setDataSource(AppContext.baseContext, Uri.parse(mUrl));
        }
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        mCurrentUrl = path;
        mMediaPlayer.setOnCompletionListener(listener);
    }

    public static void releaseMedia() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            if (mMediaStateChangedListener != null) {
                mMediaStateChangedListener.onStop(mCurrentUrl);
                mMediaStateChangedListener = null;
            }
            mIsPause = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopMedia() {
        try {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                if (mMediaStateChangedListener != null) {
                    mMediaStateChangedListener.onPause(mCurrentUrl);
                }
                mIsPause = true;
                mMediaPlayer.pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
