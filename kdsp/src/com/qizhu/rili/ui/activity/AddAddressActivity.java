package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.RequestCodeConfig;
import com.qizhu.rili.bean.Address;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.pick.CityModel;
import com.qizhu.rili.pick.DistrictModel;
import com.qizhu.rili.pick.OptionsPickerView;
import com.qizhu.rili.pick.ProvinceBean;
import com.qizhu.rili.pick.ProvinceModel;
import com.qizhu.rili.pick.XmlParserHandler;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.RegexUtil;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by lindow on 22/02/2017.
 * 添加收货地址
 */

public class AddAddressActivity extends BaseActivity {
    private EditText mName;
    private EditText mPhone;
    private EditText mPostCode;
    private EditText mAddress;
    private TextView mAreaTv;
    private TextView mAreaTitleTv;

    private ArrayList<ProvinceBean>      options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private OptionsPickerView pvOptions;
    private List<ProvinceModel> provinceList = null;
    private String province;
    private String city;
    private String area;

    private boolean mSelect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_address_lay);
        initView();
        mSelect = getIntent().getBooleanExtra(IntentExtraConfig.EXTRA_MODE, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                parseDataXML();
            }
        }).start();

    }

    private void initView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.enter_address);
        mAreaTv = (TextView) findViewById(R.id.area_tv);
        mAreaTitleTv = (TextView) findViewById(R.id.area_title_tv);
        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        mName = (EditText) findViewById(R.id.name);
        mPhone = (EditText) findViewById(R.id.phone);
        mPostCode = (EditText) findViewById(R.id.postcode);
        mAddress = (EditText) findViewById(R.id.address);


        findViewById(R.id.area_llayout).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mName.getWindowToken(), 0);
                if (pvOptions != null) {
                    pvOptions.show();
                } else {
                    showLoadingDialog();
                    parseDataXML();
                    dismissLoadingDialog();
                    pvOptions.show();

                }

            }
        });


        findViewById(R.id.save).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String mPhoneNumber = mPhone.getText().toString();

                if (!TextUtils.isEmpty(mName.getText())) {
                    if (!TextUtils.isEmpty(mPhoneNumber) && RegexUtil.isMobileNumber(mPhoneNumber)) {
                        if (!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(area)) {
                        if (!TextUtils.isEmpty(mAddress.getText())) {

                                KDSPApiController.getInstance().addShippingAddr(mPhoneNumber, mName.getText().toString(), mAddress.getText().toString(), mPostCode.getText().toString(),
                                        province, city, area, new KDSPHttpCallBack() {
                                            @Override
                                            public void handleAPISuccessMessage(JSONObject response) {
                                                AppContext.mAddressNum++;
                                                if (mSelect) {
                                                    Intent intent1 = new Intent();
                                                    Address address = Address.parseObjectFromJSON(response.optJSONObject("shipping"));
                                                    intent1.putExtra(IntentExtraConfig.EXTRA_JSON, address);
                                                    AddAddressActivity.this.setResult(RESULT_OK, intent1);
                                                }
                                                finish();
                                            }

                                            @Override
                                            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                                showFailureMessage(error);
                                            }
                                        });
                            } else {
                            UIUtils.toastMsg("地址为空");
                        }
                    } else{
                            UIUtils.toastMsg("请选择所在地区");
                        }
                    }else {
                        UIUtils.toastMsg("手机号不合法");
                    }
                } else {
                    UIUtils.toastMsg("收货人为空");
                }
            }
        });
    }

    private void parseDataXML() {
        {

            AssetManager asset = getAssets();
            try {
                InputStream input = asset.open("province_data.xml");
                // 创建一个解析xml的工厂对象
                SAXParserFactory spf = SAXParserFactory.newInstance();
                // 解析xml
                SAXParser parser = spf.newSAXParser();
                XmlParserHandler handler = new XmlParserHandler();
                parser.parse(input, handler);
                input.close();
                // 获取解析出来的数据
                provinceList = handler.getDataList();
                for (int i = 0; i < provinceList.size(); i++) {
                    ProvinceModel provinceModel1 = provinceList.get(i);
                    options1Items.add(new ProvinceBean(i, provinceModel1.getName(), "", ""));


                    ArrayList<String> item = new ArrayList<>();
                    ArrayList<ArrayList<String>> item3 = new ArrayList<>();
                    List<CityModel> cityList = provinceModel1.getCityList();
                    for (int j = 0; j < cityList.size(); j++) {

                        CityModel cityModel = cityList.get(j);


                        item.add(cityModel.getName());

                        List<DistrictModel> districtModels = cityModel.getDistrictList();
                        ArrayList<String> item33 = new ArrayList<>();
                        for (int k = 0, len = districtModels.size(); k < len; k++) {
                            item33.add(districtModels.get(k).getName());

                        }
                        item3.add(item33);
                    }
                    options2Items.add(item);
                    options3Items.add(item3);
                }

                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initPicker();
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.d("rex", "Exception-->" + e.toString());
            } finally {

            }
        }

    }

    private void initPicker() {
        pvOptions = new OptionsPickerView(this);

        //三级联动效果
        pvOptions.setPicker(options1Items, options2Items, options3Items, true);
        //设置选择的三级单位
//        pwOptions.setLabels("省", "市", "区");
        pvOptions.setCyclic(false, false, false);
        //设置默认选中的三级项目
        //监听确定选择按钮
        pvOptions.setSelectOptions(0, 0, 0);
        pvOptions.setCancelable(true);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                try {

                    province = options1Items.get(options1).getPickerViewText().toString();
                    city = options2Items.get(options1).get(option2).toString();
                    area = options3Items.get(options1).get(option2).get(options3).toString();
                    mAreaTitleTv.setText(province.toString()  + city.toString() + area.toString());
                    mAreaTitleTv.setTextColor(ContextCompat.getColor(AddAddressActivity.this, R.color.black));
                    mAreaTv.setVisibility(View.GONE);
                }catch (Exception e){

                }

            }
        });


    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, AddAddressActivity.class);
        context.startActivity(intent);
    }

    public static void goToPageWithResult(BaseActivity baseActivity, boolean select) {
        Intent intent = new Intent(baseActivity, AddAddressActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, select);
        baseActivity.startActivityForResult(intent, RequestCodeConfig.REQUEST_SELECT_ADDRESS);
    }
}
