/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.view;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.CheckPoint;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

/**
 * This is Overlay class which draw plans.
 * @author sacchin
 */
public class CheckPointOverlay extends Overlay{
	private Context activity;
	private ArrayList<CheckPoint> points;
	private Paint areaPaint;
	private Paint pointPaint;
	private boolean isPlanAdded = false;
	
	@Override
	public boolean onTap(GeoPoint arg0, MapView arg1) {
		Log.v("onTap", arg0.toString());
		for(int i = 0 ; i < points.size() ; i++){
			CheckPoint point = points.get(i);
			GeoPoint high = point.getMostHighGeoPoint();
			GeoPoint low = point.getMostLowGeoPoint();
			if(high == null || low == null){
				continue;
			}
			
			if(low.getLatitudeE6() < arg0.getLatitudeE6() &&
					arg0.getLatitudeE6() < high.getLatitudeE6() &&
					low.getLongitudeE6() < arg0.getLongitudeE6() &&
					arg0.getLongitudeE6() < high.getLongitudeE6()){
				alert(i, point);
			}
		}
		return super.onTap(arg0, arg1);
	}
	
	private void alert(int number, CheckPoint mp){
		List<Integer> index = new ArrayList<Integer>();
		index.add(0);
		index.add(1);
		index.add(2);
		index.add(6);
		
		AlertDialog.Builder adb = new AlertDialog.Builder(activity);
		adb.setTitle(number + ":" + mp.getStayTime());
		String standardDeviationOfAll = "A=" + mp.getAverageAllStayTimes() + ", S=" + mp.getSdAllStayTimes();
		String standardDeviationOfOver = "A=" + mp.getAverageOverStayTimes() + ", S=" + mp.getSdOverStayTimes();
		
		adb.setMessage("All:\n" + standardDeviationOfAll + 
				"\nOver:\n" + standardDeviationOfOver);
		adb.show();
	}
	
	/**
	 * this is constructor
	 */
	public CheckPointOverlay(Context context){
		areaPaint = new Paint();
		areaPaint.setAntiAlias(true);
		areaPaint.setColor(Color.YELLOW);
		areaPaint.setAlpha(70);
		
		pointPaint = new Paint();
		pointPaint.setAntiAlias(true);
		pointPaint.setStrokeWidth(5);
		pointPaint.setColor(Color.BLACK);
		pointPaint.setTextSize(70);
		
		this.activity = context;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		drawCheckpoint(canvas, mapView);
		super.draw(canvas, mapView, shadow);
	}

	private void drawCheckpoint(Canvas canvas, MapView mapView) {
		for(int i = 0 ; i < points.size() ; i++){
			CheckPoint point = points.get(i);
			
			GeoPoint startLatLng = point.getMostHighGeoPoint();
			GeoPoint endLatLng = point.getMostLowGeoPoint();
			if(startLatLng == null || endLatLng == null){
				return;
			}
			Point startPoint = mapView.getProjection().toPixels(startLatLng, null);
			Point endPoint = mapView.getProjection().toPixels(endLatLng, null);
			if(startPoint != null && endPoint != null){
				canvas.drawRect(endPoint.x, startPoint.y,
						startPoint.x, endPoint.y, areaPaint);
				canvas.drawPoint(
						(startPoint.x + endPoint.x) / 2, (startPoint.y + endPoint.y) / 2, 
						pointPaint);
//				canvas.drawText(String.valueOf(i) + "-" + String.valueOf(point.getOrder()), 
//						(startPoint.x + endPoint.x) / 2, (startPoint.y + endPoint.y) / 2, pointPaint);
			}
		}
	}
	
	public boolean isPlanAdded(){
		return this.isPlanAdded;
	}
	
	public void addCheckPoints(ArrayList<CheckPoint> points) {
		if(this.points == null){
			this.points = new ArrayList<CheckPoint>();
		}
		this.points.addAll(points);
		this.isPlanAdded = true;
	}
}