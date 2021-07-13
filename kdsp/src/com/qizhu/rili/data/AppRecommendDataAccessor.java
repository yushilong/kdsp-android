package com.qizhu.rili.data;

import android.text.TextUtils;

import com.qizhu.rili.bean.AppInfo;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnDataGetListener;
import com.qizhu.rili.ui.activity.BaseActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用推荐
 */
public class AppRecommendDataAccessor extends AbstractDataAccessor<AppInfo> {
    private static final int PAGER_SIZE = 20;
    public BaseActivity mActivity;      //当前的界面

    public AppRecommendDataAccessor() {
    }

    public AppRecommendDataAccessor(BaseActivity baseActivity) {
        mActivity = baseActivity;
    }

    @Override
    public void getDataFromServer(final OnDataGetListener<AppInfo> listener, final boolean useAppend) {
        KDSPApiController.getInstance().findApplication(getPage(useAppend), PAGER_SIZE, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                JSONArray listJson = response.optJSONArray("applications");
                final ArrayList<AppInfo> obtainList = AppInfo.parseListFromJSON(listJson);
                if (mActivity != null) {
                    mActivity.runOnUiThread(new Runnable() {
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
                } else {
                    //获取数据后的回调
                    listener.onGetData(DataMessage.buildServerSuccessDataMessage(
                            obtainList.size(),
                            handleNewData(useAppend, obtainList, DataMessage.SUCCESS_FROM_SERVER),
                            getTotalNum(),
                            mData
                    ));
                }
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                String msg = error.getMessage();
                listener.onGetData(DataMessage.<AppInfo>buildFailDataMessage(TextUtils.isEmpty(msg) ? MSG_API_FAILURE_ERROR : msg));
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
