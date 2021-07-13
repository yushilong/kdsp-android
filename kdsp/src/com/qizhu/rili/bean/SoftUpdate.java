package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 15/7/29.
 * 软件更新bean
 */
public class SoftUpdate {
	public String appUrl;			//下载链接
	public String version;			//version name
	public int versionCode;			//version code
	public String description;		//描述

	public static SoftUpdate parseObjectFromJSON(JSONObject json) {
		if (json == null) {
			return null;
		}
		SoftUpdate softUpdate = new SoftUpdate();
		softUpdate.appUrl = json.optString("appUrl");
		softUpdate.version = json.optString("version");
		softUpdate.versionCode = json.optInt("versionCode");
		softUpdate.description = json.optString("description");

		return softUpdate;
	}

	public static ArrayList<SoftUpdate> parseListFromJSON(JSONArray jsonArray) {
		ArrayList<SoftUpdate> softUpdates = new ArrayList<SoftUpdate>();
		if (jsonArray == null) {
			return softUpdates;
		}
		int length = jsonArray.length();
		for (int i = 0; i < length; i++) {
			softUpdates.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
		}
		return softUpdates;
	}
}
