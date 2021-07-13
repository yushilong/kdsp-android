package com.qizhu.rili.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.qizhu.rili.R;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.UIUtils;
import com.zoomable.DoubleTapGestureListener;
import com.zoomable.ZoomableDraweeView;

import java.util.List;

/**
 * Created by lindow on 06/03/2017.
 * 图片adapter
 */

public class ZoomableImagePagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<String> mList;      //列表数据

    public ZoomableImagePagerAdapter(Context context, List<String> list) {
        super();
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.zoomable_view_pager_item, null);
        ZoomableDraweeView zoomableDraweeView = (ZoomableDraweeView) view.findViewById(R.id.zoomable_image);
        //允许缩放时切换
        zoomableDraweeView.setAllowTouchInterceptionWhileZoomed(true);
        //长按
        zoomableDraweeView.setIsLongpressEnabled(false);
        //双击击放大或缩小
        zoomableDraweeView.setTapListener(new DoubleTapGestureListener(zoomableDraweeView));

        String picUrl = UIUtils.getPicUrl(mList.get(position), 600);
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(picUrl))
                .build();
        //加载图片
        zoomableDraweeView.setController(draweeController);
        container.addView(view);
        view.requestLayout();
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        LogUtils.d("--->  destroyItem = " + position);
        container.removeView((View) object);
    }
}
