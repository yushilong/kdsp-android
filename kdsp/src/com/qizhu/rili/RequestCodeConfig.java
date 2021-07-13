package com.qizhu.rili;

/**
 * request code配置类
 * 全部写在这里
 */
public class RequestCodeConfig {
    //请求相机
    public static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 1;
    //请求相册
    public static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 2;
    //登陆之后返回
    public static final int REQUEST_CODE_LOGIN = 3;
    //webView选择文件
    public static final int REQUEST_CODE_WEBVIEW_CHOOSE = 4;
    //webView选择多个文件,支持android 5.0以上
    public static final int REQUEST_CODE_WEBVIEW_CHOOSE_LOLLIPOP = 5;
    //用户绑定开放平台后直接发布，不用用户再次点击分享
    public static final int REQUEST_DIRECT_SHARE_AUTH = 6;
    //刷新页面
    public static final int REQUEST_FRESH = 7;
    //选择完毕地址页面
    public static final int REQUEST_SELECT_ADDRESS = 8;
    //绑定手机成功
    public static final int REQUEST_BIND_PHONE_SUCCESS = 9;

    public static final int REQUEST_BIND_collect = 10;
}
