package com.qizhu.rili.data;

import android.text.TextUtils;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.bean.Divination;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnDataGetListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindow on 2/29/16.
 * 聊天列表的data
 */
public class WordRewardListDataAccessor extends AbstractDataAccessor<Divination> {

    public WordRewardListDataAccessor() {

    }

    @Override
    protected void getDataFromServer(final OnDataGetListener<Divination> listener, final boolean useAppend) {
        KDSPApiController.getInstance().findPayDivinationList(getPage(useAppend), 10, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                if (response != null) {
                    JSONArray listJson = response.optJSONArray("divinations");
                    final ArrayList<Divination> obtainList = Divination.parseListFromJSON(listJson);

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
                listener.onGetData(DataMessage.<Divination>buildFailDataMessage(TextUtils.isEmpty(msg) ? MSG_API_FAILURE_ERROR : msg));
            }
        });
    }

    @Override
    protected boolean getDataFromDb(OnDataGetListener<Divination> listener) {
        return false;
    }

    @Override
    protected void afterAppendNewData(List<Divination> data, int msgStatus) {

    }

    @Override
    protected void afterResetData(List<Divination> data, int msgStatus) {

    }
}
