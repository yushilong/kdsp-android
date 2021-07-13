package com.qizhu.rili;

import android.os.Environment;

/**
 * 应用程序配置类，保存程序的一些配置信息
 */
public class AppConfig {

    public static final String TAG = "qizhu";

    //应用文件目录，默认在外置SD卡上
    public final static String FILE_SAVEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ysrl/";
    //用户自己的图片
    public final static String USER_IMAGE_PATH = FILE_SAVEPATH + "images/";
    //缓存图片的路径，所有用户可看
    public final static String USER_CACHE_PATH = FILE_SAVEPATH + "cache/";
    //用户使用过程中出错的日志目录
    public final static String USER_LOG_PATH = FILE_SAVEPATH + "logs/";
    //系统存储文件目录
    public final static String USER_FILE_PATH = FILE_SAVEPATH + "files/";

    /**
     * ******************后台api 配置 start*****************************
     */
    //production config
    public static String API_BASE_PRODUCT = "http://api.ishenpo.com:8080/Fortune-Calendar/";

    public static String API_BASE = BuildConfig.API_BASE;          //api请求url
    public static String API_HTTPS_BASE = BuildConfig.API_HTTPS_BASE;
    public static String API_IMG_URL = BuildConfig.API_IMG_URL;

    public static String API_IMG_THUMBNAIL = "?imageMogr2/auto-orient/";

    public static String API_URL = API_HTTPS_BASE;
    public static String API_DATA_URL = BuildConfig.API_DATA_URL;      //统计url

    public static final String API_SESSION_NAME = "session";
    public static final String API_USERID_NAME = "userId";
    public static final String API_VERSION = "3.2";

    /*********************后台api 配置 end******************************/

    /**
     * ************各个配置的key name ***************
     */
    //当前用户id
    public static final String CURRENT_USER_ID = "current_user_id";
    //当前用户session
    public static final String CURRENT_SESSION = "current_session";
    //当前用户device id
    public static final String CURRENT_DEVICE_ID = "current_device_id";
    //当前device id对应下的userId
    public static final String CURRENT_DEVICE_USER_ID = "current_device_user_id";

    //全局记录输入键盘的高度
    public static final String KEY_KEYBOARD_HEIGHT = "keyboard_height";

    /**
     * ************各个配置的key name end ***************
     */

    //图片大小，以kb结尾
    public static final int IMAGE_SIZE_IN_KB = 120;

    //最大的图片宽度设置为不超过1600
    public static final int IMAGE_SCALE_MAX_WIDTH = 1600;

    //消息轮询的间隔时间
    public static final long INTERVAL_THREAD_SLEEP = 120 * 1000;            //120s

    public static final long INTERVAL_REFRESH_TIME = 30 * 60 * 1000;        //界面刷新间隔30分钟

    public static final long UODATE_DATA_TIME_INTERVAL = 15 * 60 * 1000;    //从服务器获取数据的间隔时间

    public static final long INTERVAL_CLICK_TIME = 500;                     //应用全局的单击判断生效时间，以此时间判断生效
    public static final int DELAY_DISPLAY_LOADING_TOAST = 1000;             //加载数据提示的延迟时间
    public static final int DELAY_GET_UNREAD = 60000;                       //获取未读提示数字的延迟时间

    public static final String DEVICE_TYPE = "2";    //设备类型（ios => 1；Android => 2 ）

    //自有id的aliasType
    public static final String ALIAS_USER_ID = "userId";

    //判断是否需要登录的键值
    public static final String CONFIG_NEED_LOGIN = "config_need_login";

    //用户进入应用的跳转方向
    public static final String CONFIG_REDIRECT_DIRECTION = "config_redirect_direction";

    //外部app下载标示头部
    public static final String CONFIG_EXTERNAL_APP_TAG = "external_app_";
}
