package com.qizhu.rili.data;

import android.text.TextUtils;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.bean.Goods;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnDataGetListener;
import com.qizhu.rili.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindow on 01/03/2017.
 * 商品数据
 */

public class GoodsDataAccessor extends AbstractDataAccessor<Goods> {
    private String mBirthday = "";
    private boolean mIsTheme;                   //是否主题
    private String  classifyId;                   //是否主题
    private int     mType;

    public GoodsDataAccessor(String birthday, boolean isTheme, int type) {
        mBirthday = birthday;
        mIsTheme = isTheme;
        mType = type;
    }

    @Override
    protected void getDataFromServer(final OnDataGetListener<Goods> listener, final boolean useAppend) {
        KDSPApiController.getInstance().getGoodsList(mType,mIsTheme, getPage(useAppend), 10, mBirthday, classifyId, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                LogUtils.d("response:" + response);
                if (response != null) {
                    JSONArray listJson = response.optJSONArray("goods");
                    final ArrayList<Goods> obtainList = Goods.parseListFromJSON(listJson);
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
                listener.onGetData(DataMessage.<Goods>buildFailDataMessage(TextUtils.isEmpty(msg) ? MSG_API_FAILURE_ERROR : msg));
            }
        });
    }

    public void setBirthday(String birthday) {
        mBirthday = birthday;
    }

    public void setclassifyId(String classifyId) {
        this.classifyId = classifyId;
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
