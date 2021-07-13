package com.qizhu.rili.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lyf.yflibrary.Permission;
import com.example.lyf.yflibrary.PermissionResult;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.RequestCodeConfig;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.controller.QiNiuUploadCallBack;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.db.YSRLUserDBHandler;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.BloodPickDialogFragment;
import com.qizhu.rili.ui.dialog.PhotoChooseDialogFragment;
import com.qizhu.rili.ui.dialog.SexPickDialogFragment;
import com.qizhu.rili.ui.dialog.TimePickDialogFragment;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.ChinaDateUtil;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.ImageUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.SPUtils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import org.json.JSONObject;

import java.io.File;

public class SettingUserInfoActivity extends BaseActivity {
    private YSRLDraweeView mUserAvatar;
    private TextView mComplete;
    private EditText mName;
    private TextView mGender;
    private TextView mBirthTime;
    private TextView mBirthTimeMode;            //生日模式
    private TextView mStar;                     //星宿
    private TextView mStarSign;                 //星座
    private TextView mBlood;                    //血型
    private TextView mAddressHint;              //未填写地址提示
    private TextView mWuxingTv;              //五行

    private String picPath;                     //选择图片的返回路径
    private String cameraFilePath = "";         //相机拍摄时存储的路径
    private boolean mIsLogin;                   //是否登录为正式用户
    private DateTime mBirthDate;                //出生日期
    private int mMode;                //生日模式
    private String   mYourBloodType = "A型";
    private String[] REQUEST_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_user_info_lay);
        mIsLogin = getIntent().getBooleanExtra(IntentExtraConfig.EXTRA_IS_LOGIN, false);
        initUI();

    }

    protected void initUI() {
        mUserAvatar = (YSRLDraweeView) findViewById(R.id.user_avatar);

        mComplete = (TextView) findViewById(R.id.btn_complete);
        // 昵称
        mName = (EditText) findViewById(R.id.edit_nickname);
        // 性别
        mGender = (TextView) findViewById(R.id.gender_txt);
        // 生日
        mBirthTime = (TextView) findViewById(R.id.btn_birthTime);
        mBirthTimeMode = (TextView) findViewById(R.id.txt_birthTime);
        mStar = (TextView) findViewById(R.id.star);
        mStarSign = (TextView) findViewById(R.id.star_sign);
        mBlood = (TextView) findViewById(R.id.blood_type);
        mAddressHint = (TextView) findViewById(R.id.address_hint);
        mWuxingTv = (TextView)findViewById(R.id.wuxing_tv);
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.personal_data);

        findViewById(R.id.go_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissLoadingDialog();
                goBack();
            }
        });
        mComplete.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                try {
                    //昵称
                    String nickname = mName.getText().toString();

                    if (StringUtils.isEmpty(nickname)) {
                        UIUtils.toastMsg(getString(R.string.please_enter_name));
                        return;
                    }
                    if (mBirthDate == null) {
                        UIUtils.toastMsg(getString(R.string.please_enter_birthday));
                        return;
                    }
                    saveBirthDay();
                    AppContext.mUser.nickName = nickname;
                    showLoadingDialog();

                    if (TextUtils.isEmpty(picPath)) {
                        editUserInfo(AppContext.mUser.imageUrl);
                    } else {
                        final File headFile = new File(picPath);
                        final String key = KDSPApiController.getInstance().generateUploadKey(picPath);
                        KDSPApiController.getInstance().uploadImageToQiNiu(key, headFile, new QiNiuUploadCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {
                                editUserInfo(key);
                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                editUserInfo(headFile);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.gender_lay).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogFragment(SexPickDialogFragment.newInstance(true), "设置性别");
            }
        });
        findViewById(R.id.birthTime_lay).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


                if (0 == mMode) {
                    LogUtils.d("----before"+mBirthDate.toString());
                    showDialogFragment(TimePickDialogFragment.newInstance(mBirthDate, TimePickDialogFragment.PICK_ALL, mMode, "my_birthday"), "设置生日");
                } else {
                    showDialogFragment(TimePickDialogFragment.newInstance(ChinaDateUtil.solarToLunar(mBirthDate) , TimePickDialogFragment.PICK_ALL, mMode, "my_birthday"), "设置生日");
                }
            }
        });
        findViewById(R.id.avatar_lay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Permission.checkPermisson(SettingUserInfoActivity.this, REQUEST_PERMISSIONS, new PermissionResult() {

                    @Override
                    public void success() {
                        showDialogFragment(PhotoChooseDialogFragment.newInstance(), "选择照片");
                    }

                    @Override
                    public void fail() {
                        //失败
                    }
                });


            }
        });
        findViewById(R.id.blood_type_lay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showDialogFragment(BloodPickDialogFragment.newInstance(mYourBloodType), "选择血型");
            }
        });
        findViewById(R.id.address_lay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AddressListActivity.goToPage(SettingUserInfoActivity.this);
            }
        });
    }

    private  void saveBirthDay(){
        String gender = (String) mGender.getText();
        if (gender.equals(getString(R.string.girl))) {
            AppContext.mUser.userSex = User.GIRL;
        } else {
            AppContext.mUser.userSex = User.BOY;
        }
            AppContext.mUser.birthTime = mBirthDate.getDate();

    }

    private void editUserInfo(File headFile) {

        KDSPApiController.getInstance().editUserInfo(AppContext.mUser.nickName, DateUtils.getServerTimeFormatDate(AppContext.mUser.birthTime), AppContext.mUser.userSex, AppContext.mUser.description, headFile, AppContext.mUser.blood, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                editSuccess(response);
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                dismissLoadingDialog();
                showFailureMessage(error);
            }
        });
    }

    private void editUserInfo(String imageUrl) {

        KDSPApiController.getInstance().editUserInfo(AppContext.mUser.nickName, DateUtils.getServerTimeFormatDate(AppContext.mUser.birthTime), AppContext.mUser.userSex, AppContext.mUser.description, imageUrl, AppContext.mUser.blood, mMode , new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                editSuccess(response);
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                dismissLoadingDialog();
                showFailureMessage(error);
            }
        });
    }

    private void editSuccess(JSONObject response) {
        SettingUserInfoActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissLoadingDialog();
            }
        });
        AppContext.mUser = User.parseObjectFromJSON(response.optJSONObject("user"));
        LogUtils.d("imageUrl = " + AppContext.mUser.imageUrl);
        LogUtils.d("----- = " + AppContext.mUser.isLunar + ",birthday:" +AppContext.mUser.birthTime.toString());
        UIUtils.toastMsg(getString(R.string.update_success));
        YSRLUserDBHandler.insertOrUpdateUser(AppContext.mUser);
        LogUtils.d("YSRLUserDBHandler " + YSRLUserDBHandler.getUser(AppContext.mUser.userId).toString());
        //用户信息发生改变，发送广播
        BroadcastUtils.sendUpdateUserDataBroadcast();
        goBack();
    }

    // 程序获取焦点,设置时间
    @Override
    public void onResume() {
        super.onResume();
        refreshUI();
    }

    // 更新UI
    private void refreshUI() {
        if (AppContext.mUser != null) {
            UIUtils.displayBigAvatarImage(AppContext.mUser.imageUrl, mUserAvatar, R.drawable.default_avatar);
            if (!StringUtils.isEmpty(AppContext.mUser.nickName)) {
                mName.setText(AppContext.mUser.nickName);
                mName.setSelection(AppContext.mUser.nickName.length());
            }

            if (AppContext.mUser.userSex == User.GIRL) {
                mGender.setText(getString(R.string.girl));
            } else {
                mGender.setText(getString(R.string.boy));
            }
            if (mBirthDate == null) {

                   mBirthDate = new DateTime(AppContext.mUser.birthTime);

            }

            mMode = AppContext.mUser.isLunar;
            if (1 == AppContext.mUser.isLunar) {
                mMode = 1;
                mBirthTimeMode.setText(R.string.birthday_lunar);
                mBirthTime.setText(ChinaDateUtil.solarToLunar(mBirthDate).toMinString());

            } else {
                mMode = 0;
                mBirthTimeMode.setText(R.string.birthday_solar);
                mBirthTime.setText(mBirthDate.toMinString());
            }

            mStar.setText(CalendarCore.getStellarName(mBirthDate));
            mStarSign.setText(DateUtils.getConstellationFromDate(AppContext.mUser.birthTime));
            mWuxingTv.setText(CalendarCore.getElementName(mBirthDate));
            if (TextUtils.isEmpty(AppContext.mUser.blood)) {
                mBlood.setText(R.string.no_enter);
                mBlood.setTextColor(ContextCompat.getColor(this, R.color.gray9));
            } else {
                mBlood.setText(AppContext.mUser.blood + "型");
                mBlood.setTextColor(ContextCompat.getColor(this, R.color.gray3));
            }
        }

        if (AppContext.mAddressNum > 0) {
            mAddressHint.setText(R.string.has_enter);
        } else {
            mAddressHint.setText(R.string.no_enter);
        }
    }

    //设置生日
    @Override
    public <T> void setExtraData(T t) {
        if (t instanceof Integer) {
            int sex = (Integer) t;
            if (sex == User.BOY) {
                mGender.setText(R.string.boy);
            } else {
                mGender.setText(R.string.girl);
            }
            //当用户没有设置头像的时候，更改默认头像
            if (TextUtils.isEmpty(picPath)) {
                UIUtils.displayBigAvatarImage(AppContext.mUser.imageUrl, mUserAvatar, R.drawable.default_avatar);
            }
        } else if (t instanceof String) {
            String blood = (String) t;
            if (!TextUtils.isEmpty(blood)) {
                //血型选择返回
                if (blood.length() == 3) {
                    AppContext.mUser.blood = blood.substring(0, 2);
                } else {
                    AppContext.mUser.blood = blood.substring(0, 1);
                }
                mYourBloodType = blood;
                mBlood.setText(blood);
                mBlood.setTextColor(ContextCompat.getColor(this, R.color.gray3));
            }
        }
    }

    @Override
    public void setPickDateTime(DateTime dateTime, int mode) {
        SPUtils.putIntValue(YSRLConstants.USER_BIRTH_MODE + AppContext.userId, mode);
        mMode = mode;

        if (0 == mode) {
            mBirthDate = dateTime;
            LogUtils.d("----after"+mBirthDate.toString());
            mBirthTimeMode.setText(R.string.birthday_solar);
        } else {
            mBirthDate = ChinaDateUtil.getSolarByDate(dateTime);
            mBirthTimeMode.setText(R.string.birthday_lunar);
        }

        mBirthTime.setText(dateTime.toMinString());

        mStar.setText(CalendarCore.getStellarName(mBirthDate));
        mStarSign.setText(DateUtils.getConstellationFromDate(mBirthDate.getDate()));
        mWuxingTv.setText(CalendarCore.getElementName(mBirthDate));
    }

    /**
     * 图片选择之后，执行的回调事件
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //友盟报少量data返回为空的情况
        try {
            // 通过“图片浏览器”获得自己的头像图片
            if (requestCode == RequestCodeConfig.REQUEST_CODE_GETIMAGE_BYSDCARD) {
                Uri selectedImg = data.getData();
                //测试时发现moto的一款手机返回的居然是这样的uri  file:///mnt/sdcard/DCIM/Camera/1351684944195.jpg
                if (selectedImg.toString().contains("file://")) {
                    picPath = selectedImg.getPath();
                    picPath = ImageUtils.compressImage(picPath);
                    UIUtils.displayBigAvatarImage(picPath, mUserAvatar, R.drawable.default_avatar);
                    AppContext.mUser.imageUrl = picPath;
                } else {
                    String[] filePathColumn = new String[]{MediaStore.Images.Media.DATA};
                    Cursor cursor = null;
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
                    UIUtils.displayBigAvatarImage(UIUtils.FILE_HEADER_TAG + picPath, mUserAvatar, R.drawable.default_avatar);
                    LogUtils.i("picPath:" + picPath + ":   ------" + selectedImg.toString());
                    AppContext.mUser.imageUrl = UIUtils.FILE_HEADER_TAG + picPath;
                }
            } else if (requestCode == RequestCodeConfig.REQUEST_CODE_GETIMAGE_BYCAMERA) {
                // 通过手机的拍照功能，获得自己的头像图片
                picPath = cameraFilePath;

                LogUtils.i("picPath:" + picPath);
                picPath = ImageUtils.compressImage(picPath);
                UIUtils.displayBigAvatarImage(picPath, mUserAvatar, R.drawable.default_avatar);
                AppContext.mUser.imageUrl = picPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setImagePath(String path) {
        cameraFilePath = path;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    /**
     * 返回
     */
    public void goBack() {
        if (mIsLogin) {
            MainActivity.goToPage(SettingUserInfoActivity.this);
        } else {
            finish();
        }
    }

    /**
     * 跳转
     */
    public static void goToPage(Context mContext) {
        Intent intent = new Intent(mContext, SettingUserInfoActivity.class);
        mContext.startActivity(intent);
    }

    /**
     * 跳转
     *
     * @param isLogin 是否正式登陆
     */
    public static void goToPage(Context mContext, boolean isLogin) {
        Intent intent = new Intent(mContext, SettingUserInfoActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_IS_LOGIN, isLogin);
        mContext.startActivity(intent);
    }
}
