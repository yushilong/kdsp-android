package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.Line;
import com.qizhu.rili.bean.SerializableHashMap;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.LogUtils;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by zhouyue on 11/15/17.
 * 生命灵线的activity
 */
public class LifeNumberResultActivity extends BaseActivity {

    TextView     mTitleTxt;
    TextView     mTitleTv;
    TextView     mLifeNumberTv;
    TextView     mSymbolOneTv;
    TextView     mSymbolTwoTv;
    LinearLayout mSymbolLllayout;
    TextView     mContentTv;
    LinearLayout mLifeLineLl;
    TextView     mBaZiTitleTv;
    private int    type;
    private int    lineNumber;
    private String mColor;
    private String mDesc;
    private String mSymbol;
    private String mTitle;
    private ArrayList<Line> mLines = new ArrayList<>();
    private SerializableHashMap serializableMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.life_number_result_activity);
        initUI();

    }

    private void initUI() {
        mTitleTxt = findViewById(R.id.title_txt);
        mTitleTv = findViewById(R.id.title_tv);
        mLifeNumberTv = findViewById(R.id.life_number_tv);
        mSymbolOneTv = findViewById(R.id.symbol_one_tv);
        mSymbolTwoTv = findViewById(R.id.symbol_two_tv);
        mSymbolLllayout = findViewById(R.id.symbol_lllayout);
        mContentTv = findViewById(R.id.content_tv);
        mLifeLineLl = findViewById(R.id.life_line_ll);
        mBaZiTitleTv = findViewById(R.id.ba_zi_title_tv);

        type = getIntent().getIntExtra(IntentExtraConfig.EXTRA_MODE, 0);
        lineNumber = getIntent().getIntExtra(IntentExtraConfig.EXTRA_POSITION, 0);
        mColor = getIntent().getStringExtra(IntentExtraConfig.EXTRA_GROUP_ID);
        mDesc = getIntent().getStringExtra(IntentExtraConfig.EXTRA_JSON);
        mSymbol = getIntent().getStringExtra(IntentExtraConfig.EXTRA_ID);
        mTitle = getIntent().getStringExtra(IntentExtraConfig.EXTRA_PAGE_TITLE);
        mLines = getIntent().getParcelableArrayListExtra(IntentExtraConfig.EXTRA_PARCEL);
        Bundle bundle = getIntent().getExtras();
        serializableMap = (SerializableHashMap) bundle.get(IntentExtraConfig.EXTRA_MAP);
        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        if (type == 1) {
            mTitleTxt.setText(R.string.life_number);
            mTitleTxt.setVisibility(View.VISIBLE);
            mSymbolLllayout.setVisibility(View.VISIBLE);
            mLifeNumberTv.setText("" + lineNumber);
            mTitleTv.setText(mTitle);
            mSymbolOneTv.setText(mSymbol);
            mSymbolTwoTv.setText(mColor);
            mContentTv.setText(mDesc);
        } else if (type == 2) {
            mTitleTxt.setText(R.string.life_of_line);
            mTitleTv.setVisibility(View.GONE);
            mContentTv.setVisibility(View.GONE);
            mSymbolLllayout.setVisibility(View.GONE);
            for (Line line : mLines) {
                StringBuffer sb = new StringBuffer();

                char[] lineChar = String.valueOf(line.line).toCharArray();
                for (int i = 0; i < lineChar.length; i++) {
                    sb.append(lineChar[i]);
                    if (i != lineChar.length - 1) {
                        sb.append("-");
                    }
                }
                sb.append("连线");
                sb.append(" (");
                sb.append(line.lineName);
                sb.append("）");
                View view = mInflater.inflate(R.layout.life_line_item, null);
                TextView titleView = view.findViewById(R.id.title_tv);
                TextView contentView = view.findViewById(R.id.content_tv);
                titleView.setText(sb);
                contentView.setText(line.lineDesc);
                mLifeLineLl.addView(view);
            }

        } else if (type == 3) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(DisplayUtils.dip2px(14),0,DisplayUtils.dip2px(14),0);
            mLifeLineLl.setLayoutParams(layoutParams);
            mTitleTxt.setText(R.string.bazi_life);
            mTitleTv.setVisibility(View.GONE);
            mContentTv.setVisibility(View.GONE);
            mSymbolLllayout.setVisibility(View.GONE);
            mBaZiTitleTv.setVisibility(View.VISIBLE);
            mBaZiTitleTv.setText("\t\t\t\t"+ mDesc);
            for (String key : serializableMap.getMap().keySet()) {
                LogUtils.d("------> key = " + key +",value:" + serializableMap.getMap().get(key));
                View view = mInflater.inflate(R.layout.item_ba_zi_result, null);
                TextView titleView = view.findViewById(R.id.title_tv);
                TextView contentView = view.findViewById(R.id.content_tv);
                titleView.setText(key);
                contentView.setText("\t\t\t\t"+(String) serializableMap.getMap().get(key));

                mLifeLineLl.addView(view);
            }
        }

    }


    /**
     * 跳转
     *
     * @param context 上下文环境
     */
    public static void goToPage(Context context) {
        Intent intent = new Intent(context, LifeNumberResultActivity.class);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, int type, String title, int munber, String color, String symbol, String content, ArrayList<Line> line, Map<String, Object> map) {

        Intent intent = new Intent(context, LifeNumberResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(IntentExtraConfig.EXTRA_MODE, type);
        bundle.putString(IntentExtraConfig.EXTRA_PAGE_TITLE, title);
        bundle.putInt(IntentExtraConfig.EXTRA_POSITION, munber);
        bundle.putString(IntentExtraConfig.EXTRA_GROUP_ID, color);
        bundle.putString(IntentExtraConfig.EXTRA_ID, symbol);
        bundle.putString(IntentExtraConfig.EXTRA_JSON, content);
        bundle.putParcelableArrayList(IntentExtraConfig.EXTRA_PARCEL, line);
        SerializableHashMap myMap = new SerializableHashMap();
        myMap.setMap(map);
        bundle.putSerializable(IntentExtraConfig.EXTRA_MAP, myMap);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


}
