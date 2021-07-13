package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 15/8/12.
 * image bean
 */
public class YSRLImage {
    public String filePath;         //图片地址
    public int width;               //宽度
    public int height;              //高度

    public static YSRLImage parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        YSRLImage ysrlImage = new YSRLImage();
        ysrlImage.filePath = json.optString("filePath");
        ysrlImage.width = json.optInt("width");
        ysrlImage.height = json.optInt("height");
        return ysrlImage;
    }

    public static ArrayList<YSRLImage> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<YSRLImage> infos = new ArrayList<YSRLImage>();
        if (jsonArray == null) {
            return infos;
        }
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            infos.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
        }
        return infos;
    }
}
