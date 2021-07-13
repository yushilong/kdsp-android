package com.qizhu.rili.bean;

import com.qizhu.rili.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lindow on 1/13/16.
 * 测试题的分类
 */
public class Bazi {
    public String name;         //名字
    public String title;        //标题
    public float percent;       //百分比
    public String desc;        //内容
    public Map<String,Object> attrs;        //内容


    public static Bazi parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Bazi testCategory = new Bazi();
        testCategory.name = json.optString("name");
        testCategory.title = json.optString("title");
        String percent = json.optString("percent");
        testCategory.percent = StringUtils.toFloat(percent.substring(0, percent.indexOf("%")));
        testCategory.desc = json.optString("desc");
        testCategory.attrs = getMap(json.optString("attrs"));

        return testCategory;
    }

    public static ArrayList<Bazi> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Bazi> testCategories = new ArrayList<Bazi>();
        if (jsonArray == null) {
            return testCategories;
        }
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            testCategories.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
        }
        return testCategories;
    }
    public static List<Map<String, Object>> getList(String jsonString)

    {

        List<Map<String, Object>> list = null;

        try

        {

            JSONArray jsonArray = new JSONArray(jsonString);

            JSONObject jsonObject;

            list = new ArrayList<Map<String, Object>>();

            for (int i = 0; i < jsonArray.length(); i++)

            {

                jsonObject = jsonArray.getJSONObject(i);

                list.add(getMap(jsonObject.toString()));

            }

        }

        catch (Exception e)

        {

            e.printStackTrace();

        }

        return list;

    }

    public static  Map<String, Object> getMap(String jsonString)

    {

        JSONObject jsonObject;

        try

        {

            jsonObject = new JSONObject(jsonString);

        Iterator<String> keyIter = jsonObject.keys();

            String key;

            Object value;

            Map<String, Object> valueMap = new  LinkedHashMap<String, Object>();

            while (keyIter.hasNext())

            {

                key = (String) keyIter.next();

                value = jsonObject.get(key);

                valueMap.put(key, value);

            }

            return valueMap;

        }

        catch (JSONException e)

        {

            e.printStackTrace();

        }

        return null;

    }
}
