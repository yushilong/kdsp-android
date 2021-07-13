package com.qizhu.rili.data;

import android.text.TextUtils;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.bean.Comment;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnDataGetListener;
import com.qizhu.rili.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouyue on 25/8/25
 * 评论的accessor
 */
public class ArticleCommentDataAccessor extends AbstractDataAccessor<Comment> {
    private static final int PAGER_SIZE = 10;
    private String mArticleId;

public  ArticleCommentDataAccessor(String articleId){
    mArticleId = articleId;
}

    @Override
    public void getDataFromServer(final OnDataGetListener<Comment> listener, final boolean useAppend) {
        KDSPApiController.getInstance().findArticleCommentList(mArticleId,getPage(useAppend), PAGER_SIZE, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                if (response != null) {
                    LogUtils.d("---> " +response.toString());
                    JSONArray listJson = response.optJSONArray("articleComments");
                    final ArrayList<Comment> obtainList = Comment.parseListFromJSON(listJson);

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
                listener.onGetData(DataMessage.<Comment>buildFailDataMessage(TextUtils.isEmpty(msg) ? MSG_API_FAILURE_ERROR : msg));
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
