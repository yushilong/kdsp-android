package com.qizhu.rili.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.qizhu.rili.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 23/03/2017.
 * 留言
 */

public class Chat implements Parcelable {
    public String msgId;                    //id
    public int time;                        //发布时间
    public String content;                  //内容
    public int msgType;                     //类型，默认为0：文本，1：图片，2：json数据
    public int direction;                   //自己的消息为0
    public String userId;                   //用户id,不是此条消息，而是此次对话的用户
    public String imageUrl;                 //头像
    public String createTime;               //时间
    public int count;                       //针对用户的未读消息数

    public static Chat parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Chat info = new Chat();
        info.msgId = json.optString("msgId");
        String time = json.optString("createTime");
        if (!TextUtils.isEmpty(time)) {
            info.time = DateUtils.getIntFromDateString(time);
        }
        info.content = json.optString("content");
        info.msgType = json.optInt("msgType");
        info.direction = json.optInt("direction");
        info.userId = json.optString("userId");
        info.imageUrl = json.optString("imageUrl");
        info.createTime = json.optString("createTime");
        info.count = json.optInt("count");

        return info;
    }

    public static ArrayList<Chat> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Chat> info = new ArrayList<>();
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
        return o != null && o instanceof Chat && ((Chat) o).msgId.equals(msgId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.msgId);
        dest.writeInt(this.time);
        dest.writeString(this.content);
        dest.writeInt(this.msgType);
        dest.writeInt(this.direction);
        dest.writeString(this.userId);
        dest.writeString(this.imageUrl);
        dest.writeString(this.createTime);
        dest.writeInt(this.count);
    }

    public Chat() {
    }

    protected Chat(Parcel in) {
        this.msgId = in.readString();
        this.time = in.readInt();
        this.content = in.readString();
        this.msgType = in.readInt();
        this.direction = in.readInt();
        this.userId = in.readString();
        this.imageUrl = in.readString();
        this.createTime = in.readString();
        this.count = in.readInt();
    }

    public static final Parcelable.Creator<Chat> CREATOR = new Parcelable.Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel source) {
            return new Chat(source);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };
}
