/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TransitionBetweenDropInSite {
	private long toID;
	
	private int transitionCount;
	
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
	
	public TransitionBetweenDropInSite(JSONObject object) throws JSONException{
		toID = object.getInt("to");
		transitionCount = object.getInt("count");
	}

	public long getToID() {
		return toID;
	}

	public int getTransitionCount() {
		return transitionCount;
	}
}
