package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 8/17/16.
 * 运势问题属性
 */
public class Features {
    public String alias;                //表单项
    public String featureId;            //表单的id
    public String featureName;          //表单的名称
    public String itemId;               //问题id
    public int inputType;               //为0时，是文本框（日期选择器），为1时，则为单选，单选的值为selectValues里面逗号隔开的，2文件上传，3为编辑框
    public String selectValues;         //单选的值
    public int sort;                    //排序，倒序
    public String content = "";         //表单填写的内容
    public DateTime dateTime ;         //表单填写的内容
    public int mode ;         //0阳历

    public static Features parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Features info = new Features();
        info.alias = json.optString("alias");
        info.itemId = json.optString("itemId");
        info.featureId = json.optString("featureId");
        info.featureName = json.optString("featureName");
        info.selectValues = json.optString("selectValues");
        info.inputType = json.optInt("inputType");
        info.sort = json.optInt("sort");

        return info;
    }

    public static ArrayList<Features> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Features> info = new ArrayList<Features>();
        if (jsonArray == null) {
            return info;
        }
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            info.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
        }
        return info;
    }
}
