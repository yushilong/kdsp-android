package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 10/03/2017.
 * 订单item
 */

public class OrderItem {
    //退款进度,0是填写表单,1是等待初审,2是卖家同意,3是卖家驳回,4等待终审,5卖家拒绝,6是退款成功
    public static final int REFUND = 0, REFUNDING = 1, REFUNDED = 2, REFUND_REJECT = 3, WAITING = 4, REFUND_FEFUSE = 5, REFUND_SUCCESS = 6;
    //退款状态,0是正常,1退款中,2是退款完成,3退款失败
    public static final int REFUND_START = 0, REFUND_WAITING = 1, REFUND_END = 2, REFUND_FAILED = 3;

    public String goodsId;                  //商品id
    public String goodsName;                //名称
    public String[] images;
    public String skuId;                    //图片地址
    public String spec;                     //支付时间
    public int price;                       //价格
    public int count;                       //数量
    public int goodsType;                       //商品类型（默认为0珠串，1符咒）
    public int detailStatus;                //退款状态,0是正常,1退款中,2是退款完成,3退款失败
    public String odId;                     //订单id
    public String orderId;                  //父订单id
    public String birthday;                  //出生日期
    public DateTime dateTime;                  //出生日期
    public int mode;                   //0阳历

    public static OrderItem parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        OrderItem info = new OrderItem();
        info.goodsId = json.optString("goodsId");
        info.goodsName = json.optString("goodsName");
        info.images = json.optString("images").split(",");
        if (info.images.length == 0) {
            info.images[0] = "";
        }
        info.skuId = json.optString("skuId");
        info.spec = json.optString("spec");
        info.price = json.optInt("price");
        info.count = json.optInt("count");
        info.detailStatus = json.optInt("detailStatus");
        info.goodsType = json.optInt("goodsType");
        info.odId = json.optString("odId");
        info.orderId = json.optString("orderId");

        return info;
    }

    public static ArrayList<OrderItem> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<OrderItem> info = new ArrayList<OrderItem>();
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
