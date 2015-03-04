/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.plan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckPoint extends Candidate{
	private int order;
	
	private long fromid;
	
	private long toid;

	private String transportationNames;

	private double averageAllStayTimes = 0;
		
	private double averageOverStayTimes = 0;
	
	private double sdAllStayTimes = 0;
	
	private double sdOverStayTimes = 0;
	
	private long expectedArrivalTime = 0;
	
	private long backcalcArrivalTime = 0;
	
	private boolean isGoal = false;
	
	private int stayCount = 0;
	
	private int all = 0;
	
	public static CheckPoint createMergedPoint(List<Candidate> points, List<Integer> indexs, int all) {
		if(points == null){
			return null;
		}
		double[] accRect = getAccRect(points);
		double[] centerLatlng = new double[2];
		centerLatlng[0] = (accRect[0] + accRect[2]) / 2;
		centerLatlng[1] = (accRect[1] + accRect[3]) / 2;
		
		List<Long> allStayTimes = new ArrayList<Long>();
		List<Long> overStayTimes = new ArrayList<Long>();
		double averageAllStayTimes = calcAverage(allStayTimes);
		double averageOverStayTimes = calcAverage(overStayTimes);
		double sdAllStayTimes = calcStandardDeviation(allStayTimes);
		double sdOverStayTimes = calcStandardDeviation(overStayTimes);
		return new CheckPoint(points.size(), all, accRect, centerLatlng, 
				averageAllStayTimes, averageOverStayTimes, sdAllStayTimes, sdOverStayTimes);
	}
	
	protected CheckPoint(int stayCount, int all, double[] accRectangle, double[] centerLatlng, 
			double averageAllStayTimes, double averageOverStayTimes, double sdAllStayTimes, double sdOverStayTimes){
		super(0, 0, accRectangle, centerLatlng, 0);
		this.averageAllStayTimes = averageAllStayTimes;
		this.averageOverStayTimes = averageOverStayTimes;
		this.sdAllStayTimes = sdAllStayTimes;
		this.sdOverStayTimes = sdOverStayTimes;
		this.stayCount = stayCount;
		this.all = all;
	}
	
	public static CheckPoint createCheckPoint(JSONObject point) {
		if(point == null){
			return null;
		}
		try {
			double[] accRectangle = {
					point.getDouble("minLat"),
					point.getDouble("minLng"),
					point.getDouble("maxLat"),
					point.getDouble("maxLng")
			};
			double[] centerLatlng = {
					(accRectangle[0] + accRectangle[2]) / 2,
					(accRectangle[1] + accRectangle[3]) / 2
			};
			
			return new CheckPoint(0, 0, accRectangle, centerLatlng, 0, 0, 0, 0);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static CheckPoint createMergedPoint(Candidate points) {
		if(points == null){
			return null;
		}
		List<Long> stayTimes = new ArrayList<Long>();
		stayTimes.add(points.getStayTime());
		return 
		new CheckPoint(0, 0, points.getAccuracyRect(), points.getCenterLatlng(), 0, 0, 0, 0);
	}
	
	public static <T extends MyGeoPoint> double[] getAccRect(List<T> points){
		double[] returnRect = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};
		for(T temp : points){
			double[] rect = temp.getAccuracyRect();
			if(rect[0] < returnRect[0]){
				returnRect[0] = rect[0];
			}
			if(rect[1] < returnRect[1]){
				returnRect[1] = rect[1];
			}
			if(returnRect[2] < rect[2]){
				returnRect[2] = rect[2];
			}
			if(returnRect[3] < rect[3]){
				returnRect[3] = rect[3];
			}
		}
		return returnRect;
	}
	
	public static long getMedianStayTime(List<Candidate> points){
		List<Long> stayTimes = new ArrayList<Long>();
		for(Candidate cp : points){
			stayTimes.add(cp.getStayTime());
		}
		if(stayTimes.isEmpty()){
			return -1;
		}
		Collections.sort(stayTimes);
		return stayTimes.get(stayTimes.size() / 2).longValue();
	}
	
	public static long getMaxStayTime(List<Candidate> points){
		long max = 0;
		for(Candidate cp : points){
			if(max < cp.getStayTime()){
				max = cp.getStayTime();
			}
		}
		return max;
	}
	
	public double getStayProbability(){
		double p = (double)stayCount / (double)all;
		return p;
	}
	
	public boolean isGoal() {
		return isGoal;
	}

	public void setGoal(boolean isGoal) {
		this.isGoal = isGoal;
	}
	
	public void setBackcalcArraivalTime(long backcalcArrivalTime){
		this.backcalcArrivalTime = backcalcArrivalTime;
	}
	
	public long getBackcalcArraivalTime() {
		return backcalcArrivalTime;
	}
	
	public void setExpectedArrivalTime(long expectedArrivalTime){
		this.expectedArrivalTime = expectedArrivalTime;
	}

	public long getExpectedArrivalTime() {
		return expectedArrivalTime;
	}
	
	public double getAverageAllStayTimes() {
		return averageAllStayTimes;
	}

	public double getAverageOverStayTimes() {
		return averageOverStayTimes;
	}

	public double getSdAllStayTimes() {
		return sdAllStayTimes;
	}

	public double getSdOverStayTimes() {
		return sdOverStayTimes;
	}

	public long getBackcalcArrivalTime() {
		return backcalcArrivalTime;
	}

	public static double calcStandardDeviation(List<Long> stayTimes){
		if(stayTimes == null || stayTimes.isEmpty()){
			return 0;
		}
		double average = calcAverage(stayTimes);
		long sum = 0;
		int i = 0 ;
		for(i = 0 ; i < stayTimes.size() ; i++){
			double t = stayTimes.get(i).longValue() - average;
			sum += t * t;
		}
		return Math.sqrt(sum / i);
	}
	
	public static double calcAverage(List<Long> stayTimes){
		if(stayTimes == null || stayTimes.isEmpty()){
			return 0;
		}
		long sum = 0;
		int i = 0;
		for(i = 0 ; i < stayTimes.size() ; i++){
			Long t = stayTimes.get(i);
			sum += t.longValue();
		}
		return sum / i;
	}
	
	public JSONObject getJSON(){
		JSONObject temp = super.getJSON();
		try {
			temp.put("averageAllStayTimes", averageAllStayTimes);
			temp.put("averageOverStayTimes", averageOverStayTimes);
			temp.put("sdAllStayTimes", sdAllStayTimes);
			temp.put("sdOverStayTimes", sdOverStayTimes);
			temp.put("stayCount", stayCount);
			temp.put("all", all);
			return temp;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{BackcalcAT=" + backcalcArrivalTime);
		sb.append(", ExpectedAT=" + expectedArrivalTime);
		sb.append(", averageAllStayTimes=" + averageAllStayTimes);
		sb.append(", averageOverStayTimes=" + averageOverStayTimes);
		sb.append(", sdAllStayTimes=" + sdAllStayTimes);
		sb.append(", sdOverStayTimes=" + sdOverStayTimes);
		sb.append(", stayCount=" + stayCount);
		sb.append(", all=" + all);
		sb.append(", " + super.toString() + "}");
		return sb.toString();
	}
	
	public void setOrder(int order) {
		this.order = order;
	}

	public void setTransportationNames(String transportationNames) {
		this.transportationNames = transportationNames;
	}

	public long getFromid() {
		return fromid;
	}

	public void setFromid(long fromid) {
		this.fromid = fromid;
	}

	public long getToid() {
		return toid;
	}

	public void setToid(long toid) {
		this.toid = toid;
	}

	public String getTransportationNames() {
		return transportationNames;
	}

	public int getOrder() {
		return order;
	}
}