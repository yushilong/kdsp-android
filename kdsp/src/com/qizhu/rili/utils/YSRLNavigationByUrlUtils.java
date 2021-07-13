package com.qizhu.rili.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.OrderDetail;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.service.YSRLService;
import com.qizhu.rili.ui.activity.AuguryDetailActivity;
import com.qizhu.rili.ui.activity.AugurySubmitActivity;
import com.qizhu.rili.ui.activity.BaZiActivity;
import com.qizhu.rili.ui.activity.BaseActivity;
import com.qizhu.rili.ui.activity.CalendarActivity;
import com.qizhu.rili.ui.activity.CalendarGoodActivity;
import com.qizhu.rili.ui.activity.CartListActivity;
import com.qizhu.rili.ui.activity.DefaultPageActivity;
import com.qizhu.rili.ui.activity.FateCatListActivity;
import com.qizhu.rili.ui.activity.FeedBackListActivity;
import com.qizhu.rili.ui.activity.FengShuiReportActivity;
import com.qizhu.rili.ui.activity.GoodsDetailActivity;
import com.qizhu.rili.ui.activity.GoodsListActivity;
import com.qizhu.rili.ui.activity.HandsOrFaceOrderActivity;
import com.qizhu.rili.ui.activity.HandsOrFaceOrderDetailActivity;
import com.qizhu.rili.ui.activity.HotAskListActivity;
import com.qizhu.rili.ui.activity.InferringBloodActivity;
import com.qizhu.rili.ui.activity.InferringWordActivity;
import com.qizhu.rili.ui.activity.LifeNumberActivity;
import com.qizhu.rili.ui.activity.LoginActivity;
import com.qizhu.rili.ui.activity.LoveLineActivity;
import com.qizhu.rili.ui.activity.LoveLineSettingActivity;
import com.qizhu.rili.ui.activity.MainActivity;
import com.qizhu.rili.ui.activity.MasterAskActivity;
import com.qizhu.rili.ui.activity.MasterAskDetailActivity;
import com.qizhu.rili.ui.activity.MasterAuguryActivity;
import com.qizhu.rili.ui.activity.MasterAuguryCartActivity;
import com.qizhu.rili.ui.activity.MemberShipCarListActivity;
import com.qizhu.rili.ui.activity.MessageListActivity;
import com.qizhu.rili.ui.activity.MyLifeActivity;
import com.qizhu.rili.ui.activity.OrderListActivity;
import com.qizhu.rili.ui.activity.PrayActivity;
import com.qizhu.rili.ui.activity.PrayListActivity;
import com.qizhu.rili.ui.activity.ReplyCommentActivity;
import com.qizhu.rili.ui.activity.ReplyListActivity;
import com.qizhu.rili.ui.activity.SetFriendsInfoActivity;
import com.qizhu.rili.ui.activity.SettingActivity;
import com.qizhu.rili.ui.activity.ShakeActivity;
import com.qizhu.rili.ui.activity.ShareActivity;
import com.qizhu.rili.ui.activity.TakeHandsPhotoActivity;
import com.qizhu.rili.ui.activity.TestNameActivity;
import com.qizhu.rili.ui.activity.TodayDetailActivity;
import com.qizhu.rili.ui.activity.WeChatCouponsActivity;
import com.qizhu.rili.ui.activity.WordRewardListActivity;
import com.qizhu.rili.ui.activity.YSRLWebActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 根据传入的url进行界面跳转的工具类
 */
