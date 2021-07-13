package com.qizhu.rili.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 01/03/2017.
 * 商品
 */

public class Goods implements Parcelable {
    public String goodsId;
    public String[] images;
    public int maxPrice;
    public int minPrice;
    public String sellPoint;
    public String title;
    public  int status;          //商品状态，1为正常，其余不正常
    public String tag;          //标签
    public int goodsType;          //商品类型（默认为0珠串，1符咒）
    public ArrayList<Content> contents = new ArrayList<>();

    public static Goods parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Goods info = new Goods();
        info.goodsId = json.optString("goodsId");
        info.images = json.optString("images").split(",");
        if (info.images.length == 0) {
            info.images[0] = "";
        }
        info.maxPrice = json.optInt("maxPrice");
        info.minPrice = json.optInt("minPrice");
        info.sellPoint = json.optString("sellPoint");
        info.title = json.optString("title");
        info.status = json.optInt("status");
        info.goodsType = json.optInt("goodsType");
        info.tag = json.optString("tag");
        info.contents = Content.parseListFromJSON(json.optJSONArray("goodsDtailVOs"));

        return info;
    }

    public static ArrayList<Goods> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Goods> info = new ArrayList<>();
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.goodsId);
        dest.writeStringArray(this.images);
        dest.writeInt(this.maxPrice);
        dest.writeInt(this.minPrice);
        dest.writeString(this.sellPoint);
        dest.writeString(this.title);
        dest.writeInt(this.status);
        dest.writeInt(this.goodsType);
        dest.writeString(this.tag);
    }

    public Goods() {
    }

    protected Goods(Parcel in) {
        this.goodsId = in.readString();
        this.images = in.createStringArray();
        this.maxPrice = in.readInt();
        this.minPrice = in.readInt();
        this.sellPoint = in.readString();
        this.title = in.readString();
        this.status = in.readInt();
        this.goodsType = in.readInt();
        this.tag = in.readString();
    }

    public static final Parcelable.Creator<Goods> CREATOR = new Parcelable.Creator<Goods>() {
        @Override
        public Goods createFromParcel(Parcel source) {
            return new Goods(source);
        }

        @Override
        public Goods[] newArray(int size) {
            return new Goods[size];
        }
    };
}
