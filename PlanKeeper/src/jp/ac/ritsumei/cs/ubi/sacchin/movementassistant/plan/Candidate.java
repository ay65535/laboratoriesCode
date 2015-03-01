/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan;

import java.sql.Timestamp;
import java.util.ArrayList;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.GeoPointUtils;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.MyGeoPoint;

import org.json.JSONException;
import org.json.JSONObject;

public class Candidate extends MyGeoPoint{
	private long stayTime = 0;
	
	private final double hubenySpeed;
	
	public static Candidate createCandidate(ArrayList<MyGeoPoint> points) {
		if(points == null){
			return null;
		}
		double[] accRect = getAccRect(points);
		double[] centerLatlng = new double[2];
		centerLatlng[0] = (accRect[0] + accRect[2]) / 2;
		centerLatlng[1] = (accRect[1] + accRect[3]) / 2;
		
		MyGeoPoint last = points.get(points.size() - 1);
		MyGeoPoint first = points.get(0);
		long stayTime = last.getTimestamp().getTime() - first.getTimestamp().getTime();
		long time = (first.getTimestamp().getTime() + last.getTimestamp().getTime()) / 2;
		double dist = GeoPointUtils.calcDistanceHubery(
				first.lat, first.lng, last.lat, last.lng, GeoPointUtils.GRS80);
		double hubenySpeed = dist / (stayTime / 1000);
		return new Candidate(time, stayTime, accRect, centerLatlng, hubenySpeed);
	}
	
	private static double[] getAccRect(ArrayList<MyGeoPoint> points){
		//latlng[0]:mostLowLat, latlng[1]:mostLowLng, latlng[2]:mostHighLat, latlng[3]:mostHighLng
		double[] latlng = new double[4];
		latlng[0] = Double.MAX_VALUE;
		latlng[1] = Double.MAX_VALUE;
		latlng[2] = Double.MIN_VALUE;
		latlng[3] = Double.MIN_VALUE;
		
		for(MyGeoPoint point : points){
			if(point.getLatitude() < latlng[0]){
				latlng[0] = point.getLatitude();
			}
			if(point.getLongtitude() < latlng[1]){
				latlng[1] = point.getLongtitude();
			}
			if(latlng[2] < point.getLatitude()){
				latlng[2] = point.getLatitude();
			}
			if(latlng[3] < point.getLongtitude()){
				latlng[3] = point.getLongtitude();
			}
		}
		return latlng;
	}
	
	protected Candidate(long time, long stayTime, double[] accRectangle, double[] centerLatlng, double hubenySpeed){
		super(new Timestamp(time), accRectangle, centerLatlng);
		this.stayTime = stayTime;
		this.hubenySpeed = hubenySpeed;
	}
	
//	public static Candidate createCheckPoint(JSONObject point) {
//		try {
//			return new Candidate(point);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	protected Candidate(JSONObject point) throws JSONException {
//		super(point);
//		this.name = point.getString("name");
//		this.stayTime = point.getLong("stayTime");
//		this.hubenySpeed = point.getDouble("hubeny");
//		this.order = point.getInt("order");
//		this.pathID = point.getInt("pathID");
//	}
	
	public long getStayTime() {
		return stayTime;
	}

	public double getHubenySpeed() {
		return hubenySpeed;
	}

	public JSONObject getJSON(){
		JSONObject temp = super.getJSON();
		try {
			temp.put("stayTime", stayTime);
			temp.put("hubeny", hubenySpeed);
			return temp;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{StayTime=" + (stayTime/1000) + "sec");
		sb.append(", " + super.toString() + "}");
		return sb.toString();
	}
}