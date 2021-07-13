package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 1/13/16.
 * 市场营销
 */
public class Market {
    public String marketId;                 //营销id
    public String marketName;               //营销名称
    public String description;              //营销描述
    public String androidUrl;               //android 链接
    public String imageUrl;                 //图片链接

    public static Market parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Market market = new Market();
        market.marketId = json.optString("marketId");
        market.marketName = json.optString("marketName");
        market.description = json.optString("description");
        market.androidUrl = json.optString("androidUrl");
        market.imageUrl = json.optString("imageUrl");

        return market;
    }

    public static ArrayList<Market> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Market> markets = new ArrayList<Market>();
        if (jsonArray == null) {
            return markets;
        }
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            markets.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
        }
        return markets;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof Market && ((Market) o).marketId.equals(marketId);
    }
}
