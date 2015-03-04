package jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.plan;

import java.util.ArrayList;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DropInSite extends MyGeoPoint{
	private String name;
	private long siteId;
	private int stayCount;
	private int stayAverage;
	private int radius;
	private boolean mayBeNoise = false;
	
	private ArrayList<TransitionBetweenDropInSite> transitions = null;
	private JSONArray stayTimes = null;
	
	public static DropInSite createDropInSite(JSONObject object){
		DropInSite site;
		try {
			long siteId = object.getLong("id");
			int count = object.getInt("count");
			int average = object.getInt("average");
			int radius = object.getInt("radius");
			double mostLowLat = object.getDouble("minLat");
			double mostLowLng = object.getDouble("minLng");
			double mostHighLat = object.getDouble("maxLat");
			double mostHighLng = object.getDouble("maxLng");
			JSONArray transitions = object.getJSONArray("transition");
			JSONArray stayTimes = object.getJSONArray("transition");
			boolean mayBeNoise = object.getBoolean("isNoise");
			site = new DropInSite(mostLowLat, mostLowLng, mostHighLat, mostHighLng, count, average, 
					radius, siteId, transitions, stayTimes, mayBeNoise);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		try {
			String name = object.getString("name");
			site.setName(name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return site;
	}
	
	public DropInSite(double mostLowLat, double mostLowLng, double mostHighLat, double mostHighLng, 
			int stayCount, int stayAverage, int radius, long siteId, JSONArray transitions, 
			JSONArray stayTimes, boolean mayBeNoise){
		super(mostLowLat, mostLowLng, mostHighLat, mostHighLng);
		this.name = String.valueOf(siteId);
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
	
	public static JSONObject create(int id, String name, double lat, double lng, int radius, int stayCount, 
			long stayAverage, boolean maybenoise, String transition, String stayTimes, int devid){
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

	public void setName(String name) {
		this.name = name;
	}
}