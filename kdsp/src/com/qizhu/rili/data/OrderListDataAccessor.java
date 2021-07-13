package com.qizhu.rili.data;

import android.text.TextUtils;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.bean.OrderDetail;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnDataGetListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindow on 14/03/2017.
 * 获得订单数据
 */

public class OrderListDataAccessor extends AbstractDataAccessor<OrderDetail> {
    private int mMode;

    public OrderListDataAccessor(int mode) {
        mMode = mode;
    }

    @Override
    protected void getDataFromServer(final OnDataGetListener<OrderDetail> listener, final boolean useAppend) {
        KDSPApiController.getInstance().findOrderByUserIdAndStatus(getPage(useAppend), 10, mMode, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                if (response != null) {
                    JSONArray listJson = response.optJSONArray("goodsOrders");
                    final ArrayList<OrderDetail> obtainList = OrderDetail.parseListFromJSON(listJson);
                    //主线程更新UI
                    AppContext.getAppHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            //获取数据后的回调
                            listener.onGetData(DataMessage.buildServerSuccessDataMessage(
                                    obtainList.size(),
                                    handleNewData(useAppend, obtainList, DataMessage.SUCCESS_FROM_SERVER),
                                    getTotalNum(),
                                    mData
                            ));
                        }
                    });
                }
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                String msg = error.getMessage();
                listener.onGetData(DataMessage.<OrderDetail>buildFailDataMessage(TextUtils.isEmpty(msg) ? MSG_API_FAILURE_ERROR : msg));
            }
        });
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    @Override
    protected boolean getDataFromDb(OnDataGetListener<OrderDetail> listener) {
        return false;
    }

    @Override
    protected void afterAppendNewData(List<OrderDetail> data, int msgStatus) {

    }

    @Override
    protected void afterResetData(List<OrderDetail> data, int msgStatus) {

    }
}
