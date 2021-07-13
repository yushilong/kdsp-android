package com.qizhu.rili.controller;

/**
 * Created by lindow on 9/24/15.
 * 统计的controller
 */
public class StatisticsConstant {
    //来源
    public static int SOURCE_MAIN = 1;                          //来源于主页
    public static int SOURCE_LUCKY_SEARCH = 2;                  //来源于吉日搜索
    public static int SOURCE_POCKET = 3;                        //来源于口袋
    public static int SOURCE_MINE = 4;                          //来源于我的
    public static int SOURCE_CECE = 5;                          //来源于测测
    public static int SOURCE_FRIENDS = 6;                       //来源于吉友
    public static int SOURCE_QQ = 1001;                         //来源于QQ好友
    public static int SOURCE_WEIXIN = 1002;                     //来源于微信好友
    public static int SOURCE_WEIXIN_TIMELINE = 1003;            //来源于微信朋友圈
    public static int SOURCE_WEIBO = 1004;                      //来源于微博
    public static int SOURCE_QZONE = 1005;                      //来源于QQ空间

    //事件类型
    public static int TYPE_PAGE = 1001;                //页面跳转
    public static int TYPE_SHARE = 2001;               //普通分享
    public static int TYPE_DAILY_SHARE = 2002;         //神婆日报分享
    public static int TYPE_TEST_SHARE = 2003;          //测试题分享
    public static int TYPE_APP_SHARE = 2004;           //App分享

    //子类型,可以理解为当前页面,添加之前请确保不要重复
    public static String subType_God_Daily = "button_god_daily";                    //吉日搜索页面
    public static String subType_Daily_Desc = "button_daily_desc";                  //今日详细
    public static String subType_Enter_Calendar = "button_enter_calendar";          //日历
    public static String subType_Pocket = "button_pocket";                          //口袋
    public static String subType_Mine = "button_mine";                              //我的
    public static String subType_Inferring = "button_cezi";                         //测字页面
    public static String subType_Bocai = "button_bocai";                            //博彩
    public static String subType_Yueme = "button_yueme";                            //约么
    public static String subType_Gouwu = "button_gouwu";                            //购物
    public static String subType_Hehao = "button_hehao";                            //和好
    public static String subType_Yuehui = "button_yuehui";                          //约会
    public static String subType_Biaobai = "button_biaobai";                        //表白
    public static String subType_Lvxing = "button_lvxing";                          //旅行
    public static String subType_Juhui = "button_juhui";                            //聚会
    public static String subType_Lifa = "button_lifa";                              //理发
    public static String subType_Jiehun = "button_jiehun";                          //结婚
    public static String subType_Kanyisheng = "button_kanyisheng";                  //就医
    public static String subType_Tanshengyi = "button_tanshengyi";                  //谈生意
    public static String subType_Qianyue = "button_qianyue";                        //签约
    public static String subType_Kaizhang = "button_kaizhang";                      //开张
    public static String subType_Banqian = "button_banqian";                        //搬迁
    public static String subType_Zhiye = "button_zhiye";                            //置业
    public static String subType_Mianshi = "button_mianshi";                        //面试
    public static String subType_Qifu = "button_qifu";                              //祈福
    public static String subType_Jiaoyi = "button_jiaoyi";                          //交易
    public static String subType_Dongtu = "button_dongtu";                          //动土
    public static String subType_Xuexi = "button_xuexi";                            //学习
    public static String subType_Shenporibao = "button_shenporibao";                //神婆日报
    public static String subType_Ceshiti = "button_ceshiti";                        //测试题
    public static String subType_Jimingceshi = "button_jimingceshi";                //吉名测试
    public static String subType_Fenxixiaohuoban = "button_fenxixiaohuoban";        //分析小伙伴
    public static String subType_Wodemingge = "wodemingge";                         //我的命格
    public static String subType_Wodexingxiu = "wodexingxiu";                       //我的星宿
    public static String subType_Wodeyingzi = "wodeyingzi";                         //我的影子
    public static String subType_Wodetianfu = "wodetianfu";                         //我的天赋
    public static String subType_Wodeyisheng = "wodeyisheng";                       //我的一生
    public static String subType_About_us = "about_us";                             //关于我们
    public static String subType_RENYICE = "renyice";                               //任意测
    public static String subType_JIAHOUYOU = "jiahaoyou";                           //加好友
    public static String subType_HAOYOUGUANXI = "haoyouguanxi";                     //好友关系
    public static String subType_LIAOTIAN = "liaotian";                             //聊天
    public static String subType_YAOYIYAO = "button_yaoyiyao";                      //神婆摇一摇
    public static String subType_YAOYIYAO_RECORD = "button_yaoyiyao_record";        //神婆摇一摇记录
    public static String subType_YUANLAIRUCI = "button_yuanlairuci";                //缘来如此
    public static String subType_Goods = "button_yuanlairuci";                      //缘来如此
}
