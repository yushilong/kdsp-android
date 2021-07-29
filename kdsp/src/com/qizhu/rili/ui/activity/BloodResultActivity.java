package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;

import org.json.JSONObject;

/**
 * Created by lindow on 06/01/2017.
 * 血型结果界面
 */
public class BloodResultActivity extends BaseActivity {

    TextView  mBloodType;
    ImageView mBloodIv;
    TextView  mBloodTatolWord;
    private TextView     mFatherBlood;      //父亲的血型
    private TextView     mMotherBlood;      //母亲的血型
    private TextView     mText1;
    private TextView     mText2;
    private TextView     mText3;
    private LinearLayout mContainer;    //动态textView

    private String mYour;           //你的血型
    private String mFather;         //父亲的血型
    private String mMother;         //母亲的血型

    private String mBgImage;        //背景图片地址
    private String mCharacterOneself;       //本身性格
    private String mEnjoyTypeOneself;       //本身欣赏类型
    private String mLoveSignalOneself;      //本身恋爱讯号
    private String mProminentCharacterOneself;       //本身突出特征
    private String mCareer;                 //事业
    private String mCharacter;              //性格
    private String mLove;                   //恋爱
    private String mTotalWords;                   //总言

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blood_result_lay);
        initView();
        getIntentExtra();
        getData();
    }

    private void initView() {
        mBloodType = findViewById(R.id.blood_type);
        mBloodIv = findViewById(R.id.blood_iv);
        mBloodTatolWord = findViewById(R.id.blood_tatol_word);

        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.blood_type);

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        findViewById(R.id.reload).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getData();
            }
        });


        mText1 = (TextView) findViewById(R.id.text1);
        mText2 = (TextView) findViewById(R.id.text2);
        mText3 = (TextView) findViewById(R.id.text3);
        mContainer = (LinearLayout) findViewById(R.id.container);
    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        mYour = intent.getStringExtra(IntentExtraConfig.EXTRA_ID);
        mFather = intent.getStringExtra(IntentExtraConfig.EXTRA_JSON);
        mMother = intent.getStringExtra(IntentExtraConfig.EXTRA_PARCEL);
    }

    private void getData() {
        showLoadingDialog();
        KDSPApiController.getInstance().getBloodData(mYour, mFather, mMother, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                JSONObject jsonObject = response.optJSONObject("blood");
                mBgImage = jsonObject.optString("bg_img");
                mCharacterOneself = jsonObject.optString("character_oneself").replace("/", "\n");
                mEnjoyTypeOneself = jsonObject.optString("enjoy_type_oneself");
                mLoveSignalOneself = jsonObject.optString("love_signal_oneself");
                mProminentCharacterOneself = jsonObject.optString("prominent_character_oneself");
                mCareer = jsonObject.optString("career");
                mCharacter = jsonObject.optString("character");
                mLove = jsonObject.optString("love");
                mTotalWords = jsonObject.optString("total_words");

                BloodResultActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        refreshUI();
                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                dismissLoadingDialog();
                findViewById(R.id.normal_lay).setVisibility(View.GONE);
                findViewById(R.id.request_bad).setVisibility(View.VISIBLE);
                showFailureMessage(error);
            }
        });
    }

    private void refreshUI() {
        findViewById(R.id.normal_lay).setVisibility(View.VISIBLE);
        findViewById(R.id.request_bad).setVisibility(View.GONE);
        mBloodType.setText(getString(R.string.your_blood_tip) + "  " + mYour + getString(R.string.type));
        switch (mYour) {
            case "A":
                mBloodIv.setImageResource(R.drawable.blood_a);
                break;
            case "B":
                mBloodIv.setImageResource(R.drawable.blood_b);
                break;
            case "O":
                mBloodIv.setImageResource(R.drawable.blood_o);
                break;
            case "AB":
                mBloodIv.setImageResource(R.drawable.blood_ab);
                break;
        }

        mBloodTatolWord.setText(getString(R.string.stellar_meaning) + ": " + mTotalWords);
        if (TextUtils.isEmpty(mFather) || TextUtils.isEmpty(mMother)) {
            SpannableStringBuilder character = new SpannableStringBuilder();
            character.append("性格：").append(mCharacterOneself);
            character.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            character.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BloodResultActivity.this, R.color.pink14)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableStringBuilder enjoy = new SpannableStringBuilder();
            enjoy.append("喜欢的类型：").append(mEnjoyTypeOneself);
            enjoy.setSpan(new StyleSpan(Typeface.BOLD), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            enjoy.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BloodResultActivity.this, R.color.pink14)), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableStringBuilder love = new SpannableStringBuilder();
            love.append("恋爱讯号：").append(mLoveSignalOneself);
            love.setSpan(new StyleSpan(Typeface.BOLD), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            love.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BloodResultActivity.this, R.color.pink14)), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mText1.setText(character);
            mText2.setText(enjoy);
            mText3.setText(love);

            String[] list = mProminentCharacterOneself.split("/");



            if (list.length > 0) {
                View tip = mInflater.inflate(R.layout.blood_item_lay, null);
                TextView tipText = (TextView) tip.findViewById(R.id.text);
                SpannableStringBuilder tipbuilder = new SpannableStringBuilder();
                tipbuilder.append("特点：");
                tipbuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tipbuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BloodResultActivity.this, R.color.pink14)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tipText.setText(tipbuilder);
                mContainer.addView(tip);


                for (String string : list) {
                    View item = mInflater.inflate(R.layout.blood_item_lay, null);
                    TextView text = (TextView) item.findViewById(R.id.text);
                    text.setText(string);
                    mContainer.addView(item);
                }
            }

        } else {


            SpannableStringBuilder character = new SpannableStringBuilder();
            character.append("性格：").append(mCharacter);
            character.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            character.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BloodResultActivity.this, R.color.pink14)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mText1.setText(character);
            mText2.setVisibility(View.GONE);
            mText3.setVisibility(View.GONE);

            SpannableStringBuilder love = new SpannableStringBuilder();
            love.append("恋爱：").append(mLove);
            love.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            love.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BloodResultActivity.this, R.color.pink14)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            View item2 = mInflater.inflate(R.layout.blood_item_lay, null);
            TextView text2 = (TextView) item2.findViewById(R.id.text);
            text2.setText(love);

            SpannableStringBuilder career = new SpannableStringBuilder();
            career.append("事业：").append(mCareer);
            career.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            career.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BloodResultActivity.this, R.color.pink14)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            View item3 = mInflater.inflate(R.layout.blood_item_lay, null);
            TextView text3 = (TextView) item3.findViewById(R.id.text);
            text3.setText(career);

            mContainer.addView(item2);
            mContainer.addView(item3);
        }
    }

    public static void goToPage(Context context, String mYourBlood, String mFatherBlood, String mMotherBlood) {
        Intent intent = new Intent(context, BloodResultActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, mYourBlood);
        intent.putExtra(IntentExtraConfig.EXTRA_JSON, mFatherBlood);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, mMotherBlood);
        context.startActivity(intent);
    }
}
