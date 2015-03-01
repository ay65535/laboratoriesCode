/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.view;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.EventNotificationActivity;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.R;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.direction.StepObject;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.net.DrivingDirectionDownloadRunner;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.MyGeoPoint;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.BalloonItemizedOverlay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * This is Overlay class which draw plans.
 * @author sacchin
 */
public class DrivingDirectionOverlay extends BalloonItemizedOverlay<OverlayItem>{
	private ArrayList<OverlayItem> mItems = new ArrayList<OverlayItem>();
	private EventNotificationActivity mCtx;

	private Paint linePaint, stationPaint, stepPaint, textPaint;
	private int count = 0;

	private DrivingDirectionDownloadRunner dddr = null;

	@Override
	public boolean onTap(GeoPoint arg0, MapView arg1) {
//		int size = dddr.getSctdbc().getSteps().size();
//		MyGeoPoint p = dddr.getSctdbc()
//				.getSteps().get(size - 1).getEndLocation();
//
//		double dist = GeoPointUtils.calcDistanceHubery(p.getLatitude(), p.getLongtitude(), 
//				((double)arg0.getLatitudeE6() / 1E6), 
//				((double)arg0.getLongitudeE6() / 1E6), GeoPointUtils.GRS80);
//		if(dist < 100){
//			switch (count) {
//			case 0:
//				String sctdbc = "arrive at the destination at\n" +
//						dddr.getSctdbc().getArrivalStr();
//				alert("Only Car", sctdbc);
//				break;
//			case 1:
//				String sctdbw = "arrive at the destination at\n" +
//						dddr.getSctdbw().getArrivalStr();
//				alert("Only Walk", sctdbw);
//				break;
//			case 2:
//				String sctdbb = "arrive at the destination at\n" +
//						dddr.getSctdbb().getArrivalStr();
//				alert("Only Bicycle", sctdbb);
//				break;
//			case 3:
//				String sctsbc = "arrive at the Station at\n" +
//						dddr.getSctsbc().getArrivalStr() +
//						"\narrive at the destination at\n" +
//						dddr.getSstdbc().getArrivalStr();
//				alert("Car Train Walk", sctsbc);
//				break;
//			case 4:
//				String sctsbw = "arrive at the Station at\n" +
//						dddr.getSctsbw().getArrivalStr() +
//						"\narrive at the destination at\n" +
//						dddr.getSstdbw().getArrivalStr();
//				alert("Walk Train Walk", sctsbw);
//				break;
//			case 5:
//				String sctsbb = "arrive at the Station at\n" +
//						dddr.getSctsbb().getArrivalStr() +
//						"\narrive at the destination at\n" +
//						dddr.getSstdbb().getArrivalStr();
//				alert("Bicycle Train Walk", sctsbb);
//				break;
//			default:
//				break;
//			}
//		}
		return super.onTap(arg0, arg1);
	}

//	private void alert(String title, String message){
//		AlertDialog.Builder adb = new AlertDialog.Builder(mCtx);
//		adb.setTitle(title);
//		adb.setMessage(message);
//		adb.show();
//	}

	/**
	 * this is constructor
	 */
	public DrivingDirectionOverlay(Drawable defaultMarker, MapView mapView, EventNotificationActivity activity) {
		super(boundCenter(defaultMarker), mapView);
		mCtx = activity;

		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(30);

		linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setColor(Color.RED);
		linePaint.setStrokeWidth(5);
		linePaint.setAlpha(80);

		stationPaint = new Paint();
		stationPaint.setAntiAlias(true);
		stationPaint.setStrokeWidth(20);
		stationPaint.setColor(Color.BLUE);
		stationPaint.setAlpha(80);

		stepPaint = new Paint();
		stepPaint.setAntiAlias(true);
		stepPaint.setStrokeWidth(10);
		stepPaint.setColor(Color.WHITE);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if(dddr == null){
			return;
		}
		switch (count) {
		case 0:
			drawPolyLine(dddr.getSctdbc().getSteps(), canvas, mapView, Color.RED);
			drawSteps(dddr.getSctdbc().getSteps(), canvas, mapView);
			break;
		case 1:
			drawPolyLine(dddr.getSctdbw().getSteps(), canvas, mapView, Color.DKGRAY);
			drawSteps(dddr.getSctdbw().getSteps(), canvas, mapView);
			break;
		case 2:
//			drawPolyLine(dddr.getSctdbb().getSteps(), canvas, mapView, Color.YELLOW);
//			drawSteps(dddr.getSctdbb().getSteps(), canvas, mapView);
			break;

		case 3:
			drawPolyLine(dddr.getSctsbc().getSteps(), canvas, mapView, Color.RED);
			drawPolyLine(dddr.getSstdbc().getSteps(), canvas, mapView, Color.DKGRAY);
			drawSteps(dddr.getSctsbc().getSteps(), canvas, mapView);
			drawSteps(dddr.getSstdbc().getSteps(), canvas, mapView);
			break;
		case 4:
			drawPolyLine(dddr.getSctsbw().getSteps(), canvas, mapView, Color.DKGRAY);
			drawPolyLine(dddr.getSstdbw().getSteps(), canvas, mapView, Color.DKGRAY);
			drawSteps(dddr.getSctsbw().getSteps(), canvas, mapView);
			drawSteps(dddr.getSstdbw().getSteps(), canvas, mapView);
			break;
		case 5:
//			drawPolyLine(dddr.getSctsbb().getSteps(), canvas, mapView, Color.YELLOW);
//			drawPolyLine(dddr.getSstdbb().getSteps(), canvas, mapView, Color.DKGRAY);
//			drawSteps(dddr.getSctsbb().getSteps(), canvas, mapView);
//			drawSteps(dddr.getSstdbb().getSteps(), canvas, mapView);
			break;
		default:
			break;
		}
		super.draw(canvas, mapView, shadow);
	}

