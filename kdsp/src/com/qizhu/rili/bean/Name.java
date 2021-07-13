package com.qizhu.rili.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @创建者 zhouyue
 * @创建时间 2017/5/15 17:19
 * @描述 测名结果
 */
public class Name implements Parcelable {
    public String baseFortune;          //基础运
    public String successFortune;       //成功运
    public String friendFortune;        //朋友运
    public String pandect;              //总论
    public String character;            //性格
    public String career;               //事业
    public String family;               //家庭
    public String marriage;             //婚姻
    public String children;             //子女
    public String fortune;              //财运
    public String health;               //健康
    public String oldLucky;             //老运
    public String familyName;           //姓
    public String lastName;             //名
    public int score;                   //分数


    public static Name parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Name info = new Name();
        info.baseFortune = json.optString("baseFortune");
        info.career = json.optString("career");
        info.character = json.optString("character");
        info.children = json.optString("children");
        info.family = json.optString("family");
        info.familyName = json.optString("familyName");
        info.fortune = json.optString("fortune");
        info.health = json.optString("health");
        info.friendFortune = json.optString("friendFortune");
        info.lastName = json.optString("lastName");
        info.marriage = json.optString("marriage");
        info.oldLucky = json.optString("oldLucky");
        info.pandect = json.optString("pandect");
        info.score = json.optInt("score");
        info.successFortune = json.optString("successFortune");
        info.marriage = json.optString("marriage");

        return info;
    }

    public static ArrayList<Name> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Name> info = new ArrayList<>();
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
        dest.writeString(this.baseFortune);
        dest.writeString(this.successFortune);
        dest.writeString(this.friendFortune);
        dest.writeString(this.pandect);
        dest.writeString(this.character);
        dest.writeString(this.career);
        dest.writeString(this.family);
        dest.writeString(this.marriage);
        dest.writeString(this.children);
        dest.writeString(this.fortune);
        dest.writeString(this.health);
        dest.writeString(this.oldLucky);
        dest.writeString(this.familyName);
        dest.writeString(this.lastName);
        dest.writeInt(this.score);
    }

    public Name() {
    }

    protected Name(Parcel in) {
        this.baseFortune = in.readString();
        this.successFortune = in.readString();
        this.friendFortune = in.readString();
        this.pandect = in.readString();
        this.character = in.readString();
        this.career = in.readString();
        this.family = in.readString();
        this.marriage = in.readString();
        this.children = in.readString();
        this.fortune = in.readString();
        this.health = in.readString();
        this.oldLucky = in.readString();
        this.familyName = in.readString();
        this.lastName = in.readString();
        this.score = in.readInt();
    }

    public static final Parcelable.Creator<Name> CREATOR = new Parcelable.Creator<Name>() {
        @Override
        public Name createFromParcel(Parcel source) {
            return new Name(source);
        }

        @Override
        public Name[] newArray(int size) {
            return new Name[size];
        }
    };
}
