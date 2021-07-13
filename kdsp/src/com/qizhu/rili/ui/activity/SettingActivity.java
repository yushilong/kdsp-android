package com.qizhu.rili.ui.activity;

import java.io.File;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.AppManager;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.SoftUpdate;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.service.YSRLService;
import com.qizhu.rili.ui.dialog.AlertEditDialogFragment;
import com.qizhu.rili.ui.dialog.ResultDialogFragment;
import com.qizhu.rili.ui.dialog.UpdateVersionDialogFragment;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.FileUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.utils.UmengUtils;

import org.json.JSONObject;

public class SettingActivity extends BaseActivity {
    private static final int GET_FILE_SIZE_TASK = 1;

    TextView mCacheSize;        //清除缓存
    TextView mVersion;          //版本
    TextView mLogin;            //登陆

    private SoftUpdate softUpdate;
    private int mClickMode = 0;

    private Handler getFileSizeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_FILE_SIZE_TASK:
                    mCacheSize.setText(msg.obj.toString());
                    break;
            }
        }
    };

    private BroadcastReceiver clearCacheBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BroadcastUtils.ACTION_CLEAR_CACHE_BROADCAST.equals(action)) {
                mCacheSize.setText("0.00B");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_lay);
        initUI();
        BroadcastUtils.getInstance().registerReceiver(clearCacheBroadcast, new IntentFilter(BroadcastUtils.ACTION_CLEAR_CACHE_BROADCAST));
    }

    protected void initUI() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.setting);
        View mTitleLay = findViewById(R.id.header);
        mTitleLay.setBackgroundResource(R.drawable.title_bg);

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        findViewById(R.id.about).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AboutActivity.goToPage(SettingActivity.this);
            }
        });
        findViewById(R.id.clear_cache).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mClickMode = 1;
                showDialogFragment(AlertEditDialogFragment.newInstance("清除缓存", "将清除语音，图片等缓存数据，确定清除？", "取消", "确定", AlertEditDialogFragment.TITLE_MODE), "清除缓存");
            }
        });
        findViewById(R.id.version_update).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mClickMode = 2;
                KDSPApiController.getInstance().findSoftUpdate(new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(JSONObject response) {
                        if (response != null) {
                            ArrayList<SoftUpdate> softUpdates = SoftUpdate.parseListFromJSON(response.optJSONArray("softUpdates"));
                            if (softUpdates.size() > 0) {
                                softUpdate = softUpdates.get(0);
                                //服务器版本比当前版本新，则启动更新提示
                                if (softUpdate.versionCode > AppContext.versionCode) {
                                    SettingActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                String tip = softUpdate.description.replace("<br>", "\n");
                                                showDialogFragment(UpdateVersionDialogFragment.newInstance(tip), "版本更新");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    showDialogFragment(ResultDialogFragment.newInstance("您使用的是最新版本喽", "", "", null), "版本更新");
                                }
                            }
                        }
                    }

                    @Override
                    public void handleAPIFailureMessage(Throwable error, String reqCode) {
                        UIUtils.toastMsg("版本检测失败，请检查下网络~");
                    }
                });
            }
        });
        findViewById(R.id.login).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (!AppContext.isAnonymousUser()) {
                    //友盟统计--用户登出的次数
                    UmengUtils.onEventLogoutCount(SettingActivity.this);
                    //用户主动退出登录时，清空数据
                    AppContext.baseContext.logoutAndClear();
                    AppManager.getAppManager().finishAllActivity();
                }
                LoginActivity.goToPage(SettingActivity.this, true);
            }
        });

        mCacheSize = (TextView) findViewById(R.id.cache_size);
        mVersion = (TextView) findViewById(R.id.version);
        mLogin = (TextView) findViewById(R.id.login);

        //获取缓存文件夹大小
        AppContext.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Message message = getFileSizeHandler.obtainMessage();
                    message.what = GET_FILE_SIZE_TASK;
                    message.obj = FileUtils.FormatFileSize(FileUtils.getDirFilesSize(new File(FileUtils.getImageCacheDirPath())) + FileUtils.getDirFilesSize(new File(FileUtils.getUserCacheDirPath())));
                    getFileSizeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (AppContext.isAnonymousUser()) {
            mLogin.setText(R.string.login);
        } else {
            mLogin.setText(R.string.logout);
        }

        if (LogUtils.DEBUG) {
            findViewById(R.id.setting_server).setVisibility(View.VISIBLE);
            findViewById(R.id.setting_server).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    SettingServerActivity.goToPage(SettingActivity.this);
                }
            });
        } else {
            findViewById(R.id.setting_server).setVisibility(View.GONE);
        }
        mVersion.setText(AppContext.version);
    }

    @Override
    public <T> void setExtraData(T t) {
        if ("ok".equals(t)) {
            if (1 == mClickMode) {
                YSRLService.startClearCache(SettingActivity.this);
            } else if (softUpdate != null) {
                YSRLService.startDownloadAPK(SettingActivity.this, softUpdate.appUrl, softUpdate.version, true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastUtils.getInstance().unregisterReceiver(clearCacheBroadcast);
        if (getFileSizeHandler != null) {
            getFileSizeHandler.removeCallbacksAndMessages(null);
        }
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }
}
