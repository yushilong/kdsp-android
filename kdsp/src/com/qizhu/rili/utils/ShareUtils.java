package com.qizhu.rili.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.listener.ShareListener;
import com.qizhu.rili.ui.activity.BaseActivity;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lindow on 2015/5/19.
 * 分享工具类
 */
public class ShareUtils {
    public static final int Share_Type_ANALYSIS_SELF = 1;       //个人分析命格
    public static final int Share_Type_STAR_SELF = 2;           //个人分析星宿
    public static final int Share_Type_GOOD_DAY = 3;            //吉日结果
    public static final int Share_Type_DAY_DETAIL = 4;          //日子的详情
    public static final int Share_Type_ANALYSIS_FRIENDS = 5;    //分享小伙伴分析
    public static final int Share_Type_WebView = 6;             //分享webView
    public static final int Share_Type_APP = 7;                 //分享应用
    public static final int Share_Type_DAILY = 8;               //分享神婆日报
    public static final int Share_TYPE_Friends_LIFE = 9;        //分享小伙伴命格
    public static final int Share_TYPE_Friends_STAR = 10;       //分享小伙伴星宿
    public static final int Share_Type_MY_SHADOW = 11;          //分享个人上一世的影子
    public static final int Share_Type_MY_FEELINGS = 12;        //分享个人的感情天赋
    public static final int Share_Type_FRIENDS_SHADOW = 13;     //分享小伙伴的上一世的影子
    public static final int Share_Type_FRIENDS_FEELINGS = 14;   //分享小伙伴的感情天赋
    public static final int Share_Type_CALENDAR = 15;           //分享日历
    public static final int Share_Type_MY_LIFETIME = 16;        //分享个人的一生
    public static final int Share_Type_FRIENDS_LIFETIME = 17;   //分享小伙伴的一生
    public static final int Share_Type_CEZI = 18;               //分享测字
    public static final int Share_Type_CESHOUXIANG = 19;        //分享测手相
    public static final int Share_Type_PRAY_STICKS = 20;        //签文分享
    public static final int Share_Type_LOVE_LINE = 21;          //缘来如此
    public static final int Share_Type_ORDER_DETAIL = 22;       //订单详情
    public static final int Share_Type_QR_CODE = 23;            //二维码
    public static final int Share_Type_HAND = 24;               //手相
    public static final int Share_Type_FACE = 25;               //面相
    public static final int Share_Type_Goods = 26;              //商品

    public static final String Share_Title = "title";
    public static final String Share_Type = "type";
    public static final String Share_Content = "content";
    public static final String Share_Link = "shareLink";
    public static final String Share_Image = "image";
    public static final String Share_Platform = "Share_Platform";
    public static final String Share_Statistics_Type = "share_statistics_type";
    public static final String Share_Statistics_SubType = "share_statistics_subtype";
    public static final String Share_Path = "share_path";

    public static final int CLICK_APP = 6;                      //推荐应用点击
    public static final int CLICK_WEIXIN = 101;                 //分享微信
    public static final int CLICK_WEIXIN_SUCCESS = 102;         //分享微信成功
    public static final int CLICK_WEIXIN_SCENE = 103;           //分享微信朋友圈
    public static final int CLICK_WEIXIN_SCENE_SUCCESS = 104;   //分享微信朋友圈成功
    public static final int CLICK_WEIBO = 105;                  //分享微博
    public static final int CLICK_WEIBO_SUCCESS = 106;          //分享微博成功
    public static final int CLICK_QZONE = 107;                  //分享qq空间
    public static final int CLICK_QZONE_SUCCESS = 108;          //分享qq空间成功
    public static final int CLICK_QQ = 109;                     //分享qq
    public static final int CLICK_QQ_SUCCESS = 110;             //分享qq成功

    public static final String SHARE_QQ_URL = "f=share-qq";
    public static final String SHARE_QZONE_URL = "f=share-qzone";
    public static final String SHARE_WEIXIN_URL = "f=share-weixin";
    public static final String SHARE_WXTIMELINE_URL = "f=share-wxtimeline";
    public static final String SHARE_WEIBO_URL = "f=share-weibo";
    public static final String SHARE_QR = "f=erweima";

