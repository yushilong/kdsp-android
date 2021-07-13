package com.qizhu.rili.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.qizhu.rili.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 2/29/16.
 * 聊天的bean
 */
public class Divination implements Parcelable {
    public String dtId;                         //id
    public int type;                            //消息类型，0文字1图片2语音
    public String word;                         //内容
    public String askSth;                       //询问类别
    public long birthday;                       //生日
    public long createTime;                     //创建时间
    public String leftUrl;                      //左手手相
    public String rightUrl;                     //右手手相
    public int isLike;                          //是否喜欢，即点赞，默认为0 未点赞，1为赞，2为不赞
    public int isAnswer;                        //后台是否回复，为0则没有回复，为1则已经回复
    public int answerType;                      //回复类型，0文字1图片2语音
    public String content;                      //回复内容
    public int isPay;                           //支付状态，0是未支付，1为已支付

    public ArrayList<Divination> answers = new ArrayList<Divination>();        //回复的聊天记录

    public static Divination parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Divination chat = new Divination();
        chat.dtId = json.optString("dtId");
        chat.type = json.optInt("type");
        chat.word = json.optString("word");
        chat.askSth = json.optString("askSth");
        chat.createTime = DateUtils.toDate(json.optString("createTime")).getTime();
        if (!TextUtils.isEmpty(json.optString("birthday"))) {
            chat.birthday = DateUtils.toSimpleDate(json.optString("birthday")).getTime();
        }

        chat.leftUrl = json.optString("leftUrl");
        chat.rightUrl = json.optString("rightUrl");
        chat.isLike = json.optInt("isLike");
        chat.isAnswer = json.optInt("isAnswer");
        chat.answerType = json.optInt("answerType");
        chat.content = json.optString("content");
        chat.isPay = json.optInt("isPay");

        chat.answers = parseListFromJSON(json.optJSONArray("answers"));

        return chat;
    }

    public static ArrayList<Divination> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Divination> info = new ArrayList<Divination>();
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
        return o != null && o instanceof Divination && ((Divination) o).dtId.equals(dtId);
    }

    public Divination() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.dtId);
        dest.writeInt(this.type);
        dest.writeString(this.word);
        dest.writeString(this.askSth);
        dest.writeLong(this.birthday);
        dest.writeLong(this.createTime);
        dest.writeString(this.leftUrl);
        dest.writeString(this.rightUrl);
        dest.writeInt(this.isLike);
        dest.writeInt(this.isAnswer);
        dest.writeInt(this.answerType);
        dest.writeString(this.content);
        dest.writeInt(this.isPay);
        dest.writeTypedList(this.answers);
    }

    protected Divination(Parcel in) {
        this.dtId = in.readString();
        this.type = in.readInt();
        this.word = in.readString();
        this.askSth = in.readString();
        this.birthday = in.readLong();
        this.createTime = in.readLong();
        this.leftUrl = in.readString();
        this.rightUrl = in.readString();
        this.isLike = in.readInt();
        this.isAnswer = in.readInt();
        this.answerType = in.readInt();
        this.content = in.readString();
        this.isPay = in.readInt();
        this.answers = in.createTypedArrayList(Divination.CREATOR);
    }

    public static final Creator<Divination> CREATOR = new Creator<Divination>() {
        @Override
        public Divination createFromParcel(Parcel source) {
            return new Divination(source);
        }

        @Override
        public Divination[] newArray(int size) {
            return new Divination[size];
        }
    };
}
