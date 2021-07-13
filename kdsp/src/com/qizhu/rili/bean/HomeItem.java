package com.qizhu.rili.bean;

import org.json.JSONObject;

/**
 * Created by zhouyue on 8/01/2017.
 * 主页个人日常
 */
public class HomeItem {
    public String getUp;                //起床
    public String travel;               //出行
    public String work;                 //工作
    public String study;                 //学习
    public String diet;                  //饮食
    public String birth;                 //生日
    public String birthday;             //生日
    public String luckyDay;              //吉日
    public String dayDesc;              //今天是你的**日
    public String dayEvent;              //今日宜
    public String luckyColor;              //幸运颜色
    public String luckyPos;              //幸运方位
    public int  luckyScore;              //吉日分数
    public String luckyTitle;              //吉日
    public String lunarCalendar;              //农历
    public String publicCalendar;              //阳历

    public static HomeItem parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        HomeItem info = new HomeItem();
        info.getUp = json.optString("getUp");
        info.travel = json.optString("travel");
        info.work = json.optString("work");
        info.study = json.optString("study");
        info.diet = json.optString("diet");
        info.birth = json.optString("birth");
        info.birthday = json.optString("birthday");
        info.luckyDay = json.optString("luckyDay");
        info.dayDesc = json.optString("dayDesc");
        info.dayEvent = json.optString("dayEvent");
        info.luckyColor = json.optString("luckyColor");
        info.luckyPos = json.optString("luckyPos");
        info.luckyScore = json.optInt("luckyScore");
        info.luckyTitle = json.optString("luckyTitle");
        info.lunarCalendar = json.optString("lunarCalendar");
        info.publicCalendar = json.optString("publicCalendar");

        return info;
    }

//    public static ArrayList<HomeItem> parseListFromJSON(JSONArray jsonArray) {
//        ArrayList<HomeItem> info = new ArrayList<HomeItem>();
//        if (jsonArray == null) {
//            return info;
//        }
//        int length = jsonArray.length();
//        for (int i = 0; i < length; i++) {
//            info.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
//        }
//        return info;
//    }
}
