package com.qizhu.rili;

/**
 * Created by lindow on 15/6/29.
 * 数据配置项
 */
public class YSRLConstants {
    public static final String FIRST_ENTER = "isFirst_66";                          //是否初次进入,后面加入version code来区分版本
    public static final String HAS_ENTER_INFO = "has_enter_info";                   //是否已经填写生日等信息
    public static final String CONFIG_KEY_INIT = "config_key_init";                 //是否应该init
    public static final String SERVICE_POLICY_AGREE = "service_policy_agree";                 //是否同意协议
    public static final String QINIU_TOKEN = "qiniu_token";                         //七牛的上传token
    public static final String EVENT_SEARCH_VERSION = "event_search_version";       //吉日搜索的verison
    public static final String DISPLAY_SPLASH = "display_splash";                   //显示的splash id
    public static final String NEED_CLEAR_WEBVIEW_HISTORY = "need_clear_webview_history";       //需要清除webview的历史记录
    public static final String NEED_CLEAR_WEBVIEW_CACHE = "need_clear_webview_cache";           //需要清除webview的缓存
    public static final String SHOW_MAIN_GUIDE = "show_main_guide_53";              //显示主页的引导图,后面加入version code来区分版本
    public static final String SIGN_ONLINE = "sign_online";                         //是否上线签到和测手相功能，0是下线，1是上线
    public static final String SIGN_NO_READ = "sign_no_read";                       //签到的未读消息数
    public static final String CAROUSEL_JSON_32 = "carousel_json_32";               //保存当前的轮播图json，数据量比较小，就不用数据库了
    public static final String PRAY_LOVE_TIME_32 = "pray_love_time_32";             //月老签的求签时间
    public static final String PRAY_CAREER_TIME_32 = "pray_career_time_32";         //事业签的求签时间
    public static final String PRAY_WEALTH_TIME_32 = "pray_wealth_time_32";         //财富签的求签时间
    public static final String SHOW_VOICE_TIP_48 = "show_voice_tip_48";             //语音提示

    //关键的user信息，性别和生日
    public static final String USER_SEX = "user_sex";                               //用户性别
    public static final String USER_BIRTH = "user_birth";                           //用户生日
    public static final String USER_BIRTH_MODE = "user_birth_mode";                 //用户生日的模式，1为阳历2为阴历
    public static final String CEZI_TIMES = "cezi_times";                           //测字的剩余次数
    public static final String CESHOUXIANG_TIMES = "ceshouxiang_times";             //测手相的剩余次数
    public static final String SHOW_SIGN_GUIDE = "show_sign_guide";                 //显示签到的引导
    public static final String SHOW_CESHOUXIANG_GUIDE = "show_ceshouxiang_guide";   //显示测手相的引导
    public static final String SHOW_SIGN_TIMEOUT_GUIDE = "show_sign_timeout_guide"; //显示测字提交倒计时的引导
    public static final String SHOW_RENYICE_GUIDE = "show_renyice_guide";           //显示任意测的引导

    public static final String WEIXIN_PAY = "weixin_pay";                           //微信支付
    public static final String ALIPAY = "alipay";                                   //支付宝支付
    public static final String CANCEL_PAY = "cancel_pay";                           //取消支付

    //
    public static final String LOGIN_PHONE_NUMBER = "login_phone_number";           //登录手机号
    public static final String LOGIN_PHONE_PASSWORD = "login_phone_password";       //登录密码
}