public class YSRLNavigationByUrlUtils {
    private static       Map<String, Integer> navigationMap       = new HashMap<String, Integer>();
    private final static int                  MAIN                = 1;                       //主页面
    private final static int                  LOGIN               = 2;                      //登陆页面
    private final static int                  BROWSER_LINK        = 3;               //外部浏览器打开的链接
    private final static int                  SHARE               = 4;                      //分享
    private final static int                  GOOD_SEARCH         = 5;                //吉日搜索
    private final static int                  POCKET              = 6;                     //口袋
    private final static int                  MINE                = 7;                       //我的
    private final static int                  FEEDBACK            = 8;                   //意见反馈
    private final static int                  CALENDAR            = 9;                   //日历
    private final static int                  TODAY_DETAIL        = 10;              //今日详情
    private final static int                  ANALYSIS_FRIEND     = 11;           //分析小伙伴
    private final static int                  TEST                = 12;                      //测试题
    private final static int                  SETTING             = 13;                   //设置
    private final static int                  WISH_LIST           = 14;                 //许愿清单
    private final static int                  SHAKE               = 15;                     //神婆摇一摇界面
    private final static int                  SHAKE_STICKS        = 16;              //摇签界面
    private final static int                  STICKS_RECORD       = 17;             //签文记录界面
    private final static int                  SIGN                = 18;                      //算算的签到页面
    private final static int                  RELATION_ANALYSIS   = 19;         //缘来如此输入界面
    private final static int                  RELATION_RESULT     = 20;           //缘来如此结果界面
    private final static int                  INFERRING_WORD      = 21;            //测字
    private final static int                  FATE_CAT            = 22;                  //占卜分类
    private final static int                  ORDER_DETAIL        = 23;              //订单详情
    private final static int                  WEBVIEW_LINK        = 24;              //以webview的形式打开链接
    private final static int                  ORDER_SUBMIT        = 25;              //提交订单
    private final static int                  HANDS_OR_FACE_ORDER = 26;       //手相或者面相下单页面
    private final static int                  CONVERT_POINT       = 27;             //福豆兑换
    private final static int                  MASTER_AUGUR        = 28;              //大师亲算
    private final static int                  GOODS_LIST          = 29;                   //开运物品
    private final static int                  MASTER_ASK          = 30;                   //大师问答
    private final static int                  GOODS_DETAIL        = 31;                 //商品详情
    private final static int                  CART                = 32;                         //购物车
    private final static int                  ORDER_LIST          = 33;                   //订单页面(待付款，待发货，待收货)
    private final static int                  COMPLETED_ORDER     = 34;              //已完成订单页面
    private final static int                  CHAT_MSG            = 35;                     //买家留言
    private final static int                  CUSTOMER_REPLY_LIST = 36;          //客服回复列表
    private final static int                  CUSTOMER_REPLY_USER = 37;          //客服回复用户界面
    private final static int                  LIFE_NUM            = 38;          //生命灵数界面
    private final static int                  ANALYSE_NAME        = 39;          //测名 起名
    private final static int                  EIGHTFONT_LIFE      = 40;          //八字论命
    private final static int                  BLOOD               = 41;          //血型
    private final static int                  MYELEMENT           = 42;          //我的五行
    private final static int                  MYLIFE              = 43;          //我的命格
    private final static int                  VIPCARD             = 44;          //我的会员卡
    private final static int                  FENGSHUIREPORT      = 45;          //风水报告
    private final static int                  MASTERONE2ONE      = 46;          //风水报告
    private final static int                  MASTERONE2ONEORDER      = 47;          //风水报告
    private final static int                  CANUSE      = 48;          //风水报告

    static {
        navigationMap.put("main", MAIN);
        navigationMap.put("login", LOGIN);
        navigationMap.put("browserLink", BROWSER_LINK);
        navigationMap.put("share", SHARE);
        navigationMap.put("goodSearch", GOOD_SEARCH);
        navigationMap.put("pocket", POCKET);
        navigationMap.put("mine", MINE);
        navigationMap.put("feedback", FEEDBACK);
        navigationMap.put("calendar", CALENDAR);
        navigationMap.put("todayDetail", TODAY_DETAIL);
        navigationMap.put("analysisFriend", ANALYSIS_FRIEND);
        navigationMap.put("test", TEST);
        navigationMap.put("setting", SETTING);
        navigationMap.put("wishlist", WISH_LIST);
        navigationMap.put("shake", SHAKE);
        navigationMap.put("shakeSticks", SHAKE_STICKS);
        navigationMap.put("sticksRecord", STICKS_RECORD);
        navigationMap.put("sign", SIGN);
        navigationMap.put("relationAnalysis", RELATION_ANALYSIS);
        navigationMap.put("relationResult", RELATION_RESULT);
        navigationMap.put("inferringWord", INFERRING_WORD);
        navigationMap.put("astrologyCat", FATE_CAT);
        navigationMap.put("orderDetail", ORDER_DETAIL);
        navigationMap.put("orderSubmit", ORDER_SUBMIT);
        navigationMap.put("webviewLink", WEBVIEW_LINK);
        navigationMap.put("handsOrfaceOrder", HANDS_OR_FACE_ORDER);
        navigationMap.put("convertPoint", CONVERT_POINT);
        navigationMap.put("masterAugur", MASTER_AUGUR);
        navigationMap.put("goodsList", GOODS_LIST);
        navigationMap.put("masterAsk", MASTER_ASK);
        navigationMap.put("goodsDetail", GOODS_DETAIL);
        navigationMap.put("cart", CART);
        navigationMap.put("orderList", ORDER_LIST);
        navigationMap.put("completedOrder", COMPLETED_ORDER);
        navigationMap.put("chatMsg", CHAT_MSG);
        navigationMap.put("customerReplyList", CUSTOMER_REPLY_LIST);
        navigationMap.put("customerReplyUser", CUSTOMER_REPLY_USER);
        navigationMap.put("lifeNum", LIFE_NUM);
        navigationMap.put("analyseName", ANALYSE_NAME);
        navigationMap.put("eightFontLife", EIGHTFONT_LIFE);
        navigationMap.put("blood", BLOOD);
        navigationMap.put("myElement", MYELEMENT);
        navigationMap.put("myLife", MYLIFE);
        navigationMap.put("vipCard", VIPCARD);
        navigationMap.put("fengshuiReport", FENGSHUIREPORT);
        navigationMap.put("masterOne2One", MASTERONE2ONE);
        navigationMap.put("masterOne2OneOrder", MASTERONE2ONEORDER);
        navigationMap.put("canUse", CANUSE);
    }

