package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 20/12/2016.
 * 会员卡信息
 */

public class MembershipGift {
    public String msgId;                     //礼品id
    public String itemId;                    //问题id
    public long createTime;                  //创建时间
    public int currentPrice;                 //当前价格
    public String description;               //描述
    public int isUserful;                    //是否可用，0可用，1已经使用
    public String msgName;                   //礼品名称
    public int originalPrice;                //原价
    public int type;                         //类型，1为手相，2为面相，客户端用不到

    public static MembershipGift parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        MembershipGift info = new MembershipGift();
        info.msgId = json.optString("msgId");
        info.itemId = json.optString("itemId");
        info.createTime = json.optLong("createTime");
        info.currentPrice = json.optInt("currentPrice");
        info.description = json.optString("description");
        info.isUserful = json.optInt("isUserful", 0);
        info.msgName = json.optString("msgName");
        info.originalPrice = json.optInt("originalPrice");
        info.type = json.optInt("type");

        return info;
    }

    public static ArrayList<MembershipGift> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<MembershipGift> info = new ArrayList<MembershipGift>();
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
