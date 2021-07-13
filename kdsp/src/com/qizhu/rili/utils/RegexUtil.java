package com.qizhu.rili.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	/**
	 * 判断是否为手机号码
	 */
	public static Boolean isMobileNumber(String mobiles) {
		Pattern p = Pattern.compile("^(13|15|18|16|17)\\d{9}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 判断是否为固定电话号码
	 */
	public static Boolean isPhoneNumber(String phone) {
		Pattern p = Pattern.compile("[0]{1}[0-9]{2,3}-[0-9]{7,8}");
		Matcher m = p.matcher(phone);
		return m.matches();
	}

	/**
	 * 判断是否为邮箱地址
	 */
	public static Boolean isEmail(String email) {
		Pattern p = Pattern
				.compile("^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 判断是否为合法IP
	 */
	public static boolean isboolIp(String ipAddress) {
		if (ipAddress.length() < 7 || ipAddress.length() > 15
				|| "".equals(ipAddress)) {
			return false;
		}
		String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}

	/**
	 * 判断是否为身份证号
	 */
	public static Boolean isIdCard(String idNum) {
		Pattern p = Pattern
				.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");
		Matcher m = p.matcher(idNum);
		return m.matches();
	}

	/**
	 * 根据身份证号，获取出生年月日
	 */
	public static String getBirth(String idNum) {
		Pattern birthDatePattern = Pattern
				.compile("\\d{6}(\\d{4})(\\d{2})(\\d{2}).*");// 身份证上的前6位以及出生年月日
		// 通过Pattern获得Matcher
		Matcher birthDateMather = birthDatePattern.matcher(idNum);
		// 通过Matcher获得用户的出生年月日
		if (birthDateMather.find()) {
			String year = birthDateMather.group(1);
			String month = birthDateMather.group(2);
			String date = birthDateMather.group(3);
			// 输出用户的出生年月日
			return year + "年" + month + "月" + date + "日";
		}
		return "";
	}

	/**
	 * 判断URL是否合理
	 */
	public static Boolean isUrl(String url) {
		String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		Pattern patt = Pattern.compile(regex);
		Matcher matcher = patt.matcher(url);
		return matcher.matches();
	}
}
