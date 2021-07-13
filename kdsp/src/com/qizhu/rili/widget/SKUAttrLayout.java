package com.qizhu.rili.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.bean.SKU;
import com.qizhu.rili.listener.SKUChooseListener;
import com.qizhu.rili.utils.DisplayUtils;

import java.util.HashMap;

/**
 * Created by lindow on 27/02/2017.
 * sku的属性view
 */

public class SKUAttrLayout extends LinearLayout {
    private Context mContext;
    private String mSelectAttr = "";                                //默认选中属性为空

    private HashMap<String, SKUAttrText> mChilds = new HashMap<>();     //属性子view,key为属性，view为对应的view
    private HashMap<String, SKU> mSkuMap = new HashMap<>();             //保存sku属性的查询map

    public SKUAttrLayout(Context context) {
        super(context);
        mContext = context;
    }

    public SKUAttrLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public SKUAttrLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public SKUAttrLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    /**
     * 设置属性并布局
     * 如果属性少于3个，那么按照3个布局，否则换行布局
     *
     * @param attrs  属性数组
     * @param skuMap sku map
     */
    public void setAttrs(final String attr, String[] attrs, HashMap<String, SKU> skuMap, final SKUChooseListener skuChooseListener) {
        setTag(attr);
        mSkuMap = skuMap;
        int size = attrs.length;
        int width;
        if (size > 3) {
            setOrientation(VERTICAL);
            width = (AppContext.getScreenWidth() - DisplayUtils.dip2px(110)) / 3;
            for (int i = 0; i <= size / 3; i++) {
                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(HORIZONTAL);
                LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (i != 0) {
                    layoutParams.setMargins(0, DisplayUtils.dip2px(10), 0, 0);
                }
                for (int k = i * 3; k < i * 3 + 3; k++) {
                    if (k < size) {
                        final SKUAttrText text = new SKUAttrText(mContext);
                        LayoutParams params = new LayoutParams(width, DisplayUtils.dip2px(40));
                        if (k != i * 3) {
                            params.setMargins(DisplayUtils.dip2px(40), 0, 0, 0);
                        }
                        final String key = attrs[k];
                        text.setContent(key);
                        if (mSkuMap.get(key) != null && mSkuMap.get(key).stock != 0) {
                            text.setMode(SKUAttrText.ENABLE_MODE);
                        } else {
                            text.setMode(SKUAttrText.DISABLE_MODE);
                        }
                        text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mSelectAttr = key;
                                text.setMode(SKUAttrText.SELECT_MODE);
                                skuChooseListener.chooseSku(attr, mSelectAttr);
                            }
                        });
                        mChilds.put(key, text);
                        layout.addView(text, params);
                    }
                }
                addView(layout, layoutParams);
            }
        } else {
            setOrientation(HORIZONTAL);
            if (size > 1) {
                width = (AppContext.getScreenWidth() - DisplayUtils.dip2px(40) * (size - 1) - DisplayUtils.dip2px(30)) / size;
            } else {
                width = (AppContext.getScreenWidth() - DisplayUtils.dip2px(70)) / 2;
            }
            for (int i = 0; i < size; i++) {
                final SKUAttrText text = new SKUAttrText(mContext);
                LayoutParams layoutParams = new LayoutParams(width, DisplayUtils.dip2px(40));
                if (i != 0) {
                    layoutParams.setMargins(DisplayUtils.dip2px(40), 0, 0, 0);
                }
                final String key = attrs[i];
                text.setContent(key);
                if (mSkuMap.get(key) != null && mSkuMap.get(key).stock != 0) {
                    text.setMode(SKUAttrText.ENABLE_MODE);
                } else {
                    text.setMode(SKUAttrText.DISABLE_MODE);
                }
                text.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectAttr = key;
                        text.setMode(SKUAttrText.SELECT_MODE);
                        skuChooseListener.chooseSku(attr, mSelectAttr);
                    }
                });
                mChilds.put(key, text);
                addView(text, layoutParams);
            }
        }
    }

    public void refreshSelf() {
        for (String key : mChilds.keySet()) {
            SKUAttrText attrText = mChilds.get(key);
            if (!key.equals(mSelectAttr)) {
                if (mSkuMap.get(key) != null && mSkuMap.get(key).stock != 0) {
                    attrText.setMode(SKUAttrText.ENABLE_MODE);
                } else {
                    attrText.setMode(SKUAttrText.DISABLE_MODE);
                }
            }
        }
    }

    public void refreshOne(String attr, int mode) {
        SKUAttrText attrText = mChilds.get(attr);
        attrText.setMode(mode);
    }

    /**
     * 更新选中的属性
     */
    public void setSelectAttr(String value) {
        mSelectAttr = value;
    }

    /**
     * 返回选中的属性
     */
    public String getSelectedAttr() {
        return mSelectAttr;
    }
}
