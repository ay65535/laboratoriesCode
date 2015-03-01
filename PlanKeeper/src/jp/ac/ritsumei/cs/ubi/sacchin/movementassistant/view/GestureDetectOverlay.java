/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.view;

import java.util.LinkedList;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.AssistMapActivity;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.MyGeoPoint;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ZoomButtonsController;
import android.widget.ZoomButtonsController.OnZoomListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * this is overlay which accept gestures to manipulate the map
 * @author sacchin
 *
 */
public class GestureDetectOverlay extends Overlay implements
GestureDetector.OnDoubleTapListener,
GestureDetector.OnGestureListener{

	/**
	 * for gesture.
	 */
	private GestureDetector gesture;
	protected boolean mZoom = false;
	protected boolean mDoubleTap = false;
	protected boolean mSingleTap = true;
	
	/**
	 * these are draw properties.
	 */
	protected Paint geopointPaint;
	protected Paint enterPointPaint;
	
	/**
	 * list of geopoint.
	 */
	private LinkedList<MyGeoPoint> trajectory;
	
	/**
	 * meta data of trajectory.
	 */
	private LinkedList<Integer> metaOfTrajectory;
	
	/**
	 * Instance of MapView.
	 */
	private MapView kubiwaMap;
	
	/**
	 * This is an activity which has this object.
	 */
	private AssistMapActivity activity;
	
	/**
	 * this is constructor.
	 * @param activity AssistMapActivity.this
	 * @param mapView
	 */
	public GestureDetectOverlay(AssistMapActivity activity, MapView mapView){
		this.kubiwaMap = mapView;
		this.activity = activity;
		this.gesture = new GestureDetector(activity, this);
		this.trajectory = new LinkedList<MyGeoPoint>();
		this.metaOfTrajectory = new LinkedList<Integer>();
		
		this.geopointPaint = new Paint();
		geopointPaint.setStrokeWidth(5);
		this.enterPointPaint = new Paint();
		enterPointPaint.setStrokeWidth(5);
		enterPointPaint.setColor(Color.RED);

		ZoomButtonsController zbc = mapView.getZoomButtonsController();
		zbc.setOnZoomListener(new OnZoomListener() {
			@Override
			public void onZoom(boolean zoomIn) {
				mZoom = true;
				if(zoomIn){
					kubiwaMap.getController().zoomIn();
				} else {
					kubiwaMap.getController().zoomOut();
				}
			}
			@Override
			public void onVisibilityChanged(boolean visible) {}
		});
	}

	@Override
	public void draw(Canvas arg0, MapView arg1, boolean arg2) {
		super.draw(arg0, arg1, arg2);
		
		for(int i = 0 ; i < trajectory.size() ; i++){
			MyGeoPoint p = trajectory.get(i);
			if(metaOfTrajectory.get(i) == 0){
				ViewUtility.drawGeoPoint(p, arg0, arg1, geopointPaint);
			}else{
				ViewUtility.drawGeoPoint(p, arg0, arg1, enterPointPaint);
			}
		}
		arg1.invalidate();
	}

	/**
	 * classify all gestures
	 */
	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {
		if (gesture.onTouchEvent(e)) {
			return true;
		}
		return super.onTouchEvent(e, mapView);
	}

	/**
	 * download driving direction.
	 */
	@Override
	public void onLongPress(MotionEvent e) {
		GeoPoint temp = kubiwaMap.getProjection().fromPixels((int)e.getX(), (int)e.getY());
		Vibrator vib = (Vibrator)activity.getSystemService(Activity.VIBRATOR_SERVICE);
		vib.vibrate(100);
		activity.downloadDrivingDirection(activity.latestPoint, MyGeoPoint.createMyGeoPoint(temp));
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2,
			float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		mDoubleTap = true;
		return false;
	}

	/**
	 * Zoom in event
	 */
	@Override
	public boolean onDoubleTapEvent(MotionEvent event) {
		if (mDoubleTap) {
			mDoubleTap = false;
			GeoPoint gp = getGeoPointFromPixel((int)event.getX(), (int)event.getY());
			GeoPoint cgp = kubiwaMap.getMapCenter();
			GeoPoint point = new GeoPoint(
					(gp.getLatitudeE6() + cgp.getLatitudeE6()) / 2,
					(gp.getLongitudeE6() + cgp.getLongitudeE6()) / 2);
			if (kubiwaMap.getController().zoomIn()) {
				kubiwaMap.getController().setCenter(point);
			}
		}
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		mSingleTap = true;
		if (mZoom) {
			mZoom = false;
		}
		return false;
	}
	
	/**
	 * get geopoint from pixels.
	 * @param x
	 * @param y
	 * @return geopoint
	 */
	private GeoPoint getGeoPointFromPixel(int x, int y) {
		Projection projection = kubiwaMap.getProjection();
		return projection.fromPixels(x, y);
	}
	
	/**
	 * add a geopoint to list of drawing
	 * @param point geopoint
	 * @param metaData this is color to draw. if this is 0, draw a black point.
	 * Otherwise, draw a red point.
	 */
	public void addGeoPoint(MyGeoPoint point, int metaData){
		if(trajectory == null){
			trajectory = new LinkedList<MyGeoPoint>();
		}
		if(metaOfTrajectory == null){
			metaOfTrajectory = new LinkedList<Integer>();
		}
		
		trajectory.add(point);
		metaOfTrajectory.add(metaData);
	}
	
	/**
	 * clear all trajectory
	 */
	public void clearTrajectory(){
		if(trajectory != null){
			trajectory.clear();
		}
	}
}
