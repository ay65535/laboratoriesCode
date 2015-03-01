/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * 単一の移動手段が通る1つのルートを表現するクラス．
 * @author sacchin
 *
 */
public class Route {
	/**
	 * 出発地点となる立ち寄りポイントID．
	 */
	private long fromid;
	
	/**
	 * 到着地点となる立ち寄りポイントID．
	 */
	private long toid;
	
	/**
	 * 移動手段の組み合わせ内での位置．
	 */
	private int order;
	
	/**
	 * このルートが始まる乗り換えポイント．
	 */
	private TransferPoint startPoint;
	
	/**
	 * このルートが終わる乗り換えポイント．
	 */
	private TransferPoint endPoint;
	
	/**
	 * このルートに含まれるチェックポイント．
	 */
	private ArrayList<CheckPoint> points;
	
	/**
	 * このルートの移動時間のリスト．
	 */
	private ArrayList<Long> travelTimes;
	
	/**
	 * Factoryメソッド．
	 * @param order 移動手段の組み合わせ内での位置．
	 * @param route　ルートを表すJSONObject．
	 * @param transportationNames この移動手段が含まれる組み合わせの名前．
	 * @param fromid 出発地点となる立ち寄りポイントID．
	 * @param toid　到着地点となる立ち寄りポイントID．
	 * @return
	 */
	public static Route createRoute(int order, JSONObject route, 
			String transportationNames, long fromid, long toid){
		if(route == null){
			return null;
		}
		try {
			return new Route(order, route, transportationNames, fromid, toid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Route(int order, JSONObject object, String transportationNames, long fromid, long toid)
			throws JSONException{
		JSONObject startPoint = object.getJSONObject("startpoint");
		this.startPoint = new TransferPoint(
				startPoint.getDouble("minLat"), startPoint.getDouble("minLng"), 
				startPoint.getDouble("maxLat"), startPoint.getDouble("maxLng"), 
				order, fromid, toid, transportationNames);
		
		JSONObject endPoint = object.getJSONObject("endpoint");
		this.endPoint = new TransferPoint(
				endPoint.getDouble("minLat"), endPoint.getDouble("minLng"), 
				endPoint.getDouble("maxLat"), endPoint.getDouble("maxLng"), 
				order, fromid, toid, transportationNames);
		
		this.points = new ArrayList<CheckPoint>();
		JSONArray checkpoints = object.getJSONArray("checkpoints");
		for(int i = 0 ; i < checkpoints.length() ; i++){
			CheckPoint cp = CheckPoint.createCheckPoint(
					checkpoints.getJSONObject(i));
			cp.setOrder(order);
			cp.setFromid(fromid);
			cp.setToid(toid);
			cp.setTransportationNames(transportationNames);
			if(cp != null){
				points.add(cp);
			}
		}
		this.travelTimes = new ArrayList<Long>();
		JSONArray travelArray = object.getJSONArray("traveltimes");
		for(int i = 0 ; i < travelArray.length() ; i++){
			Long t = travelArray.getLong(i);
			if(t != null){
				travelTimes.add(t);
			}
		}
		
		this.fromid = fromid;
		this.toid = toid;
		this.order = order;
		Log.v("Route", this.toString());
	}

	public TransferPoint getStartPoint() {
		return startPoint;
	}

	public TransferPoint getEndPoint() {
		return endPoint;
	}

	public ArrayList<CheckPoint> getCheckPoints() {
		return points;
	}
	
	public String toString(){
		String str = "[";
		for(Long t : travelTimes){
			str += t + ",";
		}
		return "{[" + fromid + "to" + toid + "] " +
				"start:(" + startPoint.getLatitude() + ", " + startPoint.getLongtitude() +
				"), end:{" + endPoint.getLatitude() + ", " + endPoint.getLongtitude() +
				"), CP:" + points.size() + ", travelTimes:" + str + "]}";
	}

	public int getOrder() {
		return order;
	}

	public long getFromid() {
		return fromid;
	}

	public long getToid() {
		return toid;
	}

	public ArrayList<Long> getTravelTimes() {
		return travelTimes;
	}
	
	public long getMinTravelTime() {
		long min = Long.MAX_VALUE;
		for(Long time : travelTimes){
			if(time < min){
				min = time;
			}
		}
		return min;
	}
	
	public long getMaxTravelTime() {
		long max = Long.MIN_VALUE;
		for(Long time : travelTimes){
			if(max < time){
				max = time.longValue();
			}
		}
		return max;
	}
}
