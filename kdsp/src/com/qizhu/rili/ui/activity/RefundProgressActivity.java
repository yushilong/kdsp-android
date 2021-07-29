package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.fragment.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.RequestCodeConfig;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.fragment.RefundProgressFragment;
import com.qizhu.rili.ui.fragment.RefundRequestFragment;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.ImageUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 17/03/2017.
 * 退款进度activity
 */

public class RefundProgressActivity extends BaseActivity {
    private String mOrderId = "";            //整个订单ID
    private String mOdId = "";               //订单明细ID
    private int mRefundStatus = 0;           //退款状态(0是填写表单，1是等待处理，2是卖家同意，3是卖家拒绝,4是退款成功)

    private RefundRequestFragment mRefundRequestFragment;           //表单
    private RefundProgressFragment mRefundProgressFragment;         //进度

    private String mCameraFilePath;             //拍照路径
    private boolean mShouldRefresh;             //是否应该刷新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refund_lay);
        mOrderId = getIntent().getStringExtra(IntentExtraConfig.EXTRA_GROUP_ID);
        mOdId = getIntent().getStringExtra(IntentExtraConfig.EXTRA_ID);
        initView();
        getData();
    }

    private void initView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);

        mTitle.setText(R.string.refund_detail);

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
    }

    private void getData() {
        showLoadingDialog();
        KDSPApiController.getInstance().toRefund(mOrderId, mOdId, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(final JSONObject response) {
                mRefundStatus = response.optInt("refund_status");

                RefundProgressActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        findViewById(R.id.body_fragment).setVisibility(View.VISIBLE);
                        findViewById(R.id.request_bad).setVisibility(View.GONE);
                        refreshUI(response);
                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                dismissLoadingDialog();
                showFailureMessage(error);
                findViewById(R.id.body_fragment).setVisibility(View.GONE);
                findViewById(R.id.request_bad).setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 创建并将得到的json传入fragment
     */
    private void refreshUI(JSONObject jsonObject) {
        if (mRefundStatus == 0) {
            mRefundRequestFragment = RefundRequestFragment.newInstance(mOrderId, mOdId);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.body_fragment, mRefundRequestFragment);
            fragmentTransaction.commitAllowingStateLoss();
            mRefundRequestFragment.setData(jsonObject);
        } else {
            mRefundProgressFragment = RefundProgressFragment.newInstance(mOrderId, mOdId, mRefundStatus);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.body_fragment, mRefundProgressFragment);
            fragmentTransaction.commitAllowingStateLoss();
            mRefundProgressFragment.setData(jsonObject);
        }
    }

    /**
     * 跳转申请,此时当前页肯定为mRefundProgressFragment
     */
    public void toRequest(JSONObject jsonObject) {
        mShouldRefresh = true;
        if (mRefundRequestFragment == null) {
            mRefundRequestFragment = RefundRequestFragment.newInstance(mOrderId, mOdId);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.body_fragment, mRefundRequestFragment);
        fragmentTransaction.commitAllowingStateLoss();
        mRefundRequestFragment.setData(jsonObject);
    }

    /**
     * 跳转进度,此时当前页肯定为mRefundRequestFragment
     */
    public void toProgress(int status) {
        mShouldRefresh = true;
        if (mRefundProgressFragment == null) {
            mRefundProgressFragment = RefundProgressFragment.newInstance(mOrderId, mOdId, status);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.body_fragment, mRefundProgressFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 图片选择之后，执行的回调事件
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //友盟报少量data返回为空的情况
        try {
            if (resultCode == RESULT_OK) {
                // 通过“图片浏览器”获得图片
                if (requestCode == RequestCodeConfig.REQUEST_CODE_GETIMAGE_BYSDCARD) {
                    Uri selectedImg = data.getData();
                    //测试时发现moto的一款手机返回的居然是这样的uri  file:///mnt/sdcard/DCIM/Camera/1351684944195.jpg
                    if (selectedImg.toString().contains("file://")) {
                        String path = ImageUtils.compressImage(selectedImg.getPath());
                        mRefundRequestFragment.displayImage(path);
                    } else {
                        String[] filePathColumn = new String[]{MediaStore.Images.Media.DATA};
                        Cursor cursor = null;
                        String picPath = "";
                        try {
                            cursor = getContentResolver().query(selectedImg, filePathColumn, null, null, null);
                            if (cursor == null) {
                                UIUtils.toastMsg("图片格式不合要求~");
                                picPath = "";
                            } else {
                                cursor.moveToFirst();
                                picPath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                            }
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                        picPath = ImageUtils.compressImage(picPath);
                        mRefundRequestFragment.displayImage(picPath);

                        LogUtils.i("picPath:" + picPath + ":   ------" + selectedImg.toString());
                    }
                } else if (requestCode == RequestCodeConfig.REQUEST_CODE_GETIMAGE_BYCAMERA) {
                    // 通过手机的拍照功能，获得图片
                    mCameraFilePath = ImageUtils.compressImage(mCameraFilePath);
                    mRefundRequestFragment.displayImage(mCameraFilePath);
                    LogUtils.i("mCameraFilePath:" + mCameraFilePath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setImagePath(String path) {
        mCameraFilePath = path;
    }

    //设置生日
    @Override
    public <T> void setExtraData(T t) {
        if (t instanceof String) {
            String str = (String) t;
            if (!TextUtils.isEmpty(str)) {
                mRefundRequestFragment.refreshNames(str);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            //点击空白位置 隐藏软键盘
            UIUtils.hideSoftKeyboard(RefundProgressActivity.this, focusView);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onClickBackBtnEvent() {
        if (mShouldRefresh) {
            BroadcastUtils.sendRefreshBroadcast(mOrderId);
        }

        return super.onClickBackBtnEvent();
    }

    public static void goToPage(Context context, String orderId, String odId) {
        Intent intent = new Intent(context, RefundProgressActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_GROUP_ID, orderId);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, odId);
        context.startActivity(intent);
    }
}
