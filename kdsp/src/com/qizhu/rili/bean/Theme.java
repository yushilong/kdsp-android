package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 01/03/2017.
 * 主题
 */

public class Theme {
    public String name;                         //名称
    public String desc;                          //描述
    public String imageUrl;                     //头像
    public int type;                            //类型

    public ArrayList<FateItem> mFateItem = new ArrayList<>();
    public ArrayList<Goods> mGoods = new ArrayList<>();

    public static Theme parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Theme info = new Theme();
        info.name = json.optString("name");
        info.desc = json.optString("desc");
        info.imageUrl = json.optString("imageUrl");
        info.type = json.optInt("type");
        if (info.type == 1 || info.type == 3 ) {
            info.mFateItem = FateItem.parseListFromJSON(json.optJSONArray("data"));
        } else {
            info.mGoods = Goods.parseListFromJSON(json.optJSONArray("data"));
        }

        return info;
    }

    public static ArrayList<Theme> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Theme> info = new ArrayList<>();
        if (jsonArray == null) {
            return info;
        }
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            info.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
        }
        return info;
    }
}
