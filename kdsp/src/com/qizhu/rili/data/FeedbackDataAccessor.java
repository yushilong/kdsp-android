package com.qizhu.rili.data;

import android.text.TextUtils;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.bean.Feedback;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnDataGetListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindow on 11/5/15.
 * 意见反馈的accessor
 */
public class FeedbackDataAccessor extends AbstractDataAccessor<Feedback> {
    private static final int PAGER_SIZE = 20;

    public FeedbackDataAccessor() {
    }

    @Override
    protected void getDataFromServer(final OnDataGetListener<Feedback> listener, final boolean useAppend) {
        KDSPApiController.getInstance().findFeedback(getPage(useAppend), PAGER_SIZE, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                if (response != null) {
                    JSONArray listJson = response.optJSONArray("feedbacks");
                    final ArrayList<Feedback> obtainList = Feedback.parseListFromJSON(listJson);
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
                listener.onGetData(DataMessage.<Feedback>buildFailDataMessage(TextUtils.isEmpty(msg) ? MSG_API_FAILURE_ERROR : msg));
            }
        });
    }

    @Override
    protected boolean getDataFromDb(OnDataGetListener<Feedback> listener) {
        return false;
    }

    @Override
    protected void afterAppendNewData(List<Feedback> data, int msgStatus) {

    }

    @Override
    protected void afterResetData(List<Feedback> data, int msgStatus) {

    }
}
