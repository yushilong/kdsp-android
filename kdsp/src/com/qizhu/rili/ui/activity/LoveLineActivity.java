package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.ui.fragment.LoveLineViewPagerFragment;
import com.qizhu.rili.utils.ChinaDateUtil;

import java.util.ArrayList;

/**
 * Created by lindow on 4/7/16.
 * 一线牵的界面
 */
public class LoveLineActivity extends BaseActivity {
    private String mItemId;                 //问题id

    private LoveLineViewPagerFragment mLoveLineFragment;
    private DateTime mYourBirth = new DateTime();            //你的生日
    private DateTime mHerBirth  = new DateTime();             //她的生日
    private int mMode;                      //模式
    private int yourBirthMode;
    private int herBirthMode;


    private ArrayList<String> mCats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.love_line_lay);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            mYourBirth = bundle.getParcelable(IntentExtraConfig.EXTRA_PARCEL);
            mHerBirth = bundle.getParcelable(IntentExtraConfig.EXTRA_JSON);
            yourBirthMode = bundle.getInt(IntentExtraConfig.EXTRA_POST_ID);
            herBirthMode = bundle.getInt(IntentExtraConfig.EXTRA_ID);
            mMode = bundle.getInt(IntentExtraConfig.EXTRA_MODE, 1);

            if (yourBirthMode != 0) {
                mYourBirth = ChinaDateUtil.getSolarByDate(mYourBirth);
            }
            if (herBirthMode != 0) {
                mHerBirth = ChinaDateUtil.getSolarByDate(mHerBirth);
            }
        }


        mLoveLineFragment = LoveLineViewPagerFragment.newInstance(mYourBirth, mHerBirth, mMode);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.body_fragment, mLoveLineFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }


    public static void goToPage(Context context, DateTime yourBirth, DateTime herBirth, int yourBirthMode, int herBirthMode, int mode) {
        Intent intent = new Intent(context, LoveLineActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, yourBirth);
        intent.putExtra(IntentExtraConfig.EXTRA_JSON, herBirth);
        intent.putExtra(IntentExtraConfig.EXTRA_POST_ID, yourBirthMode);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, herBirthMode);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, mode);
        context.startActivity(intent);
    }
}
