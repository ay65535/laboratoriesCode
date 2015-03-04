/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.drivingdirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LegObject {
	JSONObject distance = null;
	JSONObject duration = null;
	String startAddress = "";
	JSONObject startLocation = null;
	String endAddress = "";
	JSONObject endLocation = null;
	JSONArray steps = null;
	JSONArray viaWaypoint = null;
	
	public static LegObject create(JSONObject leg){
		try {
			return new LegObject(leg);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public LegObject(JSONObject leg) throws JSONException{
		distance = leg.getJSONObject("distance");
		duration = leg.getJSONObject("duration");
		startAddress = leg.getString("start_address");
		startLocation = leg.getJSONObject("start_location");
		endAddress = leg.getString("end_address");
		endLocation = leg.getJSONObject("end_location");
		steps = leg.getJSONArray("steps");
		viaWaypoint = leg.getJSONArray("via_waypoint");
	}

	public JSONObject getDistance() {
		return distance;
	}

	public JSONObject getDuration() {
		return duration;
	}

	public String getStartAddress() {
		return startAddress;
	}

	public JSONObject getStartLocation() {
		return startLocation;
	}

	public String getEndAddress() {
		return endAddress;
	}

	public JSONObject getEndLocation() {
		return endLocation;
	}

	public JSONArray getSteps() {
		return steps;
	}

	public JSONArray getViaWaypoint() {
		return viaWaypoint;
	}
	
}
