package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 07/03/2017.
 * 商品图文详情
 */

public class Content {
    public String content;
    public int type;

    public static Content parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Content info = new Content();
        info.content = json.optString("content");
        info.type = json.optInt("type");

        return info;
    }

    public static ArrayList<Content> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Content> info = new ArrayList<>();
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
