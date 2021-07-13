package com.qizhu.rili.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zhouyue on 25/8/2017
 * 文章
 */

public class Article implements Parcelable {
    public String articleId;                //id
    public int    commentCount;             //评论数
    public int    readCount;               //阅读量
    public String poster;                  //海报
    public String title;                   //标题
    public String subTitle;                //神婆日报
    public String content;                //
    public String fileUrl;                //
    public int    articleType;             //articleType=1文章，articleType=2语音，不传是全部
    public int isCollect;                   //isCollect:是否收藏(0是，1否)

    public static Article parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Article info = new Article();
        info.articleId = json.optString("articleId");
        info.commentCount = json.optInt("commentCount");
        info.readCount = json.optInt("readCount");
        info.poster = json.optString("poster");
        info.title = json.optString("title");
        info.subTitle = json.optString("subTitle");
        info.content = json.optString("content");
        info.fileUrl = json.optString("fileUrl");
        info.articleType = json.optInt("articleType");
        info.isCollect = json.optInt("isCollect");

        return info;
    }

    public static ArrayList<Article> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Article> info = new ArrayList<>();
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
        return o != null && o instanceof Article && ((Article) o).articleId.equals(articleId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.articleId);
        dest.writeInt(this.commentCount);
        dest.writeInt(this.readCount);
        dest.writeString(this.poster);
        dest.writeString(this.title);
        dest.writeString(this.subTitle);
        dest.writeString(this.content);
        dest.writeString(this.fileUrl);
        dest.writeInt(this.articleType);
        dest.writeInt(this.isCollect);
    }

    public Article() {
    }

    protected Article(Parcel in) {
        this.articleId = in.readString();
        this.commentCount = in.readInt();
        this.readCount = in.readInt();
        this.poster = in.readString();
        this.subTitle = in.readString();
        this.content = in.readString();
        this.fileUrl = in.readString();
        this.title = in.readString();
        this.articleType = in.readInt();
        this.isCollect = in.readInt();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
