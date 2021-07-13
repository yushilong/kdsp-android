package com.qizhu.rili.utils;

import android.os.Build;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * json工具集
 */
public class JSONUtils {
    /**
     * 将jsonObject中的value，以 , 拼接
     */
    public static String appendJSONObjectToStr(JSONObject jsonObject) {
        ArrayList<ArrayData> dataList = new ArrayList<ArrayData>();
        StringBuilder sb = new StringBuilder("");
        String rtn = "";
        if(jsonObject != null){
            Iterator<String> keyItr = jsonObject.keys();
            while (keyItr.hasNext()){
                String key = keyItr.next();
                ArrayData data = new ArrayData();
                data.value = jsonObject.optString(key, "");
                data.order = Integer.parseInt(key);
                dataList.add(data);
            }

            //按key升序排序
            Collections.sort(dataList, new Comparator<ArrayData>() {
                @Override
                public int compare(ArrayData arrayData, ArrayData arrayData2) {
                    return arrayData.order - arrayData2.order;
                }
            });

            if (!dataList.isEmpty()) {
                 for (ArrayData data : dataList) {
                     sb.append(data.value).append(",");
                 }
            }
            if(sb.length() > 0){
                rtn = sb.substring(0,sb.length()-1);//去除最后一个逗号
            }
        }
        return rtn;
    }

    /**
     * 解析JSONObject
     */
    public static JSONObject parseJSONObject(String jsonStr) {
        if(TextUtils.isEmpty(jsonStr)){
            return null;
        }
        //android 4.0 以下错误
        //Value ﻿ of type java.lang.String cannot be converted to JSONObject 错误解决
        //详细参考 http://www.xuebuyuan.com/114109.html
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (jsonStr.startsWith("\ufeff")) {
                jsonStr = jsonStr.substring(1);
            }
        }
        try {
            Object rtn = parseStr(jsonStr);
            if(rtn instanceof JSONObject){
                return (JSONObject)rtn;
            }else{
                LogUtils.d("json parse error " + rtn.getClass().getName() + jsonStr );
                return null;
            }
        } catch (JSONException e) {
            LogUtils.e("json parse error " + jsonStr,e);
            return null;
        }
    }


    public static JSONArray parseJSONArray(String jsonStr) {
        if(TextUtils.isEmpty(jsonStr)){
            return null;
        }
        try {
            Object rtn = parseStr(jsonStr);
            if(rtn instanceof JSONArray){
                return (JSONArray)rtn;
            }else{
                return null;
            }
        } catch (JSONException e) {
            return null;
        }
    }

    private static Object parseStr(String jsonStr) throws JSONException {
        Object rtn;
        jsonStr = jsonStr.trim();
        try{
            rtn = new JSONTokener(jsonStr).nextValue();
        }catch (JSONException e){
            rtn = null;
        }
        if(rtn == null){
            rtn = jsonStr;
        }
        return rtn;
    }

    private static class ArrayData {
        public String value;
        public int order;
    }
}
