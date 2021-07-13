package com.qizhu.rili.data;

import android.text.TextUtils;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.bean.YSRLTest;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnDataGetListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindow on 15/7/31.
 * 测试的accessor
 */
public class TestDataAccessor extends AbstractDataAccessor<YSRLTest> {
    private static final int PAGER_SIZE = 10;
    private boolean mIsMine;                //是否是我的
    private String mCategoryId;             //类别id

    public TestDataAccessor(boolean isMine) {
        mIsMine = isMine;
    }

    @Override
    public void getDataFromServer(final OnDataGetListener<YSRLTest> listener, final boolean useAppend) {
        if (mIsMine) {
            KDSPApiController.getInstance().findMyTestList(getPage(useAppend), PAGER_SIZE, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    if (response != null) {
                        JSONArray listJson = response.optJSONArray("tests");
                        final ArrayList<YSRLTest> obtainList = YSRLTest.parseListFromJSON(listJson);

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
                    listener.onGetData(DataMessage.<YSRLTest>buildFailDataMessage(TextUtils.isEmpty(msg) ? MSG_API_FAILURE_ERROR : msg));
                }
            });
        } else {
            if (TextUtils.isEmpty(mCategoryId)) {
                KDSPApiController.getInstance().findTestList(getPage(useAppend), PAGER_SIZE, new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(JSONObject response) {
                        if (response != null) {
                            JSONArray listJson = response.optJSONArray("tests");
                            final ArrayList<YSRLTest> obtainList = YSRLTest.parseListFromJSON(listJson);

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
                        listener.onGetData(DataMessage.<YSRLTest>buildFailDataMessage(TextUtils.isEmpty(msg) ? MSG_API_FAILURE_ERROR : msg));
                    }
                });
            } else {
                KDSPApiController.getInstance().findTestListByTcId(mCategoryId, getPage(useAppend), PAGER_SIZE, new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(JSONObject response) {
                        if (response != null) {
                            JSONArray listJson = response.optJSONArray("tests");
                            final ArrayList<YSRLTest> obtainList = YSRLTest.parseListFromJSON(listJson);

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
                        listener.onGetData(DataMessage.<YSRLTest>buildFailDataMessage(TextUtils.isEmpty(msg) ? MSG_API_FAILURE_ERROR : msg));
                    }
                });
            }
        }
    }

    public void setCategoryId(String categoryId) {
        mCategoryId = categoryId;
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
