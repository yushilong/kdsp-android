package com.qizhu.rili.data;

import android.text.TextUtils;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.bean.Cart;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnDataGetListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lindow on 08/03/2017.
 * 购物车
 */

public class CartDataAccessor extends AbstractDataAccessor<Cart> {

    @Override
    protected void getDataFromServer(final OnDataGetListener<Cart> listener, final boolean useAppend) {
        KDSPApiController.getInstance().findCartsByUserId(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                if (response != null) {
                    JSONArray listJson = response.optJSONArray("carts");
                    final ArrayList<Cart> obtainList = Cart.parseListFromJSON(listJson);
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
                listener.onGetData(DataMessage.<Cart>buildFailDataMessage(TextUtils.isEmpty(msg) ? MSG_API_FAILURE_ERROR : msg));
            }
        });
    }

    @Override
    protected Comparator<Cart> getSortComparator() {
        return new Comparator<Cart>() {
            @Override
            public int compare(Cart cart1, Cart cart2) {
                //根据购物车的商品状态排序
                int status1 = 0, status2 = 0;
                if (cart1.sku != null) {
                    status1 = cart1.sku.showStatus;
                }
                if (cart2.sku != null) {
                    status2 = cart2.sku.showStatus;
                }
                return status1 - status2;
            }
        };
    }

    @Override
    protected boolean getDataFromDb(OnDataGetListener<Cart> listener) {
        return false;
    }

    @Override
    protected void afterAppendNewData(List<Cart> data, int msgStatus) {

    }

    @Override
    protected void afterResetData(List<Cart> data, int msgStatus) {

    }
}
