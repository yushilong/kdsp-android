package com.qizhu.rili.data;

import android.text.TextUtils;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.bean.AuguryItem;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnDataGetListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindow on 8/17/16.
 * 订单列表
 */
public class AuguryListDataAccessor extends AbstractDataAccessor<AuguryItem> {
    private boolean mHasReply;                  //是否已回复

    public AuguryListDataAccessor(boolean hasReply) {
        mHasReply = hasReply;
    }

    @Override
    protected void getDataFromServer(final OnDataGetListener<AuguryItem> listener, final boolean useAppend) {
        if (mHasReply) {
            KDSPApiController.getInstance().findRepliedOrderList(getPage(useAppend), 10, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    if (response != null) {
                        JSONArray listJson = response.optJSONArray("itemOrders");
                        final ArrayList<AuguryItem> obtainList = AuguryItem.parseListFromJSON(listJson);

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
                    listener.onGetData(DataMessage.<AuguryItem>buildFailDataMessage(TextUtils.isEmpty(msg) ? MSG_API_FAILURE_ERROR : msg));
                }
            });
        } else {
            KDSPApiController.getInstance().findPreReplyOrderList(getPage(useAppend), 10, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    if (response != null) {
                        JSONArray listJson = response.optJSONArray("itemOrders");
                        final ArrayList<AuguryItem> obtainList = AuguryItem.parseListFromJSON(listJson);

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
                    listener.onGetData(DataMessage.<AuguryItem>buildFailDataMessage(TextUtils.isEmpty(msg) ? MSG_API_FAILURE_ERROR : msg));
                }
            });
        }
    }

    @Override
    protected boolean getDataFromDb(OnDataGetListener<AuguryItem> listener) {
        return false;
    }

    @Override
    protected void afterAppendNewData(List<AuguryItem> data, int msgStatus) {

    }

    @Override
    protected void afterResetData(List<AuguryItem> data, int msgStatus) {

    }
}
