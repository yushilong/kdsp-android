package com.qizhu.rili.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @创建者 luxing
 * @创建时间 2016/6/13 14:55
 * @描述 线的详情
 */
public class LineDetail implements Parcelable {
    public String pvId;                     //主线id
    public String pbId;                     //支线id
    public String subTitle;                 //标题
    public int startLength;                 //开始长度点
    public String fontContent;              //内容
    public String name;                     //名称
    public String voiceUrl;                 //语音
    public int type;                        //类型，2为一线类推
    public int endLength;                   //结束长度点
    public String femalePicUrl;             //女生图片
    public String malePicUrl;               //男生图片

    public static LineDetail parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        LineDetail oneLine = new LineDetail();
        oneLine.pvId = json.optString("pvId");
        oneLine.pbId = json.optString("pbId");
        oneLine.subTitle = json.optString("subTitle");
        oneLine.fontContent = json.optString("fontContent");
        oneLine.name = json.optString("name");
        oneLine.voiceUrl = json.optString("voiceUrl");
        oneLine.type = json.optInt("type");
        oneLine.startLength = json.optInt("startLength");
        oneLine.endLength = json.optInt("endLength");
        oneLine.femalePicUrl = json.optString("femalePicUrl");
        oneLine.malePicUrl = json.optString("malePicUrl");
        return oneLine;
    }

    public static ArrayList<LineDetail> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<LineDetail> info = new ArrayList<LineDetail>();
        if (jsonArray == null) {
            return info;
        }
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            info.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
        }
        return info;
    }

    public LineDetail() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pvId);
        dest.writeString(this.pbId);
        dest.writeString(this.subTitle);
        dest.writeInt(this.startLength);
        dest.writeString(this.fontContent);
        dest.writeString(this.name);
        dest.writeString(this.voiceUrl);
        dest.writeInt(this.type);
        dest.writeInt(this.endLength);
        dest.writeString(this.femalePicUrl);
        dest.writeString(this.malePicUrl);
    }

    protected LineDetail(Parcel in) {
        this.pvId = in.readString();
        this.pbId = in.readString();
        this.subTitle = in.readString();
        this.startLength = in.readInt();
        this.fontContent = in.readString();
        this.name = in.readString();
        this.voiceUrl = in.readString();
        this.type = in.readInt();
        this.endLength = in.readInt();
        this.femalePicUrl = in.readString();
        this.malePicUrl = in.readString();
    }

    public static final Creator<LineDetail> CREATOR = new Creator<LineDetail>() {
        @Override
        public LineDetail createFromParcel(Parcel source) {
            return new LineDetail(source);
        }

        @Override
        public LineDetail[] newArray(int size) {
            return new LineDetail[size];
        }
    };
}

