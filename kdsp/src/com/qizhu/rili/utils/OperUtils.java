package com.qizhu.rili.utils;

import android.app.Activity;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.AppManager;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.ui.activity.BaseActivity;
import com.qizhu.rili.ui.dialog.PointDialogFragment;

import org.json.JSONObject;

/**
 * Created by lindow on 09/12/2016.
 * 评分工具类
 */

public class OperUtils {
    public static String BIG_CAT_SHARE = "share";           //分享大类
    public static String BIG_CAT_COMMENT = "comment";       //评论大类
    public static String SMALL_CAT_TEST = "test";           //测试题小类
    public static String SMALL_CAT_ITEM = "item";           //问题小类
    public static String SMALL_CAT_FONT = "font";           //测字小类
    public static String SMALL_CAT_SHAKE = "shake";         //摇签小类
    public static String SMALL_CAT_OTHER = "other";         //other小类

    public static String KEY_CAT_APP = "share_kdsp";                            //分享app
    public static String KEY_CAT_FORTUNE_IMG = "share_fortune_img";             //分享运势图片
    public static String KEY_CAT_STAR_IMG = "share_constellation_img";          //分享星宿图片
    public static String KEY_CAT_NO_SURE = "share_no_sure";                     //无固定入口
    public static String KEY_CAT_CALENDAR = "share_calendar";                   //日历界面
    public static String KEY_CAT_FRIEND_STAR = "share_friend_constellation";    //分析小伙伴星宿界面
    public static String KEY_CAT_FRIEND_MINGGE = "share_friend_mingge";         //分析小伙伴命格界面
    public static String KEY_CAT_FRIEND_LIFE = "share_friend_life";             //分析小伙伴星宿一生
    public static String KEY_CAT_FRIEND_SHADOW = "share_friend_shadow";         //分析小伙伴星宿影子
    public static String KEY_CAT_GOOD_DATE = "share_good_date";                 //吉日分享
    public static String KEY_CAT_PALM = "share_ palm";                          //手相（一线二线….）
    public static String KEY_CAT_LOVE = "share_yuanlairuci";                    //缘来如此

    public static String mSmallCat = "";
    public static String mKeyCat = "";

    public static boolean mShouldOper = false;

    /**
     * 分享评论送积分接口
     *
     * @param bigCat   大类：分享传share,评论传comment
     * @param smallCat 小类，测试题分享传test,问题分享传item，其它分享传other。若大类为comment则传android
     * @param keyCat   每个分享的唯一标示，若为comment则传userid
     */
    public static void oper(String bigCat, String smallCat, String keyCat) {
        KDSPApiController.getInstance().oper(bigCat, smallCat, keyCat, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                final int point = response.optInt("point");
                if (point > 0) {
                    AppContext.mPointSum = response.optInt("pointSum");
                    Activity activity = AppManager.getAppManager().currentActivity();
                    if (activity instanceof BaseActivity) {
                        ((BaseActivity) activity).showDialogFragment(PointDialogFragment.newInstance(point), "领取福豆对话框");
                    }
                }
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {

            }
        });
    }
}
