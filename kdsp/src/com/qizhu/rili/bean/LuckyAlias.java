package com.qizhu.rili.bean;

import com.j256.ormlite.field.DatabaseField;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 2/21/16.
 * 吉日别名
 */
public class LuckyAlias {
    public static String NAME_PARAM = "ldName";

    @DatabaseField(id = true)
    public String ldName;                   //名称
    @DatabaseField
    public String alias;                    //别名

    public static LuckyAlias parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        LuckyAlias alias = new LuckyAlias();
        alias.ldName = json.optString("ldName");
        StringBuilder stringBuilder = new StringBuilder();
        JSONArray jsonArray = json.optJSONArray("luckydayAlias");
        if (jsonArray != null) {
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                stringBuilder.append(jsonArray.optJSONObject(i).optString("name")).append(",");
            }
        }
        alias.alias = String.valueOf(stringBuilder);

        return alias;
    }

    public static ArrayList<LuckyAlias> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<LuckyAlias> info = new ArrayList<LuckyAlias>();
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
