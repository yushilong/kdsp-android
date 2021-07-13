package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @创建者 zhouyue
 * @创建时间 2018/4/26 10:56
 * @描述 一对一订单

 */

public class one2oneOrders {
    public String description;
    public ArrayList<one2oneOrderDetails> mOne2oneOrderDetails = new ArrayList<one2oneOrderDetails>();
    public ArrayList<one2oneOrderDetails> one2oneOrderDetails ;

    public  one2oneOrders parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        one2oneOrders info = new one2oneOrders();
        info.description = json.optString("groupInfo");
        info.mOne2oneOrderDetails = new one2oneOrderDetails().parseListFromJSON(json.optJSONArray("one2oneOrderDetails"));


        return info;
    }

    public  ArrayList<one2oneOrders> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<one2oneOrders> info = new ArrayList<>();
        if (jsonArray == null) {
            return info;
        }
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            info.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
        }
        return info;
    }

    public class one2oneOrderDetails {
        public String itemName;
        public String num;
        public int isUsed;


        public  one2oneOrderDetails parseObjectFromJSON(JSONObject json) {
            if (json == null) {
                return null;
            }
            one2oneOrderDetails info = new one2oneOrderDetails();
            info.itemName = json.optString("itemName");
            info.num = json.optString("num");
            info.isUsed = json.optInt("isUsed");





            return info;
        }

        public  ArrayList<one2oneOrderDetails> parseListFromJSON(JSONArray jsonArray) {
            ArrayList<one2oneOrderDetails> info = new ArrayList<>();
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

}
