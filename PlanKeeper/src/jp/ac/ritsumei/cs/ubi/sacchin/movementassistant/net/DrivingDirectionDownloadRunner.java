/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.net;

import java.net.URISyntaxException;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.EventNotificationActivity;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.direction.StationObject;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.direction.StepsArray;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.HttpConnector;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.MyGeoPoint;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.URLConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 非日常の移動プランをダウンロードするクラス．
 * Google Directions apiと乗り換え案内の結果をマージしたものです．
 * 乗り換え案内は，定期的に異なるものを使用しています．
 * @author sacchin
 *
 */
public class DrivingDirectionDownloadRunner implements Runnable{
	/**
	 * サーブレットのURL
	 */
	private static final String DOWNLOD_URL = URLConstants.SERVER_URL + "?pattern=notdaily&";
	
	/**
	 * 出発地点
	 */
	private MyGeoPoint origin;

	/**
	 * 目的地
	 */
	private MyGeoPoint destination;
	
	/**
	 * 現在地の最寄り駅
	 */
	private StationObject stationsCloseToCurrent;

	/**
	 * 目的地の最寄り駅
	 */
	private StationObject stationsCloseToDestination;
	
	/**
	 * 現在地から目的地までの車でのルート
	 */
	private StepsArray currentToDestinationByCar;
	
	/**
	 * 現在地から目的地までの徒歩でのルート
	 */
	private StepsArray currentToDestinationByWalk;
	
	/**
	 * 現在地から目的地までの自転車でのルート
	 */
	private StepsArray currentToDestinationByBicycle;
	
	/**
	 * 現在地から最寄り駅までの車でのルート
	 */
	private StepsArray currentToStationByCar;
	
	/**
	 * 現在地から最寄り駅までの徒歩でのルート
	 */
	private StepsArray currentToStationByWalk;
	
	/**
	 * 現在地から最寄り駅までの自転車でのルート
	 */
	private StepsArray currentToStationByBicycle;
	
	/**
	 * 最寄り駅から目的地までの車でのルート
	 */
	private StepsArray stationToDestinationByCar;
	
	/**
	 * 最寄り駅から目的地までの徒歩でのルート
	 */
	private StepsArray stationToDestinationByWalk;
	
	/**
	 * 最寄り駅から目的地までの自転車でのルート
	 */
	private StepsArray stationToDestinationByBicycle;
	
	/**
	 * EventNotificationActivityのインスタンス．
	 */
	private EventNotificationActivity activity;
	
	/**
	 * ダウンロードが成功した場合，true
	 */
	private boolean isNotSuccess = false;
	
	/**
	 * コンストラクタ
	 * @param activity EventNotificationActivityのインスタンス
	 * @param origin 現在地の緯度経度
	 * @param destination 目的地の緯度経度
	 */
	public DrivingDirectionDownloadRunner(EventNotificationActivity activity, 
			MyGeoPoint origin, MyGeoPoint destination){
		this.origin = origin;
		this.destination = destination;
		this.activity = activity;
	}
	