    //微信分享种类，区分微信好友与朋友圈
    public static int mWeixinType = 0;

    public static String DEFAULT_URL = "http://www.ishenpo.com";
    public static String DEFAULT_DOWNLOAD_URL = "http://h5.ishenpo.com/s/UrAJbu";//http://um0.cn/3a5fy8";
    public static String DEFAULT_IMAGE = "http://pt.qi-zhu.com/qizhulogo.png";
    public static String BASE_SHARE_URL = AppConfig.API_BASE + "app/share/shareUrl";

    /**
     * 分享到微信
     * 微信分享有专门的回调函数，写在基类activity中
     *
     * @param mergeTitle 是否合并标题,主要用于朋友圈的分享
     */
    public static void shareToWeixin(BaseActivity activity, Map<String, String> paramsMap, int scene, boolean mergeTitle, int mStatisticsType, String mStatisticSubType) {
        String webUrl = DEFAULT_URL;
        if (paramsMap.containsKey(Share_Link) && paramsMap.get(Share_Link) != null) {
            webUrl = paramsMap.get(Share_Link).length() > 4 ? paramsMap.get(Share_Link) : webUrl;
        }
        String content = paramsMap.get(Share_Content);
        content = !TextUtils.isEmpty(content) ? content : "神婆推荐";
        String imageUrl = "";
        if (paramsMap.containsKey(Share_Image) && paramsMap.get(Share_Image) != null) {
            imageUrl = paramsMap.get(Share_Image);
        }

        WeixinUtils.getInstance().sendWxReq(webUrl, paramsMap.get(Share_Title), content, imageUrl, scene, activity, mergeTitle);


            ShareUtils.shareClick(mStatisticsType, StatisticsConstant.SOURCE_WEIXIN, mStatisticSubType);

        mWeixinType = scene;
    }

    /**
     * 分享到微信
     * 微信分享有专门的回调函数，写在基类activity中
     *
     * @param mergeTitle 是否合并标题,主要用于朋友圈的分享
     */
    public static void shareToWeixinMini(BaseActivity activity, Map<String, String> paramsMap, int scene, boolean mergeTitle, int mStatisticsType, String mStatisticSubType) {
        String webUrl = DEFAULT_URL;
        if (paramsMap.containsKey(Share_Link) && paramsMap.get(Share_Link) != null) {
            webUrl = paramsMap.get(Share_Link).length() > 4 ? paramsMap.get(Share_Link) : webUrl;
        }
        String content = paramsMap.get(Share_Content);
        content = !TextUtils.isEmpty(content) ? content : "神婆推荐";
        String imageUrl = "";
        if (paramsMap.containsKey(Share_Image) && paramsMap.get(Share_Image) != null) {
            imageUrl = paramsMap.get(Share_Image);
        }
        WeixinUtils.getInstance().sendWxMiniReq(webUrl, paramsMap.get(Share_Title), content, imageUrl, scene, activity, mergeTitle,paramsMap.get(Share_Path));

        ShareUtils.shareClick(mStatisticsType, StatisticsConstant.SOURCE_WEIXIN, mStatisticSubType);

        mWeixinType = scene;
    }

