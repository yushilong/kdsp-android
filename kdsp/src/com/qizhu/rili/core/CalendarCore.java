package com.qizhu.rili.core;

import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.utils.ChinaDateUtil;

import java.util.Calendar;

/**
 * jni的核心类
 */
public class CalendarCore {
    public static String[] WeekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    public static String[] Months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    public static int mDays1901To2100 = 73049;             //1901-2100的天数
    public static int mWeeks1901To2100 = 10435;            //1901-2100的周数
    public static int mMonths1901To2100 = 2400;            //1901-2100的月数,由于阴历数组的限制，所以我们以此判定200年

    static {
        System.loadLibrary("CalendarCore");
    }

    //1901年之后多少天的日期,截止到2100年,从距0天开始
    public static DateTime getDateTimeByDaysFrom1901(int days) {
        int tmpdays = 0;
        int year = 1901;
        int month = 0;
        //算出年份
        for (int i = 1901; i < 2101; i++) {
            int td = tmpdays + (isLeapYear(i) ? 366 : 365);
            if (td > days) {
                year = i;
                break;
            }
            tmpdays = td;
        }

        days -= tmpdays;
        tmpdays = 0;
        //算出月份
        for (int i = 0; i < 12; i++) {
            int td = tmpdays + daysInMonth(year, i);
            if (td > days) {
                month = i;
                break;
            }
            tmpdays = td;
        }

        //距离从0开始，每月从1号开始
        DateTime dt = new DateTime(year, month, days - tmpdays + 1);
        if (days == tmpdays - 1) {
            dt.month = dt.month - 1;
            if (month == 0) {
                dt.year = dt.year - 1;
                dt.month = 11;
            }
            dt.day = daysInMonth(dt.year, dt.month);
        }

        return dt;
    }

    //1901年之后多少月的日期
    public static DateTime getDateTimeByMonthsFrom1901(int months) {
        int year = months / 12 + 1901;
        int month = months % 12;
        return new DateTime(year, month, 1);
    }

    //1901年之后多少星期的日期
    public static DateTime getDateTimeByWeeksFrom1901(int weeks) {
        int days = weeks * 7;
        return getDateTimeByDaysFrom1901(days);
    }

    //1901到当前日期的天数,1号距今天的天数为0，所以应减去1
    public static int daysFrom1901(DateTime dt) {
        int days = 0;
        for (int i = 1901; i < dt.year; i++) {
            days += (isLeapYear(i) ? 366 : 365);
        }
        Calendar c = Calendar.getInstance();
        c.set(dt.year, dt.month, dt.day);
        return days + c.get(Calendar.DAY_OF_YEAR) - 1;
    }

    //1901到当前日期的周数
    public static int weeksFrom1901(DateTime dt) {
        int days = daysFrom1901(dt);
        return days / 7;
    }

    //1901到当前日期的月数,从0开始
    public static int monthsFrom1901(DateTime dt) {
        return (dt.year - 1901) * 12 + dt.month;
    }

    //获取节气的名字
    public static String getSolarTermName(DateTime dt) {
        return nativeGetSolarTermName(dt.year, dt.month, dt.day);
    }

