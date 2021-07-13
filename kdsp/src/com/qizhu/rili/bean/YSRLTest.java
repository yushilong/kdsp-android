package com.qizhu.rili.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 15/7/31.
 * 测试
 */
@DatabaseTable
public class YSRLTest {
    public static final String TEST_TIME_PARAM = "mTestedTime";

    @DatabaseField(id = true)
    public String testId;                   //测试id
    @DatabaseField
    public String description;              //描述
    @DatabaseField
    public String title;                    //标题
    @DatabaseField
    public String imgUrl;                   //图片
    @DatabaseField
    public String linkUrl;                  //链接
    @DatabaseField
    public int attationVal;                 //测试的人数
    @DatabaseField
    public boolean mHasTested;              //是否已经测试过
    @DatabaseField
    public int userState;               //是否是匿名用户,默认是匿名用户,0为匿名，1为正式注册账号
    @DatabaseField
    public long mTestedTime;                 //用户测试的时间

    public static YSRLTest parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        YSRLTest ysrlTest = new YSRLTest();

        ysrlTest.testId = json.optString("testId");
        ysrlTest.description = json.optString("description");
        ysrlTest.title = json.optString("title");
        ysrlTest.imgUrl = json.optString("imgUrl");
        ysrlTest.linkUrl = json.optString("linkUrl");
        ysrlTest.attationVal = json.optInt("attationVal");

        return ysrlTest;
    }

    public static ArrayList<YSRLTest> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<YSRLTest> infos = new ArrayList<YSRLTest>();
        if (jsonArray == null) {
            return infos;
        }
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            infos.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
        }
        return infos;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof YSRLTest && ((YSRLTest) o).testId.equals(testId);
    }
}
