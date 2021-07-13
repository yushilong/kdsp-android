package com.qizhu.rili.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zhouyue on 06/20/2017.
 * 商品分类
 */

public class GoodsClass implements Parcelable {
    public String classifyId;
    public String classifyName;
    public int sort;


    public static GoodsClass parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        GoodsClass info = new GoodsClass();
        info.classifyId = json.optString("classifyId");
        info.classifyName = json.optString("classifyName");
        info.sort = json.optInt("sort");
        return info;
    }

    public static ArrayList<GoodsClass> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<GoodsClass> info = new ArrayList<>();
        if (jsonArray == null) {
            return info;
        }
        GoodsClass goodsClass = new GoodsClass();
        goodsClass.classifyName = "显示全部";
        goodsClass.sort = -1;
        info.add(goodsClass);
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
        dest.writeString(this.classifyId);
        dest.writeString(this.classifyName);
        dest.writeInt(this.sort);

    }

    public GoodsClass() {
    }

    protected GoodsClass(Parcel in) {
        this.classifyId = in.readString();
        this.sort = in.readInt();
        this.classifyName = in.readString();
    }

    public static final Creator<GoodsClass> CREATOR = new Creator<GoodsClass>() {
        @Override
        public GoodsClass createFromParcel(Parcel source) {
            return new GoodsClass(source);
        }

        @Override
        public GoodsClass[] newArray(int size) {
            return new GoodsClass[size];
        }
    };
}
