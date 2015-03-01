/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.direction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ある地点からある地点までのルート情報を保持するクラス
 * @author sacchin
 *
 */
public class StepsArray {
	/**
	 * 要所間ルートのリスト
	 */
	private ArrayList<StepObject> steps;
	
	/**
	 * 出発時刻
	 */
	private long departure;
	
	/**
	 * 到着時刻
	 */
	private long arrival;
	
	/**
	 * 到着時刻のテキスト
	 */
	private String arrivalStr = "";
	
	/**
	 * コンストラクタ
	 * @param steps
	 * @param departure
	 */
	public StepsArray(ArrayList<StepObject> steps, long departure){
		this.steps = steps;
		this.departure = departure;
		try {
			this.arrival = departure + accumulate(steps) * 1000;
			this.arrivalStr = new Date(arrival).toString();
		} catch (JSONException e) {
			this.arrival = 0;
		}
	}
	
	/**
	 * コンストラクタ．
	 * @param json JSON形式のルート情報．
	 * @throws JSONException
	 */
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
			this.arrivalStr = new Date(arrival).toString();
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
	
	/**
	 * すべての推定移動時間の和を計算する．
	 * @param steps 全ルート情報
	 * @return 推定所要時間()
	 * @throws JSONException
	 */
	public int accumulate(List<StepObject> steps) throws JSONException {
		int sumSec = 0;
		for(StepObject so : steps){
			sumSec += so.getDuration().getInt("value");
		}
		return sumSec;
	}

	public ArrayList<StepObject> getSteps() {
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

	public String getArrivalStr() {
		return arrivalStr;
	}
}
