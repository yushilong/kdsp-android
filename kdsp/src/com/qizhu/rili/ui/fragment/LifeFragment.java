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
 * 性格的fragment
 */
public class LifeFragment extends BaseFragment {
    private DateTime mDateTime = new DateTime();         //日期
    private boolean mIsMine;            //是否我的
    private int mPosition;

    public static LifeFragment newInstance(DateTime dateTime, int position, boolean isMine) {
        LifeFragment fragment = new LifeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        bundle.putInt(IntentExtraConfig.EXTRA_POSITION, position);
        bundle.putBoolean(IntentExtraConfig.EXTRA_IS_MINE, isMine);
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
            mIsMine = bundle.getBoolean(IntentExtraConfig.EXTRA_IS_MINE);
        }

        initView();
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.life_fragment_lay, container, false);
    }

    protected void initView() {
        TextView mLifeText = (TextView) mMainLay.findViewById(R.id.life_text);

        switch (mPosition) {
            case 0:
                mLifeText.setText(CalendarCore.getCharacterDesc(mDateTime));
                break;
            case 1:
                mLifeText.setText(CalendarCore.getLoveDesc(mDateTime));
                break;
            case 2:
                mLifeText.setText(CalendarCore.getTemperDesc(mDateTime));
                break;
            case 3:
                mLifeText.setText(CalendarCore.getWeakDesc(mDateTime));
                break;
            case 4:
                mLifeText.setText(CalendarCore.getElementDesc(mDateTime));
                break;
        }
    }
}
