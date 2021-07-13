package com.qizhu.rili.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Zhang on 2015/4/16.
 * 提醒事件的事件封装
 */
@DatabaseTable
public class EventItem implements Parcelable {
    //事件数组，index值与so里一致
    public static String[] ACTION_ARRAY = {"博彩", "约么", "就医", "购物", "和好", "约会", "表白", "结婚", "旅行", "谈生意", "签约", "聚会", "开张", "搬迁", "置业", "面试", "学习", "交易", "理发", "动土"};

    @DatabaseField(id = true)
    public String Id;          //自增长的主键
    @DatabaseField
    public int index;           //事件的index
    @DatabaseField
    public String title;        //事件的标题
    @DatabaseField
    public String context;      //内容
    @DatabaseField
    public long time;           //时间

    public EventItem() {
    }

    public EventItem(int index, String title, String context, long time) {
        //同一天同一index，同一title的id是一致的
        Id = index + title + time;
        this.index = index;
        this.title = title;
        this.context = context;
        this.time = time;
    }

    @Override
    public String toString() {
        return "EventItem{" +
                "index='" + index + '\'' +
                ", title='" + title + '\'' +
                ", context='" + context + '\'' +
                ", time=" + time +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Id);
        dest.writeInt(this.index);
        dest.writeString(this.title);
        dest.writeString(this.context);
        dest.writeLong(this.time);
    }

    protected EventItem(Parcel in) {
        this.Id = in.readString();
        this.index = in.readInt();
        this.title = in.readString();
        this.context = in.readString();
        this.time = in.readLong();
    }

    public static final Creator<EventItem> CREATOR = new Creator<EventItem>() {
        public EventItem createFromParcel(Parcel source) {
            return new EventItem(source);
        }

        public EventItem[] newArray(int size) {
            return new EventItem[size];
        }
    };
}