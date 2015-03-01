/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.net;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.EventNotificationActivity;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.HttpConnector;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.MyGeoPoint;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.URLConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * this is Runnable class to download trajectory for simulation.
 * @author sacchin
 *
 */
public class TrajectoryDownloadRunner implements Runnable{
	/**
	 * this is url of servlet.
	 */
	private static String DOWNLOD_URL = URLConstants.SERVER_URL + "?pattern=trajectory";
	
	/**
	 * these are inputed by datepicker and timepicker.
	 */
	private int year = - 1;
	private int month = - 1;
	private int day = - 1;
	private int hour = -1;
	private int minute = -1;
	private int interval = -1;

	/**
	 * list of geopoint.
	 * this represents the trajectory.
	 */
	private ArrayList<MyGeoPoint> trajectory = null;
	
	/**
	 * if can't download, this is true.
	 */
	private boolean isNotSuccess = false;
	private EventNotificationActivity activity = null;

	/**
	 * this is constructor.
	 * @param activity EventNotificationActivity.this
	 */
	public TrajectoryDownloadRunner(EventNotificationActivity activity){
		this.activity = activity;
	}

	/**
	 * download trajectory from servlet and convert JSON format into ArrayLst.
	 */
	public void download() {
		if(year < 0 || month < 0 || day < 0 ||
				hour < 0 || minute < 0 || interval < 0){
			return;
		}
		if(trajectory == null){
			trajectory = new ArrayList<MyGeoPoint>();
		}else{
			trajectory.clear();
		}

		try{
			String jsonStr = HttpConnector.downloadDataForALine(DOWNLOD_URL + 
					"&y=" + year + "&m=" + month + "&d=" + day + 
					"&h=" + hour + "&mi=" + minute + "&i=" + interval + "&devid=" + activity.getDevid());
			Log.v("url", DOWNLOD_URL + 
					"&y=" + year + "&m=" + month + "&d=" + day + 
					"&h=" + hour + "&mi=" + minute + "&i=" + interval + "&devid=" + activity.getDevid());
			JSONObject jObject = new JSONObject(jsonStr);
			String status = jObject.getString("status");
			if(!"success".equals(status)){
				isNotSuccess = true;
				return;
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			JSONArray jArray = jObject.getJSONArray("trajectory");
			final int SIZE = jArray.length();
			for(int i = 0 ; i < SIZE; i++){
				JSONObject o = jArray.getJSONObject(i);
				long time = sdf.parse(o.getString("time")).getTime();
				double lat = o.getDouble("lat");
				double lng = o.getDouble("lng");
				double acc = o.getDouble("acc");
				double speed = o.getDouble("speed");
				trajectory.add(new MyGeoPoint(time, lat, lng, acc, speed));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * set year of beginning of trajectory.
	 * @param year 0 - 2013
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * set month of beginning of trajectory.
	 * @param month 0 - 11
	 */
	public void setMonth(int month) {
		this.month = month;
	}

	/**
	 * set day of beginning of trajectory.
	 * @param day 1 - 31
	 */
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * set hour of beginning of trajectory.
	 * @param hour 0 - 23
	 */
	public void setHour(int hour) {
		this.hour = hour;
	}

	/**
	 * set minute of beginning of trajectory.
	 * @param minute 0 - 59
	 */
	public void setMinute(int minute) {
		this.minute = minute;
	}
	
	/**
	 * set interval of trajectory.
	 * @param interval 0 ~ [minute]
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	/**
	 * if this.download() id finish, this is called.
	 */
	@Override
	public void run() {
		activity.startSimulation(isNotSuccess, trajectory);
	}
}
