/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.direction;

import java.util.List;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.MyGeoPoint;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 要所から要所までのルート情報
 * @author sacchin
 *
 */
public class StepObject {
	/**
	 * 推定移動時間()
	 */
	JSONObject duration = null;
	
	/**
	 * Stepの始まりの緯度経度
	 */
	MyGeoPoint startLocation = null;
	
	/**
	 * Stepの終わりの緯度経度
	 */
	MyGeoPoint endLocation = null;
	
	/**
	 * エンコードされたpolyline
	 */
	String encodedPolyline = "";
	
	/**
	 * 要所と要所間の緯度経度の軌跡
	 */
	List<MyGeoPoint> polyline = null;
	
	/**
	 * 車，徒歩，自転車(アメリカのみ)のいずれか
	 */
	String travelMode = "";
	
	/**
	 * Google Directionsが提供している案内表示用のテキスト
	 */
	String htmlInstructions = "";
	
	/**
	/**
	 * Factoryメソッド．
	 * JSONObjectからStepObjectを生成する．
	 * @param step JSON形式のルート情報．
	 * @return StepObject
	 */
	public static StepObject create(JSONObject step){
		try {
			return new StepObject(step);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * コンストラクタ．
	 * @param step JSON形式のルート情報．
	 * @throws JSONException
	 */
	public StepObject(JSONObject step) throws JSONException{
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
	
	/**
	 * JSON形式のルート情報を生成する．
	 * @return ルート情報
	 */
	public JSONObject toJSONObject(){
		JSONObject re = new JSONObject();
		try {
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

