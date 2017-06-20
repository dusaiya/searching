package com.alibaba.webx.citation.app1.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

	public static final String FULL_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String SHORT_FORMAT = "yyyy-MM-dd";

	/**
	 * 根据时间获取对应Datetime
	 * 
	 * @param currentTime
	 * @param format
	 * @return
	 */
	public static String getStringDate(Date currentTime, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
	 */
	public static String getNextDay(String nowdate, long delay) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(FULL_FORMAT);
			String mdate = "";
			Date d = strToDateLong(nowdate);
			long myTime = (d.getTime() / 1000) + delay * 24 * 60 * 60;
			d.setTime(myTime * 1000);
			mdate = format.format(d);
			return mdate;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date strToDateLong(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat(FULL_FORMAT);
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}
}
