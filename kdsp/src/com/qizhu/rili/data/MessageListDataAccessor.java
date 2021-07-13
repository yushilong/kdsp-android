package com.qizhu.rili.data;

import android.text.TextUtils;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.bean.Chat;
import com.qizhu.rili.bean.Feedback;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnDataGetListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindow on 23/03/2017.
 * 留言
 */

public class MessageListDataAccessor extends AbstractDataAccessor<Chat> {
    public String mAvatarUrl = "";

    @Override
    protected void getDataFromServer(final OnDataGetListener<Chat> listener, final boolean useAppend) {
        KDSPApiController.getInstance().getChatMsgList(getPage(useAppend), 10, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                if (response != null) {
                    if (1 == getPage()) {
                        mAvatarUrl = response.optString("imageUrl");
                    }
                    JSONArray listJson = response.optJSONArray("chatMsgs");
                    final ArrayList<Chat> obtainList = Chat.parseListFromJSON(listJson);
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
                listener.onGetData(DataMessage.<Chat>buildFailDataMessage(TextUtils.isEmpty(msg) ? MSG_API_FAILURE_ERROR : msg));
            }
        });
    }

    @Override
    protected boolean getDataFromDb(OnDataGetListener<Chat> listener) {
        return false;
    }

    @Override
    protected void afterAppendNewData(List<Chat> data, int msgStatus) {

    }

    @Override
    protected void afterResetData(List<Chat> data, int msgStatus) {

    }
}
