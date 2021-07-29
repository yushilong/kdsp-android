package com.qizhu.rili.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.YSRLTest;
import com.qizhu.rili.ui.activity.YSRLWebActivity;
import com.qizhu.rili.utils.OperUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.FitWidthImageView;

import java.util.List;

/**
 * Created by lindow on 15/7/31.
 * 测试题的adapter
 */
public class TestAdapter extends BaseRecyclerAdapter {
    private int mImageWidth;


    public TestAdapter(Context context, List<?> list) {
        super(context, list);
        mImageWidth = AppContext.getScreenWidth() ;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.test_item_lay, parent,false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        final Object itemData = mList.get(position);
        if (itemData != null && itemData instanceof YSRLTest) {
            final YSRLTest mTest = (YSRLTest) itemData;

            itemHolder.mTestImage.setDefheight(mImageWidth, 720, 320);
            UIUtils.display600Image(mTest.imgUrl, itemHolder.mTestImage, R.drawable.def_test);
            itemHolder.mTestTitle.setText(mTest.title);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OperUtils.mSmallCat = OperUtils.SMALL_CAT_TEST;
                    OperUtils.mKeyCat = mTest.testId;
                    YSRLWebActivity.goToPage(mContext, mTest.linkUrl, mTest.imgUrl, mTest.title, mTest.description);
                }
            });
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        View mTestLay;                      //测试布局
        FitWidthImageView mTestImage;       //测试头像
        TextView mTestTitle;                //测试说明

        private ItemHolder(View itemView) {
            super(itemView);
            mTestLay = itemView.findViewById(R.id.test_lay);
            mTestImage = (FitWidthImageView) itemView.findViewById(R.id.test_image);
            mTestTitle = (TextView) itemView.findViewById(R.id.test_title);
        }
    }
}
