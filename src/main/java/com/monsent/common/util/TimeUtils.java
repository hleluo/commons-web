package com.monsent.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 */
public class TimeUtils {

	public final static String yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";
	public final static String yyyyMMddHHmmss = "yyyyMMddHHmmss";

	public static Date parseFormat(String dateStr, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String dateFormat(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/* -------- UTC Date -------- */
	public static Date getUtcDate(Date localDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(localDate);
		int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
		int dstOffset = calendar.get(Calendar.DST_OFFSET);
		calendar.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		return calendar.getTime();
	}

	public static Date getUtcDate(String localDateStr, String format) {
		return getUtcDate(parseFormat(localDateStr, format));
	}

	public static Date getCurrentUtcDate() {
		return getUtcDate(new Date());
	}

	/* -------- UTC Date String -------- */
	public static String getUtcDateStr(Date localDate, String format) {
		return dateFormat(getUtcDate(localDate), format);
	}

	public static String getUtcDateStr(String localDateStr, String localFormat, String utcFormat) {
		return getUtcDateStr(parseFormat(localDateStr, localFormat), utcFormat);
	}
	
	public static String getUtcDateStr(String localDateStr, String format) {
		return getUtcDateStr(localDateStr, format, format);
	}

	public static String getCurrentUtcDateStr(String format) {
		return getUtcDateStr(new Date(), format);
	}

	/* -------- Local Date -------- */
	public static Date getLocalDate(Date utcDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(utcDate);
		int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
		int dstOffset = calendar.get(Calendar.DST_OFFSET);
		calendar.add(Calendar.MILLISECOND, zoneOffset + dstOffset);
		return calendar.getTime();
	}
	
	public static Date getLocalDate(String utcDateStr, String format) {
		return getLocalDate(parseFormat(utcDateStr, format));
	}
	
	public static Date getCurrentLocalDate() {
		return new Date();
	}
	
	/* -------- Local Date String -------- */
	public static String getLocalDateStr(Date utcDate, String format) {
		return dateFormat(getLocalDate(utcDate), format);
	}
	
	public static String getLocalDateStr(String utcDateStr, String utcFormat, String localFormat) {
		return getLocalDateStr(parseFormat(utcDateStr, utcFormat), localFormat);
	}
	
	public static String getLocalDateStr(String utcDateStr, String format) {
		return getLocalDateStr(utcDateStr, format, format);
	}
	
	public static String getCurrentLocalDateStr(String format) {
		return dateFormat(new Date(), format);
	}

	/* -------- main -------- */
	public static void main(String[] args) {
		System.out.println(getCurrentUtcDateStr(yyyyMMddHHmmssSSS));
		System.out.println(getCurrentLocalDateStr(yyyyMMddHHmmssSSS));
	}

}
