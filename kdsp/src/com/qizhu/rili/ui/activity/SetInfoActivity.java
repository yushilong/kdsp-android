package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.db.YSRLUserDBHandler;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.SexPickDialogFragment;
import com.qizhu.rili.ui.dialog.TimePickDialogFragment;
import com.qizhu.rili.utils.ChinaDateUtil;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.SPUtils;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 15/9/15.
 * 设置个人资料页面,为设置个人资料和其他人资料，若设置自己的个人资料，则调用edituserInfo接口
 */
public class SetInfoActivity extends BaseActivity {
    private TextView mBirthday;         //生日
    private TextView mSexText;          //性别

    private DateTime mBirthDate;        //出生日期
    private int mSex = 0;               //性别
    private int mSolarMode = 0;         //阴阳历

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_info_lay);

        initView();
    }

    private void initView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mBirthday = (TextView) findViewById(R.id.birthday);
        mSexText = (TextView) findViewById(R.id.sex);

        TextView mComplete = (TextView) findViewById(R.id.complete);
        mTitle.setText(R.string.edit_info);
        mComplete.setText(R.string.complete);

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        mBirthday.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mBirthDate == null) {
                    mBirthDate = new DateTime(1990, 0, 1);
                }
                showDialogFragment(TimePickDialogFragment.newInstance(mBirthDate, TimePickDialogFragment.PICK_ALL, mSolarMode , "my_birthday" ), "设置生日");
            }
        });

        mSexText.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showDialogFragment(SexPickDialogFragment.newInstance(true), "设置性别");
            }
        });

        findViewById(R.id.complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBirthDate == null) {
                    UIUtils.toastMsgByStringResource(R.string.not_set_birthday);
                    return;
                }
                if (0 == mSex) {
                    UIUtils.toastMsgByStringResource(R.string.select_sex);
                    return;
                }

                if (mSolarMode != 0) {
                    mBirthDate = ChinaDateUtil.getSolarByDate(mBirthDate);
                }
                if (!DateTime.isBeforeNow(mBirthDate)) {
                    UIUtils.toastMsgByStringResource(R.string.current_birthday);
                    return;
                }

                showLoadingDialog();
                editUserInfo();
            }
        });
    }

    private void editUserInfo() {

        KDSPApiController.getInstance().editUserInfo("", DateUtils.getServerTimeFormatDate(mBirthDate.getDate()), mSex, "", "", "",mSolarMode, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                SetInfoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        KDSPApiController.getInstance().initSysConfig(new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {
                                AppContext.doUserInit(response);
                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {

                            }
                        });
                    }
                });
                AppContext.mUser = User.parseObjectFromJSON(response.optJSONObject("user"));
                UIUtils.toastMsg(getString(R.string.update_success));
                SPUtils.putBoolSync(YSRLConstants.HAS_ENTER_INFO, true);
                YSRLUserDBHandler.insertOrUpdateUser(AppContext.mUser);
                SPUtils.putIntValue(YSRLConstants.USER_BIRTH_MODE + AppContext.userId, mSolarMode);
                MainActivity.goToPage(SetInfoActivity.this);
                finish();
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                dismissLoadingDialog();
                showFailureMessage(error);
            }
        });
    }

    //设置生日
    @Override
    public <T> void setExtraData(T t) {
        if (t instanceof Integer) {
            mSex = (Integer) t;
            if (mSex == User.BOY) {
                mSexText.setText(R.string.boy);
            } else {
                mSexText.setText(R.string.girl);
            }
            mSexText.setTextColor(ContextCompat.getColor(this, R.color.black));
        }
    }

    @Override
    public void setPickDateTime(DateTime dateTime, int mode) {
        mSolarMode = mode;
        mBirthDate = dateTime;
        mBirthday.setText(dateTime.toMinString());
        mBirthday.setTextColor(ContextCompat.getColor(this, R.color.black));
        if (1 == mSolarMode) {
//            mBirthdayMode.setText(R.string.birthday_solar);
        } else {
//            mBirthdayMode.setText(R.string.birthday_lunar);
        }
    }

    @Override
    public boolean onClickBackBtnEvent() {
        if (!SPUtils.getBoolleanValue(YSRLConstants.HAS_ENTER_INFO, false)) {
            UIUtils.toastMsg("请输入个人信息");
            return true;
        }
        return super.onClickBackBtnEvent();
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, SetInfoActivity.class);
        context.startActivity(intent);
    }
}
