package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 08/12/2016.
 * 问题答案
 */
public class ItemAnswer {
    public String iaId;                         //id
    public String content;                      //回复内容
    public int isRead;                          //是否已读,0是未读，1是已读;默认为0
    public int type;                            //消息类型，0文字1图片2语音

    public static ItemAnswer parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }

        ItemAnswer info = new ItemAnswer();
        info.iaId = json.optString("iaId");
        info.content = json.optString("content");
        info.isRead = json.optInt("isRead");
        info.type = json.optInt("type");

        return info;
    }

    public static ArrayList<ItemAnswer> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<ItemAnswer> info = new ArrayList<ItemAnswer>();
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
