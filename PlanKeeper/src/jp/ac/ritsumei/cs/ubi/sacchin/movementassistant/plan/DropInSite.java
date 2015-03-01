/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.GeoPointUtils;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.MyGeoPoint;

/**
 * 立ち寄りポイントを表現するクラス．
 * @author sacchin
 *
 */
public class DropInSite extends MyGeoPoint{
	/**
	 * この立ち寄りポイントのラベル．
	 */
	private String name;
	
	/**
	 * たくちゃんがつけた固有のID．
	 */
	private long siteId;
	
	/**
	 * この立ち寄りポイントに直近で滞在した時刻．
	 */
	private JSONArray stayTimes = null;
	
	/**
	 * この立ち寄りポイントへの滞在回数．
	 */
	private int stayCount;
	
	/**
	 * この立ち寄りポイントの平均滞在時間（分）．
	 */
	private int stayAverage;
	
	/**
	 * この立ち寄りポイントを円で表現した場合の半径．
	 */
	private int radius;
	
	/**
	 * この立ち寄りポイントがノイズであるがどうか．
	 * 現在は，巨大すぎる場合にtrueとなる．
	 */
	private boolean mayBeNoise = false;
	
	/**
	 * 推定されたこの立ち寄りポイントへの到着時間．
	 */
	private long estimatedArrivalTime = 0;
	
	/**
	 * 推定到着時間を算出した際の移動手段．
	 */
	private String meansOfTransportation = "";

	/**
	 * この立ち寄りポイントから過去に遷移した立ち寄りポイントのIDのリスト．
	 */
	private ArrayList<TransitionBetweenDropInSite> transitions = null;

	/**
	 * Factoryメソッド．
	 * @param object JSON形式の立ち寄りポイント．
	 * @return このクラスのインスタンス．
	 */
	public static DropInSite createDropInSite(JSONObject object){
		long siteId = -1;
		int count = -1, average = -1, radius = -1;
		double mostLowLat = -1, mostLowLng = -1, mostHighLat = -1, mostHighLng = -1;
		JSONArray transitions = null, stayTimes = null;
		boolean mayBeNoise = false;
		String name = "";

		try {
			siteId = object.getLong("id");
			count = object.getInt("count");
			average = object.getInt("average");
			radius = object.getInt("radius");
			mostLowLat = object.getDouble("minLat");
			mostLowLng = object.getDouble("minLng");
			mostHighLat = object.getDouble("maxLat");
			mostHighLng = object.getDouble("maxLng");
			transitions = object.getJSONArray("transition");
			stayTimes = object.getJSONArray("transition");
			mayBeNoise = object.getBoolean("isNoise");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		try {
			name = object.getString("name");
			return new DropInSite(mostLowLat, mostLowLng, mostHighLat, mostHighLng,
					count, average, radius, siteId, transitions, stayTimes, mayBeNoise, name);
		} catch (JSONException e) {
			e.printStackTrace();
			return new DropInSite(mostLowLat, mostLowLng, mostHighLat, mostHighLng,
					count, average, radius, siteId, transitions, stayTimes, mayBeNoise, String.valueOf(siteId));
		}
	}
	
	/**
	 * コンストラクタ．
	 * @param mostLowLat 
	 * @param mostLowLng 
	 * @param mostHighLat 
	 * @param mostHighLng 
	 * @param stayCount この立ち寄りポイントへの滞在回数．
	 * @param stayAverage この立ち寄りポイントの平均滞在時間（分）．
	 * @param radius この立ち寄りポイントを円で表現した場合の半径．
	 * @param siteId たくちゃんがつけた固有のID．
	 * @param transitions この立ち寄りポイントから過去に遷移した立ち寄りポイントのIDのリスト．
	 * @param stayTimes この立ち寄りポイントに直近で滞在した時刻．
	 * @param mayBeNoise この立ち寄りポイントがノイズであるがどうか．現在は，巨大すぎる場合にtrueとなる．
	 * @param name この立ち寄りポイントのラベル．
	 */
	public DropInSite(double mostLowLat, double mostLowLng, 
			double mostHighLat, double mostHighLng, int stayCount, int stayAverage, int radius, 
			long siteId, JSONArray transitions, JSONArray stayTimes, boolean mayBeNoise, String name){
		super(mostLowLat, mostLowLng, mostHighLat, mostHighLng);
		this.name = name;
		this.stayCount = stayCount;
		this.stayAverage = stayAverage;
		this.siteId = siteId;
		this.transitions = TransitionBetweenDropInSite.createList(transitions);
		this.stayTimes = stayTimes;
		this.radius = radius;
		this.mayBeNoise = mayBeNoise;
	}

	public ArrayList<TransitionBetweenDropInSite> getTransitions() {
		return transitions;
	}
	public long getSiteId() {
		return siteId;
	}
	public int getStayCount() {
		return stayCount;
	}
	public int getStayAverage() {
		return stayAverage;
	}
	public boolean mayBeNoise() {
		return mayBeNoise;
	}
	public String getName() {
		return name;
	}
	public int getRadius() {
		return radius;
	}
	public JSONArray getStayTimes() {
		return stayTimes;
	}
	
	public static JSONObject toJSONObject(int id, String name, int stayCount, long stayAverage,
			double lat, double lng, int radius, boolean maybenoise, String transition, String stayTimes, int devid){
		JSONObject jObject = new JSONObject();
		try {
			if(radius < 10){
				radius = 10;
			}
			double[] g = GeoPointUtils.calculateRect(lat, lng, radius);
			jObject.put("id", id);
			jObject.put("name", name);
			jObject.put("count", stayCount);
			jObject.put("average", stayAverage);
			jObject.put("minLat", g[0]);
			jObject.put("minLng", g[1]);
			jObject.put("maxLat", g[2]);
			jObject.put("maxLng", g[3]);
			jObject.put("radius", radius);
			JSONArray transitionJSON = new JSONArray(transition);
			jObject.put("transition", transitionJSON);
			JSONArray stayTimesJSON = new JSONArray(stayTimes);
			jObject.put("stayTimes", stayTimesJSON);
			jObject.put("isNoise", maybenoise);
			return jObject;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public long getEstimatedArrivalTime() {
		return estimatedArrivalTime;
	}

	public void setEstimatedArrivalTime(long estimatedArrivalTime) {
		this.estimatedArrivalTime = estimatedArrivalTime;
	}

	public String getMeansOfTransportation() {
		return meansOfTransportation;
	}

	public void setMeansOfTransportation(String meansOfTransportation) {
		this.meansOfTransportation = meansOfTransportation;
	}
}