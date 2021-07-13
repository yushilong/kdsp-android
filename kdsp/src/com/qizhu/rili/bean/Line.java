package com.qizhu.rili.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Line implements Parcelable {
    public float  r;         // 半径长度
    public int    x;        //矩形x坐标
    public int    y;        //矩形y坐标
    public int    positonX;        //
    public int    positonY;        //
    public int    centerX;        //中心点x坐标
    public int    centerY;        //中心点y坐标
    public int    line;        //要画的线
    public String lineColor;        //要画的线颜色
    public String lineName;        //要画的线名字
    public String lineDesc;        //要画的线名字

    public static Line parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Line info = new Line();
        info.lineColor = json.optString("lineColor");
        info.lineName = json.optString("lineName");
        info.lineDesc = json.optString("lineDesc");
        info.line = json.optInt("line");

        return info;
    }

    public static ArrayList<Line> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Line> info = new ArrayList<>();
        if (jsonArray == null) {
            return info;
        }
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            info.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
        }
        return info;
    }


    public Line() {
    }


    public int getNum() {
        return (3 * this.positonY + this.positonX);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.r);
        dest.writeInt(this.x);
        dest.writeInt(this.y);
        dest.writeInt(this.positonX);
        dest.writeInt(this.positonY);
        dest.writeInt(this.centerX);
        dest.writeInt(this.centerY);
        dest.writeInt(this.line);
        dest.writeString(this.lineColor);
        dest.writeString(this.lineName);
        dest.writeString(this.lineDesc);
    }

    protected Line(Parcel in) {
        this.r = in.readFloat();
        this.x = in.readInt();
        this.y = in.readInt();
        this.positonX = in.readInt();
        this.positonY = in.readInt();
        this.centerX = in.readInt();
        this.centerY = in.readInt();
        this.line = in.readInt();
        this.lineColor = in.readString();
        this.lineName = in.readString();
        this.lineDesc = in.readString();
    }

    public static final Creator<Line> CREATOR = new Creator<Line>() {
        @Override
        public Line createFromParcel(Parcel source) {
            return new Line(source);
        }

        @Override
        public Line[] newArray(int size) {
            return new Line[size];
        }
    };
}
