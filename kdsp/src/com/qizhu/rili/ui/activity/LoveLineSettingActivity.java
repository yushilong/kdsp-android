package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.TimePickDialogFragment;
import com.qizhu.rili.utils.ChinaDateUtil;
import com.qizhu.rili.utils.UIUtils;

/**
 * Created by lindow on 4/7/16.
 * 一线牵的设置界面
 */
public class LoveLineSettingActivity extends BaseActivity {
    private TextView   mYourBirthday;         //你的生日
    private TextView   mHerBirthday;          //她的生日
    private RadioGroup mRelationshipRadio;  //关系单选

    private DateTime mYourBirth;            //你的生日
    private DateTime mHerBirth;             //她的生日
    private int     mMode          = 0;                  //关系模式，1为恋人，2为朋友，3为同事
    private boolean isYourBirth    = true;     //是否是你的生日
    private int     mYourBirthMode = 0;            //你的生日
    private int     mHerBirthMode  = 0;             //她的生日


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.love_line_setting_lay);
        initView();
    }

    private void initView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.this_previous_life);
        mYourBirthday = (TextView) findViewById(R.id.your_birthday);
        mHerBirthday = (TextView) findViewById(R.id.her_birthday);
        mRelationshipRadio = (RadioGroup) findViewById(R.id.relationship_group);

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        findViewById(R.id.complete).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mYourBirth == null) {
                    UIUtils.toastMsgByStringResource(R.string.no_empty_your_birth);
                    return;
                }
                if (mHerBirth == null) {
                    UIUtils.toastMsgByStringResource(R.string.no_empty_her_birth);
                    return;
                }
                if (0 == mMode) {
                    UIUtils.toastMsgByStringResource(R.string.no_empty_your_relationship);
                    return;
                }
//                if(isYourBirthFirtTime){
//                    if (mYourBirthMode != 0) {
//                        mYourBirth = ChinaDateUtil.getSolarByDate(mYourBirth);
//                    }
//
//                }
//                if(isHerBirthFirtTime){
//                    if (mHerBirthMode != 0) {
//                        mHerBirth = ChinaDateUtil.getSolarByDate(mHerBirth);
//                    }
//                }

//                isYourBirthFirtTime = false;
//                isHerBirthFirtTime = false;
                LoveLineActivity.goToPage(LoveLineSettingActivity.this, mYourBirth, mHerBirth, mYourBirthMode, mHerBirthMode, mMode);
            }
        });

        findViewById(R.id.your_birthday_ll).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                isYourBirth = true;
                DateTime dateTime = mYourBirth != null ? mYourBirth : new DateTime(1990, 0, 1);
                showDialogFragment(TimePickDialogFragment.newInstance(dateTime, TimePickDialogFragment.PICK_ALL, mYourBirthMode, true, "my_birthday" ), "选择你的生日");

            }
        });
        findViewById(R.id.her_birthday_ll).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                isYourBirth = false;
                DateTime dateTime = mHerBirth != null ? mHerBirth : new DateTime(1990, 0, 1);
                showDialogFragment(TimePickDialogFragment.newInstance(dateTime, TimePickDialogFragment.PICK_ALL, mHerBirthMode, true , "my_birthday"), "选择她的生日");

            }
        });

        mRelationshipRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.lover:
                        mMode = 1;
                        break;
                    case R.id.friend:
                        mMode = 2;
                        break;
                    case R.id.workmate:
                        mMode = 3;
                        break;
                }
            }
        });

        if (AppContext.mUser != null) {

            String type;

            if (AppContext.mUser.isLunar == 0) {
                mYourBirthMode = 0;
                type = getString(R.string.solar);
                mYourBirth = new DateTime(AppContext.mUser.birthTime);
            } else {
                mYourBirthMode = 1;
                type = getString(R.string.lunar);
                mYourBirth = ChinaDateUtil.solarToLunar(new DateTime(AppContext.mUser.birthTime));
            }

            mYourBirthday.setText(type + mYourBirth.toMinString());
        }
    }

    @Override
    public void setPickDateTime(DateTime dateTime, int mode) {
        String type;
        if (mode == 0) {
            type = getString(R.string.solar);
        } else {
            type = getString(R.string.lunar);
        }
        if (isYourBirth) {
            mYourBirthMode = mode;
            mYourBirth = dateTime;
            mYourBirthday.setText(type + mYourBirth.toMinString() );
        } else {
            mHerBirth = dateTime;
            mHerBirthMode = mode;
            mHerBirthday.setText(type + mHerBirth.toMinString() );
        }
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, LoveLineSettingActivity.class);
        context.startActivity(intent);
    }
}
