/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.plan;

import java.sql.Timestamp;


import org.json.JSONException;
import org.json.JSONObject;

public class MyGeoPoint {
	public Timestamp time;
	
	public final double lat;
	
	public final double lng;
	
	public final double acc;
	
	public final double dopplerSpeed;
	
	protected final double[] centerLatlng;
	
	protected final double[] accuracyRect;
//	
//	protected final GeoPoint mostLowGeoPoint;
//	
//	protected final GeoPoint mostHighGeoPoint;
	
	/**
	 * 
	 * @param point
	 * @throws JSONException
	 */
	protected MyGeoPoint(JSONObject point) throws JSONException{
		this.time = new Timestamp(point.getLong("time"));
		this.lat = point.getDouble("lat");
		this.lng = point.getDouble("lng");
		this.acc = point.getDouble("acc"); 
		this.dopplerSpeed = point.getDouble("speed");

		this.centerLatlng = new double[2];
		this.centerLatlng[0] = point.getDouble("centerLat");
		this.centerLatlng[1] = point.getDouble("centerLng");
		
		this.accuracyRect = new double[4];
		this.accuracyRect[0] = point.getDouble("accuracyRect0");
		this.accuracyRect[1] = point.getDouble("accuracyRect1");
		this.accuracyRect[2] = point.getDouble("accuracyRect2");
		this.accuracyRect[3] = point.getDouble("accuracyRect3");
		
//		this.mostLowGeoPoint = convertToGeoPoint(accuracyRect[0], accuracyRect[1]);
//		this.mostHighGeoPoint = convertToGeoPoint(accuracyRect[2], accuracyRect[3]);
	}

//	public static MyGeoPoint createMyGeoPoint(GeoPoint point){
//		double lat = point.getLatitudeE6();
//		double lng = point.getLongitudeE6();
//		return new MyGeoPoint(lat / 1E6, lng / 1E6, lat / 1E6, lng / 1E6);
//	}
	
	public static MyGeoPoint createMyGeoPoint(JSONObject point){
		try {
			return new MyGeoPoint(point);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected MyGeoPoint(Timestamp time, double[] accRectangle, double[] centerLatlng) {
		this.time = time;
		this.centerLatlng = centerLatlng;
		this.accuracyRect = accRectangle;
		this.lat = centerLatlng[0];
		this.lng = centerLatlng[1];
		this.acc = 0;
		this.dopplerSpeed = 0;
		
//		this.mostLowGeoPoint = convertToGeoPoint(accuracyRect[0], accuracyRect[1]);
//		this.mostHighGeoPoint = convertToGeoPoint(accuracyRect[2], accuracyRect[3]);
	}
	
	/**
	 * @param time
	 * @param lat
	 * @param lng
	 * @param acc
	 * @param speed
	 */
	public MyGeoPoint(long time, double lat, double lng, double acc, double speed){
		this.time = new Timestamp(time);
		this.lat = lat;
		this.lng = lng;
		this.acc = acc;
		this.dopplerSpeed = speed;
		
		this.centerLatlng = new double[2];
		this.centerLatlng[0] = lat;
		this.centerLatlng[1] = lng;
		if(acc < 15){
			acc = 15;
		}
		this.accuracyRect = GeoPointUtils.calculateRect(lat, lng, (int)acc);
//		this.mostLowGeoPoint = convertToGeoPoint(accuracyRect[0], accuracyRect[1]);
//		this.mostHighGeoPoint = convertToGeoPoint(accuracyRect[2], accuracyRect[3]);
	}
	
	public JSONObject getJSON(){
		try {
			JSONObject temp = new JSONObject();
			if(time != null){
				temp.put("time", time.getTime());
			}else{
				temp.put("time", 0);
			}
			temp.put("lat", lat);
			temp.put("lng", lng);
			temp.put("acc", acc);
			temp.put("speed", dopplerSpeed);
			
			temp.put("centerLat", centerLatlng[0]);
			temp.put("centerLng", centerLatlng[1]);
			
			temp.put("accuracyRect0", accuracyRect[0]);
			temp.put("accuracyRect1", accuracyRect[1]);
			temp.put("accuracyRect2", accuracyRect[2]);
			temp.put("accuracyRect3", accuracyRect[3]);
			
			return temp;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public MyGeoPoint(double mostLowLat, double mostLowLng, double mostHighLat, double mostHighLng){
		this.time = null;
		this.acc = -1;
		this.dopplerSpeed = -1;
		this.centerLatlng = new double[2];
		this.centerLatlng[0] = (mostLowLat + mostHighLat)/2;
		this.centerLatlng[1] = (mostLowLng + mostHighLng)/2;
		this.accuracyRect = new double[4];
		this.accuracyRect[0] = mostLowLat;
		this.accuracyRect[1] = mostLowLng;
		this.accuracyRect[2] = mostHighLat;
		this.accuracyRect[3] = mostHighLng;
//		this.mostLowGeoPoint = convertToGeoPoint(mostLowLat, mostLowLng);
//		this.mostHighGeoPoint = convertToGeoPoint(mostHighLat, mostHighLng);
		this.lat = centerLatlng[0];
		this.lng = centerLatlng[1];
	}
	
	public double[] getLatlng(){
		return centerLatlng;
	}	
	
	public double getLatitude(){
		return centerLatlng[0];
	}
	
	public double getLongtitude(){
		return centerLatlng[1];
	}
	
//	public GeoPoint getMostLowGeoPoint() {
//		return mostLowGeoPoint;
//	}
//
//	public GeoPoint getMostHighGeoPoint() {
//		return mostHighGeoPoint;
//	}
//
//	public GeoPoint convertToGeoPoint(double lat, double lng){
//        return new GeoPoint((int)(lat*1E6),(int)(lng*1E6));
//	}

	public double[] getAccuracyRect() {
		return accuracyRect;
	}

	public Timestamp getTimestamp() {
		return time;
	}

	public double[] getCenterLatlng() {
		return centerLatlng;
	}
	
//	public GeoPoint getCenterGeoPoint(){
//		return convertToGeoPoint(lat, lng);
//	}
	
	public String forVisualize(){
		return lat + " " + lng;
	}
	
	public void setTime(Timestamp time) {
		this.time = time;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{[" + time);
		sb.append("], C:(" + centerLatlng[0] + ", " + centerLatlng[1]);
		sb.append("), AccRect:(" + accuracyRect[0] + ", " + accuracyRect[1] + ", " + accuracyRect[2] + ", " + accuracyRect[3]);
		sb.append("), doppler=" + dopplerSpeed + "}");
		return sb.toString();
	}
}