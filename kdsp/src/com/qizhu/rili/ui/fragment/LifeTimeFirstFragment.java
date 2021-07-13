package com.qizhu.rili.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.User;

import java.util.HashMap;

/**
 * Created by lindow on 2/22/16.
 * 我的一生的fragment
 */
public class LifeTimeFirstFragment extends BaseFragment {
    private TextView mLifeTimeName;         //五行
    private TextView mLike;                 //与我最配的属性
    private TextView mDislike;              //我要远离的属性
    private TextView mLikeColor;            //最适合我的颜色
    private TextView mDislikeColor;         //不适合的颜色

    private String mElement;                //五行
    private int mUserSex;                   //性别
    private int mPosition;                  //position
    private HashMap<String, String> mLikeMap = new HashMap<String, String>();               //与我最配的属性
    private HashMap<String, String> mDislikeMap = new HashMap<String, String>();            //我要远离的属性
    private HashMap<String, String> mLikeColorMap = new HashMap<String, String>();          //最适合我的颜色
    private HashMap<String, String> mDislikeColorMap = new HashMap<String, String>();       //不适合的颜色

    public static LifeTimeFirstFragment newInstance(String element, int sex, int position) {
        LifeTimeFirstFragment fragment = new LifeTimeFirstFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_PARCEL, element);
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
            mElement = bundle.getString(IntentExtraConfig.EXTRA_PARCEL);
            mUserSex = bundle.getInt(IntentExtraConfig.EXTRA_USER_SEX, User.BOY);
            mPosition = bundle.getInt(IntentExtraConfig.EXTRA_POSITION, 0);
        }
        initView();
        refreshUI();
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.lifetime_first_fragment_lay, container, false);
    }

    protected void initView() {
        mLifeTimeName = (TextView) mMainLay.findViewById(R.id.lifetime_name);
        mLike = (TextView) mMainLay.findViewById(R.id.like);
        mDislike = (TextView) mMainLay.findViewById(R.id.dislike);
        mLikeColor = (TextView) mMainLay.findViewById(R.id.like_color);
        mDislikeColor = (TextView) mMainLay.findViewById(R.id.dislike_color);
        mLikeMap.put("强金", "木  水  火");
        mLikeMap.put("弱金", "土  金");
        mLikeMap.put("强木", "火  金  土");
        mLikeMap.put("弱木", "水  木");
        mLikeMap.put("强水", "木  火  土");
        mLikeMap.put("弱水", "金  水");
        mLikeMap.put("强火", "土  水  金");
        mLikeMap.put("弱火", "火  木");
        mLikeMap.put("强土", "金  木  水");
        mLikeMap.put("弱土", "土  火");
        mDislikeMap.put("强金", "土  金");
        mDislikeMap.put("弱金", "木  水  火");
        mDislikeMap.put("强木", "木  水");
        mDislikeMap.put("弱木", "土  金  火");
        mDislikeMap.put("强水", "水  金");
        mDislikeMap.put("弱水", "火  木  土");
        mDislikeMap.put("强火", "木   火");
        mDislikeMap.put("弱火", "金  水  土");
        mDislikeMap.put("强土", "土  火");
        mDislikeMap.put("弱土", "金  木  水");
        mLikeColorMap.put("强金", "绿色  黑色  蓝色  红色");
        mLikeColorMap.put("弱金", "黄色  咖啡色  金色  白色");
        mLikeColorMap.put("强木", "红色  金色  白色  黄色  咖啡色");
        mLikeColorMap.put("弱木", "黑色  蓝色  绿色");
        mLikeColorMap.put("强水", "绿色  红色  黄色  咖啡色");
        mLikeColorMap.put("弱水", "金色  白色  黑色  蓝色");
        mLikeColorMap.put("强火", "黄色  咖啡色  黑色  蓝色  金色  白色");
        mLikeColorMap.put("弱火", "红色  绿色");
        mLikeColorMap.put("强土", "金色  白色  绿色  黑色  蓝色");
        mLikeColorMap.put("弱土", "黄色  咖啡色  红色");
        mDislikeColorMap.put("强金", "黄色  咖啡色  金色  白色");
        mDislikeColorMap.put("弱金", "绿色  黑色  蓝色  红色");
        mDislikeColorMap.put("强木", "黑色  蓝色  绿色");
        mDislikeColorMap.put("弱木", "红色  金色  白色  黄色  咖啡色");
        mDislikeColorMap.put("强水", "金色  白色  黑色  蓝色");
        mDislikeColorMap.put("弱水", "绿色  红色  黄色  咖啡色");
        mDislikeColorMap.put("强火", "红色  绿色");
        mDislikeColorMap.put("弱火", "黄色  咖啡色  黑色  蓝色  金色  白色");
        mDislikeColorMap.put("强土", "黄色  咖啡色  红色");
        mDislikeColorMap.put("弱土", "金色  白色  绿色  黑色  蓝色");
    }

    private void refreshUI() {
        mLifeTimeName.setText("TA的五行:" + mElement);
        mLike.setText(mLikeMap.get(mElement));
        mDislike.setText(mDislikeMap.get(mElement));
        mLikeColor.setText(mLikeColorMap.get(mElement));
        mDislikeColor.setText(mDislikeColorMap.get(mElement));
    }
}
