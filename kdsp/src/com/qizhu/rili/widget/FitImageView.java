package com.qizhu.rili.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.imagepipeline.image.ImageInfo;
import com.qizhu.rili.AppContext;

/**
 * Created by Lindow on 2014/9/1.
 * 自适应宽度的imageView，采用centerCrop缩放
 * 可传入固定宽高，用于瀑布流的显示，防止图片加载完成之后的弹动
 */
public class FitImageView extends YSRLDraweeView{
    private int mDefWidth = 0;
    private int mDefHeight = 0;

    public FitImageView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public FitImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FitImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FitImageView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mDefWidth != 0 && mDefHeight != 0) {
            setMeasuredDimension(mDefWidth, mDefHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * 设置默认的宽高,若传入的图片宽高不为0，则依比例设置图片的显示高度
     *
     * @param displayWidth 需要显示的宽度
     * @param width        传入的图片宽
     * @param height       传入的图片高
     */
    public void setDefheight(int displayWidth, int width, int height) {
        if (displayWidth > 0) {
            mDefWidth = displayWidth;
            if (width > 0 && height > 0) {
                mDefHeight = displayWidth * height / width;
            } else {
                mDefHeight = displayWidth;
            }
        } else {
            AppContext.baseContext.getScreenWidthAndHeight();
        }
    }

    /**
     * 设置默认的宽高,若传入的图片宽高不为0，则依比例设置图片的显示宽度
     *
     * @param displayHeight 需要显示的高度
     * @param width        传入的图片宽
     * @param height       传入的图片高
     */
    public void setDefWidth(int displayHeight, int width, int height) {
        if (displayHeight > 0) {
            mDefHeight = displayHeight;
            if (width > 0 && height > 0) {
                mDefWidth = displayHeight * width / height;
            } else {
                mDefWidth = displayHeight;
            }
        } else {
            AppContext.baseContext.getScreenWidthAndHeight();
        }
    }

    /**
     * 设置默认的宽高,若传入的图片宽高不为0，则依比例设置图片的显示高度
     *
     * @param displayWidth 需要显示的宽度
     * @param imageInfo    load完的图片信息
     */
    public void setInfoHeight(int displayWidth, ImageInfo imageInfo) {
        try {
            if (displayWidth > 0 && imageInfo != null && imageInfo.getWidth() > 0 && imageInfo.getHeight() > 0) {
                int width = imageInfo.getWidth();
                int height = imageInfo.getHeight();
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.width = displayWidth;
                layoutParams.height = displayWidth * height / width;
                setLayoutParams(layoutParams);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
