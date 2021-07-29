package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.TimePickDialogFragment;
import com.qizhu.rili.utils.ChinaDateUtil;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.LogUtils;

/**
 * 生肖大运
 */
public class AnimalsSubmitActivity extends BaseActivity implements View.OnClickListener {


    TextView     mFeatureContent;
    private String mBirthday;
    private TimePickDialogFragment mTimePickDialogFragment;
    private int mYourBirthMode;
    private DateTime mYourBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animals_submit_activity);
        initUI();

    }


    protected void initUI() {
        mFeatureContent = findViewById(R.id.feature_content);
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.animals_luck);
        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        if (AppContext.mUser != null) {
            String type;
            if(AppContext.mUser.isLunar == 0){
                mYourBirthMode = 0 ;
                type = getString(R.string.solar);
                mYourBirth = new DateTime(AppContext.mUser.birthTime);
                mBirthday = DateUtils.getWebTimeFormatDate(mYourBirth.getDate());
            }else {
                mYourBirthMode = 1 ;
                type = getString(R.string.lunar);
                mYourBirth = ChinaDateUtil.solarToLunar(new DateTime(AppContext.mUser.birthTime)) ;
                mBirthday = DateUtils.getWebTimeFormatDate(ChinaDateUtil.getSolarByDate(mYourBirth).getDate());
            }

            mFeatureContent.setText(type + mYourBirth.toMinString() );
        }
        findViewById(R.id.feature_llay).setOnClickListener(this);
        findViewById(R.id.pay_confirm).setOnClickListener(this);
    }


    public static void goToPage(Context context) {
        Intent intent = new Intent(context, AnimalsSubmitActivity.class);
        context.startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feature_llay:

                if(mTimePickDialogFragment == null){
                    mTimePickDialogFragment = TimePickDialogFragment.newInstance(mYourBirth == null ?new DateTime():mYourBirth, TimePickDialogFragment.PICK_ALL, mYourBirthMode, true,"my_birthday", new TimePickDialogFragment.TimePickListener() {
                        @Override
                        public void setPickDateTime(DateTime dateTime, int mode) {
                            if (0 == mode) {
                                mFeatureContent.setText(getString(R.string.solar) + dateTime.toMinString() );
                                mBirthday = DateUtils.getWebTimeFormatDate(dateTime.getDate());
                            } else {
                                mFeatureContent.setText(getString(R.string.lunar) + dateTime.toMinString() );
                                mBirthday = DateUtils.getWebTimeFormatDate(ChinaDateUtil.getSolarByDate(dateTime).getDate());
                            }
                        }
                    });
                }

               showDialogFragment(mTimePickDialogFragment, "设置日期");
                break;
            case R.id.pay_confirm:
                if(TextUtils.isEmpty(mBirthday)){

                }else {
                    LogUtils.d("----"+"http://h5.ishenpo.com/app/share/zodiac_share_answer?birthday=" +mBirthday);
                    YSRLWebActivity.goToPage(AnimalsSubmitActivity.this,"http://h5.ishenpo.com/app/share/zodiac_share_answer?birthday=" +mBirthday);
                }

                break;
        }
    }
}
