/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.utils;

import java.util.List;

public class MyGeoPointUtils extends GeoPointUtils{
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
	
	public static double[] getAccRect(MyGeoPoint a, MyGeoPoint b){
		double[] latlng = new double[4];
		
		if(a.getAccuracyRect()[0] < b.getAccuracyRect()[0]){
			latlng[0] = a.getAccuracyRect()[0];
		}else{
			latlng[0] = b.getAccuracyRect()[0];
		}
		
		if(a.getAccuracyRect()[1] < b.getAccuracyRect()[1]){
			latlng[1] = a.getAccuracyRect()[1];
		}else{
			latlng[1] = b.getAccuracyRect()[1];
		}
		
		if(a.getAccuracyRect()[2] < b.getAccuracyRect()[2]){
			latlng[2] = a.getAccuracyRect()[2];
		}else{
			latlng[2] = b.getAccuracyRect()[2];
		}
		
		if(a.getAccuracyRect()[3] < b.getAccuracyRect()[3]){
			latlng[3] = a.getAccuracyRect()[3];
		}else{
			latlng[3] = b.getAccuracyRect()[3];
		}
		return latlng;
	}
	
	/**
	 * 
	 * @param p1
	 * @param p2
	 * @return meter / second
	 */
	public static double calcHuberySpeed(MyGeoPoint p1, MyGeoPoint p2){
		long diff = Math.abs(p2.getTimestamp().getTime() - 
				p1.getTimestamp().getTime()) / 1000;
		
		double dist = GeoPointUtils.calcDistanceHubery(p1.lat, p1.lng, 
				p2.lat, p2.lng, GeoPointUtils.GRS80);
		
		if(diff == 0 || dist <= 0){
			return -1;
		}
		
		return dist / diff;
	}
	
	public static double calculateTotalDistance(List<MyGeoPoint> trajectory, int start, int end ){
		double distance = 0;
		if(0 <= start && start < trajectory.size() && 0 < end && end < trajectory.size() && start < end){
			for(int i = start ; i < end ; i++) {
				distance += calcDistanceHubery(
						trajectory.get(i).lat, trajectory.get(i).lng, 
						trajectory.get(i+1).lat, trajectory.get(i+1).lng, GRS80);
			}
		}
		return distance;
	}
	
	public static <T extends MyGeoPoint> int getNearestIndex(T cp, List<MyGeoPoint> path, int from) {
		double min = Double.MAX_VALUE;
		int index = from;
		for(int i = from ; i < path.size() ; i++){
			MyGeoPoint p = path.get(i);
			double dist = GeoPointUtils.calcDistanceHubery(p.lat, p.lng, cp.lat, cp.lng, GeoPointUtils.GRS80);
			if(dist < min){
				min = dist;
				index = i;
			}
		}
		return index;
	}
	
	public static <T extends MyGeoPoint> int getNearestListIndex(
			T candidate, List<List<T>> list, double threshold){
		double min = Double.MAX_VALUE;
		int index = -1;
		for(int j = 0 ; j < list.size() ; j++){
			List<T> temp = list.get(j);
			double dist = GeoPointUtils.calcDistanceHubery(
					calcAverageLat(temp), calcAverageLng(temp), 
					candidate.getLatitude(), 
					candidate.getLongtitude(), GeoPointUtils.GRS80);
			if(dist < min){
				min = dist;
				index = j;
			}
		}
		if(min < threshold && 0 <= index && index < list.size()){
			return index;
		}else{
			return -1;
		}
	}
	
	public static <T extends MyGeoPoint> double calcAverageLat(List<T> candidates){
		double sum = 0;
		for(T point : candidates){
			sum += point.lat;
		}
		return sum / candidates.size();
	}
	
	public static <T extends MyGeoPoint> double calcAverageLng(List<T> candidates){
		double sum = 0;
		for(T point : candidates){
			sum += point.lng;
		}
		return sum / candidates.size();
	}
	
}
