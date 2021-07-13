package com.qizhu.rili.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.LineDetail;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.ui.activity.LineDetailActivity;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import java.util.List;

/**
 * Created by lindow on 6/16/16.
 * 支线的adapter
 */
public class BranchLinesAdapter extends BaseListAdapter {
    private static final int ITEM_ID = R.layout.plam_item_lay;

    private int mType;              //支线类型
    private boolean mIsMale;        //是否是男生
    private int mWidth;             //子item的宽度

    public BranchLinesAdapter(Context context, List<?> list, int type) {
        super(context, list);
        mIsMale = AppContext.mUser != null && AppContext.mUser.userSex == User.BOY;
        mWidth = (AppContext.getScreenWidth() - DisplayUtils.dip2px(60)) / 2;
        mType = type;
    }

    @Override
    protected int getItemResId() {
        return ITEM_ID;
    }

    @Override
    protected void initItemView(View convertView, int position) {
        ViewHolder holder = new ViewHolder();
        holder.mItemLay = convertView.findViewById(R.id.item_lay);
        holder.image = (YSRLDraweeView) convertView.findViewById(R.id.palm_image);
        holder.image.setLayoutParams(new LinearLayout.LayoutParams(mWidth, mWidth));
        holder.mText = (TextView) convertView.findViewById(R.id.palm_text);
        convertView.setTag(holder);
    }

    @Override
    protected void setItemView(Object tag, Object itemData, int position) {
        if (itemData != null && itemData instanceof LineDetail) {
            final LineDetail lineDetail = (LineDetail) itemData;
            ViewHolder holder = (ViewHolder) tag;
            if (mIsMale) {
                UIUtils.display400Image(lineDetail.malePicUrl, holder.image, R.drawable.def_loading_img);
            } else {
                UIUtils.display400Image(lineDetail.femalePicUrl, holder.image, R.drawable.def_loading_img);
            }
            holder.mText.setText(lineDetail.name);
            holder.mItemLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LineDetailActivity.goToPage(mContext, lineDetail, mType);
                }
            });
        }
    }

    class ViewHolder {
        View mItemLay;              //布局
        YSRLDraweeView image;       //图片
        TextView mText;             //文字
    }
}
