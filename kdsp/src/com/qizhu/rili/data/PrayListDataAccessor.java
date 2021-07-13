package com.qizhu.rili.data;

import android.text.TextUtils;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.bean.ShakingSign;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnDataGetListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindow on 3/31/16.
 * 签文记录的accessor
 */
public class PrayListDataAccessor extends AbstractDataAccessor<ShakingSign> {

    public PrayListDataAccessor() {

    }

    @Override
    protected void getDataFromServer(final OnDataGetListener<ShakingSign> listener, final boolean useAppend) {
        KDSPApiController.getInstance().findShaking(getPage(useAppend), 10, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                if (response != null) {
                    JSONArray listJson = response.optJSONArray("shakings");
                    final ArrayList<ShakingSign> obtainList = ShakingSign.parseListFromJSON(listJson);

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
                listener.onGetData(DataMessage.<ShakingSign>buildFailDataMessage(TextUtils.isEmpty(msg) ? MSG_API_FAILURE_ERROR : msg));
            }
        });
    }

    @Override
    protected boolean getDataFromDb(OnDataGetListener<ShakingSign> listener) {
        return false;
    }

    @Override
    protected void afterAppendNewData(List<ShakingSign> data, int msgStatus) {

    }

    @Override
    protected void afterResetData(List<ShakingSign> data, int msgStatus) {

    }
}