    /**
     * @param handleHttp 是否处理http请求
     */
    private static boolean navigate(String uriStr, Context ctx, boolean handleHttp, boolean backNewsList, boolean displayTitle) {
        Uri uri = Uri.parse(uriStr);
        String scheme = uri.getScheme();
        String host = uri.getHost();
        LogUtils.d("uri scheme is %s, query is %s,total uri is %s", scheme, host, uriStr);
        Intent intent;

        if (TextUtils.isEmpty(scheme)) {
            return false;
        }

        if (handleHttp && (scheme.equals("http") || scheme.equals("https"))) {
            YSRLWebActivity.goToPage(ctx, uriStr);
            return true;
        }

        //只处理ysrl的请求
        if (scheme.equals("ysrl")) {
            //如果不包含该host，返回
            if (!navigationMap.containsKey(host)) {
                goToRedirectPage(ctx, uri);

                return false;
            }
            LogUtils.d("load url original = host" + host);
            int navType = navigationMap.get(host);
            String json = uri.getQueryParameter("json");
            LogUtils.d("load url original = json" + navType + json);
            JSONObject data = new JSONObject();

            try {
                if (!TextUtils.isEmpty(json)) {
                    data = new JSONObject(json);
                }
                switch (navType) {
                    case MAIN:
                        int tab = data.optInt("tab");
                        int childTab = data.optInt("childTab");
                        MainActivity.goToPage(ctx, tab, childTab);
                        return true;
                    case LOGIN:
                        int refresh = data.optInt("refresh");
                        if (AppContext.isAnonymousUser()) {         //匿名用户跳转到登陆界面
                            if (1 == refresh) {
                                LoginActivity.goToPageWithResult((BaseActivity) ctx, false, json);
                            } else {
                                LoginActivity.goToPage(ctx);
                            }
                        } else {
                            UIUtils.toastMsg("您已经登录了！");
                        }
                        return true;
                    case BROWSER_LINK:
                        String linkUrl = data.optString("link");
                        //判断是否直接下载
                        int linkType = data.optInt("directDownload");
                        if (1 == linkType) {
                            YSRLService.startDownloadAPK(AppContext.baseContext, linkUrl, AppConfig.CONFIG_EXTERNAL_APP_TAG, true);
                            return true;
                        }
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(linkUrl));
                        ctx.startActivity(intent);
                        return true;
                    case SHARE:
                        String shareTitle = data.optString("title");
                        String shareContent = data.optString("content");
                        String shareLink = data.optString("link");
                        String shareImage = data.optString("imageUrl");
                        int shareType = data.optInt("shareType");
                        int sharePlatform = data.optInt("sharePlatform");
                        int shareStatisticsType = data.optInt("shareStatisticsType", StatisticsConstant.TYPE_SHARE);
                        String shareStatisticsSubType = data.optString("shareStatisticsSubType");

                        OperUtils.mSmallCat = OperUtils.SMALL_CAT_OTHER;
                        OperUtils.mKeyCat = OperUtils.KEY_CAT_NO_SURE;
                        ShareActivity.goToShare(ctx, shareTitle, shareContent, shareLink, shareImage, shareType, sharePlatform, shareStatisticsType, shareStatisticsSubType);
                        return true;
                    case GOOD_SEARCH:
                        CalendarGoodActivity.goToPage(ctx);
                        return true;
                    case POCKET:
                        MainActivity.goToPage(ctx, MainActivity.POS_POCKET);
                        return true;
                    case MINE:
                        MainActivity.goToPage(ctx, MainActivity.POS_MINE);
                        return true;
                    case FEEDBACK:
                        FeedBackListActivity.goToPage(ctx);
                        return true;
                    case CALENDAR:
                        DateTime cDateTime = new DateTime(DateUtils.getServerTime(data.optString("date")));
                        CalendarActivity.goToPage(ctx, cDateTime);
                        return true;
                    case TODAY_DETAIL:
                        DateTime tDateTime = new DateTime(DateUtils.getServerTime(data.optString("date")));
                        TodayDetailActivity.goToPage(ctx, tDateTime);
                        return true;
                    case ANALYSIS_FRIEND:
                        SetFriendsInfoActivity.goToPage(ctx);
                        return true;
                    case SETTING:
                        SettingActivity.goToPage(ctx);
                        return true;
                    case WISH_LIST:
                        int wish_type = data.optInt("type");
                        DateTime wish_date = new DateTime(DateUtils.getsimpleDateFormater4Time(data.optString("date")));
                        if (21 == wish_type) {
                            InferringWordActivity.goToPage(ctx, false);
                        } else if (22 == wish_type) {
                            TakeHandsPhotoActivity.goToPage(ctx);
                        } else {
                            WordRewardListActivity.goToPage(ctx);
                        }
                        return true;
                    case SHAKE:
                        PrayActivity.goToPage(ctx);
                        return true;
                    case SHAKE_STICKS:
                        int shake_type = data.optInt("type");
                        ShakeActivity.goToPage(ctx, shake_type);
                        return true;
                    case STICKS_RECORD:
                        PrayListActivity.goToPage(ctx);
                        return true;
                    case RELATION_ANALYSIS:
                        LoveLineSettingActivity.goToPage(ctx);
                        return true;
                    case RELATION_RESULT:
                        DateTime myBirthday = new DateTime(DateUtils.toSimpleDate(data.optString("myBirthday")));
                        DateTime yourBirthday = new DateTime(DateUtils.toSimpleDate(data.optString("yourBirthday")));
                        int myBirthdayyMode = data.optInt("myBirthdayyMode");
                        int yourBirthdayMode = data.optInt("yourBirthdayMode");
                        int index = data.optInt("index");
                        LoveLineActivity.goToPage(ctx, myBirthday, yourBirthday, myBirthdayyMode, yourBirthdayMode, index);
                        return true;
                    case INFERRING_WORD:
                        boolean mWordRead = data.optBoolean("setRead");
                        InferringWordActivity.goToPage(ctx, mWordRead);
                        return true;
                    case FATE_CAT:
                        String catId = data.optString("catId");
                        FateCatListActivity.goToPage(ctx, catId);
                        return true;
                    case ORDER_DETAIL:
                        String ioId = data.optString("ioId");
                        String itemName = data.optString("itemName");
                        String imageUrl = data.optString("imageUrl");
                        String answerContent = data.optString("answerContent");
                        int orderType = data.optInt("type");
                        String itemParam = data.optString("itemParam");
                        boolean mOrderRead = data.optBoolean("setRead");
                        if (orderType != 0) {
                            HandsOrFaceOrderDetailActivity.goToPage(ctx, ioId, orderType, imageUrl, itemParam, 1, mOrderRead);
                        } else {
                            AuguryDetailActivity.goToPage(ctx, ioId, itemName, imageUrl, answerContent, mOrderRead);
                        }
                        return true;
                    case ORDER_SUBMIT:
                        String ioSubmitId = data.optString("itemId");
                        int orderSubmitType = data.optInt("type");
                        if (orderSubmitType == 5) {
                            MasterAskDetailActivity.goToPage(ctx, ioSubmitId);
                        } else {
                            AugurySubmitActivity.goToPage(ctx, ioSubmitId);
                        }

                        return true;
                    case WEBVIEW_LINK:
                        String webviewLink = data.optString("link");
                        YSRLWebActivity.goToPage(ctx, webviewLink);
                        return true;
                    case HANDS_OR_FACE_ORDER:
                        String handId = data.optString("itemId");
                        int handType = data.optInt("type");
                        HandsOrFaceOrderActivity.goToPage(ctx, handId, handType == 1);
                        return true;
                    case CONVERT_POINT:
                        FateCatListActivity.goToPage(ctx, "");
                        return true;
                    case MASTER_AUGUR:
                        String augurTheme = data.optString("theme");
                        if (TextUtils.isEmpty(augurTheme)) {
                            MasterAuguryActivity.goToPage(ctx);
                        }
                        return true;
                    case GOODS_LIST:
                        String goodsTheme = data.optString("theme");
                        int type = data.optInt("type");

                        if (TextUtils.isEmpty(goodsTheme)) {
                            GoodsListActivity.goToPage(ctx);
                        } else {
                            GoodsListActivity.goToPage(ctx, true, goodsTheme, type);
                        }
                        return true;
                    case MASTER_ASK:
                        String askTheme = data.optString("theme");
                        int  masterIndex = data.optInt("index");
                        if (TextUtils.isEmpty(askTheme)) {
                            MasterAskActivity.goToPage(ctx,masterIndex);
                        } else {
                            HotAskListActivity.goToPage(ctx, askTheme);
                        }
                        return true;
                    case GOODS_DETAIL:
                        String goodsId = data.optString("goodsId");
                        GoodsDetailActivity.goToPage(ctx, goodsId);
                        return true;
                    case CART:
                        CartListActivity.goToPage(ctx);
                        return true;
                    case ORDER_LIST:
                        int orderStatus = data.optInt("status");
                        OrderListActivity.goToPage(ctx, orderStatus);
                        return true;
                    case COMPLETED_ORDER:
                        OrderListActivity.goToPage(ctx, OrderDetail.COMPLETED);
                        return true;
                    case CHAT_MSG:
                        MessageListActivity.goToPage(ctx);
                        return true;
                    case CUSTOMER_REPLY_LIST:
                        ReplyCommentActivity.goToPage(ctx);
                        return true;
                    case CUSTOMER_REPLY_USER:
                        String sendUserId = data.optString("sendUserId");
                        ReplyListActivity.goToPage(ctx, sendUserId);
                        return true;
                    case LIFE_NUM:
                        LifeNumberActivity.goToPage(ctx);
                        return true;
                    case ANALYSE_NAME:
                        String nameItemId = data.optString("itemId");
                        int nameIndex = data.optInt("index");
                        TestNameActivity.goToPage(ctx, nameItemId, nameIndex);
                        return true;
                    case EIGHTFONT_LIFE:
                        BaZiActivity.goToPage(ctx);
                        return true;
                    case BLOOD:
                        InferringBloodActivity.goToPage(ctx);
                        return true;
                    case MYELEMENT:
                        MyLifeActivity.goToPage(ctx, false);
                        return true;
                    case MYLIFE:
                        MyLifeActivity.goToPage(ctx, true);
                        return true;
                    case VIPCARD:
                        MemberShipCarListActivity.goToPage(ctx);
                        return true;

                    case FENGSHUIREPORT:
                        String fengshui = data.optString("itemId");
                        String fengshuiUrl = data.optString("url");
//                        AppContext.mFengShuiUrl = fengshuiUrl;
                        FengShuiReportActivity.goToPage(ctx,fengshui,fengshuiUrl);
                        return true;
                    case MASTERONE2ONE:
                        String materId = data.optString("itemId");
                        MasterAuguryActivity.goToPage(ctx,materId);
                        return true;

                    case MASTERONE2ONEORDER:
                        int classTypeId = data.optInt("type");
                        MasterAuguryCartActivity.goToPage(ctx,classTypeId);
                        return true;

                    case CANUSE:

                        WeChatCouponsActivity.goToPage(ctx);
                        return true;

                    default:
                        goToRedirectPage(ctx, uri);
                        return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        gotoDefaultPage(ctx);
        return false;
    }

    private static void goToRedirectPage(Context ctx, Uri uri) {
        //不支持的时候若检测到redirectUrl直接打开webView
        String redirectUrl = uri.getQueryParameter("redirectUrl");
        if (!TextUtils.isEmpty(redirectUrl)) {
            navigate(redirectUrl, ctx, true);
        } else {
            gotoDefaultPage(ctx);
        }
    }

    // 跳转连接错误，或者不存在时，跳转到默认界面
    private static void gotoDefaultPage(Context ctx) {
        Intent intent = new Intent(ctx, DefaultPageActivity.class);
        ctx.startActivity(intent);
    }

    /**
     * @param handleHttp 是否处理http请求
     */
    public static boolean navigate(String uriStr, Context ctx, boolean handleHttp) {
        return navigate(uriStr, ctx, handleHttp, false, false);
    }

    public static boolean navigate(String uriStr, Context ctx, boolean handleHttp, boolean backNewsList) {
        return navigate(uriStr, ctx, handleHttp, backNewsList, false);
    }
}
