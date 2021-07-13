package com.qizhu.rili.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.imagepipeline.image.ImageInfo;

/**
 * 充满宽度，高度按照原比例放大的imageView
 * 利用ImageView的矩阵变换来改变显示，放弃原来的bitmap变换,提升图片显示效率
 */
public class FitWidthImageView extends YSRLDraweeView {
    private int mDefheight = 0;

    public FitWidthImageView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public FitWidthImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FitWidthImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FitWidthImageView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        if (mDefheight != 0) {
            setMeasuredDimension(width, mDefheight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * 设置默认的高度,若传入的图片宽高不为0，则依比例设置图片的显示高度
     *
     * @param displayWidth 需要显示的宽度
     * @param width        传入的图片宽
     * @param height       传入的图片高
     */
    public void setDefheight(int displayWidth, int width, int height) {
        if (width > 0 && height > 0) {
            mDefheight = displayWidth * height / width;
        }
    }

    /**
     * 设置默认的高度,若传入的图片宽高不为0，则依比例设置图片的显示高度
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
