package com.qizhu.rili.utils;

import android.text.TextUtils;

import com.qizhu.rili.bean.DateTime;

import java.util.Calendar;

public class ChinaDateUtil {
    //X年农历每个月的天数以及闰月的月份,结构为|----4位闰月|---13位月份的天数,即保存的13个月的,因为可能有闰月，1为30天，0为29天---|
    //1900年为例，1900年阴历为闰8月，数字为0x1096d，即0001 0000 1001 0110 1101,从右往左,后面13位为13个月的天数,从左至右为0 1001 0110 1101,0为29天,1为30天,13位之前的4位代表着闰月的月份,为1000,即为闰8月
    //2016年为例，2016年无闰月，数字为0x14b6，即0001 0100 1011 0110,从右往左,后面13位为13个月的天数,从左至右为1 0100 1011 0110,0为29天,1为30天,13位之前的4位代表着闰月的月份,为0000,即无闰月
    private static int[] lunar_month_days = {1887, 0x1694, 0x16aa, 0x4ad5,                      //1887-1890
            0xab6, 0xc4b7, 0x4ae, 0xa56, 0xb52a, 0x1d2a, 0xd54, 0x75aa, 0x156a, 0x1096d,        //1891-1900
            0x95c, 0x14ae, 0xaa4d, 0x1a4c, 0x1b2a, 0x8d55, 0xad4, 0x135a, 0x495d, 0x95c,        //1901-1910
            0xd49b, 0x149a, 0x1a4a, 0xbaa5, 0x16a8, 0x1ad4, 0x52da, 0x12b6, 0xe937, 0x92e,      //1911-1920
            0x1496, 0xb64b, 0xd4a, 0xda8, 0x95b5, 0x56c, 0x12ae, 0x492f, 0x92e, 0xcc96,         //1921-1930
            0x1a94, 0x1d4a, 0xada9, 0xb5a, 0x56c, 0x726e, 0x125c, 0xf92d, 0x192a, 0x1a94,       //1931-1940
            0xdb4a, 0x16aa, 0xad4, 0x955b, 0x4ba, 0x125a, 0x592b, 0x152a, 0xf695, 0xd94,        //1941-1950
            0x16aa, 0xaab5, 0x9b4, 0x14b6, 0x6a57, 0xa56, 0x1152a, 0x1d2a, 0xd54, 0xd5aa,       //1951-1960
            0x156a, 0x96c, 0x94ae, 0x14ae, 0xa4c, 0x7d26, 0x1b2a, 0xeb55, 0xad4, 0x12da,        //1961-1970
            0xa95d, 0x95a, 0x149a, 0x9a4d, 0x1a4a, 0x11aa5, 0x16a8, 0x16d4, 0xd2da, 0x12b6,     //1971-1980
            0x936, 0x9497, 0x1496, 0x1564b, 0xd4a, 0xda8, 0xd5b4, 0x156c, 0x12ae, 0xa92f,       //1981-1990
            0x92e, 0xc96, 0x6d4a, 0x1d4a, 0x10d65, 0xb58, 0x156c, 0xb26d, 0x125c, 0x192c,       //1991-2000
            0x9a95, 0x1a94, 0x1b4a, 0x4b55, 0xad4, 0xf55b, 0x4ba, 0x125a, 0xb92b, 0x152a,       //2001-2010
            0x1694, 0x96aa, 0x15aa, 0x12ab5, 0x974, 0x14b6, 0xca57, 0xa56, 0x1526, 0x8e95,      //2011-2020
            0xd54, 0x15aa, 0x49b5, 0x96c, 0xd4ae, 0x149c, 0x1a4c, 0xbd26, 0x1aa6, 0xb54,        //2021-2030
            0x6d6a, 0x12da, 0x1695d, 0x95a, 0x149a, 0xda4b, 0x1a4a, 0x1aa4, 0xbb54, 0x16b4,     //2031-2040
            0xada, 0x495b, 0x936, 0xf497, 0x1496, 0x154a, 0xb6a5, 0xda4, 0x15b4, 0x6ab6,        //2041-2050
            0x126e, 0x1092f, 0x92e, 0xc96, 0xcd4a, 0x1d4a, 0xd64, 0x956c, 0x155c, 0x125c,       //2051-2060
            0x792e, 0x192c, 0xfa95, 0x1a94, 0x1b4a, 0xab55, 0xad4, 0x14da, 0x8a5d, 0xa5a,       //2061-2070
            0x1152b, 0x152a, 0x1694, 0xd6aa, 0x15aa, 0xab4, 0x94ba, 0x14b6, 0xa56, 0x7527,      //2071-2080
            0xd26, 0xee53, 0xd54, 0x15aa, 0xa9b5, 0x96c, 0x14ae, 0x8a4e, 0x1a4c, 0x11d26,       //2081-2090
            0x1aa4, 0x1b54, 0xcd6a, 0xada, 0x95c, 0x949d, 0x149a, 0x1a2a, 0x5b25, 0x1aa4,       //2091-2100
            0xfb52, 0x16b4, 0xaba, 0xa95b, 0x936, 0x1496, 0x9a4b, 0x154a, 0x136a5, 0xda4,       //2101-2110
            0x15ac};                                                                            //2111

