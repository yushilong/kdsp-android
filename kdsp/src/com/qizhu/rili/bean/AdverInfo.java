package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdverInfo {
	
	public String carouselId;
	public String imgUrl;//网络图片资源
	public String linkUrl;//

	public static AdverInfo parseObjectFromJSON(JSONObject json) {
		if (json == null) {
			return null;
		}
		AdverInfo banner = new AdverInfo();
		banner.carouselId = json.optString("carouselId");
		banner.imgUrl = json.optString("imgUrl");
		banner.linkUrl = json.optString("linkUrl");

		return banner;
	}

	public static ArrayList<AdverInfo> parseListFromJSON(JSONArray jsonArray) {
		ArrayList<AdverInfo> banners = new ArrayList<AdverInfo>();
		if (jsonArray == null) {
			return banners;
		}
		int length = jsonArray.length();
		for (int i = 0; i < length; i++) {
			banners.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
		}
		return banners;
	}
	

	
}
