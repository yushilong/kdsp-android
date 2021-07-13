package com.qizhu.rili.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 06/03/2017.
 * 问题分类
 */

public class Cat implements Parcelable {
    public String catId;                //分类id
    public String name;                 //名称

    public static Cat parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Cat info = new Cat();
        info.catId = json.optString("catId");
        info.name = json.optString("name");

        return info;
    }

    public static ArrayList<Cat> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Cat> info = new ArrayList<>();
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
        dest.writeString(this.catId);
        dest.writeString(this.name);
    }

    public Cat() {
    }

    protected Cat(Parcel in) {
        this.catId = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Cat> CREATOR = new Parcelable.Creator<Cat>() {
        @Override
        public Cat createFromParcel(Parcel source) {
            return new Cat(source);
        }

        @Override
        public Cat[] newArray(int size) {
            return new Cat[size];
        }
    };
}
