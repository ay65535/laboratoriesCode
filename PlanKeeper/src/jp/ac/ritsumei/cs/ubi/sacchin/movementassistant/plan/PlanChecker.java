/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONArray;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.EventNotificationActivity;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.net.EstimatedArrivalTimeDownloader;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.GeoPointUtils;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.MyGeoPoint;

import android.os.Handler;
import android.util.Log;

/**
 * ユーザのステータスを推定するクラス．
 * @author sacchin
 *
 */
public class PlanChecker {
	
	private HashMap<Long, Calendar> expectedArrivalTimes;
	/**
	 * ダウンロード用のハンドラ．
	 */
	private Handler downloadExecutor = new Handler();

	private ArrayList<MyGeoPoint> trajectory;
	
	private ArrayList<MovementResult> movementResults;
	
	private String progressText = "";
	
	private boolean isStarted = false;
	
	private EventNotificationActivity activity = null;
	
	private EstimatedArrivalTimeDownloader eatd;
	
	private ArrayList<StatusEstimater> statusEstimaters = null;
	
	public PlanChecker(EventNotificationActivity activity){
		this.trajectory = new ArrayList<MyGeoPoint>();
		this.activity = activity;
		this.expectedArrivalTimes = new HashMap<Long,Calendar>();
		this.statusEstimaters = new ArrayList<StatusEstimater>();
		this.eatd = new EstimatedArrivalTimeDownloader(activity.getDevid(), this);
	}
	
	public void arrivedAt(String transportationName, long fromid, long toid, int order){
		eatd.initForDownload(transportationName, fromid, toid, order);
		new Thread(new Runnable() {
			@Override
			public void run() {
				eatd.download();
				downloadExecutor.post(eatd);
			}
		}).start();
	}
	
	public void estimateStatus(boolean isNotSuccess, JSONArray path, String transportationName,  
			long estimatedToDestination, long estimatedToTransfer, int order, long fromid, long toid){
		long toDestination = GeoPointUtils.getTime(trajectory) + estimatedToDestination;
		long toTransfer = GeoPointUtils.getTime(trajectory) + estimatedToTransfer;
		for(StatusEstimater estimater : statusEstimaters){
			if(estimater.isSameDestination(fromid, toid)){
				activity.visualizeStatus(toid, 
						toTransfer, estimater.estimateStatus(transportationName, order, toTransfer), 
						toDestination, estimater.estimateStatus(transportationName, order, toDestination));
			}
		}
	}
	
	public void addTrajectory(MyGeoPoint latest){
		trajectory.add(latest);
	}
	
	public void updateExpectedArrivalTimes() {
	}
	
	public ArrayList<MyGeoPoint> getTrajectory() {
		return trajectory;
	}

	public String getProgressText(){
		return progressText;
	}

	public void startEstimate(long fromId) {
		if(trajectory == null){
			trajectory = new ArrayList<MyGeoPoint>();
		}else{
			trajectory.clear();
		}
		
		if(movementResults == null){
			return;
		}
		Log.v("startEstimate", "There are " + movementResults.size() + " movementResults!");
		isStarted = true;
		
		if(statusEstimaters == null){
			statusEstimaters = new ArrayList<StatusEstimater>();
		}else{
			statusEstimaters.clear();
		}
		
		for(MovementResult mr : movementResults){
			Calendar expectedArrivalTime = expectedArrivalTimes.get(mr.getToID());
			if(expectedArrivalTime == null){
				continue;
			}
			
			StatusEstimater estimater = new StatusEstimater(mr, expectedArrivalTime);
			
			HashMap<String, ArrayList<HashMap<Integer, Long>>> travel = mr.getMaximumTravel();
			if(travel == null){
				continue;
			}
			String key = travel.keySet().iterator().next();
			estimater.setEstimatedArrivalTime(System.currentTimeMillis() + 
					MovementResult.calcTravelTime(travel.get(key)));
			statusEstimaters.add(estimater);
		}	
	}

	public void setMovementResults(ArrayList<MovementResult> movementResults) {
		this.movementResults = movementResults;
	}
	
	public void addExpectedArrivalTime(DropInSite site, Calendar time){
		expectedArrivalTimes.put(site.getSiteId(), time);
	}

	public boolean isStarted() {
		return isStarted;
	}
}
