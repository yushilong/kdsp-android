package com.qizhu.rili.bean;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.qizhu.rili.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

@DatabaseTable
public class User {
    public static final String USER_ID_PARAM = "userId";

    public static int BOY = 1, GIRL = 2;

    @DatabaseField(id = true)
    public String userId;               //用户id
    @DatabaseField
    public String nickName;             //昵称
    @DatabaseField
    public String imageUrl;             //头像
    @DatabaseField
    public String description = "";     //备注、说明
    @DatabaseField(dataType = DataType.DATE)
    public Date birthTime = DateUtils.getServerDateTime();            //生日,阳历
    @DatabaseField
    public int userSex = 2;             //性别,1为男,2为女(默认)
    @DatabaseField
    public String telephoneNumber;      //手机号码
    @DatabaseField
    public int isLunar = 1;             //isLunar是否农历，0否，1是
    @DatabaseField
    public String blood = "";           //血型
    @DatabaseField
    public int userState;               //是否是匿名用户,默认是匿名用户,0为匿名，1为正式注册账号


    public static User parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        User user = new User();
        user.userId = json.optString("userId");
        user.nickName = json.optString("nickName");
        user.imageUrl = json.optString("imageUrl");
        user.description = json.optString("description");
        user.birthTime = DateUtils.getServerTime(json.optString("birthTime"));
        user.userSex = json.optInt("userSex");
        user.isLunar = json.optInt("isLunar");
        user.userState = json.optInt("userState");
        user.telephoneNumber = json.optString("telephoneNumber");
        user.blood = json.optString("blood");

        return user;
    }

    public static ArrayList<User> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<User> users = new ArrayList<User>();
        if (jsonArray == null) {
            return users;
        }
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            users.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
        }
        return users;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof User && ((User) o).userId.equals(userId);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", imageUrl=" + imageUrl + '\'' +
                ", description=" + description + '\'' +
                ", birthTime=" + birthTime + '\'' +
                ", userSex=" + userSex + '\'' +
                ", isLunar=" + isLunar + '\'' +
                ", telephoneNumber=" + telephoneNumber + '\'' +
                ", blood=" + blood + '\'' +
                '}';
    }
}
