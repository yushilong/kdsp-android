package com.qizhu.rili.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zhouyue on 25/8/2017
 * 优惠券
 */

public class Coupons implements Parcelable {
    public String mcId;              //id
    public int    isDiscount;             //折扣或者定额（0定额，1折扣）
    public int    price;               //定额的金额（单位为分）和折扣（比如八五折就是85，5折就是50）
    public int    isUsable;               //isUsable 1已使用
    public String startTime;                  //开始时间
    public String endTime;                   //结束时间
    public String couponName;                   //结束时间
    public boolean isChoose;

    public static Coupons parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Coupons info = new Coupons();
        info.mcId = json.optString("mcId");
        info.isDiscount = json.optInt("isDiscount");
        info.price = json.optInt("price");
        info.isUsable = json.optInt("isUsable");
        info.startTime = json.optString("startTime");
        info.endTime = json.optString("endTime");
        info.couponName = json.optString("couponName");


        return info;
    }

    public static ArrayList<Coupons> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Coupons> info = new ArrayList<>();
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
        return o != null && o instanceof Coupons && ((Coupons) o).mcId.equals(mcId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mcId);
        dest.writeInt(this.isDiscount);
        dest.writeInt(this.isUsable);
        dest.writeInt(this.price);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeString(this.couponName);

    }

    public Coupons() {
    }

    protected Coupons(Parcel in) {
        this.mcId = in.readString();
        this.isDiscount = in.readInt();
        this.price = in.readInt();
        this.isUsable = in.readInt();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.couponName = in.readString();

    }

    public static final Creator<Coupons> CREATOR = new Creator<Coupons>() {
        @Override
        public Coupons createFromParcel(Parcel source) {
            return new Coupons(source);
        }

        @Override
        public Coupons[] newArray(int size) {
            return new Coupons[size];
        }
    };
}
