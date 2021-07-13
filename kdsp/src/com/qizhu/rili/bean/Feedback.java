package com.qizhu.rili.bean;

import android.text.TextUtils;

import com.qizhu.rili.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 11/5/15.
 * 意见反馈
 */
public class Feedback {
    public String fbId;                 //id
    public String userId;               //用户id
    public String imageUrl;             //图片地址
    public String nickName;             //用户昵称
    public int time;                    //发布时间
    public String content;              //反馈内容
    public String replyUserId;          //回复的userId,兼容服务端的数据库设计，所以不为空则为我们系统的回复，此时为口袋神婆的id
    public String replyImageUrl;        //回复的图片地址
    public String replyNickName;        //回复的昵称
    public int type;                    //类型，0为文本，1为图片
    public int width;                   //图片宽度
    public int height;                  //图片高度

    public static Feedback parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Feedback feedback = new Feedback();
        feedback.fbId = json.optString("fbId");
        feedback.userId = json.optString("userId");
        feedback.imageUrl = json.optString("imageUrl");
        feedback.nickName = json.optString("nickName");
        String time = json.optString("addTime");
        if (!TextUtils.isEmpty(time)) {
            feedback.time = DateUtils.getIntFromDateString(time);
        }
        feedback.content = json.optString("content");
        feedback.replyUserId = json.optString("replyUserId");
        feedback.replyImageUrl = json.optString("replyImageUrl");
        feedback.replyNickName = json.optString("replyNickName");
        feedback.type = json.optInt("type");
        feedback.width = json.optInt("width");
        feedback.height = json.optInt("height");

        return feedback;
    }

    public static ArrayList<Feedback> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Feedback> info = new ArrayList<Feedback>();
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
        return o != null && o instanceof Feedback && ((Feedback) o).fbId.equals(fbId);
    }
}
