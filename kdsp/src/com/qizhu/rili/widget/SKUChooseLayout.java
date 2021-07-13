package com.qizhu.rili.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.SKU;
import com.qizhu.rili.listener.RefreshListener;
import com.qizhu.rili.listener.SKUChooseListener;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by lindow on 28/02/2017.
 * sku的选择view
 */

public class SKUChooseLayout extends LinearLayout {
    private Context mContext;

    private LinkedHashMap<String, String> mSkuNameMap = new LinkedHashMap<>();      //保存sku属性名称的查询map,key为属性名称，value为属性值集合，中间以-分割,此列表保证顺序
    private HashMap<String, SKU> mSkuMap = new HashMap<>();             //保存sku属性的查询map,key为属性值集合，中间以-分割，值为sku
    private ArrayList<SKUAttrLayout> mChilds = new ArrayList<>();       //属性子view
    private HashMap<String, String> mSkuSelectMap = new HashMap<>();    //保存sku属性名称选择的map,key为属性名称，值为属性值

    public SKUChooseLayout(Context context) {
        super(context);
        mContext = context;
    }

    public SKUChooseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public SKUChooseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public SKUChooseLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    public void setAttrs(LinkedHashMap<String, String> skuNameMap, HashMap<String, SKU> skuMap, final RefreshListener refreshListener) {
        mSkuNameMap = skuNameMap;
        mSkuMap = skuMap;
        SKUChooseListener mSKUChooseListener = new SKUChooseListener() {
            @Override
            public void chooseSku(String key, String value) {
                refreshState(key, value);
                refreshListener.refresh(0);
            }
        };
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, DisplayUtils.dip2px(10), 0, 0);

        for (String key : mSkuNameMap.keySet()) {
            String str = mSkuNameMap.get(key);
            TextView textView = new TextView(mContext);
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.gray6));
            textView.setTextSize(16);
            textView.setText(key);
            addView(textView, layoutParams);

            String[] attrs = str.split("-");
            SKUAttrLayout layout = new SKUAttrLayout(mContext);
            layout.setAttrs(key, attrs, mSkuMap, mSKUChooseListener);
            mChilds.add(layout);
            addView(layout, layoutParams);
        }
    }

    /**
     * 哪个key的属性值发生了改变
     *
     * @param key   更改的属性名称，比如颜色
     * @param value 更改的属性值，比如白色
     *              传入的key和value是用户点击选择，具有最高优先级,因此更新每个子view的状态
     */
    private void refreshState(String key, String value) {
        //先将上次的选择全部清空，保存用户的点击到点击map,用户点击的优先更新
        mSkuSelectMap.clear();
        mSkuSelectMap.put(key, value);
        LogUtils.d("-------------> first mSkuSelectMap = " + mSkuSelectMap);
        //遍历更新每个view,注意此次遍历是从第一个view开始
        for (SKUAttrLayout layout : mChilds) {
            //缓存一个map，此map为新结果，保存遍历之后的最优解
            HashMap<String, String> mTempMap = (HashMap<String, String>) mSkuSelectMap.clone();

            String attrName = (String) layout.getTag();     //当前view的属性名，比如颜色
            String attr = layout.getSelectedAttr();         //当前view选中的属性，比如白色
            String[] attrs = mSkuNameMap.get(attrName).split("-");          //当前view对应的属性全部值
            LogUtils.d("-------------> attrName = " + attrName);
            LogUtils.d("-------------> attr = " + attr);
            LogUtils.d("-------------> attrs = " + attrs);
            //如果属性名和点击的一致，那么遍历此选项的其余属性
            if (attrName.equals(key)) {
                layout.refreshSelf();
            } else {
                //属性名和点击的不一致，说明需要在选择属性上看其余属性，那么按照属性遍历.遍历之前更新选中
                layout.setSelectAttr("");
                for (String temp : attrs) {
                    mTempMap.put(attrName, temp);
                    String tempAttr = getAttrKey(mTempMap);
                    LogUtils.d("-------------> tempAttr = " + tempAttr);
                    SKU sku = mSkuMap.get(tempAttr);
                    if (sku == null || sku.stock == 0) {
                        //此属性在当前选择下没有，那么刷新之
                        layout.refreshOne(temp, SKUAttrText.DISABLE_MODE);
                    } else if (temp.equals(attr)) {
                        //已选择属性存在,那么将选中置为新值
                        layout.setSelectAttr(attr);
                        mSkuSelectMap.put(attrName, temp);
                        LogUtils.d("-------------> add mSkuSelectMap = " + mSkuSelectMap);
                        layout.refreshOne(temp, SKUAttrText.SELECT_MODE);
                    } else {
                        layout.refreshOne(temp, SKUAttrText.ENABLE_MODE);
                    }
                }
            }
        }

    }

    private String getAttrKey(HashMap<String, String> skuSelectMap) {
        StringBuffer result = new StringBuffer();
        if (mSkuNameMap.isEmpty()) {
            result.append("kdsp");
        } else {
            //按照返回的顺序决定key
            for (String key : mSkuNameMap.keySet()) {
                String value = skuSelectMap.get(key);
                if (!TextUtils.isEmpty(value)) {
                    result.append(value).append("-");
                }
            }
            //删除最后一个
            if (!TextUtils.isEmpty(result)) {
                result.deleteCharAt(result.length() - 1);
            }
        }

        LogUtils.d("------> getAttrKey result = " + result);
        return String.valueOf(result);
    }

    public SKU getSelectedSku() {
        return mSkuMap.get(getAttrKey(mSkuSelectMap));
    }
}
