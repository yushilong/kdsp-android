package com.qizhu.rili.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.SoftUpdate;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.service.YSRLService;
import com.qizhu.rili.ui.dialog.UpdateVersionDialogFragment;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 默认的出错界面，提示用户升级版本
 */
public class DefaultPageActivity extends BaseActivity {
    private SoftUpdate softUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_page_layout);
        findViewById(R.id.default_page_update).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                KDSPApiController.getInstance().findSoftUpdate(new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(JSONObject response) {
                        if (response != null) {
                            ArrayList<SoftUpdate> softUpdates = SoftUpdate.parseListFromJSON(response.optJSONArray("softUpdates"));
                            if (softUpdates.size() > 0) {
                                softUpdate = softUpdates.get(0);
                                //服务器版本比当前版本新，则启动更新提示
                                if (softUpdate.versionCode > AppContext.versionCode) {
                                    DefaultPageActivity.this.runOnUiThread(new Runnable() {
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
                                }
                            }
                        }
                    }

                    @Override
                    public void handleAPIFailureMessage(Throwable error, String reqCode) {

                    }
                });
            }
        });
    }

    @Override
    public <T> void setExtraData(T t) {
        if ("ok".equals(t) && softUpdate != null) {
            YSRLService.startDownloadAPK(DefaultPageActivity.this, softUpdate.appUrl, softUpdate.version, true);
        }
    }
}
