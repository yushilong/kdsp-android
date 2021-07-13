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
 * 最适合我的职业
 */
public class LifeTimeSecondFragment extends BaseFragment{
    private TextView mLifeTimeText;          //文字

    private String mElement;                //五行
    private int mUserSex;                   //性别
    private int mPosition;                  //position
    private HashMap<String, String> mLikeMap = new HashMap<String, String>();               //与我最配的属性

    public static LifeTimeSecondFragment newInstance(String element, int sex, int position) {
        LifeTimeSecondFragment fragment = new LifeTimeSecondFragment();
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
        return inflater.inflate(R.layout.lifetime_second_fragment_lay, container, false);
    }

    protected void initView() {
        mLifeTimeText = (TextView) mMainLay.findViewById(R.id.lifetime_text);
        mLikeMap.put("强金","园林 . 插花 . 鲜花产业 . 家具 . 服装业 . 印刷造纸业 . 茶叶产业 . 补习班 . 名品店 . 药品 . 食物 . 健康产品 . 农业 . 设计师 . 水利 . 航海 . 旅游 . 交通运输 . 跳水 . 洗衣店 . 饭店管理 . 广告业 . 记者 . 玩具业 . 澡堂 . 卡拉OK . 饮料业 . 速食店 . 油业（食用油 . 油漆等） . 美容业 . 销售 . 百货公司 . 照明 . 咨询 . 化工 . 酒类行业 . 照相 . 橡胶 . 玄学产业");
        mLikeMap.put("弱金","宝石产业 . 地产建筑 . 外包公司 . 代理商 . 培训业 . 会展行当 . 经纪人 . 服务业 . 安保行业 . 瓷器业 . 律师 . 航空业 . 期货 . 殡葬业 . 金融业（股票 . 会计 . 贸易） . 机械 . 体育 . 手饰行当 . 按摩业 . 法官");
        mLikeMap.put("强木","油业（食用油 . 油漆等） . 美容业 . 销售 . 百货公司 . 照明 . 咨询 . 化工 . 酒类行业 . 照相 . 橡胶 . 玄学产业 . 金融业（股票 . 会计 . 贸易） . 机械 . 体育 . 手饰行当 . 按摩业 . 法官 . 宝石产业 . 地产建筑 . 外包公司 . 代理商 . 培训业 . 会展行当 . 经纪人 . 服务业 . 安保行业 . 瓷器业 . 律师 . 航空业 . 期货 . 殡葬业");
        mLikeMap.put("弱木","水利 . 航海 . 旅游 . 交通运输 . 跳水 . 洗衣店 . 饭店管理 . 广告业 . 记者 . 玩具业 . 澡堂 . 卡拉OK . 饮料业 . 速食店 . 园林 . 插花 . 鲜花产业 . 家具 . 服装业 . 印刷造纸业 . 茶叶产业 . 补习班 . 名品店 . 药品 . 食物 . 健康产品 . 农业 . 设计师");
        mLikeMap.put("强水","园林 . 插花 . 鲜花产业 . 家具 . 服装业 . 印刷造纸业 . 茶叶产业 . 补习班 . 名品店 . 药品 . 食物 . 健康产品 . 农业 . 设计师 . 油业（食用油 . 油漆等） . 美容业 . 销售 . 百货公司 . 照明 . 咨询 . 化工 . 酒类行业 . 照相 . 橡胶 . 玄学产业 . 宝石产业 . 地产建筑 . 外包公司 . 代理商 . 培训业 . 会展行当 . 经纪人 . 服务业 . 安保行业 . 瓷器业 . 律师 . 航空业 . 期货 . 殡葬业");
        mLikeMap.put("弱水","金融业（股票 . 会计 . 贸易） . 机械 . 体育 . 手饰行当 . 按摩业 . 法官 . 水利 . 航海 . 旅游 . 交通运输 . 跳水 . 洗衣店 . 饭店管理 . 广告业 . 记者 . 玩具业 . 澡堂 . 卡拉OK . 饮料业 . 速食店");
        mLikeMap.put("强火","宝石产业 . 地产建筑 . 外包公司 . 代理商 . 培训业 . 会展行当 . 经纪人 . 服务业 . 安保行业 . 瓷器业 . 律师 . 航空业 . 期货 . 殡葬业 . 水利 . 航海 . 旅游 . 交通运输 . 跳水 . 洗衣店 . 饭店管理 . 广告业 . 记者 . 玩具业 . 澡堂 . 卡拉OK . 饮料业 . 速食店 . 金融业（股票 . 会计 . 贸易） . 机械 . 体育 . 手饰行当 . 按摩业 . 法官");
        mLikeMap.put("弱火","油业（食用油 . 油漆等） . 美容业 . 销售 . 百货公司 . 照明 . 咨询 . 化工 . 酒类行业 . 照相 . 橡胶 . 玄学产业 . 园林 . 插花 . 鲜花产业 . 家具 . 服装业 . 印刷造纸业 . 茶叶产业 . 补习班 . 名品店 . 药品 . 食物 . 健康产品 . 农业 . 设计师");
        mLikeMap.put("强土","金融业（股票 . 会计 . 贸易） . 机械 . 体育 . 手饰行当 . 按摩业 . 法官 . 园林 . 插花 . 鲜花产业 . 家具 . 服装业 . 印刷造纸业 . 茶叶产业 . 补习班 . 名品店 . 药品 . 食物 . 健康产品 . 农业 . 设计师 . 水利 . 航海 . 旅游 . 交通运输 . 跳水 . 洗衣店 . 饭店管理 . 广告业 . 记者 . 玩具业 . 澡堂 . 卡拉OK . 饮料业 . 速食店");
        mLikeMap.put("弱土","宝石产业 . 地产建筑 . 外包公司 . 代理商 . 培训业 . 会展行当 . 经纪人 . 服务业 . 安保行业 . 瓷器业 . 律师 . 航空业 . 期货 . 殡葬业 . 油业（食用油 . 油漆等） . 美容业 . 销售 . 百货公司 . 照明 . 咨询 . 化工 . 酒类行业 . 照相 . 橡胶 . 玄学产业");
    }

    private void refreshUI() {
        mLifeTimeText.setText(mLikeMap.get(mElement));
    }
}
