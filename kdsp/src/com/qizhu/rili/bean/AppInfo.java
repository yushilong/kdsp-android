package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 15/7/29.
 * app的信息bean
 */
public class AppInfo {
    public String appId;                //app id
    public String appName;              //app的名称
    public String appUrl;               //app的下载链接
    public int clickTimes;              //点击时间
    public String description;          //描述
    public String imageUrl;             //图片地址
    public String appPackageName;       //包名
    public int sort;

    public static AppInfo parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        AppInfo appInfo = new AppInfo();
        appInfo.appId = json.optString("appId");
        appInfo.appName = json.optString("appName");
        appInfo.appUrl = json.optString("appUrl");
        appInfo.clickTimes = json.optInt("clickTimes");
        appInfo.description = json.optString("description");
        appInfo.imageUrl = json.optString("imageUrl");
        appInfo.appPackageName = json.optString("appPackageName");
        appInfo.sort = json.optInt("sort");

        return appInfo;
    }

    public static ArrayList<AppInfo> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<AppInfo> infos = new ArrayList<AppInfo>();
        if (jsonArray == null) {
            return infos;
        }
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            infos.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
        }
        return infos;
    }
}
