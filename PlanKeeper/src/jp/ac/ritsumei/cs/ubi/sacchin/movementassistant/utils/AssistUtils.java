/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils;

import java.util.Calendar;
import java.util.Date;

public class AssistUtils {
	public static final String ENTER_DROP_IN_SITE = "EnterDIS";
	public static final String GOOUT_DROP_IN_SITE = "GoOutDIS";
	public static final String ENTER_TRANSFER = "EnterTP";
	public static final String GOOUT_TRANSFER = "GoOutTP";
	public static final String ENTER_CHECKPOINT = "EnterCP";
	public static final String GOOUT_CHECKPOINT = "GoOutCP";
	
	public static final int STATUS_FINE = 0;
	public static final int STATUS_WILL_BE_LATENESS = 1;
	public static final int STATUS_LATENESS = 2;
	
	public static String getStatusText(int status){
		switch (status) {
		case 0:
			return "Fine";
		case 1:
			return "Will Be Lateness";
		case 2:
			return "Lateness";
		default:
			return "Error";
		}		
	}
	
	public static String formatToYMDHMS(long millisecond){
		Calendar now = Calendar.getInstance();
		now.setTime(new Date(System.currentTimeMillis()));
		
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(millisecond));
		
		String re = "";
		if(now.get(Calendar.YEAR) != c.get(Calendar.YEAR)){
			re += c.get(Calendar.YEAR) + "年";
		}
		re += (c.get(Calendar.MONTH) + 1) + "月";
		re += c.get(Calendar.DAY_OF_MONTH) + "日 ";
		re += c.get(Calendar.HOUR_OF_DAY) + "時";
		re += c.get(Calendar.MINUTE) + "分";
		re += c.get(Calendar.SECOND) + "秒";
		return re;
	}
	
	public static String formatToHMS(long millisecond){
		Calendar now = Calendar.getInstance();
		now.setTime(new Date(System.currentTimeMillis()));
		
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(millisecond));
		
		String re = "";
		re += c.get(Calendar.HOUR_OF_DAY) + "時";
		re += c.get(Calendar.MINUTE) + "分";
		re += c.get(Calendar.SECOND) + "秒";
		return re;
	}
	
}
