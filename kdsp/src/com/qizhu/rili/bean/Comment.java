package com.qizhu.rili.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zhouyue on 25/8/2017
 * 评论
 */

public class Comment implements Parcelable {
    public String articleId;                //id
    public String commentId;                //id
    public String commentMsg;              //评论内容
    public String createTimeStr;          //评论时间
    public String headImg;                //头像
    public String nickName;                //昵称

    public static Comment parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Comment info = new Comment();
        info.articleId = json.optString("articleId");
        info.commentId = json.optString("commentId");
        info.commentMsg = json.optString("commentMsg");
        info.createTimeStr = json.optString("createTimeStr");
        info.headImg = json.optString("headImg");
        info.nickName = json.optString("nickName");
        return info;
    }

    public static ArrayList<Comment> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Comment> info = new ArrayList<>();
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
        return o != null && o instanceof Comment && ((Comment) o).articleId.equals(articleId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.articleId);
        dest.writeString(this.commentId);
        dest.writeString(this.commentMsg);
        dest.writeString(this.createTimeStr);
        dest.writeString(this.headImg);
        dest.writeString(this.nickName);

    }

    public Comment() {
    }

    protected Comment(Parcel in) {
        this.articleId = in.readString();
        this.commentId = in.readString();
        this.commentMsg = in.readString();
        this.createTimeStr = in.readString();
        this.headImg = in.readString();
        this.nickName = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
