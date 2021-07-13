package com.qizhu.rili.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.bean.LuckyAlias;
import com.qizhu.rili.utils.LogUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by lindow on 2/23/16.
 * 吉日别名
 */
public class LuckyAliasDBHandler {
    private static Dao<LuckyAlias, String> mDao = AppContext.getDBHelper().getLuckyAliasDao();

    /**
     * 插入单条数据
     */
    public static void insertOrUpdate(LuckyAlias alias) {
        if (alias != null) {
            try {
                mDao.createOrUpdate(alias);
            } catch (SQLException e) {
                LogUtils.e(e.getMessage(), e);
            }
        }
    }

    /**
     * 批量插入数据
     */
    public static void insertList(final List<LuckyAlias> lst) {
        try {
            mDao.callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (LuckyAlias alias : lst) {
                        mDao.createOrUpdate(alias);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        }
    }

    /**
     * 插入前先清空所有数据
     */
    public static void insertTestListAfterDelete(List<LuckyAlias> lst) {
        try {
            DeleteBuilder<LuckyAlias, String> deleteBuilder = mDao.deleteBuilder();
            deleteBuilder.delete();
            insertList(lst);
        } catch (SQLException e) {
            LogUtils.e(e.getMessage(), e);
        }
    }

    /**
     * 根据名字得到数组
     */
    public static String[] getArrayOfname(String name) {
        try {
            QueryBuilder<LuckyAlias, String> queryBuilder = mDao.queryBuilder();
            Where<LuckyAlias, String> whereBuilder = queryBuilder.where();
            whereBuilder.eq(LuckyAlias.NAME_PARAM, name);
            LuckyAlias alias = queryBuilder.queryForFirst();
            if (alias != null) {
                return alias.alias.split(",");
            } else {
                return null;
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
            return null;
        }
    }
}