    //X年正月初一对应的公历年月日,结构为|---12位表示年|---4位表示月|---5位表示日|
    //1900年为例,数字为0xed83f,即1110 1101 1000 0011 1111,则为1110 1101 100年0 001月1 1111日,即1900年1月31日,即1900年阴历1月1号的公历是1900年1月31号
    private static int[] solar_1_1 = {1887, 0xec04c, 0xec23f, 0xec435,                                          //1887-1890
            0xec649, 0xec83e, 0xeca51, 0xecc46, 0xece3a, 0xed04d, 0xed242, 0xed436, 0xed64a, 0xed83f,           //1891-1900
            0xeda53, 0xedc48, 0xede3d, 0xee050, 0xee244, 0xee439, 0xee64d, 0xee842, 0xeea36, 0xeec4a,           //1901-1910
            0xeee3e, 0xef052, 0xef246, 0xef43a, 0xef64e, 0xef843, 0xefa37, 0xefc4b, 0xefe41, 0xf0054,           //1911-1920
            0xf0248, 0xf043c, 0xf0650, 0xf0845, 0xf0a38, 0xf0c4d, 0xf0e42, 0xf1037, 0xf124a, 0xf143e,           //1921-1930
            0xf1651, 0xf1846, 0xf1a3a, 0xf1c4e, 0xf1e44, 0xf2038, 0xf224b, 0xf243f, 0xf2653, 0xf2848,           //1931-1940
            0xf2a3b, 0xf2c4f, 0xf2e45, 0xf3039, 0xf324d, 0xf3442, 0xf3636, 0xf384a, 0xf3a3d, 0xf3c51,           //1941-1950
            0xf3e46, 0xf403b, 0xf424e, 0xf4443, 0xf4638, 0xf484c, 0xf4a3f, 0xf4c52, 0xf4e48, 0xf503c,           //1951-1960
            0xf524f, 0xf5445, 0xf5639, 0xf584d, 0xf5a42, 0xf5c35, 0xf5e49, 0xf603e, 0xf6251, 0xf6446,           //1961-1970
            0xf663b, 0xf684f, 0xf6a43, 0xf6c37, 0xf6e4b, 0xf703f, 0xf7252, 0xf7447, 0xf763c, 0xf7850,           //1971-1980
            0xf7a45, 0xf7c39, 0xf7e4d, 0xf8042, 0xf8254, 0xf8449, 0xf863d, 0xf8851, 0xf8a46, 0xf8c3b,           //1981-1990
            0xf8e4f, 0xf9044, 0xf9237, 0xf944a, 0xf963f, 0xf9853, 0xf9a47, 0xf9c3c, 0xf9e50, 0xfa045,           //1991-2000
            0xfa238, 0xfa44c, 0xfa641, 0xfa836, 0xfaa49, 0xfac3d, 0xfae52, 0xfb047, 0xfb23a, 0xfb44e,           //2001-2010
            0xfb643, 0xfb837, 0xfba4a, 0xfbc3f, 0xfbe53, 0xfc048, 0xfc23c, 0xfc450, 0xfc645, 0xfc839,           //2011-2020
            0xfca4c, 0xfcc41, 0xfce36, 0xfd04a, 0xfd23d, 0xfd451, 0xfd646, 0xfd83a, 0xfda4d, 0xfdc43,           //2021-2030
            0xfde37, 0xfe04b, 0xfe23f, 0xfe453, 0xfe648, 0xfe83c, 0xfea4f, 0xfec44, 0xfee38, 0xff04c,           //2031-2040
            0xff241, 0xff436, 0xff64a, 0xff83e, 0xffa51, 0xffc46, 0xffe3a, 0x10004e, 0x100242, 0x100437,        //2041-2050
            0x10064b, 0x100841, 0x100a53, 0x100c48, 0x100e3c, 0x10104f, 0x101244, 0x101438, 0x10164c, 0x101842, //2051-2060
            0x101a35, 0x101c49, 0x101e3d, 0x102051, 0x102245, 0x10243a, 0x10264e, 0x102843, 0x102a37, 0x102c4b, //2061-2070
            0x102e3f, 0x103053, 0x103247, 0x10343b, 0x10364f, 0x103845, 0x103a38, 0x103c4c, 0x103e42, 0x104036, //2071-2080
            0x104249, 0x10443d, 0x104651, 0x104846, 0x104a3a, 0x104c4e, 0x104e43, 0x105038, 0x10524a, 0x10543e, //2081-2090
            0x105652, 0x105847, 0x105a3b, 0x105c4f, 0x105e45, 0x106039, 0x10624c, 0x106441, 0x106635, 0x106849, //2091-2100
            0x106a3d, 0x106c51, 0x106e47, 0x10703c, 0x10724f, 0x107444, 0x107638, 0x10784c, 0x107a3f, 0x107c53, //2101-2110
            0x107e48};                                                                                          //2111

