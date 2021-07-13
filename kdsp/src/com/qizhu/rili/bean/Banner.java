package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 15/7/29.
 * 广告的bean
 */
public class Banner {
    public String bannerId;
    public int bannerStyle;
    public String description;
    public String imgUrl;
    public String linkUrl;
    public String title;

    public static Banner parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Banner banner = new Banner();
        banner.bannerId = json.optString("bannerId");
        banner.bannerStyle = json.optInt("bannerStyle");
        banner.description = json.optString("description");
        banner.imgUrl = json.optString("imgUrl");
        banner.linkUrl = json.optString("linkUrl");
        banner.title = json.optString("title");

        return banner;
    }

    public static ArrayList<Banner> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Banner> banners = new ArrayList<Banner>();
        if (jsonArray == null) {
            return banners;
        }
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            banners.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
        }
        return banners;
    }
}
