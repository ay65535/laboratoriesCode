/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * ある立ち寄りポイントからある立ち寄りポイントまでの移動実績．
 * 複数の移動手段の組み合わせを持つ．
 * 例：自宅-クリコア間の移動で「徒歩バス徒歩」と「徒歩電車徒歩」と「徒歩」を持つ．
 * @author sacchin
 *
 */
public class MovementResult {

	/**
	 * 出発地点となる立ち寄りポイントID．
	 */
	private long fromid;

	/**
	 * 到着地点となる立ち寄りポイントID．
	 */
	private long toid;


	/**
	 * 移動手段の組み合わせのマップ．
	 * 移動手段組み合わせ名がkey．
	 * 例：「WALKBUSWALK」
	 */
	private HashMap<String, MultipleMeansOfTransportation> toDestinations;

	/**
	 * Factoryメソッド．
	 * @param movementResult 移動実績のJSONArray
	 * @param fromto 立ち寄りポイントID．例：「7105to7106」
	 * @return
	 */
	public static MovementResult createMovementResult(JSONArray movementResult, String fromto){
		if(movementResult == null || fromto == null){
			return null;
		}
		try {
			long fromid = Long.parseLong(fromto.split("to")[0]);
			long toid = Long.parseLong(fromto.split("to")[1]);
			return new MovementResult(movementResult, fromid, toid);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * コンストラクタ．
	 * @param movementResult 移動実績のJSONArray
	 * @param fromid 出発地点の立ち寄りポイントID．
	 * @param toid 到着地点の立ち寄りポイントID．
	 */
	public MovementResult(JSONArray movementResult, long fromid, long toid){
		this.fromid = fromid;
		this.toid = toid;
		if(toDestinations == null){
			toDestinations = new HashMap<String, MultipleMeansOfTransportation>();
		}else{
			toDestinations.clear();
		}

		try {
			for(int i = 0 ; i < movementResult.length() ; i++){
				JSONObject multipleMeansOfTransportation = movementResult.getJSONObject(i);
				
				JSONArray n = multipleMeansOfTransportation.names();
				for(int j = 0 ; j < n.length() ; j++){
					Log.v(i + "'s transportationNames", n.getString(j));
				}
				String transportationNames = (String) multipleMeansOfTransportation.keys().next();
				
				MultipleMeansOfTransportation mmot = 
						MultipleMeansOfTransportation.createMultipleMeansOfTransportation(
						multipleMeansOfTransportation.getJSONArray(transportationNames), 
						fromid, toid, transportationNames);
				if(mmot != null){
					this.toDestinations.put(transportationNames, mmot);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 移動実績に含まれる乗り換えポイントを重複なく取得するメソッド．
	 * @return 乗り換えポイントのリスト．
	 */
	public ArrayList<TransferPoint> getAllTransferPoint(){
		ArrayList<TransferPoint> points = new ArrayList<TransferPoint>();
		for(String key : toDestinations.keySet()){
			MultipleMeansOfTransportation mmot = toDestinations.get(key);
			points.addAll(mmot.getAllTransferPoints());
		}

		return points;
	}

	/**
	 * 移動手段の組み合わせにおいて、order番目に位置する移動手段に含まれるチェックポイントをすべて取得するメソッド．
	 * @param order チェックポイントを取得する移動手段の位置．
	 * @return チェックポイントのリスト．
	 */
	public ArrayList<CheckPoint> getCheckPoints(int order){
		ArrayList<CheckPoint> points = new ArrayList<CheckPoint>();
		for(String key : toDestinations.keySet()){
			MultipleMeansOfTransportation mmot = toDestinations.get(key);
			points.addAll(mmot.getCheckPoints(order));
		}
		Log.v("MovementResult.getCheckPoints", "CheckPoint = " + points.size());

		return points;
	}

	public long getFromid() {
		return fromid;
	}

	public long getToID() {
		return toid;
	}

	public HashMap<String, ArrayList<HashMap<Integer, Long>>> getMinimumTravel(){
		long min = Long.MAX_VALUE;
		String minimumTravelKey = null;
		for(String key : toDestinations.keySet()){
			MultipleMeansOfTransportation mmot = toDestinations.get(key);
			ArrayList<HashMap<Integer, Long>> travel = mmot.getMinimumTravel();
			long travelTime = calcTravelTime(travel);
			if(travelTime < min){
				min = travelTime;
				minimumTravelKey = key;
			}
		}
		HashMap<String, ArrayList<HashMap<Integer, Long>>> result = 
				new HashMap<String, ArrayList<HashMap<Integer,Long>>>();
		result.put(minimumTravelKey, toDestinations.get(minimumTravelKey).getMinimumTravel());
		return result;
	}
	
	public HashMap<String, ArrayList<HashMap<Integer, Long>>> getMaximumTravel(){
		long max = Long.MIN_VALUE;
		String maximumTravelKey = null;
		for(String key : toDestinations.keySet()){
			MultipleMeansOfTransportation mmot = toDestinations.get(key);
			ArrayList<HashMap<Integer, Long>> travel = mmot.getMaximumTravel();
			long travelTime = calcTravelTime(travel);
			if(max < travelTime){
				max = travelTime;
				maximumTravelKey = key;
			}
		}
		HashMap<String, ArrayList<HashMap<Integer, Long>>> result = 
				new HashMap<String, ArrayList<HashMap<Integer,Long>>>();
		result.put(maximumTravelKey, toDestinations.get(maximumTravelKey).getMinimumTravel());
		return result;
	}

	public static long calcTravelTime(ArrayList<HashMap<Integer, Long>> travel){
		long sum = 0;
		if(travel.isEmpty()){
			return sum;
		}
		for(HashMap<Integer, Long> temp : travel){
			for(Long t : temp.values()){
				sum += t.longValue();
			}
		}
		return sum;
	}

	public HashMap<String, MultipleMeansOfTransportation> getMeansOfTransportations() {
		return toDestinations;
	}

}