    /**
     * 分享到微信
     * 微信分享有专门的回调函数，写在基类activity中
     *
     * @param bitmap     bitmap为分享的图片
     * @param mergeTitle 是否合并标题,主要用于朋友圈的分享
     */
    public static void shareToWeixin(BaseActivity activity, Map<String, String> paramsMap, int scene, Bitmap bitmap, boolean mergeTitle, int mStatisticsType, String mStatisticSubType) {
        String webUrl = DEFAULT_URL;
        if (paramsMap.containsKey(Share_Link) && paramsMap.get(Share_Link) != null) {
            webUrl = paramsMap.get(Share_Link).length() > 4 ? paramsMap.get(Share_Link) : webUrl;
        }
        String content = paramsMap.get(Share_Content) != null ? paramsMap.get(Share_Content) : "神婆推荐";
        String title = paramsMap.get(Share_Title) != null ? paramsMap.get(Share_Title) : "神婆推荐";
        String imageUrl = "";
        if (paramsMap.containsKey(Share_Image) && paramsMap.get(Share_Image) != null) {
            imageUrl = paramsMap.get(Share_Image);
        }
        //以图片为优先
        if (bitmap != null) {
            WeixinUtils.getInstance().sendWxBitmapReq(title, content, scene, activity, bitmap, mergeTitle);
        } else {
            if (!TextUtils.isEmpty(imageUrl)) {
                WeixinUtils.getInstance().sendWxReq(webUrl, paramsMap.get(Share_Title), content, imageUrl, scene, activity, mergeTitle);
            } else {
                WeixinUtils.getInstance().sendWxUrlReq(webUrl, paramsMap.get(Share_Title), content, scene, activity, mergeTitle);
            }
        }

        if (scene == SendMessageToWX.Req.WXSceneSession) {
            ShareUtils.shareClick(mStatisticsType, StatisticsConstant.SOURCE_WEIXIN, mStatisticSubType);
        } else {
            ShareUtils.shareClick(mStatisticsType, StatisticsConstant.SOURCE_WEIXIN_TIMELINE, mStatisticSubType);
        }
        mWeixinType = scene;
    }

    /**
     * 分享到QQ空间,回调为异步
     */
    public static void shareToQZone(final BaseActivity activity, Map<String, String> paramsMap, final ShareListener listener, int mStatisticsType, String mStatisticSubType) {
        ShareUtils.shareClick(mStatisticsType, StatisticsConstant.SOURCE_QZONE, mStatisticSubType);
        SSOTencentUtils.shareToQZone(activity, paramsMap, new SSOTencentUtils.ShareTencentListener() {
            @Override
            public void onComplete(Object response) {
                UIUtils.toastMsg("分享成功");
                if (listener != null) {
                    listener.shareSuccess();
                }
            }

            @Override
            public void onError() {
                UIUtils.toastMsg("分享失败，请稍后再试~");
                if (listener != null) {
                    listener.shareFailed();
                }
            }

            @Override
            public void onCancel() {

            }
        });
    }

    /**
     * 分享到QQ，回调为异步
     */
    public static void shareToQQ(final BaseActivity activity, Map<String, String> paramsMap, final ShareListener listener, int mStatisticsType, String mStatisticSubType) {
        ShareUtils.shareClick(mStatisticsType, StatisticsConstant.SOURCE_QQ, mStatisticSubType);
        SSOTencentUtils.shareToQQ(activity, paramsMap, new SSOTencentUtils.ShareTencentListener() {
            @Override
            public void onComplete(Object response) {
                UIUtils.toastMsg("分享成功");
                if (listener != null) {
                    listener.shareSuccess();
                }
            }

            @Override
            public void onError() {
                UIUtils.toastMsg("分享失败，请稍后再试~");
                if (listener != null) {
                    listener.shareFailed();
                }
            }

            @Override
            public void onCancel() {

            }
        });
    }

    /**
     * 分享到微博
     */
    public static void shareToWeiBo(final BaseActivity activity, Map<String, String> paramsMap, int mStatisticsType, String mStatisticSubType) {
        ShareUtils.shareClick(mStatisticsType, StatisticsConstant.SOURCE_WEIBO, mStatisticSubType);
        String webUrl = DEFAULT_URL;
        if (paramsMap.containsKey(Share_Link) && paramsMap.get(Share_Link) != null) {
            webUrl = paramsMap.get(Share_Link).length() > 4 ? paramsMap.get(Share_Link) : webUrl;
        }
        String title = paramsMap.get(Share_Title) != null ? paramsMap.get(Share_Title) : "";
        String content = paramsMap.get(Share_Content);
        content = !TextUtils.isEmpty(content) ? content : "";
        String imageUrl = "";
        if (paramsMap.containsKey(Share_Image) && paramsMap.get(Share_Image) != null) {
            imageUrl = paramsMap.get(Share_Image);
        }
        SSOSinaUtils.getInstance().sendMessage(activity, title, content, imageUrl, webUrl);
    }

