/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.plan;

import java.util.ArrayList;
import java.util.List;


/**
 * MyGeoPointのリストから停滞した場所を抽出するクラス．
 * @author sacchin
 *
 */
public class CheckPointsCreater{
	final static int DIST = 30;	// m
	final static double WALK_SPEED_THRESHOLD = 1.333 ;	// m/s
	
	public static ArrayList<double[]> createCheckpoints(List<MyGeoPoint> path) {
		if(path == null){
			return null;
		}
		ArrayList<Candidate> candidates = new ArrayList<Candidate>();
		candidates.addAll(spliteToCandidate(path, WALK_SPEED_THRESHOLD / 2, 0));
		ArrayList<double[]> cps = createCheckpointCadidates(candidates);
		return cps;
	}
		
	private static double calcAverageLat(ArrayList<Candidate> candidates){
		double sum = 0;
		for(Candidate point : candidates){
			sum += point.lat;
		}
		return sum / candidates.size();
	}
	
	private static double calcAverageLng(ArrayList<Candidate> candidates){
		double sum = 0;
		for(Candidate point : candidates){
			sum += point.lng;
		}
		return sum / candidates.size();
	}
	
	private static ArrayList<double[]> createCheckpointCadidates(ArrayList<Candidate> candidates){
		ArrayList<ArrayList<Candidate>> mergedAreas = mergedCadidates(candidates);
		ArrayList<double[]> rects = new ArrayList<double[]>();
		for(int i = 0 ; i < mergedAreas.size() - 1 ; i++){
			ArrayList<Candidate> areas = mergedAreas.get(i);
			rects.add(CheckPoint.getAccRect(areas));
		}
		return rects;
	}

	private static ArrayList<ArrayList<Candidate>> mergedCadidates(ArrayList<Candidate> candidates){
		ArrayList<ArrayList<Candidate>> mergedAreas = new ArrayList<ArrayList<Candidate>>();
		for(int i = 0 ; i < candidates.size() ; i++){
			if(mergedAreas.isEmpty()){
				ArrayList<Candidate> temp = new ArrayList<Candidate>();
				temp.add(candidates.get(i));
				mergedAreas.add(temp);
			}else{
				if(candidates.get(i).getStayTime() <= 1000){
					continue;
				}
				boolean isAdded = false;
				double min = Double.MAX_VALUE;
				int index = -1;
				for(int j = 0 ; j < mergedAreas.size() ; j++){
					ArrayList<Candidate> temp = mergedAreas.get(j);
					double dist = GeoPointUtils.calcDistanceHubery(
							calcAverageLat(temp), calcAverageLng(temp), 
							candidates.get(i).getLatitude(), 
							candidates.get(i).getLongtitude(), GeoPointUtils.GRS80);
					if(dist < min){
						min = dist;
						index = j;
					}
				}
				if(index != -1 && min < DIST && 0 <= index && index < mergedAreas.size()){
					mergedAreas.get(index).add(candidates.get(i));
					isAdded = true;
				}
				if(!isAdded){
					ArrayList<Candidate> temp = new ArrayList<Candidate>();
					temp.add(candidates.get(i));
					mergedAreas.add(temp);
				}
			}
		}
		return mergedAreas;
	}

	private static ArrayList<Candidate> spliteToCandidate(List<MyGeoPoint> path, 
			double threshold, int pathID) {
		ArrayList<Candidate> candidates = new ArrayList<Candidate>();
		int end = path.size();
		ArrayList<MyGeoPoint> temp = new ArrayList<MyGeoPoint>();
		for(int i = 0 ; i < end - 1 ; i++){
			MyGeoPoint startPoint = path.get(i);
			MyGeoPoint endPoint = path.get(i + 1);
			double distMeter = GeoPointUtils.calcDistanceHubery(
					startPoint.lat, startPoint.lng, endPoint.lat, endPoint.lng, GeoPointUtils.GRS80);
			long diffSec = (endPoint.getTimestamp().getTime() - startPoint.time.getTime()) / 1000;
			
			if(diffSec < 1 || distMeter < 0){
				continue;
			}
			
			double speed = (distMeter / diffSec);
			
			if(speed < threshold){
				temp.add(startPoint);
			}
			if((threshold <= speed || i == end - 2) && !temp.isEmpty()){
				temp.add(startPoint);
				MyGeoPoint stayPoints[] = new MyGeoPoint[2];
				stayPoints[0] = temp.get(0);
				stayPoints[1] = temp.get(temp.size() - 1);
				candidates.add(Candidate.createCandidate(temp));
				temp.clear();
			}
		}
		return candidates;
	}
}
