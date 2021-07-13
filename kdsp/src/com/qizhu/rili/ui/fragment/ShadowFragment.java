package com.qizhu.rili.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.utils.StringUtils;

/**
 * Created by lindow on 10/29/15.
 * 上一世的影子的fragment
 */
public class ShadowFragment extends BaseFragment {
    private TextView mShadowText;          //文字

    private DateTime mDateTime;             //日期
    private int mUserSex;                   //性别
    private int mPosition;                  //position
    private int mOneInt;                    //日期转的int值里1的个数

    public static ShadowFragment newInstance(DateTime dateTime, int sex, int position) {
        ShadowFragment fragment = new ShadowFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        bundle.putInt(IntentExtraConfig.EXTRA_USER_SEX, sex);
        bundle.putInt(IntentExtraConfig.EXTRA_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mDateTime = bundle.getParcelable(IntentExtraConfig.EXTRA_PARCEL);
            mUserSex = bundle.getInt(IntentExtraConfig.EXTRA_USER_SEX, User.BOY);
            mPosition = bundle.getInt(IntentExtraConfig.EXTRA_POSITION, 0);
        }
        mOneInt = StringUtils.getCountofChar(mDateTime.toDayInt(), '1');
        initView();
        refreshUI();
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.shadow_fragment_lay, container, false);
    }

    protected void initView() {
        mShadowText = (TextView) mMainLay.findViewById(R.id.shadow_text);
    }

    private void refreshUI() {
        switch (mPosition) {
            case 0:
                mShadowText.setText(CalendarCore.getThisWorldTitle(mOneInt, mUserSex) + "\n\n" + CalendarCore.getThisWorldDesc(mOneInt, mUserSex));
                break;
            case 1:
                mShadowText.setText(CalendarCore.getLastWorldDesc(mOneInt, mUserSex));
                break;
            case 2:
                mShadowText.setText(CalendarCore.getLoveWorldDesc(mOneInt, mUserSex));
                break;
            case 3:
                mShadowText.setText(CalendarCore.getGiveYouTips(mOneInt, mUserSex));
                break;
        }
    }
}