    /**
     * 分享图片到微博
     */
    public static void shareToWeiBoImage(final BaseActivity activity, Map<String, String> paramsMap, int mStatisticsType, String mStatisticSubType) {
        ShareUtils.shareClick(mStatisticsType, StatisticsConstant.SOURCE_WEIBO, mStatisticSubType);
        String webUrl = paramsMap.get(Share_Link) != null ? paramsMap.get(Share_Link) : "";
        String title = paramsMap.get(Share_Title) != null ? paramsMap.get(Share_Title) : "";
        String content = paramsMap.get(Share_Content) != null ? paramsMap.get(Share_Content) : "";

        String imageUrl = "";
        if (paramsMap.containsKey(Share_Image) && paramsMap.get(Share_Image) != null) {
            imageUrl = paramsMap.get(Share_Image);
        }
        SSOSinaUtils.getInstance().sendMessage(activity, title, content, imageUrl, webUrl);
    }

    /**
     * 分享图片到QQ，回调为异步
     */
    public static void shareToQQImage(final BaseActivity activity, String imagePath, boolean isQZone, final ShareListener listener, int mStatisticsType, String mStatisticSubType) {
        ShareUtils.shareClick(mStatisticsType, StatisticsConstant.SOURCE_QQ, mStatisticSubType);
        SSOTencentUtils.shareToQQImage(activity, imagePath, isQZone, new SSOTencentUtils.ShareTencentListener() {
            @Override
            public void onComplete(Object response) {
                UIUtils.toastMsg("分享成功");
                if (listener != null) {
                    listener.shareSuccess();
                }
            }

            @Override
            public void onError() {
                UIUtils.toastMsg("分享失败，请稍后再试~");
                if (listener != null) {
                    listener.shareFailed();
                }
            }

            @Override
            public void onCancel() {

            }
        });
    }

    /**
     * 分享统计
     */
    public static void shareClick(int mStatisticsType, int mStatisticsSource, String mStatisticSubType) {
        KDSPApiController.getInstance().addStatistics(mStatisticsSource, mStatisticsType, mStatisticSubType, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {

            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {

            }
        });
    }

    /**
     * 对hashMap的深度克隆
     */
    public static HashMap<String, String> cloneHashMap(HashMap<String, String> paramsMap) {
        HashMap<String, String> hashmap = new HashMap<String, String>();
        for (String key : paramsMap.keySet()) {
            hashmap.put(key, paramsMap.get(key));
        }
        return hashmap;
    }

    /**
     * 获取分享的标题
     */
    public static String getShareTitle(int shareType, String title) {
        String result = title;
        switch (shareType) {
            case Share_Type_APP:
                result = "藏在你口袋里的专属神婆";
                break;
            case Share_Type_DAY_DETAIL:
                result = "超准的运势提醒：" + title;
                break;
            case Share_Type_GOOD_DAY:
                result = "今天不" + title + "，一天都白瞎";
                break;
            case Share_Type_ANALYSIS_SELF:
                result = "命格全解析，就在口袋神婆";
                break;
            case Share_Type_STAR_SELF:
                result = "用五行分析你的宜和忌？";
                break;
            case Share_Type_ANALYSIS_FRIENDS:
                result = "最潮的算命爱皮皮，李易峰都在用，小伙伴你不来算一下？  @口袋神婆";
                break;
            case Share_Type_MY_SHADOW:
                result = "原来上辈子我居然是这样的人！";
                break;
            case Share_Type_MY_FEELINGS:
                result = "你是不是恋爱达人？看看你有什么特别的恋爱技巧";
                break;
            case Share_Type_FRIENDS_SHADOW:
                result = "原来上辈子TA居然是这样的人！";
                break;
            case Share_Type_FRIENDS_FEELINGS:
                result = "每个恋爱达人都有独特的恋爱技巧，TA居然有这些感情天赋？";
                break;
            case Share_Type_CALENDAR:
                result = "吉日日历，让你日日都美丽";
                break;
            case Share_Type_MY_LIFETIME:
                result = "隐藏在你一生的秘密";
                break;
            case Share_Type_FRIENDS_LIFETIME:
                result = "隐藏在TA一生的秘密";
                break;
            case Share_Type_CEZI:
                result = "免费测字，准到你心里";
                break;
            case Share_Type_CESHOUXIANG:
                result = "免费看手相，准到你心里";
                break;
            case Share_Type_PRAY_STICKS:
                result = "运势怎么变，赶紧抽一签";
                break;
            case Share_Type_LOVE_LINE:
                result = "探索你和TA的前世今生";
                break;
            case Share_Type_ORDER_DETAIL:
                result = title + "口袋神婆告诉你！";
                break;
            case Share_Type_HAND:
                result = "手相中的纹路，满满都是套路";
                break;
            case Share_Type_FACE:
                result = "知人知面必然知心";
                break;
        }
        return result;
    }

