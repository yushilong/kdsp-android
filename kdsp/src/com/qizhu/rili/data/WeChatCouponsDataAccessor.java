package com.qizhu.rili.data;

import android.text.TextUtils;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.bean.one2oneOrders;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnDataGetListener;
import com.qizhu.rili.utils.LogUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindow on 01/03/2017.
 * 商品数据
 */

public class WeChatCouponsDataAccessor extends AbstractDataAccessor<one2oneOrders> {

    public WeChatCouponsDataAccessor() {

    }

    @Override
    protected void getDataFromServer(final OnDataGetListener<one2oneOrders> listener, final boolean useAppend) {




        KDSPApiController.getInstance().findOne2OneOrders( getPage(useAppend), 10, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                LogUtils.d("response:" + response);
                if (response != null) {

                    final ArrayList<one2oneOrders> obtainList = new one2oneOrders().parseListFromJSON(response.optJSONArray("one2oneOrders"));
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
                listener.onGetData(DataMessage.<one2oneOrders>buildFailDataMessage(TextUtils.isEmpty(msg) ? MSG_API_FAILURE_ERROR : msg));
            }
        });
    }


    @Override
    protected boolean getDataFromDb(OnDataGetListener listener) {
        return false;
    }

    @Override
    protected void afterAppendNewData(List data, int msgStatus) {

    }

    @Override
    protected void afterResetData(List data, int msgStatus) {

    }
}
