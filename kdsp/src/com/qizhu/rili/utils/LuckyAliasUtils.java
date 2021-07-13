package com.qizhu.rili.utils;

import android.content.Context;
import android.os.Looper;

import com.qizhu.rili.bean.LuckyAlias;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.db.LuckyAliasDBHandler;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

/**
 * Created by lindow on 2/23/16.
 * 吉日别名工具类
 */
public class LuckyAliasUtils {
    //版本号
    public static final String LUCKY_ALIAS_VERSION = "lucky_alias_version";

    /**
     * 根据返回的版本号更新客户端数据库
     */
    public static void updateVersion(Context context, int version) {
        if (version > getVersion()) {
            createOrUpdateVersion(context, version);
        }
    }

    /**
     * 更新信息
     */
    public static void createOrUpdateVersion(final Context context, final int version) {
        new Thread() {
            @Override
            public void run() {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                LogUtils.d("---> now create or update lucky alias db ");
                getLuckyAlias(context, version);
                Looper.loop();
            }
        }.start();
    }

    private static void getLuckyAlias(final Context context, final int version) {
        KDSPApiController.getInstance().findLuckydayAlias(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                List<LuckyAlias> list = LuckyAlias.parseListFromJSON(response.optJSONArray("luckydays"));
                LuckyAliasDBHandler.insertTestListAfterDelete(list);
                saveVersion(version);
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                //一旦下载失败，则直接解析本地json
                parseAssertsJson(context);
            }
        });
    }

    /**
     * 解析本地存储的地址数据库
     */
    public static void parseAssertsJson(Context context) {
        try {
            InputStream is = context.getAssets().open("lucky_alias.json");
            byte[] locationBytes = new byte[is.available()];
            is.read(locationBytes);
            is.close();

            String JsonStr = new String(locationBytes);
            LogUtils.d("location content is %s", JsonStr);
            JSONObject json = JSONUtils.parseJSONObject(JsonStr);
            List<LuckyAlias> list = LuckyAlias.parseListFromJSON(json.optJSONArray("luckydays"));
            LuckyAliasDBHandler.insertTestListAfterDelete(list);
            saveVersion(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取客户端保存的版本号
     */
    public static int getVersion() {
        return SPUtils.getIntValue(LUCKY_ALIAS_VERSION, 0);
    }

    /**
     * 保存地域版本号
     */
    public static void saveVersion(int version) {
        SPUtils.putIntValue(LUCKY_ALIAS_VERSION, version);
    }
}
