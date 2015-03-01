/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.MyGeoPoint;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.view.GestureDetectOverlay;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

/**
 * 基本的な地図操作をまとめたクラス．
 * @author sacchin
 *
 */
public abstract class AssistMapActivity extends MapActivity{
	/**
	 * 観測された直近のGPSデータの時刻を表示する．
	 */
	protected TextView timeText;
	
	/**
	 * 現在表示している移動プランが用いている移動手段を表示する．
	 */
	protected TextView meansOfTransportationText;
	
	/**
	 * 地図操作用インスタンス．
	 */
	protected MapController mMapController;
	
	/**
	 * 地図を表示するインスタンス．
	 */
	protected MapView kubiwaMap;

	/**
	 * ジェスチャーを判定するインスタンス．
	 */
	protected GestureDetector mGDetector;

	/**
	 * 設定（主にdevidとマイニング期間）を保持する．
	 */
	protected SharedPreferences settingPreference;
	
	/**
	 * 直近に観測された緯度経度．
	 * シミュレーション時は，移動軌跡のリスト先頭から順に代入されていく．
	 */
    public MyGeoPoint latestPoint;
    
    /**
     * devid
     */
    protected int devid = -1;
    
    /**
     * マイニング期間
     */
    protected int interval = -1;
    
    /**
     * ジェスチャー判定用．
     * 主にLongPressとDoubleTapの処理用．
     */
    protected GestureDetectOverlay longPressOverlay;
    
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.main);
		
		settingPreference = getSharedPreferences("setting", MODE_PRIVATE);
		devid = settingPreference.getInt("devid", -1);
		interval = settingPreference.getInt("interval", -1);
		
		timeText = (TextView)findViewById(R.id.timetext);
		meansOfTransportationText = (TextView)findViewById(R.id.meansoftransitionstext);
		kubiwaMap = (MapView) findViewById(R.id.myMap);
		kubiwaMap.setEnabled(true);
		kubiwaMap.setClickable(true);
		kubiwaMap.setBuiltInZoomControls(true);
		
		longPressOverlay = new GestureDetectOverlay(this, kubiwaMap);
		kubiwaMap.getOverlays().add(longPressOverlay);
		kubiwaMap.invalidate();
		
		mMapController = kubiwaMap.getController();
		mMapController.animateTo(
				new GeoPoint((int)(34.97955*1E6),(int)(135.963868*1E6)));
		mMapController.setZoom(19);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		double lat = ((double)settingPreference.getInt("lat", -1)) / ((double)1E6);
		double lng = ((double)settingPreference.getInt("lng", -1)) / ((double)1E6);
		if(lat < 0 || lng < 0){
			LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(location != null){
				latestPoint = new MyGeoPoint(
						location.getLatitude(), location.getLongitude(), 
						location.getLatitude(), location.getLongitude());
			}else{
				latestPoint = new MyGeoPoint(
						35.618983,139.731438,35.618983,139.731438);
			}
		}else{
			latestPoint = new MyGeoPoint(lat, lng, lat, lng);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	/**
	 * LongPress時にユーザが非日常の移動プランをダウンロードを選択した場合に呼ばれるメソッド．
	 * @param origin 現在地の緯度経度
	 * @param destination 目的地の緯度経度
	 */
	public abstract void downloadDrivingDirection(MyGeoPoint origin, MyGeoPoint destination);

	public int getDevid() {
		return devid;
	}

	public int getInterval() {
		return interval;
	}
}
