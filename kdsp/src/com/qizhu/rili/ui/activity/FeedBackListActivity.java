package com.qizhu.rili.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lyf.yflibrary.Permission;
import com.example.lyf.yflibrary.PermissionResult;
import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.FeedbackListAdapter;
import com.qizhu.rili.bean.Feedback;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.controller.QiNiuUploadCallBack;
import com.qizhu.rili.data.AbstractDataAccessor;
import com.qizhu.rili.data.DataMessage;
import com.qizhu.rili.data.FeedbackDataAccessor;
import com.qizhu.rili.listener.DataEmptyListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.PhotoChooseDialogFragment;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.ImageUtils;
import com.qizhu.rili.utils.JSONUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;

public class FeedBackListActivity extends BaseChatListActivity {
    private static final int SEND_SUCCESS = 1;
    private static final int SEND_FAILED = 2;

    private EditText mEditText;             //编辑框
    private ImageView mSend;                //发送
    private ImageView mSendPic;             //发送图片

    private boolean mRefreshFromTop = false;    //指明是否是上拉刷新

    //虚拟键盘的高度
    private int mKeyboardHeight = 0;

    private FeedbackDataAccessor mDataAccessor;
    private int mTempFeedCount = 0;                 //用户发表的评论个数，缓存起来用于排重
    private String[] REQUEST_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissLoadingDialog();
            switch (msg.what) {
                case SEND_SUCCESS:
                    Feedback newFeedBack = new Feedback();
                    if (msg.obj != null) {
                        JSONObject jsonObject = JSONUtils.parseJSONObject(msg.obj.toString());
                        newFeedBack.content = jsonObject.optString("content");
                        newFeedBack.fbId = jsonObject.optString("fbId");
                        newFeedBack.type = jsonObject.optInt("type");
                        newFeedBack.width = jsonObject.optInt("width");
                        newFeedBack.height = jsonObject.optInt("height");
                    }
                    newFeedBack.time = DateUtils.getCurrentIntTime();
                    newFeedBack.imageUrl = AppContext.mUser.imageUrl;
                    newFeedBack.userId = AppContext.userId;
                    mTempFeedCount++;
                    mDataAccessor.mData.add(newFeedBack);
                    mEditText.setText("");
                    picPath = "";
                    refreshListView();
                    //滚动到最底端
                    scrollToBottom();
                    break;
                case SEND_FAILED:
                    UIUtils.toastMsg("发送失败，请重试或者检查您的网络！");
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置背景
        try {
            View view = mInflater.inflate(R.layout.feedback_bg, null);
            Bitmap bitmap = UIUtils.getViewBitmap(view, AppContext.getScreenWidth(), AppContext.getScreenHeight() - 50);
            MethodCompat.setBackground(mRootLay, new BitmapDrawable(mResources, bitmap));
        } catch (Throwable e) {
            e.printStackTrace();
            mRootLay.setBackgroundColor(ContextCompat.getColor(this, R.color.gray29));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected AbsListView.OnScrollListener getScrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                LogUtils.d("---> i" + i);
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem <= 1) {
                    LogUtils.d("滑到顶部, onScroll--> firstVisibleItem = " + firstVisibleItem + ", visibleItemCount = " + visibleItemCount + ", totalItemCount = " + totalItemCount
                            + ", hasNoNextData = " + hasNoNextData);
                    if (!isRequesting) {
                        isRequesting = true;
                        if (!hasNoNextData) {
                            AppContext.getAppHandler().sendEmptyMessageDelayed(AppContext.LOAD_DATA_MESSAGE, AppConfig.DELAY_DISPLAY_LOADING_TOAST);
                            pullUpToRefresh();
                        }
                    }
                } else if (visibleItemCount + firstVisibleItem == totalItemCount) {
                    LogUtils.d("滑到底部, onScroll--> firstVisibleItem = " + firstVisibleItem + ", visibleItemCount = " + visibleItemCount + ", totalItemCount = " + totalItemCount);
                }
            }
        };
    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.title_has_back_btn, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mResources.getDimension(R.dimen.header_height));
        if (view != null) {
            view.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    goBack();
                }
            });
            TextView titleTxt = (TextView) view.findViewById(R.id.title_txt);
            titleTxt.setText(R.string.feedback);

            titleView.addView(view, params);
        }
    }

    @Override
    protected void initBottomView(RelativeLayout bottomView) {
        View view = mInflater.inflate(R.layout.bottom_chatting, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomView.addView(view, params);
        mEditText = (EditText) view.findViewById(R.id.display_edit);
        mSend = (ImageView) view.findViewById(R.id.send);
        mSendPic = (ImageView) view.findViewById(R.id.send_pic);

        //设置底部布局
        setBottomView();
    }

    /**
     * 设置底部布局
     */
    private void setBottomView() {
        mSend.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                //意见反馈，发送按钮的点击事件
                final String content = mEditText.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    KDSPApiController.getInstance().addFeedback(content, new KDSPHttpCallBack() {
                        @Override
                        public void handleAPISuccessMessage(JSONObject response) {
                            Message msg = Message.obtain();
                            msg.what = SEND_SUCCESS;
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("content", content);
                                jsonObject.put("fbId", response.optString("fbId"));
                                jsonObject.put("type", 0);
                                msg.obj = jsonObject;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void handleAPIFailureMessage(Throwable error, String content) {
                            mHandler.sendEmptyMessage(SEND_FAILED);
                        }
                    });
                }
            }
        });
        mSendPic.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Permission.checkPermisson(FeedBackListActivity.this, REQUEST_PERMISSIONS, new PermissionResult() {

                    @Override
                    public void success() {
                        showDialogFragment(PhotoChooseDialogFragment.newInstance(), "选择照片");
                    }

                    @Override
                    public void fail() {

                    }
                });

            }
        });
    }

    @Override
    protected void sendPic() {
        if (!TextUtils.isEmpty(picPath)) {
            picPath = ImageUtils.compressImage(picPath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(picPath, options);
            final int width = options.outWidth;
            final int height = options.outHeight;
            final String key = KDSPApiController.getInstance().generateUploadKey(picPath);
            final File file = new File(picPath);
            KDSPApiController.getInstance().uploadImageToQiNiu(key, file, new QiNiuUploadCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    KDSPApiController.getInstance().addFeedback(key, width, height, new KDSPHttpCallBack() {
                        @Override
                        public void handleAPISuccessMessage(JSONObject response) {
                            Message msg = Message.obtain();
                            msg.what = SEND_SUCCESS;
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("content", picPath);
                                jsonObject.put("fbId", response.optString("fbId"));
                                jsonObject.put("type", 1);
                                jsonObject.put("width", width);
                                jsonObject.put("height", height);
                                msg.obj = jsonObject;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void handleAPIFailureMessage(Throwable error, String content) {
                            mHandler.sendEmptyMessage(SEND_FAILED);
                        }
                    });
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    KDSPApiController.getInstance().addFeedback(file, width, height, new KDSPHttpCallBack() {
                        @Override
                        public void handleAPISuccessMessage(JSONObject response) {
                            Message msg = Message.obtain();
                            msg.what = SEND_SUCCESS;
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("content", picPath);
                                jsonObject.put("fbId", response.optString("fbId"));
                                jsonObject.put("type", 1);
                                jsonObject.put("width", width);
                                jsonObject.put("height", height);
                                msg.obj = jsonObject;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void handleAPIFailureMessage(Throwable error, String content) {
                            mHandler.sendEmptyMessage(SEND_FAILED);
                        }
                    });
                }
            });
        } else {
            mHandler.sendEmptyMessage(SEND_FAILED);
        }
    }

    @Override
    protected void initAdapter() {
        if (mDataAccessor == null) {
            mDataAccessor = new FeedbackDataAccessor();
        }
        if (mAdapter == null) {
            mAdapter = new FeedbackListAdapter(this, mDataAccessor.mData);
        }
    }

    @Override
    protected void onListItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    protected void getData() {
        mDataAccessor.getData(buildDefaultDataGetListener(mDataAccessor, false));
    }

    @Override
    protected void getNextData() {
        mRefreshFromTop = true;
        mDataAccessor.getNextData(buildDefaultDataGetListener(mDataAccessor));
    }

    @Override
    public void pullDownToRefresh() {
        mRefreshFromTop = false;
        mDataAccessor.getAllDataFromServer(buildDefaultDataGetListener(mDataAccessor));
    }

    @Override
    protected void doOnGetData(DataMessage dataMessage, AbstractDataAccessor accessor, DataEmptyListener listener, boolean needAutoPullDown) {
        LogUtils.d("ONDATAGETLISTENER dataMsg = " + dataMessage);
        AppContext.getAppHandler().removeMessages(AppContext.LOAD_DATA_MESSAGE);
        AppContext.mUnReadFeedback = 0;
        if (dataMessage.isFail() && accessor.mData.isEmpty()) {
            String msg = dataMessage.msg;
            if (TextUtils.isEmpty(msg)) {
                UIUtils.toastMsgByStringResource(R.string.http_request_failure);
            } else {
                UIUtils.toastMsg(msg);
            }
            refreshViewByType(LAY_TYPE_BAD);
        } else {
            if (listener == null) {
                refreshViewByType(LAY_TYPE_NORMAL);
            }
        }

        //若从网络获取，则清空用户发的反馈，避免重复
        if (dataMessage.isDataFromServer()) {
            clearTempFeedback();
        }

        sortListDataByTime();

        if (needAutoPullDown && dataMessage.isDataFromDb() && accessor.shouldPullDownToRefresh()) {
            //手动触发下拉刷新
            mPtrFrameLayout.autoRefresh();
            pullDownToRefresh();
            mListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtils.d("PULL_DOWN_REFRESH postDelay, refreshComplete!");
                    mPtrFrameLayout.refreshComplete();
                }
            }, 3000);
        } else {
            refreshListViewComplete();
        }

        if (dataMessage.appendNum > 0) {
            refreshListView();
        }

        //设置没有更多数据的标志位
        setHasNoNextData(dataMessage.hasNoNextData());

        //回调通知
        if (listener != null) {
            listener.onDataGet(dataMessage.isDataEmpty());
        }

        if (!mRefreshFromTop) {
            //此处不能延迟，否则会激发上一页请求
            mListView.setSelection(mDataAccessor.mData.size());
        } else {
            //设置其为false，防止下次数据获取不能滚动到底部
            mRefreshFromTop = false;
            //列表的位置不变，只是将添加的size个数据加到头部
            mListView.setSelection(dataMessage.appendNum);
        }
        isRequesting = false;
    }

    /**
     * 保持list的高度
     */
    private void keepListHeight() {
        mContentLay.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mContentHeight > 0 ? mContentHeight : mContentLay.getHeight()));
    }

    /**
     * 恢复list的高度
     */
    private void restoreListHeight() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.weight = 1;
        mContentLay.setLayoutParams(layoutParams);
    }

    /**
     * 清除用户发表的缓存评论，此部分评论保存在总评论列表中，因此更新时需移除刷新
     */
    private void clearTempFeedback() {
        if (mTempFeedCount > 0) {
            //删除用户发表的反馈，因为下次请求时自然会去重，因此可避免列表冗余
            for (int i = 0; i < mTempFeedCount; i++) {
                //每次删除最后一个即可，remove后列表的长度每次会发生变化
                mDataAccessor.mData.remove(mDataAccessor.mData.size() - 1);
            }
            mTempFeedCount = 0;
        }
    }

    //对列表进行排序
    private void sortListDataByTime() {
        Collections.sort(mDataAccessor.mData, new Comparator<Feedback>() {
            @Override
            public int compare(Feedback feedback, Feedback feedback2) {
                return feedback.time - feedback2.time;
            }
        });
    }

    /**
     * 将列表滑动到底部,定位到最后的一个元素
     * 在notify的时候调用很可能是无效的，延迟处理效果更好
     */
    private void scrollToBottom() {
        try {
            mListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListView.setSelection(mDataAccessor.mData.size());
                }
            }, 100);
        } catch (Exception e) {
            e.printStackTrace();
            mListView.setSelection(mDataAccessor.mData.size() - 1);
        }
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, FeedBackListActivity.class);
        context.startActivity(intent);
    }
}
