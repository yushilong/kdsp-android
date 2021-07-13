package com.qizhu.rili.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zhouyue on 20/4/2018
 * 大师一对一商品
 */

public class AuguryCart implements Parcelable {
    public String itemId;              //id
    public int    type;             //1正常处理，type为2是看风水
    public int    price;               //定额的金额（单位为分）和折扣（比如八五折就是85，5折就是50）
    public int    perPrice;               //定额的金额（单位为分）和折扣（比如八五折就是85，5折就是50）
    public String itemName;
    public String title;
    public String imgUrl;
    public boolean mHasSelected;            //是否选中;
    public int    imageId;


    public static AuguryCart parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        AuguryCart info = new AuguryCart();
        info.itemId = json.optString("itemId");
        info.type = json.optInt("type");
        info.price = json.optInt("price");
        info.perPrice = json.optInt("price");;
        info.itemName = json.optString("itemName");
        info.title = json.optString("title");
        info.imgUrl = json.optString("imgUrl");


        return info;
    }

    public static ArrayList<AuguryCart> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<AuguryCart> info = new ArrayList<>();
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
        return o != null && o instanceof AuguryCart && ((AuguryCart) o).itemId.equals(itemId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.itemId);
        dest.writeInt(this.type);
        dest.writeInt(this.price);
        dest.writeString(this.itemName);
        dest.writeString(this.title);
        dest.writeString(this.imgUrl);
        dest.writeByte((byte) (mHasSelected ? 1 : 0));

    }

    public AuguryCart() {
    }

    protected AuguryCart(Parcel in) {
        this.itemId = in.readString();
        this.type = in.readInt();
        this.price = in.readInt();
        this.itemName = in.readString();
        this.title = in.readString();
        this.imgUrl = in.readString();
        this.mHasSelected = in.readByte() != 0;


    }

    public static final Creator<AuguryCart> CREATOR = new Creator<AuguryCart>() {
        @Override
        public AuguryCart createFromParcel(Parcel source) {
            return new AuguryCart(source);
        }

        @Override
        public AuguryCart[] newArray(int size) {
            return new AuguryCart[size];
        }
    };
}
