package com.qizhu.rili.utils;

import java.util.HashMap;

public class CalendarUtil {
    private static int[] WEEK_WOOD_LUCKY = {1, 6, 3, 8};            //弱木幸运数字
    private static int[] WOOD_LUCKY = {2, 7, 4, 9, 5, 0};           //强木幸运数字
    private static int[] WEEK_FIRE_LUCKY = {3, 8, 2, 7};            //弱火幸运数字
    private static int[] FIRE_LUCKY = {5, 0, 1, 6, 4, 9};           //强火幸运数字
    private static int[] WEEK_WATER_LUCKY = {1, 6, 4, 9};           //弱水幸运数字
    private static int[] WATER_LUCKY = {2, 7, 3, 8, 5, 0};          //强水幸运数字
    private static int[] WEEK_EARTH_LUCKY = {5, 0, 2, 7};           //弱土幸运数字
    private static int[] EARTH_LUCKY = {3, 8, 1, 6, 4, 9};          //强土幸运数字
    private static int[] WEEK_GOLD_LUCKY = {4, 9, 5, 0};            //弱金幸运数字
    private static int[] GOLD_LUCKY = {3, 8, 2, 7, 1, 6};           //强金幸运数字

    //与我最配的属性
    public static HashMap<String, String> mLikePropertyMap = new HashMap<String, String>() {
        {
            put("强金", "木  水  火");
            put("弱金", "土  金");
            put("强木", "火  金  土");
            put("弱木", "水  木");
            put("强水", "木  火  土");
            put("弱水", "金  水");
            put("强火", "土  水  金");
            put("弱火", "火  木");
            put("强土", "金  木  水");
            put("弱土", "土  火");
        }
    };
    //我要远离的属性
    public static HashMap<String, String> mDislikePropertyMap = new HashMap<String, String>(){
        {
            put("强金", "土  金");
            put("弱金", "木  水  火");
            put("强木", "木  水");
            put("弱木", "土  金  火");
            put("强水", "水  金");
            put("弱水", "火  木  土");
            put("强火", "木   火");
            put("弱火", "金  水  土");
            put("强土", "土  火");
            put("弱土", "金  木  水");
        }
    };
    //最适合我的颜色
    public static HashMap<String, String> mLikeColorMap = new HashMap<String, String>(){
        {
            put("强金", "绿色  黑色  蓝色  红色");
            put("弱金", "黄色  咖啡色  金色  白色");
            put("强木", "红色  金色  白色  黄色  咖啡色");
            put("弱木", "黑色  蓝色  绿色");
            put("强水", "绿色  红色  黄色  咖啡色");
            put("弱水", "金色  白色  黑色  蓝色");
            put("强火", "黄色  咖啡色  黑色  蓝色  金色  白色");
            put("弱火", "红色  绿色");
            put("强土", "金色  白色  绿色  黑色  蓝色");
            put("弱土", "黄色  咖啡色  红色");
        }
    };
    //不适合的颜色
    public static HashMap<String, String> mDislikeColorMap = new HashMap<String, String>(){
        {
            put("强金", "黄色  咖啡色  金色  白色");
            put("弱金", "绿色  黑色  蓝色  红色");
            put("强木", "黑色  蓝色  绿色");
            put("弱木", "红色  金色  白色  黄色  咖啡色");
            put("强水", "金色  白色  黑色  蓝色");
            put("弱水", "绿色  红色  黄色  咖啡色");
            put("强火", "红色  绿色");
            put("弱火", "黄色  咖啡色  黑色  蓝色  金色  白色");
            put("强土", "黄色  咖啡色  红色");
            put("弱土", "金色  白色  绿色  黑色  蓝色");
        }
    };
    //最适合我的职业
    public static HashMap<String, String> mFitJobMap = new HashMap<String, String>(){
        {
            put("强金","园林 . 插花 . 鲜花产业 . 家具 . 服装业 . 印刷造纸业 . 茶叶产业 . 补习班 . 名品店 . 药品 . 食物 . 健康产品 . 农业 . 设计师 . 水利 . 航海 . 旅游 . 交通运输 . 跳水 . 洗衣店 . 饭店管理 . 广告业 . 记者 . 玩具业 . 澡堂 . 卡拉OK . 饮料业 . 速食店 . 油业（食用油 . 油漆等） . 美容业 . 销售 . 百货公司 . 照明 . 咨询 . 化工 . 酒类行业 . 照相 . 橡胶 . 玄学产业");
            put("弱金","宝石产业 . 地产建筑 . 外包公司 . 代理商 . 培训业 . 会展行当 . 经纪人 . 服务业 . 安保行业 . 瓷器业 . 律师 . 航空业 . 期货 . 殡葬业 . 金融业（股票 . 会计 . 贸易） . 机械 . 体育 . 手饰行当 . 按摩业 . 法官");
            put("强木","油业（食用油 . 油漆等） . 美容业 . 销售 . 百货公司 . 照明 . 咨询 . 化工 . 酒类行业 . 照相 . 橡胶 . 玄学产业 . 金融业（股票 . 会计 . 贸易） . 机械 . 体育 . 手饰行当 . 按摩业 . 法官 . 宝石产业 . 地产建筑 . 外包公司 . 代理商 . 培训业 . 会展行当 . 经纪人 . 服务业 . 安保行业 . 瓷器业 . 律师 . 航空业 . 期货 . 殡葬业");
            put("弱木","水利 . 航海 . 旅游 . 交通运输 . 跳水 . 洗衣店 . 饭店管理 . 广告业 . 记者 . 玩具业 . 澡堂 . 卡拉OK . 饮料业 . 速食店 . 园林 . 插花 . 鲜花产业 . 家具 . 服装业 . 印刷造纸业 . 茶叶产业 . 补习班 . 名品店 . 药品 . 食物 . 健康产品 . 农业 . 设计师");
            put("强水","园林 . 插花 . 鲜花产业 . 家具 . 服装业 . 印刷造纸业 . 茶叶产业 . 补习班 . 名品店 . 药品 . 食物 . 健康产品 . 农业 . 设计师 . 油业（食用油 . 油漆等） . 美容业 . 销售 . 百货公司 . 照明 . 咨询 . 化工 . 酒类行业 . 照相 . 橡胶 . 玄学产业 . 宝石产业 . 地产建筑 . 外包公司 . 代理商 . 培训业 . 会展行当 . 经纪人 . 服务业 . 安保行业 . 瓷器业 . 律师 . 航空业 . 期货 . 殡葬业");
            put("弱水","金融业（股票 . 会计 . 贸易） . 机械 . 体育 . 手饰行当 . 按摩业 . 法官 . 水利 . 航海 . 旅游 . 交通运输 . 跳水 . 洗衣店 . 饭店管理 . 广告业 . 记者 . 玩具业 . 澡堂 . 卡拉OK . 饮料业 . 速食店");
            put("强火","宝石产业 . 地产建筑 . 外包公司 . 代理商 . 培训业 . 会展行当 . 经纪人 . 服务业 . 安保行业 . 瓷器业 . 律师 . 航空业 . 期货 . 殡葬业 . 水利 . 航海 . 旅游 . 交通运输 . 跳水 . 洗衣店 . 饭店管理 . 广告业 . 记者 . 玩具业 . 澡堂 . 卡拉OK . 饮料业 . 速食店 . 金融业（股票 . 会计 . 贸易） . 机械 . 体育 . 手饰行当 . 按摩业 . 法官");
            put("弱火","油业（食用油 . 油漆等） . 美容业 . 销售 . 百货公司 . 照明 . 咨询 . 化工 . 酒类行业 . 照相 . 橡胶 . 玄学产业 . 园林 . 插花 . 鲜花产业 . 家具 . 服装业 . 印刷造纸业 . 茶叶产业 . 补习班 . 名品店 . 药品 . 食物 . 健康产品 . 农业 . 设计师");
            put("强土","金融业（股票 . 会计 . 贸易） . 机械 . 体育 . 手饰行当 . 按摩业 . 法官 . 园林 . 插花 . 鲜花产业 . 家具 . 服装业 . 印刷造纸业 . 茶叶产业 . 补习班 . 名品店 . 药品 . 食物 . 健康产品 . 农业 . 设计师 . 水利 . 航海 . 旅游 . 交通运输 . 跳水 . 洗衣店 . 饭店管理 . 广告业 . 记者 . 玩具业 . 澡堂 . 卡拉OK . 饮料业 . 速食店");
            put("弱土","宝石产业 . 地产建筑 . 外包公司 . 代理商 . 培训业 . 会展行当 . 经纪人 . 服务业 . 安保行业 . 瓷器业 . 律师 . 航空业 . 期货 . 殡葬业 . 油业（食用油 . 油漆等） . 美容业 . 销售 . 百货公司 . 照明 . 咨询 . 化工 . 酒类行业 . 照相 . 橡胶 . 玄学产业");
        }
    };
    //适合的食物
    public static HashMap<String, String> mLikeFoodMap = new HashMap<String, String>(){
        {
            put("强金", "含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理 . 鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理 . 含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒");
            put("弱金", "含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜 . 含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆");
            put("强木", "含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒 . 含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜 . 含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆");
            put("弱木", "鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理 . 含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理");
            put("强水", "含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理 . 含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒 . 含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜");
            put("弱水", "含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆 . 鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理");
            put("强火", "含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜 . 鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理 . 含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆");
            put("弱火", "含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒 . 含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理");
            put("强土", "含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆 . 含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理 . 鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理");
            put("弱土", "含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜 . 含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒");
        }
    };
    //不适合的食物
    public static HashMap<String, String> mDislikeFoodMap = new HashMap<String, String>(){
        {
            put("强金", "含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜 . 含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆");
            put("弱金", "含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理 . 鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理 . 含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒");
            put("强木", "鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理 . 含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理");
            put("弱木", "含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒 . 含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜 . 含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆");
            put("强水", "含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆 . 鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理");
            put("弱水", "含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理 . 含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒 . 含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜");
            put("强火", "含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒 . 含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理");
            put("弱火", "含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜 . 鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理 . 含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆");
            put("强土", "含维生素D的食物 . 茎根类 . 面包 . 米饭 . 所有甜食 . 蛋类 . 骨头汤 . 燕窝 . 干果 . 甘蔗 . 蜂蜜 . 含维生素B的食物 . 红枣 . 辣椒 . 枸杞 . 火锅 . 白酒");
            put("弱土", "含锌的食品 . 白萝卜 . 葱姜蒜 . 芹类 . 芥菜 . 蚕豆 . 含维生素C的食物 . 所有蔬果 . 小黄瓜 . 菌类 . 韩国料理 . 鱼肝油 . 各种鱼类 . 海带 . 小龙虾 . 日本料理");
        }
    };

