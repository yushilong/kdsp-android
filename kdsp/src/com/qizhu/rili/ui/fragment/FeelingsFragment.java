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
 * Created by lindow on 10/29/15.
 * 感情世界的fragment
 */
public class FeelingsFragment extends BaseFragment {
    private TextView mFeelingsText;          //星宿文字

    private DateTime mDateTime;             //日期
    private int mPosition;                  //position
    private char mNum;                      //对应的字符
    private int mNumCount;                  //对应的字符个数

    public static FeelingsFragment newInstance(DateTime dateTime, int position, char mNum, int mNumCount) {
        FeelingsFragment fragment = new FeelingsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        bundle.putInt(IntentExtraConfig.EXTRA_POSITION, position);
        bundle.putChar(IntentExtraConfig.EXTRA_MODE, mNum);
        bundle.putInt(IntentExtraConfig.EXTRA_PARCEL, mNumCount);
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
            mNum = bundle.getChar(IntentExtraConfig.EXTRA_MODE, '1');
            mNumCount = bundle.getInt(IntentExtraConfig.EXTRA_PARCEL, 1);
        }
        initView();
        refreshUI();
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.feelings_fragment_lay, container, false);
    }

    protected void initView() {
        mFeelingsText = (TextView) mMainLay.findViewById(R.id.feelings_text);
    }

    private void refreshUI() {
        switch (mNum) {
            case '1':
                mFeelingsText.setText(CalendarCore.getExpressionOfFeeling(mNumCount));
                break;
            case '2':
                mFeelingsText.setText(CalendarCore.getIntuitionDegree(mNumCount));
                break;
            case '3':
                mFeelingsText.setText(CalendarCore.getThinking(mNumCount));
                break;
            case '4':
                mFeelingsText.setText(CalendarCore.getActivity(mNumCount));
                break;
            case '5':
                mFeelingsText.setText(CalendarCore.getFirmnessDegree(mNumCount));
                break;
            case '6':
                mFeelingsText.setText(CalendarCore.getSelfValue(mNumCount));
                break;
            case '7':
                mFeelingsText.setText(CalendarCore.getLovelornTreat(mNumCount));
                break;
            case '8':
                mFeelingsText.setText(CalendarCore.getIntelligenceAndLogic(mNumCount));
                break;
            case '9':
                mFeelingsText.setText(CalendarCore.getConsiderateDegree(mNumCount));
                break;
        }
    }
}
