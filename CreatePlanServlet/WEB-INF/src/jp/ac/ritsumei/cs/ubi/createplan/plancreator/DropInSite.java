/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.plancreator;

import jp.ac.ritsumei.cs.ubi.createplan.utils.GeoPointUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DropInSite{
	public static JSONObject create(int id, String name, int stayCount, long stayAverage,
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
}