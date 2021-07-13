package com.qizhu.rili.bean;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.qizhu.rili.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 2015/11/13.
 * 启动图的bean
 */
@DatabaseTable
public class StartupImage {
    public static final String ID = "stId";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String SHOW_TIMES = "showTimes";

    @DatabaseField(id = true)
    public String stId;                 //id
    @DatabaseField
    public String imageUrl;             //启动界面图片
    @DatabaseField
    public String colorVal;             //启动界面背景色
    @DatabaseField
    public String linkUrl;              //启动界面点击url
    @DatabaseField
    public int startTime;               //启动界面图片开始时间,将服务器返回的时间转为int
    @DatabaseField
    public int endTime;                 //启动界面图片结束时间
    @DatabaseField
    public int duration;                //启动界面图片持续时间,ms为单位
    @DatabaseField
    public int showTimes;               //启动界面图片显示次数
    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    public byte[] bytes;                //图片的字节数组

    public StartupImage() {
    }

    public static StartupImage parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }

        StartupImage startupImage = new StartupImage();
        startupImage.stId = json.optString("stId");
        startupImage.imageUrl = json.optString("imageUrl");
//        startupImage.imageUrl = "/2015/12/18/2d5d0690-9992-450c-b0e0-40d5b250b48c.png";
        startupImage.colorVal = json.optString("colorVal");
        startupImage.linkUrl = json.optString("linkUrl");
        startupImage.startTime = DateUtils.getIntFromDateString(json.optString("startTime"));
        startupImage.endTime = DateUtils.getIntFromDateString(json.optString("endTime"));
        startupImage.duration = json.optInt("duration");
        startupImage.showTimes = json.optInt("showTimes");

        return startupImage;
    }

    public static ArrayList<StartupImage> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<StartupImage> info = new ArrayList<StartupImage>();
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
        return o != null && o instanceof StartupImage && ((StartupImage) o).stId.equals(stId);
    }
}
