/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 単一の移動手段を表現するクラス．
 * @author sacchin
 *
 */
public class SingleMeansOfTransportation {
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
	 * この移動手段名．
	 */
	private String mode;
	
	/**
	 * この移動手段が通るルートのリスト．
	 */
	private ArrayList<Route> routes;
	
	/**
	 * Factoryメソッド．
	 * @param order 移動手段の組み合わせ内での位置．
	 * @param singleMeansOfTransportation 単一の移動手段を表すJSONArray．
	 * @param transportationNames この移動手段が含まれる組み合わせの名前．
	 * @param fromid 出発地点となる立ち寄りポイントID．
	 * @param toid　到着地点となる立ち寄りポイントID．
	 * @return
	 */
	public static SingleMeansOfTransportation createSingleMeansOfTransportation(int order, 
			JSONObject singleMeansOfTransportation, String transportationNames, long fromid, long toid){
		if(singleMeansOfTransportation == null){
			return null;
		}
		try {
			String mode = singleMeansOfTransportation.getString("mode");
			JSONArray routes = singleMeansOfTransportation.getJSONArray("routes");
			if(routes == null || routes.length() == 0){
				return null;
			}
			return new SingleMeansOfTransportation(order, mode, routes, 
					transportationNames, fromid, toid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * コンストラクタ．
	 * @param order 移動手段の組み合わせ内での位置．
	 * @param mode この移動手段の名前．
	 * @param routes この移動手段が通るルート．
	 * @param transportationNames この移動手段が含まれる組み合わせの名前．
	 * @param fromid 出発地点となる立ち寄りポイントID．
	 * @param toid　到着地点となる立ち寄りポイントID．
	 */
	public SingleMeansOfTransportation(int order, 
			String mode, JSONArray routes, String transportationNames, long fromid, long toid){
		this.order = order;
		this.mode = mode;
		this.fromid = fromid;
		this.toid = toid;
		this.routes = new ArrayList<Route>();
		for(int i = 0 ; i < routes.length() ; i++){
			try {
				this.routes.add(Route.createRoute(
						order, routes.getJSONObject(i), transportationNames, fromid, toid));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getMode() {
		return mode;
	}

	public ArrayList<Route> getRoutes() {
		return routes;
	}
	
	/**
	 * 単一の移動手段に含まれる乗り換えポイントを重複なく取得するメソッド．
	 * @return 乗り換えポイントのリスト．
	 */
	public ArrayList<TransferPoint> getAllTransferPoints(){
		ArrayList<TransferPoint> points = new ArrayList<TransferPoint>();
		for(Route route : routes){
			points.add(route.getEndPoint());
		}
		return points;
	}
	
	/**
	 * この移動手段に含まれるチェックポイントをすべて取得するメソッド．
	 * @param order チェックポイントを取得する移動手段の位置．
	 * @return チェックポイントのリスト．
	 */
	public ArrayList<CheckPoint> getCheckPoints(){
		ArrayList<CheckPoint> points = new ArrayList<CheckPoint>();
		for(Route route : routes){
			points.addAll(route.getCheckPoints());
		}
		return points;
	}
	
	public String toString(){
		String routesString = "[";
		for(Route r : routes){
			routesString += r.toString() + ", ";
		}
		return "{mode:" + mode +
				", Route:" + routesString + "]}";
	}
	
	public long getFromid() {
		return fromid;
	}

	public long getToid() {
		return toid;
	}

	public int getOrder() {
		return order;
	}
	
	public HashMap<Integer, Long> getMinimumTravel(){
		int index = -1;
		long min = Long.MAX_VALUE;
		for(int i = 0 ; i < routes.size() ; i++){
			Route r = routes.get(i);
			if(r.getMinTravelTime() < min){
				min = r.getMinTravelTime();
				index = i;
			}
		}
		HashMap<Integer, Long> result = new HashMap<Integer, Long>();
		result.put(index, min);
		return result;
	}
	
	public HashMap<Integer, Long> getMaximumTravel(){
		int index = -1;
		long max = Long.MIN_VALUE;
		for(int i = 0 ; i < routes.size() ; i++){
			Route r = routes.get(i);
			if(max < r.getMaxTravelTime()){
				max = r.getMaxTravelTime();
				index = i;
			}
		}
		HashMap<Integer, Long> result = new HashMap<Integer, Long>();
		result.put(index, max);
		return result;
	}
}
