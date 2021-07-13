package com.qizhu.rili.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 08/03/2017.
 * 地址
 */

public class Address implements Parcelable {
    public  String  shipId = "";                       //id
    public  String  receiverName;                 //名字
    public  String  receiverMobile;               //电话
    public  String  receiverZip;                  //邮编
    public  String  receiverAddress;              //详细地址
    public  boolean mIsDefault;                  //是否默认
    public String  province;                   //省
    public String  city;                       //市
    public String  area;                       //区


    public static Address parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Address info = new Address();
        info.shipId = json.optString("shipId");
        info.receiverName = json.optString("receiverName");
        info.receiverMobile = json.optString("receiverMobile");
        info.receiverZip = json.optString("receiverZip");
        info.receiverAddress = json.optString("receiverAddress");
        info.province = json.optString("province");
        info.city = json.optString("city");
        info.area = json.optString("area");
        info.mIsDefault = json.optInt("isDefault") == 1;

        return info;
    }

    public static ArrayList<Address> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Address> info = new ArrayList<>();
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
        return o != null && o instanceof Address && ((Address) o).shipId.equals(shipId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.shipId);
        dest.writeString(this.receiverName);
        dest.writeString(this.receiverMobile);
        dest.writeString(this.receiverZip);
        dest.writeString(this.receiverAddress);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.area);
        dest.writeByte(this.mIsDefault ? (byte) 1 : (byte) 0);
    }

    public Address() {
    }

    protected Address(Parcel in) {
        this.shipId = in.readString();
        this.receiverName = in.readString();
        this.receiverMobile = in.readString();
        this.receiverZip = in.readString();
        this.receiverAddress = in.readString();
        this.province = in.readString();
        this.city = in.readString();
        this.area = in.readString();
        this.mIsDefault = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
}
