package com.qizhu.rili.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.EventItem;
import com.qizhu.rili.bean.LuckyAlias;
import com.qizhu.rili.bean.StartupImage;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.bean.YSRLTest;
import com.qizhu.rili.utils.LogUtils;

import java.sql.SQLException;

/**
 * 数据库的基类
 */
public class YSRLDataBaseHelper extends OrmLiteSqliteOpenHelper {
    private static String TAG = "databasehelper:";
    //数据库版本, 新建表version需要修改
    private static final int DATABASE_VERSION = 11;

    //数据库名
    private static final String DATABASE_NAME = "YSRL";

    private Dao<User, String> mUser;
    private Dao<StartupImage, String> mStartupImage;
    private Dao<LuckyAlias, String> mLuckyAlias;

    public YSRLDataBaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        LogUtils.d("-----onCreate--");
        try {
            TableUtils.createTableIfNotExists(connectionSource, User.class);

            TableUtils.createTableIfNotExists(connectionSource, EventItem.class);
            TableUtils.createTableIfNotExists(connectionSource, YSRLTest.class);
            TableUtils.createTableIfNotExists(connectionSource, StartupImage.class);
            TableUtils.createTableIfNotExists(connectionSource, LuckyAlias.class);
        } catch (SQLException e) {
            LogUtils.e(TAG + e.getMessage(), e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

        LogUtils.d("-----onUpgrade--"+"oldVersion:"+oldVersion+"--newVersion"+newVersion);
        try {
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, EventItem.class, true);
            TableUtils.dropTable(connectionSource, YSRLTest.class, true);
            TableUtils.dropTable(connectionSource, StartupImage.class, true);
            TableUtils.dropTable(connectionSource, LuckyAlias.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            LogUtils.e(TAG + e.getMessage(), e);
        }
    }

    /**
     * 获取用户的Dao
     */
    public Dao<User, String> getUserDao() {
        if (mUser == null) {
            try {
                mUser = getDao(User.class);
            } catch (SQLException e) {
                LogUtils.e(TAG + e.getMessage(), e);
            }
        }
        return mUser;
    }

    /**
     * 获取启动图的Dao
     */
    public Dao<StartupImage, String> getStartupImageDao() {
        if (mStartupImage == null) {
            try {
                mStartupImage = getDao(StartupImage.class);
            } catch (SQLException e) {
                LogUtils.e(TAG + e.getMessage(), e);
            }
        }
        return mStartupImage;
    }

    /**
     * 获取别名的Dao
     */
    public Dao<LuckyAlias, String> getLuckyAliasDao() {
        if (mLuckyAlias == null) {
            try {
                mLuckyAlias = getDao(LuckyAlias.class);
            } catch (SQLException e) {
                LogUtils.e(TAG + e.getMessage(), e);
            }
        }
        return mLuckyAlias;
    }


    @Override
    public void close() {
        super.close();
        mUser = null;
        mStartupImage = null;
        mLuckyAlias = null;
    }
}
