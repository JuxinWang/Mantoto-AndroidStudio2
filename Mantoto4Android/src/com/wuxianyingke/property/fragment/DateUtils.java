/*
package com.wuxianyingke.property.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

public class DateUtils {

	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("deprecation")
	public static final String dateFormat(String dateText,String format){
		if(dateText==null) return "";
		
		SimpleDateFormat sdf=new SimpleDateFormat(format==null?"yyyy-MM-dd hh:mm:ss":format);
		
		Date date=new Date(dateText);
		
		return sdf.format(date);
	}
	
//	@SuppressLint("SimpleDateFormat")
//	@SuppressWarnings("deprecation")
//	public static final String stringDateFormat(String dateText){
//		if(dateText==null) return "";
//		
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		
//		Date date=new Date(dateText);
//		String temp_time = sdf.format(date);
////		try {
////			date=sdf.parse(dateText);
////		} catch (ParseException e) {
////			e.printStackTrace();
////		}
//		return temp_time;
//	}
	
//	private SimpleDateFormat sf = null;
//	　　*/
/*获取系统时间 格式为："yyyy/MM/dd "*//*

//	　　public static String getCurrentDate() {
//	　　Date d = new Date();
//	　　sf = new SimpleDateFormat("yyyy年MM月dd日");
//	　　return sf.format(d);
//	　　}
//	　　
//	　　*/
/*时间戳转换成字符窜*//*

//	　　public static String getDateToString(long time) {
//	　　Date d = new Date(time);
//	　　sf = new SimpleDateFormat("yyyy年MM月dd日");
//	　　return sf.format(d);
//	　　}
//	　　
//	　　*/
/*将字符串转为时间戳*//*

//	　　public static long getStringToDate(String time) {
//	　　sdf = new SimpleDateFormat("yyyy年MM月dd日");
//	　　Date date = newDate();
//	　　try{
//	　　date = sdf.parse(time);
//	　　} catch(ParseException e) {
//	　　// TODO Auto-generated catch block
//	　　e.printStackTrace();
//	　　}
//	　　returndate.getTime();
//	}
}
*/
