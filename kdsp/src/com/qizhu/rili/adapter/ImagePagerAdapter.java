package com.qizhu.rili.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.qizhu.rili.R;
import com.qizhu.rili.ui.activity.ImageZoomViewer;
import com.qizhu.rili.utils.FileUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import java.util.List;

/**
 * Created by lindow on 07/03/2017.
 * 图片pager
 */

public class ImagePagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<String> mList;      //列表数据

    public ImagePagerAdapter(Context context, List<String> list) {
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
        final View view = LayoutInflater.from(mContext).inflate(R.layout.image_viewpager_item, null);
        YSRLDraweeView ysrlDraweeView = (YSRLDraweeView) view.findViewById(R.id.handler_image);
        UIUtils.display600Image(mList.get(position), ysrlDraweeView, R.drawable.def_loading_img);
//        final View mSave = view.findViewById(R.id.save_pic_btn);
//        UIUtils.displayAndDownload(mList.get(position), ysrlDraweeView, R.drawable.def_loading_img, new BaseBitmapDataSubscriber() {
//            @Override
//            protected void onNewResultImpl(final Bitmap bitmap) {
//                mSave.setVisibility(View.VISIBLE);
//                mSave.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (bitmap != null) {
//                            FileUtils.saveImgToAlbum(mContext, bitmap, null);
//                        } else {
//                            UIUtils.toastMsg("保存失败！");
//                        }
//                    }
//                });
//            }
//
//            @Override
//            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
//                mSave.setVisibility(View.GONE);
//            }
//        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        LogUtils.d("--->  destroyItem = " + position);
        container.removeView((View) object);
    }
}
