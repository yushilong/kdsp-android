package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 3/30/16.
 * 轮播图
 */
public class Carousel {
    public String carouselId;
    public String title;
    public String description;
    public String imgUrl;
    public String linkUrl;

    public static Carousel parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        Carousel carousel = new Carousel();

        carousel.carouselId = json.optString("carouselId");
        carousel.title = json.optString("title");
        carousel.description = json.optString("description");
        carousel.imgUrl = json.optString("imgUrl");
        carousel.linkUrl = json.optString("linkUrl");

        return carousel;
    }

    public static ArrayList<Carousel> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<Carousel> infos = new ArrayList<Carousel>();
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
        return o != null && o instanceof Carousel && ((Carousel) o).carouselId.equals(carouselId);
    }
}
