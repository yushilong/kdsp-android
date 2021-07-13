package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 20/12/2016.
 * 会员卡分类
 */

public class MembershipCat {
    public String mscId;                     //回复内容
    public long createTime;                  //创建时间
    public int price;                        //会员卡价格，单位为分
    public String imageUrl;                  //会员卡图片
    public String msgName;                   //会员卡名称
    public String mscDesc;                   //会员卡描述

    public static MembershipCat parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        MembershipCat info = new MembershipCat();
        info.mscId = json.optString("mscId");
        info.createTime = json.optLong("createTime");
        info.price = json.optInt("price");
        info.imageUrl = json.optString("imageUrl");
        info.msgName = json.optString("msgName");
        info.mscDesc = json.optString("mscDesc").replace("/", "\n");

        return info;
    }

    public static ArrayList<MembershipCat> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<MembershipCat> info = new ArrayList<>();
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
