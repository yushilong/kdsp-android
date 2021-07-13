package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.RequestCodeConfig;
import com.qizhu.rili.adapter.AddressListAdapter;
import com.qizhu.rili.bean.Address;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 22/02/2017.
 * 地址列表页
 */

public class AddressListActivity extends BaseListActivity {
    private boolean mSelect;
    TextView mAdd;
    private ArrayList<Address> mAddressList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        mSelect = getIntent().getBooleanExtra(IntentExtraConfig.EXTRA_MODE, false);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.title_has_back_text, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mResources.getDimension(R.dimen.header_height));
        if (view != null) {
            TextView mTitle = (TextView) view.findViewById(R.id.title_txt);
            mAdd = (TextView) view.findViewById(R.id.right_text);

            view.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    goBack();
                }
            });

            if (mSelect) {
                mTitle.setText(R.string.select_address);
            } else {
                mTitle.setText(R.string.manage_address);
            }

            mAdd.setText(R.string.add);
            mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddAddressActivity.goToPageWithResult(AddressListActivity.this, true);
                }
            });

            titleView.addView(view, params);
        }
    }

    @Override
    protected void initAdapter() {
        if (mAdapter == null) {
            mAdapter = new AddressListAdapter(this, mAddressList, mSelect);
        }
    }

    @Override
    protected void getData() {
        KDSPApiController.getInstance().findShippingAddrList(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                mAddressList = Address.parseListFromJSON(response.optJSONArray("shippings"));
                AppContext.mAddressNum = mAddressList.size();
                AddressListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        refreshViewByType(LAY_TYPE_NORMAL);
                        refreshListViewComplete();
                        mAdapter.reset(mAddressList);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                dismissLoadingDialog();
                showFailureMessage(error);
            }
        });
    }

    @Override
    protected void getNextData() {

    }

    @Override
    public void pullDownToRefresh() {
        super.pullDownToRefresh();
    }

    @Override
    protected boolean canPullDownRefresh() {
        return true;
    }

    @Override
    protected RecyclerView.OnScrollListener getScrollListener() {
        return mScrollListener;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RequestCodeConfig.REQUEST_SELECT_ADDRESS) {
                Address address = data.getParcelableExtra(IntentExtraConfig.EXTRA_JSON);
                mAddressList.add(0, address);
                mAdapter.reset(mAddressList);
                mAdapter.notifyDataSetChanged();
            }
        }
    }






    public static void goToPage(Context context) {
        Intent intent = new Intent(context, AddressListActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转至地址列表并返回所选地址
     */
    public static void goToPageWithResult(BaseActivity baseActivity, boolean select) {
        Intent intent = new Intent(baseActivity, AddressListActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, select);
        baseActivity.startActivityForResult(intent, RequestCodeConfig.REQUEST_SELECT_ADDRESS);
    }
}
