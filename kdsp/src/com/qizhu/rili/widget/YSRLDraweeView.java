package com.qizhu.rili.widget;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.utils.UIUtils;

/**
 * Created by Lindow on 2015/4/8.
 * 继承facebook的SimpleDraweeView，用于图片显示
 */
public class YSRLDraweeView extends SimpleDraweeView {
    public YSRLDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public YSRLDraweeView(Context context) {
        super(context);
    }

    public YSRLDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YSRLDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 显示图片
     */
    public void display(String picUrl, int width, Integer resId) {
        display(picUrl, width, resId, null);
    }

    /**
     * 显示图片
     *
     * @param picUrl   图片地址
     * @param width    要显示的宽度
     * @param resId    PlaceholderImage 资源
     * @param listener 图片下载监听器
     */
    public void display(String picUrl, int width, Integer resId, BaseControllerListener listener) {
        picUrl = UIUtils.getPicUrl(picUrl, width);
        GenericDraweeHierarchy hierarchy = getHierarchy();
        if (resId != null) {
            if (hierarchy == null) {
                hierarchy = GenericDraweeHierarchyBuilder.newInstance(AppContext.baseContext.getResources())
                        .setPlaceholderImage(ContextCompat.getDrawable(AppContext.baseContext, resId), ScalingUtils.ScaleType.CENTER_INSIDE).build();
                setHierarchy(hierarchy);
            } else {
                hierarchy.setPlaceholderImage(resId);
            }
        }
        if (listener != null) {
            DraweeController controller = Fresco.newDraweeControllerBuilder().setUri(Uri.parse(picUrl)).setOldController(getController()).setControllerListener(listener).build();
            if (hierarchy != null) {
                controller.setHierarchy(hierarchy);
            }
            setController(controller);
        } else {
            setImageURI(Uri.parse(picUrl));
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

    /**
     * 设置宽度和高度
     *
     * @param width  需要显示的宽度
     * @param height 需要显示的高度
     */
    public void setWidthAndHeight(int width, int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);
    }
}
