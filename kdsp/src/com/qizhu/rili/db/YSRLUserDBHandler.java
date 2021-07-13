package com.qizhu.rili.db;

import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.SPUtils;

/**
 * 用户数据操纵
 */
public class YSRLUserDBHandler {
    private static Dao<User, String> mDao = AppContext.getDBHelper().getUserDao();

    /**
     * 插入单条数据,插入的时候同步更新匿名状态
     */
    public static void insertOrUpdateUser(User user) {
        if (user != null) {
            try {
                mDao.createOrUpdate(user);
                SPUtils.putIntValue(YSRLConstants.USER_SEX, user.userSex);
                SPUtils.putStringValue(YSRLConstants.USER_BIRTH, user.birthTime.toString());
            } catch (Exception e) {
                LogUtils.e(e.getMessage(), e);
            }
        }
    }

    /**
     * 获取指定用户
     */
    public static User getUser(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        try {
            QueryBuilder<User, String> queryBuilder = mDao.queryBuilder();
            Where<User, String> whereBuilder = queryBuilder.where();
            whereBuilder.eq(User.USER_ID_PARAM, userId);
            return queryBuilder.queryForFirst();
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
            return null;
        }
    }
}
