package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.BloodPickDialogFragment;
import com.qizhu.rili.ui.dialog.ResultDialogFragment;
import com.qizhu.rili.utils.UIUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lindow on 06/01/2017.
 * 血型的activity
 */
public class InferringBloodActivity extends BaseActivity {
    private TextView mYourBlood;        //你的血型
    private TextView mFatherBlood;      //父亲的血型
    private TextView mMotherBlood;      //母亲的血型
    private String   mYour;
    private String   mFather;
    private String   mMother;
    private String   mYourType = "A型";
    private String   mFatherType= "A型";
    private String   mMotherType= "A型";
    private int mBlood = 0;             //选择的血型类别，1为你的血型，2为父亲，3为母亲

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inferring_blood_lay);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.blood_type);
        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        mYourBlood = (TextView) findViewById(R.id.your_blood);
        mFatherBlood = (TextView) findViewById(R.id.father_blood);
        mMotherBlood = (TextView) findViewById(R.id.mother_blood);

        findViewById(R.id.confirm).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (TextUtils.isEmpty(mYour)) {
                    UIUtils.toastMsg("请输入你的血型~");
                } else {
                    if (isVaildBlood(mYour, mFather, mMother)) {
                        BloodResultActivity.goToPage(InferringBloodActivity.this, mYour, mFather, mMother);
                    } else {
                        showDialogFragment(ResultDialogFragment.newInstance("请小主再确认一下吧", "这个遗传规律可不是地球人的哟！", "", null), "血型检测");
                    }
                }
            }
        });
    }

    @Override
    public <T> void setExtraData(T t) {
        if (t instanceof String) {
            String blood = (String) t;
            if (!TextUtils.isEmpty(blood)) {
                //血型选择返回
                if (blood.length() == 3) {
                    blood = blood.substring(0, 2);
                } else {
                    blood = blood.substring(0, 1);
                }
                switch (mBlood) {
                    case 1:
                        mYour = blood;
                        mYourType = blood + "型";
                        mYourBlood.setText(blood + "型");
                        mYourBlood.setTextColor(ContextCompat.getColor(this, R.color.purple));
                        break;
                    case 2:
                        mFather = blood;
                        mFatherType = blood + "型";
                        mFatherBlood.setText(blood + "型");
                        mFatherBlood.setTextColor(ContextCompat.getColor(this, R.color.purple));
                        break;
                    case 3:
                        mMother = blood;
                        mMotherType = blood + "型";
                        mMotherBlood.setText(blood + "型");
                        mMotherBlood.setTextColor(ContextCompat.getColor(this, R.color.purple));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    //血型是否合法
    private boolean isVaildBlood(String myBlood, String dadBlood, String momBlood) {
        if (TextUtils.isEmpty(myBlood)) {
            return false;
        } else {
            if (!TextUtils.isEmpty(dadBlood) && !TextUtils.isEmpty(momBlood)) {
                String result = "";
                if ("A".equals(dadBlood) && "A".equals(momBlood)) {
                    result = "A,O";
                } else if ("A".equals(dadBlood) && "B".equals(momBlood)) {
                    result = "A,B,AB,O";
                } else if ("A".equals(dadBlood) && "AB".equals(momBlood)) {
                    result = "A,B,AB";
                } else if ("A".equals(dadBlood) && "O".equals(momBlood)) {
                    result = "A,O";
                } else if ("B".equals(dadBlood) && "A".equals(momBlood)) {
                    result = "A,B,AB,O";
                } else if ("B".equals(dadBlood) && "B".equals(momBlood)) {
                    result = "B,O";
                } else if ("B".equals(dadBlood) && "AB".equals(momBlood)) {
                    result = "A,B,AB";
                } else if ("B".equals(dadBlood) && "O".equals(momBlood)) {
                    result = "B,O";
                } else if ("AB".equals(dadBlood) && "A".equals(momBlood)) {
                    result = "A,B,AB";
                } else if ("AB".equals(dadBlood) && "B".equals(momBlood)) {
                    result = "A,B,AB";
                } else if ("AB".equals(dadBlood) && "AB".equals(momBlood)) {
                    result = "A,B,AB";
                } else if ("AB".equals(dadBlood) && "O".equals(momBlood)) {
                    result = "A,B";
                } else if ("O".equals(dadBlood) && "A".equals(momBlood)) {
                    result = "A,O";
                } else if ("O".equals(dadBlood) && "B".equals(momBlood)) {
                    result = "B,O";
                } else if ("O".equals(dadBlood) && "AB".equals(momBlood)) {
                    result = "A,B";
                } else if ("O".equals(dadBlood) && "O".equals(momBlood)) {
                    result = "O";
                }

                if (!TextUtils.isEmpty(result)) {
                    if (result.contains(myBlood)) {
                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        return false;
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, InferringBloodActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.your_blood_ll, R.id.father_blood_ll, R.id.mother_blood_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.your_blood_ll:
                mBlood = 1;
                showDialogFragment(BloodPickDialogFragment.newInstance(mYourType), "选择你的血型");
                break;
            case R.id.father_blood_ll:
                mBlood = 2;
                showDialogFragment(BloodPickDialogFragment.newInstance(mFatherType), "选择你父亲的血型");
                break;
            case R.id.mother_blood_ll:
                mBlood = 3;
                showDialogFragment(BloodPickDialogFragment.newInstance(mMotherType), "选择你母亲的血型");
                break;
        }
    }
}