    //1900-2100的阴历数组，每个农历年用16bit来表示,
    //前12bit分别表示12个月份的大小月,最后4bit表示闰月
    private static int[] lunarInfo = new int[]{
            0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,   //1900-1909
            0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,   //1910-1919
            0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,   //1920-1929
            0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,   //1930-1939
            0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,   //1940-1949
            0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5b0, 0x14573, 0x052b0, 0x0a9a8, 0x0e950, 0x06aa0,   //1950-1959
            0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,   //1960-1969
            0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b6a0, 0x195a6,   //1970-1979
            0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,   //1980-1989
            0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,   //1990-1999
            0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,   //2000-2009
            0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,   //2010-2019
            0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,   //2020-2029
            0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,   //2030-2039
            0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0,   //2040-2049
            0x14b63, 0x09370, 0x049f8, 0x04970, 0x064b0, 0x168a6, 0x0ea50, 0x06b20, 0x1a6c4, 0x0aae0,   //2050-2059
            0x0a2e0, 0x0d2e3, 0x0c960, 0x0d557, 0x0d4a0, 0x0da50, 0x05d55, 0x056a0, 0x0a6d0, 0x055d4,   //2060-2069
            0x052d0, 0x0a9b8, 0x0a950, 0x0b4a0, 0x0b6a6, 0x0ad50, 0x055a0, 0x0aba4, 0x0a5b0, 0x052b0,   //2070-2079
            0x0b273, 0x06930, 0x07337, 0x06aa0, 0x0ad50, 0x14b55, 0x04b60, 0x0a570, 0x054e4, 0x0d160,   //2080-2089
            0x0e968, 0x0d520, 0x0daa0, 0x16aa6, 0x056d0, 0x04ae0, 0x0a9d4, 0x0a2d0, 0x0d150, 0x0f252,   //2090-2099
            0x0d520};                                                                                   //2100

