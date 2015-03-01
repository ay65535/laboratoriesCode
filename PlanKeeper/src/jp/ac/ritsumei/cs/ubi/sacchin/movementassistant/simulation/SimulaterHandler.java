/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.simulation;

import java.util.ArrayList;
import java.util.HashMap;

import jp.ac.ritsumei.cs.ubi.logger.client.api.matching.EventDetectionRequest;
import jp.ac.ritsumei.cs.ubi.logger.client.api.matching.LatLngCompareOperation;
import jp.ac.ritsumei.cs.ubi.logger.client.api.utility.MatchingConstants;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.EventNotificationActivity;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.MyGeoPoint;
import android.os.Handler;
import android.os.Message;

/**
 * this class simulate the plankeeper.
 * ALCの代わりにイベント検知機能も搭載！
 * @author sacchin
 *
 */
public class SimulaterHandler extends Handler {
	/**
	 * this is trajectory of testcase
	 */
	private ArrayList<MyGeoPoint> testCase;
	
	/**
	 * if simulation is started, this is rue.
	 */
	private boolean isInProgress = false;
	
	/**
	 * this is a count of event detection.
	 * to prevent mistakes.
	 */
	private HashMap<String, Integer> detectCount;
	
	/**
	 * this is a simulation speed [millsec]
	 */
	private int sleepTime = 1000;
	
	/**
	 * 検知したいイベントのマップ
	 */
	private HashMap<String, EventDetectionRequest> registedRequestMap;
	
	private EventNotificationActivity activity;

	/**
	 * this is constructor.
	 * @param activity EventNotificationActivity.this
	 */
	public SimulaterHandler(EventNotificationActivity activity){
		this.activity = activity;
	}

	/**
	 * start simulation.
	 */
	public void start(){
		this.isInProgress = true;
		handleMessage(Message.obtain(this, 0, "0"));
	}

	/**
	 * stop simulation.
	 */
	public void stop(){
		this.isInProgress = false;
	}

	/**
	 * Advance the simulation at intervals determined(this.sleepTime)
	 */
	@Override
	public void handleMessage(Message msg) {
		int count = Integer.parseInt(((String) msg.obj));
		this.removeMessages(0);
		if(this.isInProgress && testCase != null && count < testCase.size()){
			activity.addGeoPoint(testCase.get(count), detectEvent(testCase.get(count)));
			Message m = Message.obtain(this, 0, String.valueOf(count + 1));
			sendMessageDelayed(m, sleepTime);
		}
	}

	/**
	 * set test case.
	 * @param testCase this is trajectory to simulate.
	 */
	public void setTestCase(ArrayList<MyGeoPoint> testCase) {
		this.testCase = testCase;
	}
	
	/**
	 * this method detect a event instead of ALC.
	 * @param point latest geopoint
	 * @return if detect a event, return true.
	 */
	public boolean detectEvent(MyGeoPoint point){
		if(registedRequestMap == null || registedRequestMap.isEmpty()){
			return false;
		}
		if(detectCount == null){
			detectCount = new HashMap<String, Integer>();
		}
		boolean isDetect = false;
		for(String key : registedRequestMap.keySet()){
			EventDetectionRequest edr = registedRequestMap.get(key);
			if(edr == null){
				continue;
			}
			LatLngCompareOperation operation = ((LatLngCompareOperation)edr.getPredicate());
			double[] latlngConstant = operation.getLatLngConstant();

			double lat = point.getLatitude();
			double lng = point.getLongtitude();

			if(MatchingConstants.SMALLER_THAN == operation.getCalculater() && 
					(latlngConstant[0] < lat && latlngConstant[1] < lng &&
					latlngConstant[2] > lat && latlngConstant[3] > lng)){

				Integer count = detectCount.get(key);
				if(count == null){
					detectCount.put(key, 1);
				}else if(3 == count.intValue()){
					activity.arriveAt(key);
					isDetect = true;
				}else{
					detectCount.put(key, (count + 1));
				}
			}		
			if(MatchingConstants.LARGER_THAN == operation.getCalculater() && 
					!(latlngConstant[0] < lat && latlngConstant[1] < lng &&
					latlngConstant[2] > lat && latlngConstant[3] > lng)){

				Integer count = detectCount.get(key);
				if(count == null){
					detectCount.put(key, 1);
				}else if(3 < count.intValue()){
					activity.leaveFrom(key);
					isDetect = true;
				}else{
					detectCount.put(key, (count + 1));
				}
			}
		}
		return isDetect;
	}
	
	/**
	 * 矩形内に入ったまたは出たイベント群を登録するメソッド．
	 * @param requests イベントのマップ．
	 */
	public void registEventDetections(HashMap<String, EventDetectionRequest> requests){
		if(registedRequestMap == null){
			registedRequestMap = new HashMap<String, EventDetectionRequest>();
		}
		
		for(String key : requests.keySet()){
			registedRequestMap.put(key, requests.get(key));
		}
	}
	
	/**
	 * 矩形内に入ったまたは出たイベントを登録するメソッド．
	 * @param key イベントのkey．
	 * @param request イベント．
	 */
	public void registEventDetection(String key, EventDetectionRequest request){
		registedRequestMap.put(key, request);
	}
	
	/**
	 * すべてのイベントを削除するメソッド．
	 */
	public void unregistAllEventDetection(){
		registedRequestMap.clear();
	}
	
	/**
	 * 指定したイベントを削除するメソッド．
	 * @param key 削除するイベントのkey．
	 */
	public void unregistEventDetection(String key){
		registedRequestMap.remove(key);
	}
	
	/**
	 * シミュレーションの速度を変更するメソッド．
	 * @param millsec
	 */
	public void setSleepTime(int millsec){
		this.sleepTime = millsec;
	}
}
