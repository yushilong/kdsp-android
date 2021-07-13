package com.qizhu.rili.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lyf.yflibrary.Permission;
import com.example.lyf.yflibrary.PermissionResult;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.OrderItem;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.controller.QiNiuUploadCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.RefundProgressActivity;
import com.qizhu.rili.ui.dialog.PhotoChooseDialogFragment;
import com.qizhu.rili.ui.dialog.StringPickDialogFragment;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by lindow on 17/03/2017.
 * 退款申请
 */

public class RefundRequestFragment extends BaseFragment {
    private TextView mServerNameText;
    private TextView mReasonText;
    private TextView mPrice;
    private EditText mDetail;
    private YSRLDraweeView mImage1;
    private YSRLDraweeView mImage2;
    private YSRLDraweeView mImage3;
    private ImageView mDelete1;
    private ImageView mDelete2;
    private ImageView mDelete3;
    private TextView mConfirm;               //提交申请

    private String mOrderId = "";            //整个订单ID
    private String mOdId = "";               //订单明细ID
    private int mAmount;                     //订单金额
    private String mServerName = "";         //退款类型(退款退货、仅退款)
    private String mReason = "";             //退款原因
    private String mImages = "";             //图片地址（多张图片，客户端拼接以逗号“,”隔开）

    private ArrayList<String> mNames = new ArrayList<>();           //服务种类
    private ArrayList<String> mReasons = new ArrayList<>();         //退款原因
    private boolean mClickNames = true;                             //选择服务
    private String mImagePath1 = "";             //图片地址1
    private String mImagePath2 = "";             //图片地址2
    private String mImagePath3 = "";             //图片地址3
    private String mKey1;                       //图片1的key
    private String mKey2;                       //图片2的key
    private String mKey3;                       //图片3的key
    private int mPickImage = 0;                 //选择哪个图片
    private String[] REQUEST_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static RefundRequestFragment newInstance(String orderId, String odId) {
        RefundRequestFragment fragment = new RefundRequestFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_GROUP_ID, orderId);
        bundle.putString(IntentExtraConfig.EXTRA_ID, odId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mOrderId = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_GROUP_ID, "");
            mOdId = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_ID, "");
        }

        initView();
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.refund_request_lay, container, false);
    }

    protected void initView() {
        mServerNameText = (TextView) mMainLay.findViewById(R.id.request_service);
        mReasonText = (TextView) mMainLay.findViewById(R.id.return_reason);
        mPrice = (TextView) mMainLay.findViewById(R.id.refund_amount);
        mDetail = (EditText) mMainLay.findViewById(R.id.detail);
        mImage1 = (YSRLDraweeView) mMainLay.findViewById(R.id.image1);
        mImage2 = (YSRLDraweeView) mMainLay.findViewById(R.id.image2);
        mImage3 = (YSRLDraweeView) mMainLay.findViewById(R.id.image3);
        mDelete1 = (ImageView) mMainLay.findViewById(R.id.delete_image1);
        mDelete2 = (ImageView) mMainLay.findViewById(R.id.delete_image2);
        mDelete3 = (ImageView) mMainLay.findViewById(R.id.delete_image3);
        mConfirm = (TextView) mMainLay.findViewById(R.id.submit_request);

        mConfirm.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (TextUtils.isEmpty(mServerName)) {
                    UIUtils.toastMsg("请选择服务~");
                } else if (TextUtils.isEmpty(mReason)) {
                    UIUtils.toastMsg("请选择退货原因~");
                } else {
                    uploadImage();
                }
            }
        });

        mMainLay.findViewById(R.id.request_service_lay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mClickNames = true;
                mActivity.showDialogFragment(StringPickDialogFragment.newInstance(mNames), "选择服务");
            }
        });
        mMainLay.findViewById(R.id.return_reason_lay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mClickNames = false;
                mActivity.showDialogFragment(StringPickDialogFragment.newInstance(mReasons), "选择原因");
            }
        });
        mMainLay.findViewById(R.id.image1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //第一张
                mPickImage = 1;
                Permission.checkPermisson(mActivity, REQUEST_PERMISSIONS, new PermissionResult() {

                    @Override
                    public void success() {
                        mActivity.showDialogFragment(PhotoChooseDialogFragment.newInstance(), "选择第一张照片");
                    }

                    @Override
                    public void fail() {

                    }
                });

            }
        });
        mMainLay.findViewById(R.id.image2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //第二张
                mPickImage = 2;
                mActivity.showDialogFragment(PhotoChooseDialogFragment.newInstance(), "选择第二张照片");
            }
        });
        mMainLay.findViewById(R.id.image3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //第三张
                mPickImage = 3;
                mActivity.showDialogFragment(PhotoChooseDialogFragment.newInstance(), "选择第三张照片");
            }
        });

        mMainLay.findViewById(R.id.delete_image1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除第一张
                if (TextUtils.isEmpty(mImagePath3)) {
                    if (TextUtils.isEmpty(mImagePath2)) {
                        mImagePath1 = "";
                        mDelete1.setVisibility(View.GONE);
                        UIUtils.display400Image(mImagePath1, mImage1, R.drawable.add_image);
                        mImage2.setVisibility(View.GONE);
                    } else {
                        mImagePath1 = mImagePath2;
                        UIUtils.display400Image(mImagePath1, mImage1, R.drawable.add_image);
                        mImagePath2 = "";
                        mDelete2.setVisibility(View.GONE);
                        UIUtils.display400Image(mImagePath2, mImage2, R.drawable.add_image);
                    }
                } else {
                    mImagePath1 = mImagePath2;
                    UIUtils.display400Image(mImagePath1, mImage1, R.drawable.add_image);
                    mImagePath2 = mImagePath3;
                    UIUtils.display400Image(mImagePath2, mImage2, R.drawable.add_image);
                    mImagePath3 = "";
                    mDelete3.setVisibility(View.GONE);
                    UIUtils.display400Image(mImagePath3, mImage3, R.drawable.add_image);
                }
            }
        });
        mMainLay.findViewById(R.id.delete_image2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除第二张
                if (TextUtils.isEmpty(mImagePath3)) {
                    mImagePath2 = "";
                    mDelete2.setVisibility(View.GONE);
                    UIUtils.display400Image(mImagePath2, mImage2, R.drawable.add_image);
                    mImage3.setVisibility(View.GONE);
                } else {
                    mImagePath2 = mImagePath3;
                    UIUtils.display400Image(mImagePath2, mImage2, R.drawable.add_image);
                    mImagePath3 = "";
                    mDelete3.setVisibility(View.GONE);
                    UIUtils.display400Image(mImagePath3, mImage3, R.drawable.add_image);
                }
            }
        });
        mMainLay.findViewById(R.id.delete_image3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除第三张
                mImagePath3 = "";
                mDelete3.setVisibility(View.GONE);
                UIUtils.display400Image(mImagePath3, mImage3, R.drawable.add_image);
            }
        });
        refreshUI();
    }

    public void setData(JSONObject response) {
        mAmount = response.optInt("refundAmount");
        mNames.clear();
        JSONArray nameArray = response.optJSONArray("serverNames");
        int length = nameArray.length();
        for (int i = 0; i < length; i++) {
            mNames.add((nameArray.optString(i)));
        }
        mReasons.clear();
        JSONArray reasonArray = response.optJSONArray("reasons");
        int len = reasonArray.length();
        for (int i = 0; i < len; i++) {
            mReasons.add((reasonArray.optString(i)));
        }
        refreshUI();
    }

    private void refreshUI() {
        if (mPrice != null) {
            mPrice.setText("¥" + StringUtils.roundingDoubleStr((double) mAmount / 100, 2));
        }
    }

    private void uploadImage() {
        mConfirm.setClickable(false);
        mActivity.showLoadingDialog();
        if (TextUtils.isEmpty(mImagePath1)) {
            mImages = "";
            submitRefundApply();
        } else {
            File file1 = new File(mImagePath1);
            mKey1 = KDSPApiController.getInstance().generateUploadKey(mImagePath1);

            KDSPApiController.getInstance().uploadImageToQiNiu(mKey1, file1, new QiNiuUploadCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    if (TextUtils.isEmpty(mImagePath2)) {
                        mImages = mKey1;
                        submitRefundApply();
                    } else {
                        File file2 = new File(mImagePath2);
                        mKey2 = KDSPApiController.getInstance().generateUploadKey(mImagePath2);
                        KDSPApiController.getInstance().uploadImageToQiNiu(mKey2, file2, new QiNiuUploadCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {
                                if (TextUtils.isEmpty(mImagePath3)) {
                                    mImages = mKey1 + "," + mKey2;
                                    submitRefundApply();
                                } else {
                                    File file3 = new File(mImagePath3);
                                    mKey3 = KDSPApiController.getInstance().generateUploadKey(mImagePath3);
                                    KDSPApiController.getInstance().uploadImageToQiNiu(mKey3, file3, new QiNiuUploadCallBack() {
                                        @Override
                                        public void handleAPISuccessMessage(JSONObject response) {
                                            mImages = mKey1 + "," + mKey2 + mKey3;
                                            submitRefundApply();
                                        }

                                        @Override
                                        public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                            mActivity.dismissLoadingDialog();
                                            UIUtils.toastMsgByStringResource(R.string.http_request_failure);
                                            mConfirm.setClickable(true);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                mActivity.dismissLoadingDialog();
                                UIUtils.toastMsgByStringResource(R.string.http_request_failure);
                                mConfirm.setClickable(true);
                            }
                        });
                    }
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    mActivity.dismissLoadingDialog();
                    UIUtils.toastMsgByStringResource(R.string.http_request_failure);
                    mConfirm.setClickable(true);
                }
            });
        }
    }

    private void submitRefundApply() {
        String mDescription = mDetail.getText().toString();
        KDSPApiController.getInstance().submitRefundApply(mOrderId, mOdId, mServerName, mReason, mDescription, mImages, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mActivity.dismissLoadingDialog();
                        //进入等待处理页面
                        ((RefundProgressActivity) mActivity).toProgress(OrderItem.REFUNDING);
                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                mActivity.dismissLoadingDialog();
                showFailureMessage(error);
                mConfirm.setClickable(true);
            }
        });
    }

    public void displayImage(String path) {
        switch (mPickImage) {
            case 1:
                mImagePath1 = path;
                mDelete1.setVisibility(View.VISIBLE);
                UIUtils.display400Image(mImagePath1, mImage1, R.drawable.add_image);
                mImage2.setVisibility(View.VISIBLE);
                UIUtils.display400Image(mImagePath2, mImage2, R.drawable.add_image);
                break;
            case 2:
                mImagePath2 = path;
                mDelete2.setVisibility(View.VISIBLE);
                UIUtils.display400Image(mImagePath2, mImage2, R.drawable.add_image);
                mImage3.setVisibility(View.VISIBLE);
                UIUtils.display400Image(mImagePath3, mImage3, R.drawable.add_image);
                break;
            case 3:
                mImagePath3 = path;
                mDelete3.setVisibility(View.VISIBLE);
                UIUtils.display400Image(mImagePath3, mImage3, R.drawable.add_image);
                break;
        }
    }

    public void refreshNames(String str) {
        if (mClickNames) {
            mServerName = str;
            mServerNameText.setText(mServerName);
        } else {
            mReason = str;
            mReasonText.setText(mReason);
        }
    }
}
