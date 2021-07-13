package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 15/9/11.
 * 神婆日报的bean
 */
public class Daily {
    public static int TYPE_WEB = 1;         //webView形式
    public static int TYPE_HTML = 2;        //html形式

    public String dailyId;          //id
    public String title;            //标题
    public int type;                //日报类型，1为链接形式，直接以webview打开，2为链接形式的固定页面加载
    public String imageUrl;         //图片地址
    public String linkUrl;          //链接地址
    public String createTime;       //创建时间
    public String dailyType;        //日报类型
    public String description;      //日报内容
    public boolean isCollection;    //是否收藏，0为未收藏，1为收藏
    public ArrayList<Daily> childrenDailys;     //子日报

    public static Daily parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Daily daily = new Daily();
        daily.dailyId = json.optString("dailyId");
        daily.title = json.optString("title");
        daily.type = json.optInt("type", 1);
        daily.imageUrl = json.optString("imageUrl");
        daily.linkUrl = json.optString("linkUrl");
        daily.createTime = json.optString("createTime");
        daily.dailyType = json.optString("dailyType");
        daily.description = json.optString("description");
        daily.isCollection = json.optInt("isCollection") == 1;
        daily.childrenDailys = parseListFromJSON(json.optJSONArray("childrenDailys"));

        return daily;
    }

    public static ArrayList<Daily> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Daily> info = new ArrayList<Daily>();
        if (jsonArray == null) {
            return info;
        }
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            info.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
        }
        return info;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof Daily && ((Daily) o).dailyId.equals(dailyId);
    }
}