    public static String[] mMonthStr = new String[]{"", "正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "腊"};
    public static String[] mMonths = new String[]{"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
    //天干
    private static String[] TianGan = new String[]{"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    //地支
    private static String[] DiZhi = new String[]{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    //十二生肖
    private static String[] Animals = new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};

    //24节气
    private static String[] solarTerm = new String[]{
            "小寒", "大寒", "立春", "雨水", "惊蛰", "春分",
            "清明", "谷雨", "立夏", "小满", "芒种", "夏至",
            "小暑", "大暑", "立秋", "处暑", "白露", "秋分",
            "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"};

    //24节气对应的分钟
    private static int[] solarTermInfo = new int[]{
            0, 21208, 42467, 63836, 85337, 107014, 128867, 150921, 173149, 195551, 218072, 240693,
            263343, 285989, 308563, 331033, 353350, 375494, 397447, 419210, 440795, 462224, 483532, 504758};

    private static String[] lunarString1 = {
            "零", "一", "二", "三", "四", "五", "六", "七", "八", "九"
    };
    private static String[] lunarString2 = {
            "初", "十", "廿", "三", "正", "腊", "冬", "闰"
    };

    /**
     * 返回农历年正常月份的总天数
     *
     * @param lunarYear  指定农历年份(数字)
     * @param lunarMonth 指定农历月份(数字)
     * @return 该农历年闰月的月份(数字, 没闰返回0)
     */
    public static int getLunarMonthDays(int lunarYear, int lunarMonth) {
        // 数据表中,每个农历年用16bit来表示,
        // 前12bit分别表示12个月份的大小月,最后4bit表示闰月
        return ((lunarInfo[lunarYear - 1900] & (0x10000 >> lunarMonth)) != 0) ? 30 : 29;
    }

    /**
     * 返回公历年节气的日期
     *
     * @param solarYear 指定公历年份(数字)
     * @param index     指定节气序号(数字,0从小寒算起),以1900年1月6号（小寒）开始算起
     * @return 日期(数字, 所在月份的第几天)
     */
    private static int getSolarTermDay(int solarYear, int index) {
        long l = (long) 31556925974.7 * (solarYear - 1900) + solarTermInfo[index] * 60000L;
        Calendar calendar = Calendar.getInstance();
        calendar.set(1900, 0, 6, 2, 5, 0);
        //获取到此日期的毫秒
        l = l + calendar.getTimeInMillis();
        calendar.setTimeInMillis(l);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 取农历年生肖
     *
     * @return 农历年生肖(例:龙)
     */
    public static String getAnimalString(int lunarYear) {
        return Animals[(lunarYear - 4) % 12];
    }

    /**
     * 返回公历日期的节气字符串
     *
     * @return 二十四节气字符串, 若不是节气日, 返回空串(例:冬至)
     */
    public static String getTermString(DateTime dateTime) {
        // 二十四节气
        String termString = "";
        if (getSolarTermDay(dateTime.year, dateTime.month * 2) == dateTime.day) {
            termString = solarTerm[dateTime.month * 2];
        } else if (getSolarTermDay(dateTime.year, dateTime.month * 2 + 1) == dateTime.day) {
            termString = solarTerm[dateTime.month * 2 + 1];
        }
        return termString;
    }

    /**
     * 干支字符串
     *
     * @param cyclicalNumber 指定干支位置(数字,0为甲子)
     * @return 干支字符串
     */
    private static String getCyclicalString(int cyclicalNumber) {
        return TianGan[getTianGan(cyclicalNumber)] + DiZhi[getDiZhi(cyclicalNumber)];
    }

    /**
     * 获得地支
     *
     * @param cyclicalNumber
     * @return 地支 (数字)
     */
    private static int getDiZhi(int cyclicalNumber) {
        return cyclicalNumber % 12;
    }

    /**
     * 获得天干
     *
     * @param cyclicalNumber
     * @return 天干 (数字)
     */
    private static int getTianGan(int cyclicalNumber) {
        return cyclicalNumber % 10;
    }

    /**
     * 返回指定数字的农历年份表示字符串
     *
     * @param lunarYear 农历年份(数字,0为甲子)
     * @return 农历年份字符串
     */
    public static String getLunarYearString(int lunarYear) {
        return getCyclicalString(lunarYear - 1900 + 36);
    }

    /**
     * 返回指定数字的农历月份表示字符串
     *
     * @param lunarMonth 农历月份(数字)
     * @return 农历月份字符串 (例:正)
     */
    private static String getLunarMonthString(int lunarMonth) {
        String lunarMonthString = "";
        if (lunarMonth == 1) {
            lunarMonthString = lunarString2[4];
        } else {
            if (lunarMonth > 9)
                lunarMonthString += lunarString2[1];
            if (lunarMonth % 10 > 0)
                lunarMonthString += lunarString1[lunarMonth % 10];
        }
        return lunarMonthString;
    }

    /**
     * 返回指定数字的农历日表示字符串
     *
     * @param lunarDay 农历日(数字)
     * @return 农历日字符串 (例: 廿一)
     */
    public static String getLunarDayString(int lunarDay) {
        if (lunarDay < 1 || lunarDay > 30) return "";
        int i1 = lunarDay / 10;
        int i2 = lunarDay % 10;
        String c1 = lunarString2[i1];
        String c2 = lunarString1[i2];
        if (lunarDay < 11) c1 = lunarString2[0];
        if (i2 == 0) c2 = lunarString2[1];
        return c1 + c2;
    }

    /**
     * 获取对应日期的阴历
     * 节气与节日已替换
     * 月份范围为1-12
     */
    public static String getLunarByDate(DateTime dateTime) {
        try {
            DateTime lunar = solarToLunar(dateTime);
            int mYear = lunar.year;
            int mMonth = lunar.month + 1;
            int mDay = lunar.day;
            //如果为阴历12月的最后一天，那么就是除夕
            if (12 == mMonth) {
                int i = getLunarMonthDays(mYear, 12);
                if (mDay == i) {
                    return "除夕";
                }
            }
            String mTerm = getTermString(dateTime);
            if (TextUtils.isEmpty(mTerm)) {
                return mMonthStr[mMonth] + "月" + getLunarDayString(mDay);
            } else {
                return mTerm;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static DateTime getSolarByDate(DateTime dateTime) {
        DateTime rtn = lunarToSolar(dateTime, false);
        rtn.hour = dateTime.hour;
        rtn.min = dateTime.min;
        return rtn;
    }

    /**
     * 是否是公历闰年
     */
    static boolean isSolarLeapYear(int iYear) {
        return ((iYear % 4 == 0) && (iYear % 100 != 0) || iYear % 400 == 0);
    }

    /**
     * 左移shift位然后截取length长度的bit值得到的值
     * 注意返回的值是length个bit位的值
     *
     * @param data   对应的int值
     * @param length 截取的bit位长度
     * @param shift  左移位数
     */
    private static int getBitInt(int data, int length, int shift) {
        return (data & (((1 << length) - 1) << shift)) >> shift;
    }

    /**
     * 获取阳历对应的天数
     * WARNING: Dates before Oct. 1582 are inaccurate,1582年10月之前的数据是错误的
     *
     * @param year  年
     * @param month 月
     * @param day   日
     */
    private static long solarToInt(int year, int month, int day) {
        month = (month + 9) % 12;
        year = year - month / 10;
        return 365 * year + year / 4 - year / 100 + year / 400 + (month * 306 + 5) / 10 + (day - 1);
    }

    /**
     * @param lunarYear 农历年份
     * @return String of Ganzhi: 甲子年
     * Tiangan:甲乙丙丁戊己庚辛壬癸,Dizhi: 子丑寅卯辰巳无为申酉戌亥
     */
    public static String lunarYearToGanZhi(int lunarYear) {
        return TianGan[(lunarYear - 4) % 10] + DiZhi[(lunarYear - 4) % 12] + "年";
    }

    /**
     * 将天数转为阳历
     */
    private static DateTime solarFromInt(long g) {
        long year = (10000 * g + 14780) / 3652425;
        long ddd = g - (365 * year + year / 4 - year / 100 + year / 400);
        if (ddd < 0) {
            year--;
            ddd = g - (365 * year + year / 4 - year / 100 + year / 400);
        }
        long mi = (100 * ddd + 52) / 3060;
        long month = (mi + 2) % 12 + 1;
        year = year + (mi + 2) / 12;
        long day = ddd - (mi * 306 + 5) / 10 + 1;
        DateTime solar = new DateTime();
        solar.year = (int) year;
        solar.month = (int) month - 1;      //月份改为0-11
        solar.day = (int) day;
        return solar;
    }

    /**
     * 阴历转阳历
     * 注意此方法的输入和返回月份为1-12
     *
     * @param lunar  阴历日期
     * @param isleap 是否是阴历的闰月，因为阴历有闰月，闰月的阳历对应的会不同,现在默认认为都不是闰月
     * @return 对应的阳历日期
     */
    public static DateTime lunarToSolar(DateTime lunar, boolean isleap) {
        //先把月份改为1-12
        int year = lunar.year;
        int month = lunar.month + 1;
        int day = lunar.day;
        int days = lunar_month_days[year - lunar_month_days[0]];
        int leap = getBitInt(days, 4, 13);
        int offset = 0;
        int loopend = leap;
        if (!isleap) {
            if (month <= leap || leap == 0) {
                loopend = month - 1;
            } else {
                loopend = month;
            }
        }
        for (int i = 0; i < loopend; i++) {
            offset += getBitInt(days, 1, 12 - i) == 1 ? 30 : 29;
        }
        offset += day;

        int solar11 = solar_1_1[year - solar_1_1[0]];

        int y = getBitInt(solar11, 12, 9);
        int m = getBitInt(solar11, 4, 5);
        int d = getBitInt(solar11, 5, 0);

        return solarFromInt(solarToInt(y, m, d) + offset - 1);
    }

    /**
     * 阳历转阴历
     * 注意此方法的输入和返回月份为1-12
     *
     * @param solar 阳历日期
     * @return 对应的阴历日期
     */
    public static DateTime solarToLunar(DateTime solar) {
        DateTime lunar = new DateTime();
        //先把月份改为1-12
        int year = solar.year;
        int month = solar.month + 1;
        int day = solar.day;
        int index = year - solar_1_1[0];
        int data = (year << 9) | (month << 5) | (day);
        int solar11 = 0;
        if (solar_1_1[index] > data) {
            index--;
        }
        solar11 = solar_1_1[index];
        int y = getBitInt(solar11, 12, 9);
        int m = getBitInt(solar11, 4, 5);
        int d = getBitInt(solar11, 5, 0);
        long offset = solarToInt(year, month, day) - solarToInt(y, m, d);

        int days = lunar_month_days[index];
        int leap = getBitInt(days, 4, 13);

        int lunarY = index + solar_1_1[0];
        int lunarM = 1;
        int lunarD = 1;
        offset += 1;

        for (int i = 0; i < 13; i++) {
            int dm = getBitInt(days, 1, 12 - i) == 1 ? 30 : 29;
            if (offset > dm) {
                lunarM++;
                offset -= dm;
            } else {
                break;
            }
        }
        lunarD = (int) (offset);
        lunar.year = lunarY;
        lunar.month = lunarM;
        //此月是否为闰月
//        boolean isleap = false;
        if (leap != 0 && lunarM > leap) {
            lunar.month = lunarM - 1;
//            if (lunarM == leap + 1) {
//                isleap = true;
//            }
        }
        //月份改为0-11
        lunar.month = lunar.month - 1;
        lunar.day = lunarD;
        lunar.hour = solar.hour;
        lunar.min = solar.min;
        return lunar;
    }

    /**
     * 阳历转阴历
     * 注意此方法的输入和返回月份为1-12
     *
     * @param solar 阳历日期
     * @return 对应的阴历日期
     */
    public static String solarToStringLunar(DateTime solar) {
        DateTime lunar = new DateTime();
        //先把月份改为1-12
        int year = solar.year;
        int month = solar.month + 1;
        int day = solar.day;
        int index = year - solar_1_1[0];
        int data = (year << 9) | (month << 5) | (day);
        int solar11 = 0;
        if (solar_1_1[index] > data) {
            index--;
        }
        solar11 = solar_1_1[index];
        int y = getBitInt(solar11, 12, 9);
        int m = getBitInt(solar11, 4, 5);
        int d = getBitInt(solar11, 5, 0);
        long offset = solarToInt(year, month, day) - solarToInt(y, m, d);

        int days = lunar_month_days[index];
        int leap = getBitInt(days, 4, 13);

        int lunarY = index + solar_1_1[0];
        int lunarM = 1;
        int lunarD = 1;
        offset += 1;

        for (int i = 0; i < 13; i++) {
            int dm = getBitInt(days, 1, 12 - i) == 1 ? 30 : 29;
            if (offset > dm) {
                lunarM++;
                offset -= dm;
            } else {
                break;
            }
        }
        lunarD = (int) (offset);
        lunar.year = lunarY;
        lunar.month = lunarM;
        //此月是否为闰月
//        boolean isleap = false;
        if (leap != 0 && lunarM > leap) {
            lunar.month = lunarM - 1;
//            if (lunarM == leap + 1) {
//                isleap = true;
//            }
        }
        //月份改为0-11
        lunar.month = lunar.month - 1;
        lunar.day = lunarD;
        lunar.hour = solar.hour;
        lunar.min = solar.min;
        String  tempMonth ;
        String tempDay ;
        String tempHour ;
        if(lunar.month < 9){
            tempMonth = "0" + (lunar.month+1);
        }else
        {
            tempMonth =""+ (lunar.month+1);
        }
        if(lunar.day < 10){
            tempDay = "0" + lunar.day ;
        }else
        {
            tempDay =""+ lunar.day ;
        }
        if(lunar.hour < 10){
            tempHour = "0" + lunar.hour;
        }else
        {
            tempHour =""+ lunar.hour;
        }

        return tempMonth+"月"+tempDay+"日"+tempHour+"时";
    }

}