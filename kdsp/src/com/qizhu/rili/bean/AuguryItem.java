package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 8/17/16.
 * 占卜订单项
 */
public class AuguryItem {
    public String ioId;                 //订单id
    public String itemName;             //问题
    public String imageUrl;             //图片地址
    public String payTime;              //支付时间
    public int totalFee;                //价格
    public String answerContent;        //回复内容
    public String itemParam;            //问题详细内容
    public int isRead;                  //1为未读,2为已读
    public int type;                    //类型,0是问题,1是手相,2是面相,4是测名,5是大师亲测

    public static AuguryItem parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        AuguryItem info = new AuguryItem();
        info.ioId = json.optString("ioId");
        info.itemName = json.optString("itemName");
        info.imageUrl = json.optString("imageUrl");
        info.payTime = json.optString("payTime");
        info.totalFee = json.optInt("totalFee");
        info.answerContent = json.optString("answerContent");
        info.isRead = json.optInt("isRead", 1);
        info.type = json.optInt("type");
        info.itemParam = json.optString("itemParam");

        return info;
    }

    public static ArrayList<AuguryItem> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<AuguryItem> info = new ArrayList<AuguryItem>();
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
        return o != null && o instanceof AuguryItem && ((AuguryItem) o).ioId.equals(ioId);
    }
}
