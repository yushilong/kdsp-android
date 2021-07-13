package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.SexPickDialogFragment;
import com.qizhu.rili.ui.dialog.TimePickDialogFragment;
import com.qizhu.rili.utils.ChinaDateUtil;
import com.qizhu.rili.utils.UIUtils;

/**
 * Created by lindow on 5/10/16.
 * 设置小伙伴的信息
 */
public class SetFriendsInfoActivity extends BaseActivity {
    private TextView mBirthday;         //生日
    private TextView mSexText;          //性别
    private TextView mBirthdayMode;     //生日模式

    private DateTime mBirthDate;        //出生日期
    private int mSex = 0;               //性别
    private int mSolarMode = 0;         //阴阳历
    private TimePickDialogFragment mTimePickDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_friends_info_lay);

        initView();
    }

    private void initView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        TextView mTip = (TextView) findViewById(R.id.tip);
        mBirthday = (TextView) findViewById(R.id.birthday);
        mSexText = (TextView) findViewById(R.id.sex);
        mBirthdayMode = (TextView) findViewById(R.id.birthday_mode);

        TextView mComplete = (TextView) findViewById(R.id.complete);
        mTitle.setText(R.string.analysis_friend);
        mComplete.setText(R.string.confirm);

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
                if(mTimePickDialogFragment == null){
                    mTimePickDialogFragment = TimePickDialogFragment.newInstance(mBirthDate, TimePickDialogFragment.PICK_ALL, mSolarMode , "my_birthday");
                }
                showDialogFragment(mTimePickDialogFragment, "设置生日");
            }
        });

        mSexText.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showDialogFragment(SexPickDialogFragment.newInstance(false), "设置性别");
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

                AnalysisFriendActivity.goToPage(SetFriendsInfoActivity.this, mBirthDate, mSex);
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
        }
    }

    @Override
    public void setPickDateTime(DateTime dateTime, int mode) {
        mSolarMode = mode;
        mBirthDate = dateTime;
        mBirthday.setText(dateTime.toMinString());
        if (0 == mSolarMode) {
            mBirthdayMode.setText(R.string.birthday_solar);
        } else {
            mBirthdayMode.setText(R.string.birthday_lunar);
        }
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, SetFriendsInfoActivity.class);
        context.startActivity(intent);
    }
}
