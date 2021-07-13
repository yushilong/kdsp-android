package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 08/03/2017.
 * 购物车数据
 */

public class Cart {
    public String cartId;
    public String skuId;
    public int count;
    public String[] images;
    public int price;
    public String goodsName;
    public String spec;
    public SKU sku;
    public boolean mHasSelected;            //是否选中

    public static Cart parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Cart info = new Cart();
        info.cartId = json.optString("cartId");
        info.skuId = json.optString("skuId");
        info.count = json.optInt("count");
        info.images = json.optString("imageUrl").split(",");
        if (info.images.length == 0) {
            info.images[0] = "";
        }
        info.price = json.optInt("price");
        info.goodsName = json.optString("goodsName");
        info.spec = json.optString("spec");
        info.sku = SKU.parseObjectFromJSON(json.optJSONObject("sku"));

        return info;
    }

    public static ArrayList<Cart> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Cart> info = new ArrayList<>();
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
        return o != null && o instanceof Cart && ((Cart) o).cartId.equals(cartId);
    }
}
