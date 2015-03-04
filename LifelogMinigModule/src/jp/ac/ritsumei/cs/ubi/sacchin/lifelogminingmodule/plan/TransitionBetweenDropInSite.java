package jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.plan;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TransitionBetweenDropInSite {
	private long toID;
	
	private int transitionCount;
	
	private Timestamp[][] travelTimes;
	
	public static ArrayList<TransitionBetweenDropInSite> createList(
			JSONArray transitions){
		ArrayList<TransitionBetweenDropInSite> re = 
				new ArrayList<TransitionBetweenDropInSite>();
		
		final int SIZE = transitions.length();
		for(int i = 0 ; i < SIZE ; i++){
			try {
				JSONObject o = transitions.getJSONObject(i);
				TransitionBetweenDropInSite tbls = createTransitionBetweenLongStay(o);
				if(tbls == null){
					continue;
				}
				re.add(tbls);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return re;
	}
	
	public static TransitionBetweenDropInSite createTransitionBetweenLongStay(
			JSONObject object){
		try {
			return new TransitionBetweenDropInSite(object);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * {"to":7794,"count":1,"traveltimes":[{"start":"2013-01-01 00:00:00","end":"2013-01-01 00:10:00"}]}
	 * @param object
	 * @throws JSONException
	 */
	public TransitionBetweenDropInSite(JSONObject object) throws JSONException{
		this.toID = object.getInt("to");
		this.transitionCount = object.getInt("count");
		JSONArray times = object.getJSONArray("travelTimes");
		this.travelTimes = new Timestamp[times.length()][2];
		for(int i = 0 ; i < times.length() ; i++){
			JSONObject temp = times.getJSONObject(i);
			travelTimes[i][0] = Timestamp.valueOf(temp.getString("begin"));
			travelTimes[i][1] = Timestamp.valueOf(temp.getString("end"));
		}
	}

	public long getToID() {
		return toID;
	}

	public int getTransitionCount() {
		return transitionCount;
	}

	public Timestamp[][] getTravelTimes() {
		return travelTimes;
	}
}
