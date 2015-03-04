/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.drivingdirection;

import java.util.List;

import jp.ac.ritsumei.cs.ubi.createplan.utils.MyGeoPoint;

import org.json.JSONException;
import org.json.JSONObject;

public class StepObject {
	JSONObject distance = null;
	JSONObject duration = null;
	MyGeoPoint startLocation = null;
	MyGeoPoint endLocation = null;
	String encodedPolyline = "";
	List<MyGeoPoint> polyline = null;
	String travelMode = "";
	String htmlInstructions = "";
	
	public static StepObject create(JSONObject step){
		try {
			return new StepObject(step);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public StepObject(JSONObject step) throws JSONException{
		distance = step.getJSONObject("distance");
		duration = step.getJSONObject("duration");
		startLocation = new MyGeoPoint(0, 
				step.getJSONObject("start_location").getDouble("lat"), 
				step.getJSONObject("start_location").getDouble("lng"), 0, 0);
		endLocation = new MyGeoPoint(0, 
				step.getJSONObject("end_location").getDouble("lat"), 
				step.getJSONObject("end_location").getDouble("lng"), 0, 0);
		
		encodedPolyline = step.getJSONObject("polyline").getString("points");
		polyline = PolylineDecoder.decodePoly(encodedPolyline);
		
		travelMode = step.getString("travel_mode");
		htmlInstructions = step.getString("html_instructions");
	}
	
	public JSONObject toJSONObject(){
		JSONObject re = new JSONObject();
		try {
			re.put("distance", distance);
			re.put("duration", duration);
			JSONObject sl = new JSONObject();
			sl.put("lat", startLocation.getLatitude());
			sl.put("lng", startLocation.getLongtitude());
			re.put("start_location", sl);
			
			JSONObject el = new JSONObject();
			el.put("lat", endLocation.getLatitude());
			el.put("lng", endLocation.getLongtitude());
			re.put("end_location", el);
			
			JSONObject pl = new JSONObject();
			pl.put("points", encodedPolyline);
			re.put("polyline", pl);
			
			re.put("travel_mode", travelMode);
			re.put("html_instructions", htmlInstructions);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return re;
	}

	public JSONObject getDistance() {
		return distance;
	}

	public JSONObject getDuration() {
		return duration;
	}

	public String getHtmlInstructions() {
		return htmlInstructions;
	}

	public String getTravelMode() {
		return travelMode;
	}

	public List<MyGeoPoint> getPolyline() {
		return polyline;
	}

	public MyGeoPoint getStartLocation() {
		return startLocation;
	}

	public MyGeoPoint getEndLocation() {
		return endLocation;
	}
}

