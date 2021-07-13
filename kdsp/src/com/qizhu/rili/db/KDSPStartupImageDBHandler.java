package com.qizhu.rili.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.bean.StartupImage;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.LogUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by lindow on 2014/11/13.
 * 启动图的数据库
 */
public class KDSPStartupImageDBHandler {
    private static Dao<StartupImage, String> mDao = AppContext.getDBHelper().getStartupImageDao();

    /**
     * 批量插入数据
     */
    public static void insertImageList(final List<StartupImage> lst) {
        try {
            mDao.callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (StartupImage image : lst) {
                        mDao.createOrUpdate(image);
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
    public static void insertImageListAfterDelete(List<StartupImage> lst) {
        try {

            LogUtils.d("----- before" + lst.get(0).imageUrl);
            //删除原数据之前，将原数据的启动图移动到新数据,注意比对图片地址是否发生变化,只有图片地址一致才迁移,一定要迁移启动次数，否则启动次数会无作用
            for (StartupImage startupImage : lst) {
                StartupImage tmpImage = getStartupImage(startupImage.stId);
                if (tmpImage != null && startupImage.imageUrl.equals(tmpImage.imageUrl)) {
                    startupImage.bytes = tmpImage.bytes;
                    startupImage.showTimes = tmpImage.showTimes;
                }

            }
            StartupImage startupImage = getNowImage();
            if(startupImage != null){
                mDao.delete(startupImage);
            }

            DeleteBuilder<StartupImage, String> deleteBuilder = mDao.deleteBuilder();

            deleteBuilder.delete();

            LogUtils.d("----- after" + lst.get(0).imageUrl);
            insertImageList(lst);
        } catch (SQLException e) {
            LogUtils.e(e.getMessage(), e);
        }
    }



    public static void deleteImage() {
        try {
            StartupImage startupImage = getNowImage();
            if(startupImage != null){
                mDao.delete(startupImage);
            }


        } catch (SQLException e) {
            LogUtils.e(e.getMessage(), e);
        }
    }


    /**
     * 获取所有的启动图列表
     */
    public static List<StartupImage> getAllList() {
        List<StartupImage> rtn = new ArrayList<StartupImage>();
        try {
            QueryBuilder<StartupImage, String> queryBuilder = mDao.queryBuilder();
            return queryBuilder.query();
        } catch (SQLException e) {
            LogUtils.e(e.getMessage(), e);
        }
        return rtn;
    }

    public static void insertOrUpdate(StartupImage startupImage) {
        try {
            mDao.createOrUpdate(startupImage);
        } catch (SQLException e) {
            LogUtils.e(e.getMessage(), e);
        }
    }

    /**
     * 获取符合当前时间的启动图
     */
    public static StartupImage getNowImage() {
        int time = DateUtils.getCurrentIntTime();
        try {
            QueryBuilder<StartupImage, String> queryBuilder = mDao.queryBuilder();
            Where<StartupImage, String> whereBuilder = queryBuilder.where();
            whereBuilder.lt(StartupImage.START_TIME, time).and().gt(StartupImage.END_TIME, time);
            return queryBuilder.queryForFirst();
        } catch (SQLException e) {
            LogUtils.e(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取当前id的启动图
     */
    private static StartupImage getStartupImage(String id) {
        try {
            QueryBuilder<StartupImage, String> queryBuilder = mDao.queryBuilder();
            Where<StartupImage, String> whereBuilder = queryBuilder.where();
            whereBuilder.eq(StartupImage.ID, id);
            return queryBuilder.queryForFirst();
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 更新数据库中启动图的显示次数
     */
    public static void updateShowTimes(StartupImage startupImage) {
        try {
            UpdateBuilder<StartupImage, String> updateBuilder = mDao.updateBuilder();
            updateBuilder.where().eq(StartupImage.ID, startupImage.stId);
            updateBuilder.updateColumnValue(StartupImage.SHOW_TIMES, startupImage.showTimes);
            updateBuilder.update();
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        }
    }
}
