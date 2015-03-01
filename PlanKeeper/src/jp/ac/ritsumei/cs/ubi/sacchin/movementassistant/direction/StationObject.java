/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.direction;

import org.json.JSONException;
import org.json.JSONObject;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.MyGeoPoint;

/**
 * 駅の情報を保持するクラス
 * @author sacchin
 *
 */
public class StationObject {
	/**
	 * 駅名
	 */
	private String name;
	
	/**
	 * 次に停車する駅名
	 * Yahoo乗り換え案内をスクレイピングした場合のみ
	 */
	private String prev;
	
	/**
	 * 前に停車していた駅名
	 * Yahoo乗り換え案内をスクレイピングした場合のみ
	 */
	private String next;
	
	/**
	 * 駅の緯度経度
	 */
	private MyGeoPoint latlng;
	
	/**
	 * 現在地からの直線距離(m)
	 */
	private String distance;
	
	/**
	 * 路線名
	 */
	private String line;
	
	/**
	 * Factoryメソッド．
	 * JSONObjectからStationObjectを生成する．
	 * @param station JSON形式の駅情報．
	 * @return StationObject
	 */
	public static StationObject create(JSONObject station){
		try {
			return new StationObject(station);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * コンストラクタ．
	 * @param station JSON形式の駅情報．
	 * @throws JSONException
	 */
	private StationObject(JSONObject station) throws JSONException{
		this.name = station.getString("name");
		this.prev = station.getString("prev");
		this.next = station.getString("next");
		this.distance = station.getString("distance");
		this.line = station.getString("line");
		double lng = station.getDouble("x");
		double lat = station.getDouble("y");
		this.latlng = new MyGeoPoint(0, lat, lng, 0, 0);
	}
	
	/**
	 * JSON形式の駅情報を生成する．
	 * @return JSON形式の駅情報
	 */
	public JSONObject toJSONObject(){
		JSONObject re = new JSONObject();
		try {
			re.put("name", name);
			re.put("prev", prev);
			re.put("next", next);
			re.put("distance", distance);
			re.put("line", line);
			re.put("x", latlng.lng);
			re.put("y", latlng.lat);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return re;
	}

	/**
	 * 駅名
	 * @return　駅名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 次に停車する駅名を返す．
	 * @return 次に停車する駅名．
	 */
	public String getPrev() {
		return prev;
	}

	/**
	 * 前に停車した駅名を返す．
	 * @return 前に停車した駅名
	 */
	public String getNext() {
		return next;
	}

	/**
	 * 駅の緯度経度を返す．
	 * @return 緯度経度
	 */
	public MyGeoPoint getLatlng() {
		return latlng;
	}

	/**
	 * 駅までの直線距離(m)を返す．
	 * @return 直線距離(m)
	 */
	public String getDistance() {
		return distance;
	}

	/**
	 * 路線名を返す．
	 * @return 路線名
	 */
	public String getLine() {
		return line;
	}
}
