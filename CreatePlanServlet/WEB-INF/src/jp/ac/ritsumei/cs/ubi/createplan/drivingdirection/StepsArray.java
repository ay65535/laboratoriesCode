/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.drivingdirection;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StepsArray {
	private List<StepObject> steps;
	private long departure;
	private long arrival;
	
	public StepsArray(List<StepObject> steps, long departure){
		this.steps = steps;
		this.departure = departure;
		try {
			this.arrival = departure + accumulate(steps) * 1000;
		} catch (JSONException e) {
			this.arrival = 0;
		}
	}
	
	public StepsArray(JSONObject json){
		try {
			this.steps = new ArrayList<StepObject>();
			JSONArray jArray =  json.getJSONArray("steps");
			for(int i = 0 ; i < jArray.length() ; i++){
				StepObject so = StepObject.create(jArray.getJSONObject(i));
				if(so != null){
					steps.add(so);
				}
			}
			this.departure = json.getLong("departure");
			this.arrival = json.getLong("arrival");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject toJSON(){
		try {
			JSONObject jObject = new JSONObject();
			jObject.put("arrival", arrival);
			jObject.put("departure", departure);
			JSONArray jArray = new JSONArray();
			for(int i = 0 ; i < steps.size() ; i++){
				jArray.put(steps.get(i).toJSONObject());
			}
			jObject.put("steps", jArray);
			return jObject;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int accumulate(List<StepObject> steps) throws JSONException {
		int sumSec = 0;
		for(StepObject so : steps){
			sumSec += so.getDuration().getInt("value");
		}
		return sumSec;
	}

	public List<StepObject> getSteps() {
		return steps;
	}

	public long getDeparture() {
		return departure;
	}

	public void setDeparture(long departure) {
		this.departure = departure;
	}

	public long getArrival() {
		return arrival;
	}

	public void setArrival(long arrival) {
		this.arrival = arrival;
	}
}