    /**
     * 获取喜神
     *
     * @param str 五行属性
     */
    public static String getXiGod(String str) {
        if (("弱木").equals(str)) {
            return "木，水";
        } else if (("强木").equals(str)) {
            return "土，火，金";
        } else if (("弱火").equals(str)) {
            return "火，木";
        } else if (("强火").equals(str)) {
            return "水，土，金";
        } else if (("弱水").equals(str)) {
            return "金，水";
        } else if (("强水").equals(str)) {
            return "木，土，火";
        } else if (("弱土").equals(str)) {
            return "土，火";
        } else if (("强土").equals(str)) {
            return "木，金，水";
        } else if (("弱金").equals(str)) {
            return "金，土";
        } else if (("强金").equals(str)) {
            return "木，水，火";
        }
        return "";
    }

    /**
     * 获取幸运数字
     *
     * @param str 五行属性
     * @param day 传入天数,为这个月的第多少天
     */
    public static String getLuckyNum(String str, int day) {
        int[] mLuckyNum;

        if (("弱木").equals(str)) {
            mLuckyNum = WEEK_WOOD_LUCKY;
        } else if (("强木").equals(str)) {
            mLuckyNum = WOOD_LUCKY;
        } else if (("弱火").equals(str)) {
            mLuckyNum = WEEK_FIRE_LUCKY;
        } else if (("强火").equals(str)) {
            mLuckyNum = FIRE_LUCKY;
        } else if (("弱水").equals(str)) {
            mLuckyNum = WEEK_WATER_LUCKY;
        } else if (("强水").equals(str)) {
            mLuckyNum = WATER_LUCKY;
        } else if (("弱土").equals(str)) {
            mLuckyNum = WEEK_EARTH_LUCKY;
        } else if (("强土").equals(str)) {
            mLuckyNum = EARTH_LUCKY;
        } else if (("弱金").equals(str)) {
            mLuckyNum = WEEK_GOLD_LUCKY;
        } else if (("强金").equals(str)) {
            mLuckyNum = GOLD_LUCKY;
        } else {
            return "";
        }
        int length = mLuckyNum.length;
        int firstIndex = day % length;
        int lastIndex = day % (length - 1);
        //当两者相等，即day为length和length - 1的积的时候，会发生这种情况，那么把lastIndex做加1处理
        if (firstIndex == lastIndex) {
            lastIndex++;
        }
        return mLuckyNum[firstIndex] + "、" + mLuckyNum[lastIndex];
    }
}
