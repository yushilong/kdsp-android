package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 8/15/16.
 * 运势问题
 */
public class FateItem {
    public String catId;                //分类id
    public String itemId;               //问题id
    public String imageUrl;             //图片
    public String itemName;             //名称
    public int playTimes;               //支付次数
    public int price;                   //价格,以分为单位
    public int isHot;                   //1为最热
    public int isNew;                   //1为最新
    public int type;                    //类型,0是问题,1是手相,2是面相

    public static FateItem parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        FateItem fateItem = new FateItem();
        fateItem.catId = json.optString("catId");
        fateItem.itemId = json.optString("itemId");
        fateItem.imageUrl = json.optString("imageUrl");
        fateItem.itemName = json.optString("itemName");
        fateItem.playTimes = json.optInt("playTimes");
        fateItem.price = json.optInt("price");
        fateItem.isHot = json.optInt("isHot");
        fateItem.isNew = json.optInt("isNew");
        fateItem.type = json.optInt("type");

        return fateItem;
    }

    public static ArrayList<FateItem> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<FateItem> info = new ArrayList<FateItem>();
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
