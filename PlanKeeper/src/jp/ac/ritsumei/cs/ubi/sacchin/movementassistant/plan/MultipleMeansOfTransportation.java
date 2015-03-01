/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * 移動手段の組み合わせを表現するクラス．
 * @author sacchin
 *
 */
public class MultipleMeansOfTransportation {
	/**
	 * この移動手段の組み合わせの名前．
	 */
	private String transportationNames;
	
	/**
	 * 出発地点となる立ち寄りポイントID．
	 */
	private long fromid;
	
	/**
	 * 到着地点となる立ち寄りポイントID．
	 */
	private long toid;
	
	/**
	 * 単一の移動手段のリスト．
	 * これが移動手段の組み合わせとなる．
	 */
	private ArrayList<SingleMeansOfTransportation> transportations;
	
	/**
	 * Factoryメソッド．
	 * @param multipleMeansOfTransportation 移動手段の組み合わせのJSONArray
	 * @param fromid 出発地点となる立ち寄りポイントID．
	 * @param toid 出発地点となる立ち寄りポイントID．
	 * @param transportationNames この移動手段の組み合わせの名前．
	 * @return
	 */
	public static MultipleMeansOfTransportation createMultipleMeansOfTransportation(
			JSONArray multipleMeansOfTransportation, long fromid, long toid, String transportationNames){
		if(multipleMeansOfTransportation == null){
			return null;
		}
		try {
			return new MultipleMeansOfTransportation(multipleMeansOfTransportation, 
					fromid, toid, transportationNames);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * コンストラクタ．
	 * @param multipleMeansOfTransportation 移動手段の組み合わせのJSONArray
	 * @param fromid 出発地点となる立ち寄りポイントID．
	 * @param toid 出発地点となる立ち寄りポイントID．
	 * @param transportationNames この移動手段の組み合わせの名前．
	 * @throws JSONException
	 */
	public MultipleMeansOfTransportation(JSONArray multipleMeansOfTransportation, 
			long fromid, long toid, String transportationNames) throws JSONException{
		this.transportationNames = transportationNames;
		this.fromid = fromid;
		this.toid = toid;
		this.transportations = new ArrayList<SingleMeansOfTransportation>();
		for(int i = 0 ; i < multipleMeansOfTransportation.length() ; i++){
			SingleMeansOfTransportation smot = SingleMeansOfTransportation.createSingleMeansOfTransportation(
					i, multipleMeansOfTransportation.getJSONObject(i), transportationNames, fromid, toid);
			if(smot != null){
				transportations.add(smot);
			}
		}
	}
	
	/**
	 * 移動手段の組み合わせに含まれる乗り換えポイントを重複なく取得するメソッド．
	 * @return 乗り換えポイントのリスト．
	 */
	public ArrayList<TransferPoint> getAllTransferPoints(){
		ArrayList<TransferPoint> points = new ArrayList<TransferPoint>();
		if(transportations.isEmpty() || transportations.size() == 1){
			return points;
		}
		
		int size = transportations.size() - 1;	//最後のEndoPointは立ち寄りポイントのため除外．
		for(int i = 0 ; i < size ; i++){
			SingleMeansOfTransportation smot = transportations.get(i);
			points.addAll(smot.getAllTransferPoints());
		}
		return points;
	}
	
	/**
	 * order番目に位置する移動手段に含まれるチェックポイントをすべて取得するメソッド．
	 * @param order チェックポイントを取得する移動手段の位置．
	 * @return チェックポイントのリスト．
	 */
	public ArrayList<CheckPoint> getCheckPoints(int order){
		ArrayList<CheckPoint> points = new ArrayList<CheckPoint>();
		if(transportations.isEmpty() || order < 0 || transportations.size() - 1 <= order){
			return points;
		}
		SingleMeansOfTransportation smot = transportations.get(order);
		points.addAll(smot.getCheckPoints());
		return points;
	}
	
	public String toString(){
		String smotString = "[";
		for(SingleMeansOfTransportation smot : transportations){
			smotString += smot.toString() + ", ";
		}
		return smotString + "]";
	}
	
	public long getFromid() {
		return fromid;
	}

	public long getToid() {
		return toid;
	}

	public String getTransportationNames() {
		return transportationNames;
	}
	
	public ArrayList<HashMap<Integer, Long>> getMinimumTravel(){
		ArrayList<HashMap<Integer, Long>> result = new ArrayList<HashMap<Integer,Long>>();
		for(int i = 0 ; i < transportations.size() ; i++){
			SingleMeansOfTransportation smot = transportations.get(i);
			result.add(smot.getMinimumTravel());
		}
		return result;
	}
	
	public ArrayList<HashMap<Integer, Long>> getMaximumTravel(){
		ArrayList<HashMap<Integer, Long>> result = new ArrayList<HashMap<Integer,Long>>();
		for(int i = 0 ; i < transportations.size() ; i++){
			SingleMeansOfTransportation smot = transportations.get(i);
			result.add(smot.getMaximumTravel());
		}
		return result;
	}
}
