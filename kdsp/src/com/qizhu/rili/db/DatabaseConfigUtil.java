package com.qizhu.rili.db;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.qizhu.rili.bean.EventItem;
import com.qizhu.rili.bean.LuckyAlias;
import com.qizhu.rili.bean.StartupImage;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.bean.YSRLTest;

import java.io.IOException;
import java.sql.SQLException;

/**
 * DatabaseConfigUtl writes a configuration file to avoid using Annotation processing in runtime. This gains a
 * noticable performance improvement. configuration file is written to /res/raw/ by default.
 * More info at: http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Config-Optimization
 * <p/>
 * 生成ormlite_config文件，减少annotation带来的效率问题,每次更新数据库需更新此文件，具体方法直接以java 工程的形式编译即可(第一次生成时需新建raw文件夹)
 * 2016-9-5更新,由于更改了gradle的集成工程方式,所以会出现找不到raw文件夹的错误,因为writeConfigFile会默认从工程最上层目录开始寻找,所以解决方式为最上层新建一个临时res/raw文件夹然后覆盖
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    private static final Class<?>[] classes = new Class[]{User.class, EventItem.class, YSRLTest.class, StartupImage.class, LuckyAlias.class};

    public static void main(String[] args) throws SQLException, IOException {
        writeConfigFile("ormlite_config.txt", classes);
    }
}
