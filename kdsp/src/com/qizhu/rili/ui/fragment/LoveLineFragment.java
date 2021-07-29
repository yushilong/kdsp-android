package com.qizhu.rili.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.PagerBean;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.core.CalendarCore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouyue on 3/5/2018
 * 前世今生的fragment
 */
public class LoveLineFragment extends BaseFragment {
    TextView mYourTextTv;
    TextView mHerTextTv;
    TextView mRelationshipTv;
    TextView mAnalysisResult;
    TextView mContent;
    ImageView mLoveLineIv;
    private int mType;                 //0前世 1 今生
    private DateTime mYourBirth = new DateTime();            //你的生日
    private DateTime mHerBirth  = new DateTime();             //她的生日
    private int mMode;                      //模式
    public List<PagerBean> mList = new ArrayList<PagerBean>();
    private SpannableStringBuilder mStringBuilder;

    public static LoveLineFragment newInstance(int type, DateTime yourBirth, DateTime herBirth, int mode) {
        LoveLineFragment fragment = new LoveLineFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IntentExtraConfig.EXTRA_ID, type);
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, yourBirth);
        bundle.putParcelable(IntentExtraConfig.EXTRA_JSON, herBirth);
        bundle.putInt(IntentExtraConfig.EXTRA_MODE, mode);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();

        if (bundle != null) {
            mType = bundle.getInt(IntentExtraConfig.EXTRA_ID, 0);
            mYourBirth = bundle.getParcelable(IntentExtraConfig.EXTRA_PARCEL);
            mHerBirth = bundle.getParcelable(IntentExtraConfig.EXTRA_JSON);
            mMode = bundle.getInt(IntentExtraConfig.EXTRA_MODE, 1);
        }

        getData();
    }

    private void getData() {
        KDSPApiController.getInstance().twoPeopleRelation(mYourBirth.toServerDayString(), mHerBirth.toServerDayString(), mMode, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(final JSONObject response) {

                PagerBean pagerBean1 = new PagerBean(R.drawable.me, R.drawable.she, CalendarCore.getStellarName(mYourBirth).substring(2), CalendarCore.getStellarName(mHerBirth).substring(2), response.optString("lastWorldRelationName"), response.optString("lastWorldRelationAnalysis"), response.optString("lastWorldRelationResult"), mMode);
                PagerBean pagerBean2 = new PagerBean(R.drawable.me, R.drawable.she, CalendarCore.getElementName(mYourBirth), CalendarCore.getElementName(mHerBirth), response.optString("thisWorldRelationName"), response.optString("thisWorldRelationResult"), response.optString("thisWorldRelationAnalysis"), mMode);
                mList.add(pagerBean1);
                mList.add(pagerBean2);

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshUI();
                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
            }
        });
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.love_line_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mYourTextTv = view.findViewById(R.id.your_text_tv);
        mHerTextTv = view.findViewById(R.id.her_text_tv);
        mRelationshipTv = view.findViewById(R.id.relationship_tv);
        mAnalysisResult = view.findViewById(R.id.analysis_result);
        mContent = view.findViewById(R.id.content);
        mLoveLineIv = view.findViewById(R.id.love_line_iv);
    }

    protected void initView() {

    }


    private void setLastWorldRelationResult() {
        mStringBuilder = new SpannableStringBuilder();
        switch (mMode) {
            case 1:
                mStringBuilder.append("恋人关系:");
                break;
            case 2:
                mStringBuilder.append("朋友关系:");
                break;
            case 3:
                mStringBuilder.append("同事关系:");
                break;
            default:
                break;
        }

        mStringBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.pink3)), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    private String getRelation() {
        String relation = getString(R.string.lover);
        switch (mMode) {
            case 1:
                relation = getString(R.string.lover);
                break;
            case 2:
                relation = getString(R.string.friend);
                break;
            case 3:
                relation = getString(R.string.colleague);
                break;
            default:
                break;
        }
        return relation;
    }

    private void refreshUI() {
        PagerBean pagerBean = mList.get(mType);
        String type ;
        if (mType == 0) {
            mLoveLineIv.setImageResource(R.drawable.previous_life);
            mYourTextTv.setText(getString(R.string.your_star , pagerBean.tvLeft) );
        } else {
            mLoveLineIv.setImageResource(R.drawable.this_life);
            mYourTextTv.setText(getString(R.string.your_wu_xing, pagerBean.tvLeft) );
        }

        mHerTextTv.setText(getRelation() + ": " + pagerBean.tvRight);
        mRelationshipTv.setText(getString(R.string.relation) + ": " + pagerBean.type);


        if (TextUtils.isEmpty(pagerBean.content)){
            mContent.setVisibility(View.GONE);
        }else {
            mContent.setVisibility(View.VISIBLE);

        }
        mAnalysisResult.setText(pagerBean.analysis_result);
        setLastWorldRelationResult();
        mStringBuilder.append(pagerBean.content);
        mContent.setText(mStringBuilder);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
