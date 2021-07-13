package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 22/03/2017.
 * 物流信息
 */

public class Logistics {
    public String context;                          //id
    public String ftime;                            //名称，属性的集合

    public static Logistics parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Logistics item = new Logistics();
        item.context = json.optString("context");
        item.ftime = json.optString("ftime");

        return item;
    }

    public static ArrayList<Logistics> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Logistics> info = new ArrayList<>();
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
