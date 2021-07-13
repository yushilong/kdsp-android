package com.qizhu.rili.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 3/30/16.
 * 摇签
 */
public class ShakingSign implements Parcelable {
    public String shakId;                       //id
    public int type;                            //类型，1为月老签，2为事业签,3为财运
    public String createTime;                   //求签时间
    public int isLike;                          //是否点赞，1为赞，2为不赞
    public String askSth;                       //所求
    public String name;                         //签的名称
    public String mean;                         //意思
    public String word;                         //签文
    public String solution;                     //解签
    public String confessionDegree;             //告白指数
    public String happinessDegree;              //幸福指数
    public String fateDegree;                   //缘分指数
    public String marriage;                     //问婚姻
    public String fate;                         //问缘分

    public static ShakingSign parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        ShakingSign sign = new ShakingSign();

        sign.shakId = json.optString("shakId");
        sign.type = json.optInt("type", 1);
        sign.createTime = json.optString("createTime");
        sign.isLike = json.optInt("isLike");
        sign.askSth = json.optString("askSth");

        JSONObject jsonObject;
        switch (sign.type) {
            case 1:
                jsonObject = json.optJSONObject("param").optJSONObject("matchmakerSignVO");
                if (jsonObject != null) {
                    sign.name = jsonObject.optString("name");
                    sign.mean = jsonObject.optString("mean");
                    sign.confessionDegree = jsonObject.optString("confessionDegree");
                    sign.happinessDegree = jsonObject.optString("happinessDegree");
                    sign.fateDegree = jsonObject.optString("fateDegree");
                    sign.marriage = jsonObject.optString("marriage");
                    sign.fate = jsonObject.optString("fate");
                }
                break;
            case 2:
                jsonObject = json.optJSONObject("param").optJSONObject("businessSignVO");
                if (jsonObject != null) {
                    sign.name = jsonObject.optString("name");
                    sign.mean = jsonObject.optString("mean");
                    sign.word = jsonObject.optString("word");
                    sign.solution = jsonObject.optString("solution");
                }
                break;
            case 3:
                jsonObject = json.optJSONObject("param").optJSONObject("fortuneSignVO");
                if (jsonObject != null) {
                    sign.name = jsonObject.optString("name");
                    sign.mean = jsonObject.optString("mean");
                    sign.word = jsonObject.optString("word");
                    sign.solution = jsonObject.optString("solution");
                }
                break;
        }
        return sign;
    }

    public static ArrayList<ShakingSign> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<ShakingSign> infos = new ArrayList<ShakingSign>();
        if (jsonArray == null) {
            return infos;
        }
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            infos.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
        }
        return infos;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof ShakingSign && ((ShakingSign) o).shakId.equals(shakId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.shakId);
        dest.writeInt(this.type);
        dest.writeString(this.createTime);
        dest.writeInt(this.isLike);
        dest.writeString(this.askSth);
        dest.writeString(this.name);
        dest.writeString(this.mean);
        dest.writeString(this.word);
        dest.writeString(this.solution);
        dest.writeString(this.confessionDegree);
        dest.writeString(this.happinessDegree);
        dest.writeString(this.fateDegree);
        dest.writeString(this.marriage);
        dest.writeString(this.fate);
    }

    public ShakingSign() {
    }

    protected ShakingSign(Parcel in) {
        this.shakId = in.readString();
        this.type = in.readInt();
        this.createTime = in.readString();
        this.isLike = in.readInt();
        this.askSth = in.readString();
        this.name = in.readString();
        this.mean = in.readString();
        this.word = in.readString();
        this.solution = in.readString();
        this.confessionDegree = in.readString();
        this.happinessDegree = in.readString();
        this.fateDegree = in.readString();
        this.marriage = in.readString();
        this.fate = in.readString();
    }

    public static final Parcelable.Creator<ShakingSign> CREATOR = new Parcelable.Creator<ShakingSign>() {
        @Override
        public ShakingSign createFromParcel(Parcel source) {
            return new ShakingSign(source);
        }

        @Override
        public ShakingSign[] newArray(int size) {
            return new ShakingSign[size];
        }
    };
}
