package com.qizhu.rili.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 24/02/2017.
 * sku
 */

public class SKU implements Parcelable {
    public String skuId;                        //id
    public String goodsId;                      //商品id
    public String skuName;                      //名称，属性的集合
    public String tip;                      //名称，属性的集合
    public int price;                           //价格
    public int originalPrice;                   //原价格
    public int stock;                           //库存
    public int showStatus;                      //是否下架，0正常，1下架

    public static SKU parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        SKU item = new SKU();
        item.skuId = json.optString("skuId");
        item.goodsId = json.optString("goodsId");
        item.skuName = json.optString("skuName");
        item.tip = json.optString("tip");
        item.price = json.optInt("price");
        item.stock = json.optInt("stock");
        item.showStatus = json.optInt("showStatus");
        item.originalPrice = json.optInt("originalPrice");

        return item;
    }

    public static ArrayList<SKU> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<SKU> info = new ArrayList<>();
        if (jsonArray == null) {
            return info;
        }
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            info.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
        }
        return info;
    }

    public SKU() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.skuId);
        dest.writeString(this.goodsId);
        dest.writeString(this.skuName);
        dest.writeString(this.tip);
        dest.writeInt(this.price);
        dest.writeInt(this.originalPrice);
        dest.writeInt(this.stock);
        dest.writeInt(this.showStatus);
    }

    protected SKU(Parcel in) {
        this.skuId = in.readString();
        this.goodsId = in.readString();
        this.skuName = in.readString();
        this.tip = in.readString();
        this.price = in.readInt();
        this.originalPrice = in.readInt();
        this.stock = in.readInt();
        this.showStatus = in.readInt();
    }

    public static final Creator<SKU> CREATOR = new Creator<SKU>() {
        @Override
        public SKU createFromParcel(Parcel source) {
            return new SKU(source);
        }

        @Override
        public SKU[] newArray(int size) {
            return new SKU[size];
        }
    };
}
