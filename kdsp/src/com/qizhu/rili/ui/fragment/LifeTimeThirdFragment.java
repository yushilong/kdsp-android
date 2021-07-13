package com.qizhu.rili.ui.fragment;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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
 * 吃货须知
 */
public class LifeTimeThirdFragment extends BaseFragment {
    private TextView mLike;                     //适合的食物
    private TextView mDislike;                  //不适合的食物

    private String mElement;                //五行
    private int mUserSex;                   //性别
    private int mPosition;                  //position
    private HashMap<String, String> mLikeMap = new HashMap<String, String>();            //适合的食物
    private HashMap<String, String> mDislikeMap = new HashMap<String, String>();         //不适合的食物

    public static LifeTimeThirdFragment newInstance(String element, int sex, int position) {
        LifeTimeThirdFragment fragment = new LifeTimeThirdFragment();
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
        return inflater.inflate(R.layout.lifetime_third_fragment_lay, container, false);
    }

    protected void initView() {
        mLike = (TextView) mMainLay.findViewById(R.id.like);
        mDislike = (TextView) mMainLay.findViewById(R.id.dislike);
        mLikeMap.put("强金", "含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理 . 鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理 . 含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒");
        mLikeMap.put("弱金", "含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜 . 含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆");
        mLikeMap.put("强木", "含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒 . 含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜 . 含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆");
        mLikeMap.put("弱木", "鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理 . 含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理");
        mLikeMap.put("强水", "含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理 . 含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒 . 含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜");
        mLikeMap.put("弱水", "含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆 . 鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理");
        mLikeMap.put("强火", "含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜 . 鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理 . 含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆");
        mLikeMap.put("弱火", "含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒 . 含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理");
        mLikeMap.put("强土", "含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆 . 含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理 . 鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理");
        mLikeMap.put("弱土", "含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜 . 含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒");
        mDislikeMap.put("强金", "含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜 . 含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆");
        mDislikeMap.put("弱金", "含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理 . 鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理 . 含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒");
        mDislikeMap.put("强木", "鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理 . 含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理");
        mDislikeMap.put("弱木", "含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒 . 含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜 . 含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆");
        mDislikeMap.put("强水", "含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆 . 鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理");
        mDislikeMap.put("弱水", "含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理 . 含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒 . 含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜");
        mDislikeMap.put("强火", "含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒 . 含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理");
        mDislikeMap.put("弱火", "含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜 . 鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理 . 含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆");
        mDislikeMap.put("强土", "含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜 . 含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒");
        mDislikeMap.put("弱土", "含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆 . 含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理 . 鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理");
    }

    private void refreshUI() {
        SpannableStringBuilder mBuilder1 = new SpannableStringBuilder("最适合我的食物：" + mLikeMap.get(mElement));
        mBuilder1.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.pink3)), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mLike.setText(mBuilder1);
        SpannableStringBuilder mBuilder2 = new SpannableStringBuilder("不适合我的食物：" + mDislikeMap.get(mElement));
        mBuilder2.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.pink3)), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mDislike.setText(mBuilder2);
    }
}
