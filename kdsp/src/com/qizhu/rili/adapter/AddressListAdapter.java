package com.qizhu.rili.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.Address;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.ui.activity.AddressListActivity;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by lindow on 24/02/2017.
 * 地址列表
 */

public class AddressListAdapter extends BaseRecyclerAdapter {
    private boolean mSelect;
    private Address mAddress;           //本地选中的地址

    public AddressListAdapter(Context context, List<?> list, boolean select) {
        super(context, list);
        mSelect = select;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.address_item, parent,false);
        return new ItemHolder(view);
    }



    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        ItemHolder holder = (ItemHolder) itemHolder;
        final Address item = (Address)mList.get(position);
        if (item != null) {
            holder.mName.setText("收件人：" + item.receiverName);
            holder.mPhone.setText(item.receiverMobile);

            if (item.mIsDefault) {
                item.mIsDefault = false;
                mAddress = item;
            }

            if (mSelect) {
                holder.mSettingLay.setVisibility(View.GONE);
                holder.mSecondLine.setVisibility(View.GONE);
                holder.mSelect.setVisibility(View.GONE);
                holder.mSelectAddress.setVisibility(View.GONE);

                if (position == mList.size() - 1) {
                    holder.mFirstLine.setVisibility(View.GONE);
                } else {
                    holder.mFirstLine.setVisibility(View.VISIBLE);
                }

                if (item.equals(mAddress)) {
                    SpannableStringBuilder address = new SpannableStringBuilder();
                    address.append("[默认] ").append(item.province).append(item.city).append(item.area).append(item.receiverAddress);
                    address.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.purple30)), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.mAddress.setText(address);
                } else {
                    holder.mAddress.setText(item.province + item.city + item.area +item.receiverAddress);
                }

                holder.mItemLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra(IntentExtraConfig.EXTRA_JSON, item);
                        ((AddressListActivity) mContext).setResult(Activity.RESULT_OK, intent);
                        ((AddressListActivity) mContext).finish();
                    }
                });
            } else {
                holder.mSettingLay.setVisibility(View.VISIBLE);
                holder.mSecondLine.setVisibility(View.VISIBLE);
                holder.mSelect.setVisibility(View.VISIBLE);
                holder.mSelectAddress.setVisibility(View.VISIBLE);

                holder.mAddress.setText(item.province + item.city + item.area +item.receiverAddress);

                if (item.equals(mAddress)) {
                    holder.mSelect.setImageResource(R.drawable.selected);
                    holder.mSelectAddress.setTextColor(ContextCompat.getColor(mContext, R.color.purple30));
                } else {
                    holder.mSelect.setImageResource(R.drawable.unselected);
                    holder.mSelectAddress.setTextColor(ContextCompat.getColor(mContext, R.color.gray3));
                }
            }

            holder.mSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelect(item);
                }
            });
            holder.mSelectAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelect(item);
                }
            });

            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof AddressListActivity) {
                        ((AddressListActivity) mContext).showLoadingDialog();
                    }
                    KDSPApiController.getInstance().deleteShippingAddr(item.shipId, new KDSPHttpCallBack() {
                        @Override
                        public void handleAPISuccessMessage(JSONObject response) {
                            AppContext.mAddressNum--;
                            AppContext.getAppHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mContext instanceof AddressListActivity) {
                                        ((AddressListActivity) mContext).dismissLoadingDialog();
                                    }
                                    if (item.equals(mAddress)) {
                                        mAddress = null;
                                    }
                                    mList.remove(item);
                                    notifyDataSetChanged();
                                }
                            });
                        }

                        @Override
                        public void handleAPIFailureMessage(Throwable error, String reqCode) {
                            if (mContext instanceof AddressListActivity) {
                                ((AddressListActivity) mContext).dismissLoadingDialog();
                            }
                            showFailureMessage(error);
                        }
                    });
                }
            });
        }
    }


    public void setSelect(final Address address) {
        if (mContext instanceof AddressListActivity) {
            ((AddressListActivity) mContext).showLoadingDialog();
        }
        //设置默认地址
        KDSPApiController.getInstance().changeShippingAddrIsDefault(address.shipId, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                mAddress = address;
                AppContext.getAppHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (mContext instanceof AddressListActivity) {
                            ((AddressListActivity) mContext).dismissLoadingDialog();
                        }
                        notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                if (mContext instanceof AddressListActivity) {
                    ((AddressListActivity) mContext).dismissLoadingDialog();
                }
                showFailureMessage(error);
            }
        });
    }




    private class ItemHolder extends RecyclerView.ViewHolder {
        View mItemLay;                      //地址项
        TextView mName;                     //名称
        TextView mPhone;                    //手机
        TextView mAddress;                  //地址
        ImageView mSelect;                  //勾选
        TextView mSelectAddress;            //勾选
        TextView mDelete;                   //删除
        View mSettingLay;                   //设置
        View mFirstLine;                    //分割线
        View mSecondLine;                   //分割线

        private ItemHolder(View convertView) {
            super(convertView);
            mItemLay = convertView.findViewById(R.id.item_lay);
            mName = (TextView) convertView.findViewById(R.id.name);
            mPhone = (TextView) convertView.findViewById(R.id.phone_number);
            mAddress = (TextView) convertView.findViewById(R.id.address);
            mSelect = (ImageView) convertView.findViewById(R.id.select);
            mSelectAddress = (TextView) convertView.findViewById(R.id.select_address);
            mDelete = (TextView) convertView.findViewById(R.id.delete);
            mSettingLay = convertView.findViewById(R.id.setting_lay);
            mFirstLine = convertView.findViewById(R.id.first_line);
            mSecondLine = convertView.findViewById(R.id.second_line);
        }
    }
}