	/**
	 * Google Directionsと乗り換え案内の結果をダウンロード．
	 * その後，JSON形式からStepsArrayに変換．
	 */
	public void download(){
		try{
			String jsonStr = HttpConnector.downloadDataForALine(DOWNLOD_URL + 
					parseToParameter(origin, destination));
			JSONObject jObject = new JSONObject(jsonStr);
			
			String status = jObject.getString("status");
			if(!"success".equals(status)){
				isNotSuccess = true;
				return;
			}

			jObject = jObject.getJSONObject("nondailyplan");
			
			stationsCloseToCurrent = StationObject.create(
					jObject.getJSONObject("stationsCloseToCurrent"));
			stationsCloseToDestination = StationObject.create(
					jObject.getJSONObject("stationsCloseToDestination"));
			
			currentToDestinationByCar = new StepsArray(jObject.getJSONObject("sctdbc"));
			currentToDestinationByWalk = new StepsArray(jObject.getJSONObject("sctdbw"));
			currentToDestinationByBicycle = new StepsArray(jObject.getJSONObject("sctdbb"));
			currentToStationByCar = new StepsArray(jObject.getJSONObject("sctsbc"));
			currentToStationByWalk = new StepsArray(jObject.getJSONObject("sctsbw"));
			currentToStationByBicycle = new StepsArray(jObject.getJSONObject("sctsbb"));
			stationToDestinationByCar = new StepsArray(jObject.getJSONObject("sstdbc"));
			stationToDestinationByWalk = new StepsArray(jObject.getJSONObject("sstdbw"));
			stationToDestinationByBicycle = new StepsArray(jObject.getJSONObject("sstdbb"));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ダウンロード終了後に呼び出される．
	 */
	@Override
	public void run() {
		activity.addDrivingDirectionOverlay(isNotSuccess);
	}

	/**
	 * URL用に出発地と目的地を変換．
	 * @param origin 出発地
	 * @param destination 目的地
	 * @return URL用のString
	 * @throws JSONException
	 */
	private String parseToParameter(MyGeoPoint origin, MyGeoPoint destination)
			throws JSONException {
		String params = "";
		params += "originLat=" + origin.getLatitude();
		params += "&originLng=" + origin.getLongtitude();
		params += "&destinationLat=" + destination.getLatitude();
		params += "&destinationLng=" + destination.getLongtitude();
		return params;
	}

	/**
	 * 出発地点を返す．
	 * @return 出発地点
	 */
	public MyGeoPoint getOrigin() {
		return origin;
	}

	/**
	 * 目的地を返す．
	 * @return 目的地
	 */
	public MyGeoPoint getDestination() {
		return destination;
	}

	/**
	 * 現在地の最寄り駅を返す．
	 * @return 現在地の最寄り駅インスタンス．
	 */
	public StationObject getStationsCloseToCurrent() {
		return stationsCloseToCurrent;
	}

	/**
	 * 目的地の最寄り駅を返す．
	 * @return 目的地の最寄り駅インスタンス．
	 */
	public StationObject getStationsCloseToDestination() {
		return stationsCloseToDestination;
	}

	/**
	 * 現在地から目的地までの車でのルートを返す．
	 * @return 現在地から目的地までの車でのルート情報インスタンス．
	 */
	public StepsArray getSctdbc() {
		return currentToDestinationByCar;
	}

	/**
	 * 現在地から目的地までの徒歩でのルートを返す．
	 * @return 現在地から目的地までの徒歩でのルート情報インスタンス．
	 */
	public StepsArray getSctdbw() {
		return currentToDestinationByWalk;
	}

	/**
	 * 現在地から目的地までの自転車でのルートを返す．
	 * @return 現在地から目的地までの自転車でのルート情報インスタンス．
	 */
	public StepsArray getSctdbb() {
		return currentToDestinationByBicycle;
	}

	/**
	 * 現在地から最寄り駅までの車でのルートを返す．
	 * @return 現在地から最寄り駅までの車でのルート情報インスタンス．
	 */
	public StepsArray getSctsbc() {
		return currentToStationByCar;
	}

	/**
	 * 現在地から最寄り駅までの徒歩でのルートを返す．
	 * @return 現在地から最寄り駅までの徒歩でのルート情報インスタンス．
	 */
	public StepsArray getSctsbw() {
		return currentToStationByWalk;
	}

	/**
	 * 現在地から最寄り駅までの自転車でのルートを返す．
	 * @return 現在地から最寄り駅までの自転車でのルート情報インスタンス．
	 */
	public StepsArray getSctsbb() {
		return currentToStationByBicycle;
	}

	/**
	 * 最寄り駅から目的地までの車でのルートを返す．
	 * @return 最寄り駅から目的地までの車でのルート情報インスタンス．
	 */
	public StepsArray getSstdbc() {
		return stationToDestinationByCar;
	}

	/**
	 * 最寄り駅から目的地までの徒歩でのルートを返す．
	 * @return 最寄り駅から目的地までの徒歩でのルート情報インスタンス．
	 */
	public StepsArray getSstdbw() {
		return stationToDestinationByWalk;
	}

	/**
	 * 最寄り駅から目的地までの自転車でのルートを返す．
	 * @return 最寄り駅から目的地までの自転車でのルート情報インスタンス．
	 */
	public StepsArray getSstdbb() {
		return stationToDestinationByBicycle;
	}
}
