package com.baobao.utils;

import java.text.*;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Date;

/*
 * This class parse input parameters, convert
 * date range to a date string array in one day
 * interval. For example, 
 * input   "20070831","20070902"
 * output  "20070831","20070901","20070902"
 */
public class DateParser
{
	private static final DateParser instance = new DateParser();
	private static Hashtable<String,SimpleDateFormat> dateFormat;
	public static final String DATE_FORM = "yyyyMMdd";
	public static final String DATE_FORM2 = "yyyy-MM-dd";
	public static final String DATE_FORM3 = "yyyyMM";
	public static final String TIME_FORM = "HH:mm";
	public static final String DATETIME_FORM = "yyyy-MM-dd HH:mm";
	public static final String DATETIME_FORM2 = "yyyy-MM-dd HH:mm:ss";

	private DateParser(){
		dateFormat = new Hashtable<String,SimpleDateFormat>();
		SimpleDateFormat temp = new SimpleDateFormat(DATE_FORM);
		dateFormat.put(DATE_FORM, temp);
		temp = new SimpleDateFormat(DATE_FORM2);
		dateFormat.put(DATE_FORM2, temp);
		temp = new SimpleDateFormat(DATE_FORM3);
		dateFormat.put(DATE_FORM3, temp);
		temp = new SimpleDateFormat(TIME_FORM);
		dateFormat.put(TIME_FORM, temp);
		temp = new SimpleDateFormat(DATETIME_FORM);
		dateFormat.put(DATETIME_FORM, temp);
		temp = new SimpleDateFormat(DATETIME_FORM2);
		dateFormat.put(DATETIME_FORM2, temp);
	}
	/**
	 * define static functions
	 */
	public static DateParser getInstance()
	{
		return instance;
	}
	/**
	 * 检查字符串是否是要求的日期格式
	 */
	public boolean checkDateFormat(String date, String format){
		if(null == date)
			return false;
		try {
			dateFormat.get(format).parse(date);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}
	/**
	 * 判断是否是同一天
	 * @param time1
	 * @param time2
	 * @return
	 */
	public boolean isSameDay(int time1, int time2){
		Calendar ca1 = Calendar.getInstance();
		Calendar ca2 = Calendar.getInstance();
		ca1.setTimeInMillis(time1 * 1000L);
		ca2.setTimeInMillis(time2 * 1000L); 
		return (ca1.get(Calendar.YEAR) == ca2.get(Calendar.YEAR)) &&
			(ca1.get(Calendar.MONTH) == ca2.get(Calendar.MONTH)) &&
			(ca1.get(Calendar.DAY_OF_MONTH) == ca2.get(Calendar.DAY_OF_MONTH));
	}
	/**
	 *判断两个日期是否是连续两天
	 */
	public boolean isContinuousTwoDays(int time1, int time2){
		int t1 = Math.min(time1, time2);
		int t2 = Math.max(time1, time2);
		Calendar ca1 = Calendar.getInstance();
		Calendar ca2 = Calendar.getInstance();
		ca1.setTimeInMillis(t1 * 1000L);
		ca2.setTimeInMillis(t2 * 1000L); 
		ca1.add(Calendar.DAY_OF_MONTH, 1);
		return (ca1.get(Calendar.YEAR) == ca2.get(Calendar.YEAR)) &&
			(ca1.get(Calendar.MONTH) == ca2.get(Calendar.MONTH)) &&
			(ca1.get(Calendar.DAY_OF_MONTH) == ca2.get(Calendar.DAY_OF_MONTH));
	}
	
	///一天的秒数
	//private static final long DAY = 24*60*60*1000; 
	/**
	 * 判断time是否是周日，并且与当前不是一天
	 * @param time		日期
	 * @return
	 */
	public boolean isSunDayAndNot(int time){
		Calendar cal = Calendar.getInstance();
		//cal.setTimeInMillis(1310313600000L);
		long today = cal.getTimeInMillis();
		int now_day = (int)(today/1000L);
		
		/*cal.setTimeInMillis(time*1000L);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND,0);
		long lastTime = cal.getTimeInMillis();*/
		return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && isSameDay(time,now_day);
	}
	/**
	 * 判断是否是同一周
	 * @param time1
	 * @param time2
	 * @return
	 */
	public boolean isSameWeek(int time1, int time2){
		Calendar ca1 = Calendar.getInstance();
		Calendar ca2 = Calendar.getInstance();
		ca1.setFirstDayOfWeek(Calendar.MONDAY);
		ca2.setFirstDayOfWeek(Calendar.MONDAY);
		ca1.setTimeInMillis(time1 * 1000L);
		ca2.setTimeInMillis(time2 * 1000L); 
		ca1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		ca2.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return (ca1.get(Calendar.YEAR) == ca2.get(Calendar.YEAR)) &&
			(ca1.get(Calendar.MONTH) == ca2.get(Calendar.MONTH)) &&
			(ca1.get(Calendar.DAY_OF_MONTH) == ca2.get(Calendar.DAY_OF_MONTH));
	}


	/**
	 * 得到当前时间的字符串形式："yyyy-MM-dd hh:mm:ss"
	 * @param day_time 精确到毫秒的long型时间
	 * @return
	 */
	public String getDayTime(long day_time){
		return dateFormat.get(DATETIME_FORM2).format(new Date(day_time));
	}
	
	public String getDayTme(String format,Date daytime){
		return dateFormat.get(format).format(daytime);
	}
	/**
	 * 得到当前时间的日期字符串形式："yyyyMMdd"
	 * @param day_time 精确到毫秒的long型时间
	 * @return
	 */
	public String getDay(long day_time){
		return dateFormat.get(DATE_FORM).format(new Date(day_time));
	}
	
	public int getToday(){
		return Integer.valueOf(getDay(System.currentTimeMillis()));
	}
	/**
	 * 得到一周的第一天
	 * @return  本时间所在的一周第一天
	 */
	public int getWeekFistDay(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return Integer.valueOf(getDay(cal.getTimeInMillis()));
	}
	/**
	 * 得到当前时间的日期字符串形式："yyyyMM"
	 * @param day_time 精确到毫秒的long型时间
	 * @return
	 */
	public String getYM(long day_time){
		return dateFormat.get(DATE_FORM3).format(new Date(day_time));
	}
	
	/**
	 * 得到当前时间的日期字符串形式："yyyy-MM-dd"
	 * @param day_time 精确到毫秒的long型时间
	 * @return
	 */
	public String getDate(long day_time){
		return dateFormat.get(DATE_FORM2).format(new Date(day_time));
	}
	
	/**判断两个字符串表示的时间"yyyy-MM-dd hh:mm:ss"哪个在前哪个在后
	 * @params 用字符串表示的日期：time1,time2
	 * @return boolean
	 */
	public boolean isTimeBefore(String time1,String time2){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORM2);
			return sdf.parse(time1).before(sdf.parse(time2));
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**判断两个字符串表示的日期"yyyy-MM-dd"哪个在前哪个在后
	 * @params 用字符串表示的日期：time1,time2
	 * @return boolean
	 */
	public boolean isDateBefore(String time1,String time2){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORM2);
			return sdf.parse(time1).before(sdf.parse(time2));
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**判断两个字符串表示的月份"yyyy-MM"哪个在前哪个在后
	 * @params 用字符串表示的日期：time1,time2
	 * @return boolean
	 */
	public boolean isMonthBefore(String time1,String time2){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			return sdf.parse(time1).before(sdf.parse(time2));
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 将字符串格式形如"yyyy-MM-dd hh:mm:ss"的时间转换为long型时间，单位为毫秒
	 * @param time
	 * @return
	 */
	public long parseTime(String time){
		long result = -1;
		try{
			boolean tag=checkDateFormat(time,DATETIME_FORM2);
			SimpleDateFormat sdf=null;
			if(tag){
				sdf= new SimpleDateFormat(DATETIME_FORM2);
			}else{
				tag=checkDateFormat(time,DATE_FORM2);
				if(tag){
					sdf= new SimpleDateFormat(DATE_FORM2);
				}
			}
			if(sdf!=null){
				result = sdf.parse(time).getTime();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**判断两个日期之间间隔多少天，日期格式为"YYYY-MM-DD"
	 * @params 用字符串表示的日期：startTime,endTime
	 * @return int
	 */
	public int parseRange(String startTime,String endTime){
		int days = -1;
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORM2);
			Date startDate = sdf.parse(startTime);
			Date endDate = sdf.parse(endTime);
			days = (int) ((endDate.getTime() - startDate.getTime())/(1000*3600*24));
			return days;
		}catch(Exception e){
			e.printStackTrace();
			return days;
		}
	}
	
	public static int getNowTime(){
		return (int)(System.currentTimeMillis()/1000L);
	}
	
	
	
	/**
	 * 得到日期时间
	 * @param second
	 * @return 如果时间和当前时间差 time<1second  		 					返回X秒前
	 * 		   				  1second=< time <1hour  					返回X分钟前
	 * 						  1hour<=time<=1 day	 					返回X小时前
	 * 						  1day<=time<1week		 					返回X天前
	 * 						  1week<=time<1year and time in this year 	返回X月X号
	 * 						  time not in this year	 					返回X年X月
	 */
	private static final int int_second = 60;
	private static final int int_hour = 3600;
	private static final int int_day = int_hour * 24;
	private static final int int_week = int_day * 7;
	private static final int int_month = int_day * 31;
	private static Calendar cal = Calendar.getInstance();
	public static String getStrDate(int second){
		int now=(int)(System.currentTimeMillis()/1000L);
		int time = now-second;
		if(time <= 0)
			return "现在";
		if(time<int_second)
			return time + "秒前";
		if(time<int_hour)
			return time/int_second + "分钟前";
		if(time<int_day)
			return time/int_hour + "小时前";
		if(time<int_week)
			return time/int_day + "天前";
		cal.setTimeInMillis(second * 1000L);
		if(time<int_month){
			return (cal.get(Calendar.MONTH)+1)+"月"+cal.get(Calendar.DAY_OF_MONTH)+"日";
		}
		Calendar temp = Calendar.getInstance();
		if(cal.get(Calendar.YEAR)<temp.get(Calendar.YEAR))
			return cal.get(Calendar.YEAR)+"年"+(cal.get(Calendar.MONTH)+1)+"月";
		return (cal.get(Calendar.MONTH)+1)+"月"+cal.get(Calendar.DAY_OF_MONTH)+"日";
	}
	
	
	public static void main(String[] args){
		DateParser dp = DateParser.getInstance();
//		boolean isBefore = dp.isMonthBefore("2009-02","2009-03");
//		if(isBefore)
//			System.out.println("before......");
//		else
//			System.out.println("not before......");
		long range=System.currentTimeMillis();
		long range1 = dp.parseTime("2010-01-01 00:00:00");
		long range2 = dp.parseTime("2009-10-01 00:00:00");
		
		System.out.println("间隔时间："+range+" "+range1+ " " +range2+" "+dp.getDayTime(range));
		System.out.println(DateParser.getNowTime());
//		System.out.println(dp.getDayTime(1245834843313L));
//		System.out.println(dp.parseTime(dp.getDayTime(1237515411781L)));
//		System.out.println(dp.getDayTime(dp.parseTime(dp.getDayTime(1237515411781L))));
//		System.out.println(dp.parseTime(dp.getDayTime(dp.parseTime(dp.getDayTime(1237515411781L)))));
		
		long time = dp.parseTime("2011-10-9 10:20:00");
		System.out.println(dp.isSunDayAndNot((int)(time/1000L)));
		System.out.println(dp.parseTime("2011-07-11 00:00:00"));
		System.out.println(dp.getToday());
		System.out.println(dp.getWeekFistDay());
	}
	
}
