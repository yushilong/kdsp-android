package com.qizhu.rili.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 10/03/2017.
 * 订单详情项
 */

public class OrderDetail {
    //状态,1、未付款，2、已付款、未发货，3、已发货，4、交易成功，5、交易关闭,6、退款中,100已完成
    public static final int WAIT_PAY = 1, HAS_PAID = 2, HAS_SEND = 3, SUCCESS = 4, CANCEL = 5, REFUND = 6, COMPLETED = 100;

    public String orderId;                      //订单id
    public String orderNum;                     //订单编号
    public int status;                          //状态,1、未付款，2、已付款、未发货，3、已发货，4、交易成功，5、交易关闭,6、退款中
    public int pointSum;                        //花费福豆
    public int shipFee;                         //邮费
    public int totalFee;                        //总价
    public int orderRefundStatus;               //退款状态,0是正常,1退款中,2是退款完成,3退款失败
    public ArrayList<OrderItem> mOrderItems = new ArrayList<>();

    public static OrderDetail parseObjectFromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        OrderDetail info = new OrderDetail();
        info.orderId = json.optString("orderId");
        info.orderNum = json.optString("orderNum");
        info.status = json.optInt("status");
        info.pointSum = json.optInt("pointSum");
        info.shipFee = json.optInt("shipFee");
        info.totalFee = json.optInt("totalFee");
        info.orderRefundStatus = json.optInt("orderRefundStatus");
        info.mOrderItems = OrderItem.parseListFromJSON(json.optJSONArray("orderDetails"));

        return info;
    }

    public static ArrayList<OrderDetail> parseListFromJSON(JSONArray jsonArray) {
        ArrayList<OrderDetail> info = new ArrayList<>();
        if (jsonArray == null) {
            return info;
        }
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            info.add(parseObjectFromJSON(jsonArray.optJSONObject(i)));
        }
        return info;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof OrderDetail && ((OrderDetail) o).orderId.equals(orderId);
    }
}
