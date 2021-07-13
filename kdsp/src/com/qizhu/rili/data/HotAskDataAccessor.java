package com.qizhu.rili.data;

import android.text.TextUtils;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.bean.FateItem;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnDataGetListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindow on 06/03/2017.
 * 热门问题
 */

public class HotAskDataAccessor extends AbstractDataAccessor<FateItem> {
    public FateItem mAsk;

    @Override
    protected void getDataFromServer(final OnDataGetListener<FateItem> listener, final boolean useAppend) {
        KDSPApiController.getInstance().findQuestionThemeList(getPage(useAppend), 10, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                if (response != null) {
                    if (1 == getPage()) {
                        mAsk = FateItem.parseObjectFromJSON(response.optJSONArray("masterAskSths").optJSONObject(0));
                    }
                    JSONArray listJson = response.optJSONArray("questions");
                    final ArrayList<FateItem> obtainList = FateItem.parseListFromJSON(listJson);
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
                listener.onGetData(DataMessage.<FateItem>buildFailDataMessage(TextUtils.isEmpty(msg) ? MSG_API_FAILURE_ERROR : msg));
            }
        });
    }

    @Override
    protected boolean getDataFromDb(OnDataGetListener<FateItem> listener) {
        return false;
    }

    @Override
    protected void afterAppendNewData(List<FateItem> data, int msgStatus) {

    }

    @Override
    protected void afterResetData(List<FateItem> data, int msgStatus) {

    }
}
