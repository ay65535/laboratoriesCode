/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.drivingdirection;

import org.json.JSONException;
import org.json.JSONObject;

import jp.ac.ritsumei.cs.ubi.createplan.utils.MyGeoPoint;


public class StationObject {
	private String name;
	private String prev;
	private String next;
	private MyGeoPoint latlng;
	private String distance;
	private int postal;
	private String prefecture;
	private String line;
	
	public static StationObject create(JSONObject station){
		try {
			return new StationObject(station);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public StationObject(JSONObject station) throws JSONException{
		this.name = station.getString("name");
		this.prev = station.getString("prev");
		this.next = station.getString("next");
		this.distance = station.getString("distance");
		this.postal = station.getInt("postal");
		this.prefecture = station.getString("prefecture");
		this.line = station.getString("line");
		double lng = station.getDouble("x");
		double lat = station.getDouble("y");
		this.latlng = new MyGeoPoint(0, lat, lng, 0, 0);
	}
	
	public JSONObject toJSONObject(){
		JSONObject re = new JSONObject();
		try {
			re.put("name", name);
			re.put("prev", prev);
			re.put("next", next);
			re.put("distance", distance);
			re.put("postal", postal);
			re.put("prefecture", prefecture);
			re.put("line", line);
			re.put("x", latlng.lng);
			re.put("y", latlng.lat);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return re;
	}

	public String getName() {
		return name;
	}

	public String getPrev() {
		return prev;
	}

	public String getNext() {
		return next;
	}

	public MyGeoPoint getLatlng() {
		return latlng;
	}

	public String getDistance() {
		return distance;
	}

	public int getPostal() {
		return postal;
	}

	public String getPrefecture() {
		return prefecture;
	}

	public String getLine() {
		return line;
	}
}
