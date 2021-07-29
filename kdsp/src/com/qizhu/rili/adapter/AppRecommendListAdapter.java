package com.qizhu.rili.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.AppInfo;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.service.YSRLService;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import java.util.List;

/**
 * 应用推荐列表适配器
 */
public class AppRecommendListAdapter extends BaseRecyclerAdapter {
    private static final int ITEM_ID = R.layout.friendly_app_item;

    public AppRecommendListAdapter(Context context, List<?> list) {
        super(context, list);
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(ITEM_ID, null);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        ItemHolder holder = (ItemHolder) itemHolder;
        final Object itemData = mList.get(position);
        if (itemData != null && itemData instanceof AppInfo) {
            final AppInfo tmpAppInfo = (AppInfo) itemData;

            UIUtils.displayImage(tmpAppInfo.imageUrl, holder.appIcon, 100, R.drawable.def_loading_img);

            //设定app名字
            holder.appName.setText(tmpAppInfo.appName);
            holder.appReason.setText(tmpAppInfo.description);
            holder.itemLay.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    if (!TextUtils.isEmpty(tmpAppInfo.appUrl) && !TextUtils.isEmpty(tmpAppInfo.appName)) {
                        //判断本地是否已经下载
                        String appFilePath = YSRLService.checkLocalAppExsit(AppConfig.CONFIG_EXTERNAL_APP_TAG + tmpAppInfo.appName);
                        if (!TextUtils.isEmpty(appFilePath)) {
                            YSRLService.installApk(mContext, appFilePath);
                        } else {
                            //下载其他应用时，添加头部标示以区分
                            YSRLService.startDownloadAPK(mContext, tmpAppInfo.appUrl, AppConfig.CONFIG_EXTERNAL_APP_TAG + tmpAppInfo.appName, true);
                        }
                    } else {
                        UIUtils.toastMsgByStringResource(R.string.download_app_no_url);
                    }
                }
            });
        }
    }




    private class ItemHolder extends RecyclerView.ViewHolder {
        View itemLay;
        YSRLDraweeView appIcon;
        TextView appName;
        TextView appReason;

        private ItemHolder(View convertView) {
            super(convertView);
            itemLay =  convertView.findViewById(R.id.item_lay);
            appIcon = (YSRLDraweeView) convertView.findViewById(R.id.app_icon);
            appName = (TextView) convertView.findViewById(R.id.app_name);
            appReason = (TextView) convertView.findViewById(R.id.app_reason);
        }
    }

}
