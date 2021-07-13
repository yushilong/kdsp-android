package com.qizhu.rili.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.core.CalendarCore;

/**
 * Created by lindow on 15/9/15.
 * 星宿的fragment
 */
public class StarFragment extends BaseFragment {
    private TextView mStellarText;          //星宿文字

    private DateTime mDateTime;             //日期
    private int mPosition;                  //position

    public static StarFragment newInstance(DateTime dateTime, int position) {
        StarFragment fragment = new StarFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, dateTime);
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
            mPosition = bundle.getInt(IntentExtraConfig.EXTRA_POSITION, 0);
        }
        initView();
        refreshUI();
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.star_fragment_lay, container, false);
    }

    protected void initView() {
        mStellarText = (TextView) mMainLay.findViewById(R.id.stellar_text);
    }

    private void refreshUI() {
        switch (mPosition) {
            case 0:
                mStellarText.setText(CalendarCore.getStellarCharacter(mDateTime));
                break;
            case 1:
                mStellarText.setText(CalendarCore.getStellarHealth(mDateTime));
                break;
            case 2:
                mStellarText.setText(CalendarCore.getStellarCareer(mDateTime));
                break;
            case 3:
                mStellarText.setText(CalendarCore.getStellarFortune(mDateTime));
                break;
            case 4:
                mStellarText.setText(CalendarCore.getStellarLove(mDateTime));
                break;
        }
    }
}
