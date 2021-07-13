package com.qizhu.rili.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.lyf.yflibrary.Permission;
import com.example.lyf.yflibrary.PermissionResult;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.RequestCodeConfig;
import com.qizhu.rili.listener.LoginSuccessListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.PhotoChooseDialogFragment;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.ImageUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

/**
 * Created by lindow on 05/12/2016.
 * 测手相订单
 */

public class HandsOrFaceOrderActivity extends BaseActivity {
    private YSRLDraweeView mLeftImage;      //左边图片
    private YSRLDraweeView mRightImage;     //右边图片
    private TextView mLeftText;             //左边文字
    private TextView mRightText;            //右边文字
    private TextView mConfirm;              //确定

    private String mMsgId;                  //礼品id
    private String mItemId;                 //id
    private String mLeftPath;               //左边图片地址
    private String mRightPath;              //右边图片地址
    private String mCameraFilePath;         //拍照路径
    private boolean mChooseLeft;            //是否上传左边
    private boolean mPalm;                  //是否手相
    private String[] REQUEST_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //支付结果的广播接收器
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BroadcastUtils.ACTION_PAY_SUCCESS.equals(action)) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_pic_lay);
        getIntentExtra();
        initView();
        BroadcastUtils.getInstance().registerReceiver(mReceiver, new IntentFilter(BroadcastUtils.ACTION_PAY_SUCCESS));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastUtils.getInstance().unregisterReceiver(mReceiver);
    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        mMsgId = intent.getStringExtra(IntentExtraConfig.EXTRA_GROUP_ID);
        mItemId = intent.getStringExtra(IntentExtraConfig.EXTRA_ID);
        mPalm = intent.getBooleanExtra(IntentExtraConfig.EXTRA_MODE, false);
    }

    private void initView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        if (mPalm) {
            mTitle.setText(R.string.hands);
        } else {
            mTitle.setText(R.string.face);
        }
        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        mLeftImage = (YSRLDraweeView) findViewById(R.id.left_image);
        mRightImage = (YSRLDraweeView) findViewById(R.id.right_image);
        mLeftText = (TextView) findViewById(R.id.left_text);
        mRightText = (TextView) findViewById(R.id.right_text);
        mConfirm = (TextView) findViewById(R.id.confirm);

        if (mPalm) {
            mLeftText.setText(R.string.upload_left);
            mRightText.setText(R.string.upload_right);
            UIUtils.setImageResource(mLeftImage, R.drawable.order_hands_left);
            UIUtils.setImageResource(mRightImage, R.drawable.order_hands_right);
        } else {
            mLeftText.setText(R.string.upload_front);
            mRightText.setText(R.string.upload_side);
            UIUtils.setImageResource(mLeftImage, R.drawable.order_front);
            UIUtils.setImageResource(mRightImage, R.drawable.order_side);
        }

        mLeftImage.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Permission.checkPermisson(HandsOrFaceOrderActivity.this, REQUEST_PERMISSIONS, new PermissionResult() {

                    @Override
                    public void success() {
                        mChooseLeft = true;
                        showDialogFragment(PhotoChooseDialogFragment.newInstance(), "选择左侧照片");
                    }

                    @Override
                    public void fail() {
                        //失败
                    }
                });

            }
        });
        mRightImage.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Permission.checkPermisson(HandsOrFaceOrderActivity.this, REQUEST_PERMISSIONS, new PermissionResult() {

                    @Override
                    public void success() {
                        mChooseLeft = false;
                        showDialogFragment(PhotoChooseDialogFragment.newInstance(), "选择右侧照片");
                    }

                    @Override
                    public void fail() {
                        //失败
                    }
                });

            }
        });
        mConfirm.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (AppContext.isAnonymousUser()) {
                    LoginChooserActivity.goToPage(HandsOrFaceOrderActivity.this, new LoginSuccessListener() {
                        @Override
                        public void success() {
                            AugurySubmitActivity.goToPage(HandsOrFaceOrderActivity.this, mMsgId, mItemId, mLeftPath, mRightPath, mPalm ? 1 : 2);
                        }
                    });
                } else {
                    AugurySubmitActivity.goToPage(HandsOrFaceOrderActivity.this, mMsgId, mItemId, mLeftPath, mRightPath, mPalm ? 1 : 2);
                }
            }
        });
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
                        if (mChooseLeft) {
                            mLeftPath = selectedImg.getPath();
                            mLeftPath = ImageUtils.compressImage(mLeftPath);
                            UIUtils.display400Image(mLeftPath, mLeftImage, null);
                        } else {
                            mRightPath = selectedImg.getPath();
                            mRightPath = ImageUtils.compressImage(mRightPath);
                            UIUtils.display400Image(mRightPath, mRightImage, null);
                        }
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
                        if (mChooseLeft) {
                            mLeftPath = picPath;
                            UIUtils.display400Image(mLeftPath, mLeftImage, null);
                        } else {
                            mRightPath = picPath;
                            UIUtils.display400Image(mRightPath, mRightImage, null);
                        }

                        LogUtils.i("picPath:" + picPath + ":   ------" + selectedImg.toString());
                    }
                } else if (requestCode == RequestCodeConfig.REQUEST_CODE_GETIMAGE_BYCAMERA) {
                    // 通过手机的拍照功能，获得图片
                    mCameraFilePath = ImageUtils.compressImage(mCameraFilePath);
                    if (mChooseLeft) {
                        mLeftPath = mCameraFilePath;
                        UIUtils.display400Image(mLeftPath, mLeftImage, null);
                    } else {
                        mRightPath = mCameraFilePath;
                        UIUtils.display400Image(mRightPath, mRightImage, null);
                    }
                    LogUtils.i("mCameraFilePath:" + mCameraFilePath);
                }
                if (TextUtils.isEmpty(mLeftPath) || TextUtils.isEmpty(mRightPath)) {
                    mConfirm.setEnabled(false);
                    mConfirm.setBackgroundResource(R.drawable.round_gray38);
                } else {
                    mConfirm.setEnabled(true);
                    mConfirm.setBackgroundResource(R.drawable.round_purple1);
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

    public static void goToPage(Context context, String itemId, boolean mPalm) {
        Intent intent = new Intent(context, HandsOrFaceOrderActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, itemId);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, mPalm);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, String msgId, String itemId, boolean mPalm) {
        Intent intent = new Intent(context, HandsOrFaceOrderActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_GROUP_ID, msgId);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, itemId);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, mPalm);
        context.startActivity(intent);
    }
}