    //是否闰年
    public static boolean isLeapYear(int year) {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    //这个月有多少天
    public static int daysInMonth(int year, int mon) {
        return nativeintDaysInMon(year, mon);
    }

    //获取星期几
    public static String getWeekDayString(DateTime dt) {
        return WeekDays[getWeekDay(dt)];
    }

    //这一天是星期几
    public static int getWeekDay(DateTime dt) {
        return getWeekDay(dt.year, dt.month, dt.day);
    }

    //这一天是星期几
    public static int getWeekDay(int year, int mon, int day) {
        int m = mon + 1;
        if (m < 3) {
            year--;
            m += 12;
        }
        int y = year % 100;
        int c = year / 100;
        int w = y + y / 4 + c / 4 - 2 * c + 26 * (m + 1) / 10 + day - 1;
        int r = w % 7;
        return r < 0 ? 7 + r : r;
    }

    public static DateTime shiftDays(DateTime item, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(item.getDate());
        calendar.add(Calendar.DATE, days);
        return new DateTime(calendar.getTime());
    }

    public static DateTime shiftMonth(DateTime item, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(item.getDate());
        calendar.add(Calendar.MONTH, months);
        return new DateTime(calendar.getTime());
    }

    //获取星宿的名字
    public static String getStellarName(DateTime dt) {
        return nativeGetStellarName(dt.year, dt.month, dt.day);
    }

    //获取星宿的说明
    public static String getStellarSub(DateTime dt) {
        return nativeGetStellarSub(dt.year, dt.month, dt.day);
    }

    //获取星宿的总言
    public static String getStellarMeaning(DateTime dt) {
        return nativeGetStellarMeaning(dt.year, dt.month, dt.day);
    }

    //获取星宿的本命
    public static String getStellarLife(DateTime dt) {
        return nativeGetStellarLife(dt.year, dt.month, dt.day);
    }

    //获取星宿的命运
    public static String getStellarFate(DateTime dt) {
        return nativeGetStellarFate(dt.year, dt.month, dt.day);
    }

    //获取星宿的性格
    public static String getStellarCharacter(DateTime dt) {
        return nativeGetStellarCharacter(dt.year, dt.month, dt.day);
    }

    //获取星宿的事业
    public static String getStellarCareer(DateTime dt) {
        return nativeGetStellarCareer(dt.year, dt.month, dt.day);
    }

    //获取星宿的财运
    public static String getStellarFortune(DateTime dt) {
        return nativeGetStellarFortune(dt.year, dt.month, dt.day);
    }

    //获取星宿的爱情
    public static String getStellarLove(DateTime dt) {
        return nativeGetStellarLove(dt.year, dt.month, dt.day);
    }

    //获取星宿的健康
    public static String getStellarHealth(DateTime dt) {
        return nativeGetStellarHealth(dt.year, dt.month, dt.day);
    }

    //获取星宿的性格描述
    public static String getCharacterDesc(DateTime dt) {
        return nativeGetCharacterDesc(dt.year, dt.month, dt.day);
    }

    //获取星宿的脾气描述
    public static String getTemperDesc(DateTime dt) {
        return nativeGetTemperDesc(dt.year, dt.month, dt.day);
    }

    //获取星宿的爱情描述
    public static String getLoveDesc(DateTime dt) {
        return nativeGetLoveDesc(dt.year, dt.month, dt.day);
    }

    //获取星宿的罩门描述
    public static String getWeakDesc(DateTime dt) {
        return nativeGetWeakDesc(dt.year, dt.month, dt.day);
    }

    //获取星宿的格调描述
    public static String getElementDesc(DateTime dt) {
        return nativeGetElementDesc(dt.year, dt.month, dt.day);
    }

    //获取神兽
    public static int getCreatureIndex(DateTime dt) {
        return nativeGetCreatureIndex(dt.year, dt.month, dt.day);
    }

    //传入出生日期看某个action是否适宜
    public static boolean isGoodDay4Act(DateTime item, DateTime bItem, int action, int sex) {
        return nativeIsGoodDay4Act(item.year, item.month, item.day, bItem.year, bItem.month,
                bItem.day, bItem.hour, action, sex);
    }

    //获取五行
    public static String getElementName(DateTime time) {
        return nativeGetElementName(time.year, time.month, time.day, time.hour);
    }

    //获取幸运日
    public static String getDayTitle(DateTime item, DateTime bItem, boolean isMale) {
        return nativeGetDayTitle(item.year, item.month, item.day, bItem.year, bItem.month, bItem.day, isMale);
    }

    //获取幸运日说明
    public static String getDaySummary(DateTime item, DateTime bItem, boolean isMale) {
        return nativeGetDaySummary(item.year, item.month, item.day, bItem.year, bItem.month, bItem.day, isMale);
    }

    //获取幸运日详细
    public static String getDayDetail(DateTime item, DateTime bItem, boolean isMale) {
        return nativeGetDayDetail(item.year, item.month, item.day, bItem.year, bItem.month, bItem.day, isMale);
    }

    //获取幸运色
    public static String getDayColor(DateTime item, DateTime bItem) {
        return nativeGetDayColor(item.year, item.month, item.day, bItem.year, bItem.month, bItem.day, bItem.hour);
    }

    //两个日期的天数相隔
    public static int daysElapsed(DateTime from, DateTime to) {
        return nativeDaysElapsed(from.year, from.month, from.day, to.year, to.month, to.day);
    }

    //获取年的甲子
    public static String getYearSBName(DateTime dt) {
        return nativeGetSBName(nativeGetYearSB(dt.year, dt.month, dt.day));
    }

    //获取月的甲子
    public static String getMonthSBName(DateTime dt) {
        return nativeGetSBName(nativeGetMonSB(dt.year, dt.month, dt.day));
    }

    //获取天的甲子,用日的天干和小时算出
    public static String getDaySBName(DateTime dt) {
        return nativeGetSBName(nativeGetDaySB(dt.year, dt.month, dt.day));
    }

    //获取小时的甲子
    public static String getHourSBName(DateTime dt) {
        return nativeGetSBName(nativeGetHourSB(dt.year, dt.month, dt.day, dt.hour));
    }

    //获取阴历的名字
    public static String getLunarDateName(DateTime dt) {
        return nativeGetLunarDateName(dt.year, dt.month, dt.day);
    }

    public static String getLunarDateNameMD(DateTime dt) {
        return ChinaDateUtil.getLunarByDate(dt);
//        return nativeGetLunarDateNameMD(dt.year, dt.month, dt.day);
    }

    /**
     * 当世标题
     *
     * @param oneCount 生日中有几个1
     * @param sex      性别，1为男，2为女
     */
    public static String getThisWorldTitle(int oneCount, int sex) {
        return nativeGetThisWorldTitle(oneCount, sex);
    }

    /**
     * 当世描述
     *
     * @param oneCount 生日中有几个1
     * @param sex      性别，1为男，2为女
     */
    public static String getThisWorldDesc(int oneCount, int sex) {
        return nativeGetThisWorldDesc(oneCount, sex);
    }

    /**
     * 上一世标题
     *
     * @param oneCount 生日中有几个1
     * @param sex      性别，1为男，2为女
     */
    public static String getLastWorldTitle(int oneCount, int sex) {
        return nativeGetLastWorldTitle(oneCount, sex);
    }

    /**
     * 上一世描述
     *
     * @param oneCount 生日中有几个1
     * @param sex      性别，1为男，2为女
     */
    public static String getLastWorldDesc(int oneCount, int sex) {
        return nativeGetLastWorldDesc(oneCount, sex);
    }

    /**
     * //感情世界
     *
     * @param oneCount 生日中有几个1
     * @param sex      性别，1为男，2为女
     */
    public static String getLoveWorldDesc(int oneCount, int sex) {
        return nativeGetLoveWorldDesc(oneCount, sex);
    }

    /**
     * 给你的锦囊
     *
     * @param oneCount 生日中有几个1
     * @param sex      性别，1为男，2为女
     */
    public static String getGiveYouTips(int oneCount, int sex) {
        return nativeGetGiveYouTips(oneCount, sex);
    }

    /**
     * 感情表达能力
     *
     * @param oneCount 生日中有几个1
     */
    public static String getExpressionOfFeeling(int oneCount) {
        return nativeGetExpressionOfFeeling(oneCount);
    }

    /**
     * 直觉度
     *
     * @param twoCount 生日中有几个2
     */
    public static String getIntuitionDegree(int twoCount) {
        return nativeGetIntuitionDegree(twoCount);
    }

    /**
     * 思维能力和想象力
     *
     * @param threeCount 生日中有几个3
     */
    public static String getThinking(int threeCount) {
        return nativeGetThinking(threeCount);
    }

    /**
     * 行动力
     *
     * @param fourCount 生日中有几个4
     */
    public static String getActivity(int fourCount) {
        return nativeGetActivity(fourCount);
    }

    /**
     * 意志坚定度
     *
     * @param fiveCount 生日中有几个5
     */
    public static String getFirmnessDegree(int fiveCount) {
        return nativeGetFirmnessDegree(fiveCount);
    }

    /**
     * 自我价值
     *
     * @param sixCount 生日中有几个6
     */
    public static String getSelfValue(int sixCount) {
        return nativeGetSelfValue(sixCount);
    }

    /**
     * 失恋治疗能力
     *
     * @param sevenCount 生日中有几个7
     */
    public static String getLovelornTreat(int sevenCount) {
        return nativeGetLovelornTreat(sevenCount);
    }

    /**
     * 智力和逻辑性
     *
     * @param eightCount 生日中有几个8
     */
    public static String getIntelligenceAndLogic(int eightCount) {
        return nativeGetIntelligenceAndLogic(eightCount);
    }

    /**
     * 体贴度
     *
     * @param nineCount 生日中有几个9
     */
    public static String getConsiderateDegree(int nineCount) {
        return nativeGetConsiderateDegree(nineCount);
    }

    //吉位
    public static String getPosition(DateTime item, DateTime bItem) {
        return nativeGetPosition(item.year, item.month, item.day, bItem.year, bItem.month, bItem.day, bItem.hour);
    }

    //判断是不是好日子
    public static int getDayIsGoodDay(DateTime item, DateTime bItem, int sex) {
        return getDayIsGoodDay(item.year, item.month, item.day, bItem, sex);
    }

    public static int getDayIsGoodDay(int year, int month, int day, DateTime bItem, int sex) {
        return nativeGetDayIsGoodDay(year, month, day, bItem.year, bItem.month, bItem.day, sex);
    }

    //获取日子标签
    public static String getDayTags(DateTime item, DateTime bItem, boolean isMale) {
        return nativeGetDayTags(item.year, item.month, item.day, bItem.year, bItem.month, bItem.day, isMale);
    }

    //获取日子分数
    public static int getDayScore(DateTime item, DateTime bItem, boolean isMale) {
        return nativeGetDayScore(item.year, item.month, item.day, bItem.year, bItem.month, bItem.day, isMale);
    }

    //获取星宿性格标签
    public static String getStellarCharactersTags(DateTime item) {
        return nativeGetStellarCharactersTags(item.year, item.month, item.day);
    }

    //获取星宿事业/学习标签
    public static String getStellarStudyTags(DateTime item) {
        return nativeGetStellarStudyTags(item.year, item.month, item.day);
    }

    //获取星宿感情标签
    public static String getStellarLoveTags(DateTime item) {
        return nativeGetStellarLoveTags(item.year, item.month, item.day);
    }

    //获取星宿财运标签
    public static String getStellarFortuneTags(DateTime item) {
        return nativeGetStellarFortuneTags(item.year, item.month, item.day);
    }

    //获取星宿健康标签
    public static String getStellarHealthTags(DateTime item) {
        return nativeGetStellarHealthTags(item.year, item.month, item.day);
    }

    //获取一句话描述星宿标签
    public static String getStellarDesc(DateTime item) {
        return nativeGetStellarDesc(item.year, item.month, item.day);
    }

    //获取金钱观数据
    public static String getMoneyView(DateTime item) {
        return nativeGetMoneyView(item.year, item.month, item.day);
    }

    //获取先天财气数据
    public static String getCongenitalMoney(DateTime item) {
        return nativeGetCongenitalMoney(item.year, item.month, item.day);
    }

    //获取当天的十神
    public static String getDaySBName(DateTime item, DateTime bItem) {
        return nativeGetDaySBName(item.year, item.month, item.day, bItem.year, bItem.month, bItem.day);
    }

    //获取星宿的名字,不包括神兽
    public static String getStellarOnlyName(DateTime dt) {
        return nativeGetStellarOnlyName(dt.year, dt.month, dt.day);
    }

    //获取今日总况
    public static String getDayEvent(DateTime item, DateTime bItem, boolean isMale) {
        return nativeGetDayEvent(item.year, item.month, item.day, bItem.year, bItem.month, bItem.day, isMale);
    }

    //获取日子特殊标签
    public static String getSpecialDayTags(DateTime item,int type) {
        return nativeGetSpecialDayTags(item.year, item.month, item.day, type);
    }
    //获取吉凶
    public static String getDayGoodOrBad(DateTime item, DateTime bItem, boolean isMale) {
        return nativeGetDayGoodOrBad(item.year, item.month, item.day, bItem.year, bItem.month, bItem.day, isMale);
    }

    //获取节日描述
    public static String getFestivalDesc(int index) {
        return nativeGetFestivalDesc(index);
    }

    //获取节日标题
    public static String GetFestivalTitle(int index) {
        return nativeGetFestivalTitle(index);
    }

    //获取节日标题
    public static String getSpecFestivalDescByDateStr(int index) {
        return nativeGetSpecFestivalDescByDateStr(index);
    }

    private static native String nativeGetSpecFestivalDescByDateStr(int index);

    private static native String nativeGetFestivalTitle(int index);

    private static native String nativeGetFestivalDesc(int index);

    private static native String nativeGetCharacterDesc(int year, int month, int day);

    private static native String nativeGetLoveDesc(int year, int month, int day);

    private static native String nativeGetTemperDesc(int year, int month, int day);

    private static native String nativeGetWeakDesc(int year, int month, int day);

    private static native String nativeGetElementDesc(int year, int month, int day);

    private static native int nativeGetCreatureIndex(int year, int month, int day);

    private static native String nativeGetStellarName(int year, int month, int day);

    private static native String nativeGetStellarSub(int year, int month, int day);

    private static native String nativeGetStellarMeaning(int year, int month, int day);

    private static native String nativeGetStellarLife(int year, int month, int day);

    private static native String nativeGetStellarFate(int year, int month, int day);

    private static native String nativeGetStellarCharacter(int year, int month, int day);

    private static native String nativeGetStellarCareer(int year, int month, int day);

    private static native String nativeGetStellarFortune(int year, int month, int day);

    private static native String nativeGetStellarLove(int year, int month, int day);

    private static native String nativeGetStellarHealth(int year, int month, int day);

    private static native int nativeintDaysInMon(int year, int month);

    private static native String nativeGetSolarTermName(int year, int month, int day);

    public static native int nativeDaysElapsed(int fromYear, int fromMonth, int fromDay, int toYear, int toMonth, int toDay);

    public static native String nativeShiftDate(int year, int month, int day, int days);

    public static native String nativeShiftMonth(int year, int month, int day, int months);

    public static native String nativeGetLunarDateName(int year, int month, int day);

    public static native String nativeGetSBName(int index);

    public static native int nativeGetYearSB(int year, int month, int day);

    public static native int nativeGetMonSB(int year, int month, int day);

    public static native int nativeGetDaySB(int year, int month, int day);

    public static native int nativeGetHourSB(int year, int month, int day, int hour);

    private static native boolean nativeIsGoodDay4Act(int nowYear, int nowMonth, int nowDay, int birthYear, int birthMonth, int birthDay, int birthHour, int action, int sex);

    private static native String nativeGetElementName(int year, int month, int day, int hour);

    private static native String nativeGetLunarDateNameMD(int year, int month, int day);

    private static native String nativeGetDayTitle(int year, int month, int day, int birthYear, int birthMonth, int birthDay, boolean isMale);

    private static native String nativeGetDaySummary(int year, int month, int day, int birthYear, int birthMonth, int birthDay, boolean isMale);

    private static native String nativeGetDayDetail(int year, int month, int day, int birthYear, int birthMonth, int birthDay, boolean isMale);

    private static native String nativeGetDayColor(int nowYear, int nowMonth, int nowDay, int birthYear, int birthMonth, int birthDay, int birthHour);

    public static native String nativeGetThisWorldTitle(int oneCount, int sex);

    public static native String nativeGetThisWorldDesc(int oneCount, int sex);

    public static native String nativeGetLastWorldTitle(int oneCount, int sex);

    public static native String nativeGetLastWorldDesc(int oneCount, int sex);

    public static native String nativeGetLoveWorldDesc(int oneCount, int sex);

    public static native String nativeGetGiveYouTips(int oneCount, int sex);

    public static native String nativeGetExpressionOfFeeling(int oneCount);

    public static native String nativeGetIntuitionDegree(int twoCount);

    public static native String nativeGetThinking(int threeCount);

    public static native String nativeGetActivity(int fourCount);

    public static native String nativeGetFirmnessDegree(int fiveCount);

    public static native String nativeGetSelfValue(int sixCount);

    public static native String nativeGetLovelornTreat(int sevenCount);

    public static native String nativeGetIntelligenceAndLogic(int eightCount);

    public static native String nativeGetConsiderateDegree(int nineCount);

    public static native String nativeGetPosition(int nowYear, int nowMonth, int nowDay, int birthYear, int birthMonth, int birthDay, int birthHour);

    public static native int nativeGetDayIsGoodDay(int nowYear, int nowMonth, int nowDay, int birthYear, int birthMonth, int birthDay, int sex);

    public static native String nativeGetDayTags(int year, int month, int day, int birthYear, int birthMonth, int birthDay, boolean isMale);

    public static native int nativeGetDayScore(int year, int month, int day, int birthYear, int birthMonth, int birthDay, boolean isMale);

    public static native String nativeGetStellarCharactersTags(int year, int month, int day);

    public static native String nativeGetStellarStudyTags(int year, int month, int day);

    public static native String nativeGetStellarLoveTags(int year, int month, int day);

    public static native String nativeGetStellarFortuneTags(int year, int month, int day);

    public static native String nativeGetStellarHealthTags(int year, int month, int day);

    public static native String nativeGetStellarDesc(int year, int month, int day);

    public static native String nativeGetMoneyView(int year, int month, int day);

    public static native String nativeGetCongenitalMoney(int year, int month, int day);

    public static native String nativeGetDaySBName(int year, int month, int day, int birthYear, int birthMonth, int birthDay);

    private static native String nativeGetStellarOnlyName(int year, int month, int day);

    public static native String nativeGetDayEvent(int year, int month, int day, int birthYear, int birthMonth, int birthDay, boolean isMale);

    public static native String nativeGetSpecialDayTags(int year, int month, int day, int type);

    public static native String nativeGetDayGoodOrBad(int year, int month, int day, int birthYear, int birthMonth, int birthDay, boolean isMale);
}