	private void drawSteps(ArrayList<StepObject> so, Canvas canvas, MapView mapView) {
		if(so == null || so.isEmpty()){
			return;
		}
		final int STEP_SIZE = so.size();
		for(int i = 0 ; i < STEP_SIZE ; i++){
			ViewUtility.drawGeoPoint(
					so.get(i).getStartLocation(), canvas, mapView, stepPaint);
		}
		ViewUtility.drawGeoPoint(
				so.get(STEP_SIZE - 1).getStartLocation(), canvas, mapView, stepPaint);
	}

	private void drawPolyLine(ArrayList<StepObject> so, Canvas canvas, MapView mapView, int color) {
		if(so == null){
			return;
		}
		linePaint.setColor(color);
		final int STEP_SIZE = so.size();
		for(int i = 0 ; i < STEP_SIZE ; i++){
			List<MyGeoPoint> polyline = so.get(i).getPolyline();

			final int POLYLINE_SIZE = polyline.size() - 1;
			for(int j = 0 ; j < POLYLINE_SIZE ; j++){
				ViewUtility.drawGeoLine(polyline.get(j), polyline.get(j + 1), 
						canvas, mapView, linePaint);
			}
		}
	}

	public void countUp(){
		count++;
		if(5 < count){
			count = 0;
		}
		if(0 <= count && count < 3 && 1 < mItems.size()){
			mItems.remove(1);
			mItems.remove(1);
			populate();
		}
		if(3 <= count && count < 6 && mItems.size() < 2){
			OverlayItem station1 = new OverlayItem(dddr.getStationsCloseToCurrent().getLatlng().getCenterGeoPoint(),
					String.valueOf("Station"), "Close to Current");
			Bitmap station1Image = BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.transfer);
			station1.setMarker(boundCenter(new BitmapDrawable(mCtx.getResources(), station1Image)));
			addItem(station1);

			OverlayItem station2 = new OverlayItem(dddr.getStationsCloseToDestination().getLatlng().getCenterGeoPoint(),
					String.valueOf("Station"), "Close to Destination");
			Bitmap station2Image = BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.transfer);
			station2.setMarker(boundCenter(new BitmapDrawable(mCtx.getResources(), station2Image)));
			addItem(station2);
		}
	}

	public void setDddr(DrivingDirectionDownloadRunner dddr) {
		this.dddr = dddr;
		OverlayItem destination = new OverlayItem(dddr.getDestination().getCenterGeoPoint(),
				String.valueOf("Destination"), "hogehoge");
		
		Bitmap destinationImage = BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.destination);
		Matrix matrix = new Matrix();  
		matrix.postScale(0.5f, 0.5f);
		destinationImage = Bitmap.createBitmap(
				destinationImage, 0, 0, destinationImage.getWidth(), destinationImage.getHeight(), matrix, true);  
		destination.setMarker(boundCenterBottom(new BitmapDrawable(mCtx.getResources(), destinationImage)));
		addItem(destination);
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mItems.get(i);
	}

	@Override
	public int size() {
		return mItems.size();
	}

	public void addItem(OverlayItem oi) {
		mItems.add(oi);
		populate();
	}

	public int getCount() {
		return count;
	}

	@Override
	protected boolean onBalloonTap(int index, OverlayItem item) {
		return true;
	}
}