package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;

/**
 * Created by lindow on 7/25/16.
 * 房屋面积对话框
 */
public class MasterAuguryCartFragment extends BaseDialogFragment {
    private static int mPrice;
    private static int mPerPrice;
private  static  MasterAuguryCartInterface mMasterAuguryCartInterface;
    public interface  MasterAuguryCartInterface {
        public void getArea(String s);
    }

    public static MasterAuguryCartFragment newInstance(int price ,int perPrice,MasterAuguryCartInterface masterAuguryCartInterface) {
        mPrice = price;
        mPerPrice = perPrice;
        MasterAuguryCartFragment rtn = new MasterAuguryCartFragment();
        mMasterAuguryCartInterface = masterAuguryCartInterface;
        Bundle bundle = new Bundle();
        rtn.setArguments(bundle);
        return rtn;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.master_augury_cart_edit_dialog, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置背景为透明
//        Window window = getDialog().getWindow();
//        window.setBackgroundDrawableResource(R.color.transparent);
    }

    public void initView() {
       final EditText editText = (EditText) mMainLay.findViewById(R.id.area_et);
       final TextView priceTv = (TextView) mMainLay.findViewById(R.id.price_tv);
        if(mPerPrice != mPrice){
            editText.setText("" + mPrice/mPerPrice );
        }

        priceTv.setText("*" + StringUtils.roundingDoubleStr((double) mPerPrice * 0.01, 2) + "元/平" );
        Log.d("----" , mPerPrice + ":" +(double) mPerPrice * 0.01 );
        mMainLay.findViewById(R.id.confirm).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if(StringUtils.toFloat(editText.getText().toString()) <= 0){
                    UIUtils.toastMsg("房屋面积必须大于0");
                    return;
                }
                mMasterAuguryCartInterface.getArea(editText.getText().toString()) ;
                dismiss();
            }
        });

        mMainLay.findViewById(R.id.cancel).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dismiss();
            }
        });
    }
}
