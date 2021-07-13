package com.qizhu.rili.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.qizhu.rili.AppContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lindow
 * SharedPreferences读取配置类,字段名全部写在YSRLConstants类
 */
public class SPUtils {
    public static String DATA_CONFIG = "data_config";       //配置文件
    public static SharedPreferences instances;
    private final static String regularEx = ",";

    private static SharedPreferences getInstances() {
        if (instances == null) {
            instances = AppContext.baseContext.getSharedPreferences(DATA_CONFIG, Context.MODE_PRIVATE);
        }
        return instances;
    }

    public static void putLongValue(String key, long value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        getInstances().edit().putLong(key, value).apply();
    }

    public static long getLongValue(String key) {
        if (TextUtils.isEmpty(key)) {
            return 0;
        }
        return getInstances().getLong(key, 0);
    }

    public static void putBoolleanValue(String key, boolean value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        getInstances().edit().putBoolean(key, value).apply();
    }

    /**
     * 同步存储布尔值
     */
    public static void putBoolSync(String key, boolean value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        getInstances().edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolleanValue(String key) {
        return getBoolleanValue(key, false);
    }

    public static boolean getBoolleanValue(String key, boolean defaultBool) {
        if (TextUtils.isEmpty(key)) {
            return defaultBool;
        }
        return getInstances().getBoolean(key, defaultBool);
    }

    public static void putIntValue(String key, int value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        getInstances().edit().putInt(key, value).apply();
    }

    public static int getIntValue(String key) {
        return getIntValue(key, 0);
    }

    public static int getIntValue(String key, int defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        return getInstances().getInt(key, defaultValue);
    }

    public static void putStringValue(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        getInstances().edit().putString(key, value).apply();
    }

    public static String getStringValue(String key) {
        return getStringValue(key, "");
    }

    public static String getStringValue(String key, String defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        return getInstances().getString(key, defaultValue);
    }

    public static void putStringArrayValue(String key, String[] value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        String str = Arrays.toString(value);
        String temp = str.substring(1, str.length() - 1);
        getInstances().edit().putString(key, temp).apply();
    }

    public static String[] getStringArrayValue(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        String value = getInstances().getString(key, "");

        return StringUtils.convertStrToArray2(value);
    }

    /**
     * 获取字符串集合
     */
    public static Set<String> getStringSet(String key) {
        String str = getInstances().getString(key, "");
        Set<String> defValues = new HashSet<String>();
        if (!TextUtils.isEmpty(str)) {
            String[] values = str.split(regularEx);
            for (String value : values) {
                if (!TextUtils.isEmpty(value)) {
                    defValues.add(value);
                }
            }
        }
        return defValues;
    }

    /**
     * 存放字符串集合
     */
    public static void putStringSet(String key, Set<String> values) {
        if (values != null && !values.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (String str : values) {
                builder.append(str).append(regularEx);
            }
            getInstances().edit().putString(key, builder.toString()).apply();
        }
    }

    /**
     * 清除指定的key存储
     */
    public static void clearByKey(String key) {

        if (TextUtils.isEmpty(key)) {
            return;
        }
        getInstances().edit().remove(key).apply();
    } /**
     * 清除所有存储
     */
    public static void clear() {
        getInstances().edit().clear().commit();
    }
}