    /**
     * 获取分享的内容
     */
    public static String getShareContent(int shareType, String content) {
        String result = content;
        switch (shareType) {
            case Share_Type_APP:
                result = "来自未来的超逼格神器口袋神婆，快来下载吧";
                break;
            case Share_Type_DAY_DETAIL:
                result = "居然这么准，快用我一年的膝盖来膜拜，o(∩_∩)o~";
                break;
            case Share_Type_GOOD_DAY:
                result = "口袋神婆，让你在对的时间做对的事";
                break;
            case Share_Type_ANALYSIS_SELF:
                result = "性格财运桃花运，你的一切我们都知道";
                break;
            case Share_Type_STAR_SELF:
                result = "万物相生相克，人生亦是如此 ";
                break;
            case Share_Type_ANALYSIS_FRIENDS:
                result = "来自未来的超逼格神器口袋神婆，上知天文，下知地理都不如懂你，快来下载吧";
                break;
            case Share_Type_MY_SHADOW:
                result = "超神奇APP口袋神婆，不只看穿你的今生，更能看到你不知道的过去！";
                break;
            case Share_Type_MY_FEELINGS:
                result = "原来我有这么多感情天赋，谁能抵挡我的魅力？妹子汉子都到我后宫里来~";
                break;
            case Share_Type_FRIENDS_SHADOW:
                result = "超神奇APP口袋神婆，不只看穿你的今生，更能看到你不知道的过去！";
                break;
            case Share_Type_FRIENDS_FEELINGS:
                result = "生活必备神器口袋神婆，上知天文下知地理，还知道感情里的小秘密~";
                break;
            case Share_Type_CALENDAR:
                result = "好日子、好数字、好位子，有些事早点知道比较好哦";
                break;
            case Share_Type_MY_LIFETIME:
                result = "颜色没选好，一生好不了；职业没选对，上班真心累";
                break;
            case Share_Type_FRIENDS_LIFETIME:
                result = "颜色没选好，一生好不了；职业没选对，上班真心累";
                break;
            case Share_Type_CEZI:
                result = "一个字就能知道你的运势，只在口袋神婆";
                break;
            case Share_Type_CESHOUXIANG:
                result = "易经达人胖子乐免费看手相，只在口袋神婆";
                break;
            case Share_Type_PRAY_STICKS:
                result = "缘分还需天定，神仙自有妙计，小小签文蕴含大大智慧哦";
                break;
            case Share_Type_LOVE_LINE:
                result = "寻找最旺你的贵人！茫茫人海中总有一根红线属于你";
                break;
            case Share_Type_ORDER_DETAIL:
                result = "口袋神婆全新八字算命闪亮登场，命理大师实力坐镇。只要告诉我你的八字，就能解答你的一切！！";
                break;
            case Share_Type_HAND:
                result = "欲知当下事，低头看看手；大师胖子乐解析你的手相秘密";
                break;
            case Share_Type_FACE:
                result = "胖子乐大师亲测，告诉你面相的奥秘";
                break;
        }
        return result;
    }

}
