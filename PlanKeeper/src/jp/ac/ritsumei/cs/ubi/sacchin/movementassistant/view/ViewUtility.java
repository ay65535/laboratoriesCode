/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.view;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.MyGeoPoint;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Viewに関するメソッドをまとめたクラス．
 * -各種ViewやLayoutなどのFactoryメソッド
 * -緯度経度をもとにした描画メソッド
 * @author sacchin
 *
 */
public class ViewUtility {
	

	/**
	 * 
	 * 1点の緯度経度を中心として、指定された半径で円を描画するメソッド．
	 * @param point 中心の緯度経度
	 * @param radius 半径(ピクセル)
	 * @param canvas 描画対象
	 * @param mapView 描画する緯度経度が存在するmapView
	 * @param paint 描画オプション
	 */
	public static <T extends MyGeoPoint> void drawGeoCircle(T point, int radius, 
			Canvas canvas, MapView mapView, Paint paint){
		if(point == null){
			return;
		}
		
		GeoPoint startLatLng = point.getMostHighGeoPoint();
		GeoPoint endLatLng = point.getMostLowGeoPoint();
		if(startLatLng == null || endLatLng == null){
			return;
		}
		Point startPoint = mapView.getProjection().toPixels(startLatLng, null);
		Point endPoint = mapView.getProjection().toPixels(endLatLng, null);
		if(startPoint != null && endPoint != null){
			canvas.drawCircle((startPoint.x + endPoint.x) / 2, (startPoint.y + endPoint.y) / 2, radius, paint);
		}
	}
	
	/**
	 * 2点の緯度経度を結ぶ線を描画するメソッド．
	 * @param start 始点
	 * @param end 終点
	 * @param canvas 描画対象
	 * @param mapView 描画する緯度経度が存在するmapView
	 * @param paint 描画オプション
	 */
	public static <T extends MyGeoPoint> void drawGeoLine(T start, T end,
			Canvas canvas, MapView mapView, Paint paint){
		if(start == null || end == null){
			return;
		}
		Point startPoint = mapView.getProjection().toPixels(start.getCenterGeoPoint(), null);
		Point endPoint = mapView.getProjection().toPixels(end.getCenterGeoPoint(), null);
		if(startPoint != null && endPoint != null &&
				isInsideWindow(mapView, startPoint) && isInsideWindow(mapView, endPoint)){
			canvas.drawLine(startPoint.x, startPoint.y,
					endPoint.x, endPoint.y, paint);
		}
	}
	
	/**
	 * 1点の緯度経度のAccuracy矩形を描画するメソッド．
	 * @param point Accuracy矩形を描画したい緯度経度
	 * @param canvas 描画対象
	 * @param mapView 描画する緯度経度が存在するmapView
	 * @param paint 描画オプション
	 */
	public static <T extends MyGeoPoint> void drawGeoRect(T point,
			Canvas canvas, MapView mapView, Paint paint){
		drawGeoRect(point.getMostHighGeoPoint(), point.getMostLowGeoPoint(), canvas, mapView, paint);
	}
	
	/**
	 * 2点の緯度経度を頂点とした矩形を描画するメソッド．
	 * @param geoPointA 1つ目の緯度経度
	 * @param geoPointB 2つ目の緯度経度
	 * @param canvas 描画対象
	 * @param mapView 描画する緯度経度が存在するmapView
	 * @param paint 描画オプション
	 */
	public static void drawGeoRect(GeoPoint geoPointA, GeoPoint geoPointB, 
			Canvas canvas, MapView mapView, Paint paint){
		if(geoPointA == null || geoPointB == null){
			return;
		}
		Point startPoint = mapView.getProjection().toPixels(geoPointA, null);
		Point endPoint = mapView.getProjection().toPixels(geoPointB, null);
		if(startPoint != null && endPoint != null &&
				isInsideWindow(mapView, startPoint) && isInsideWindow(mapView, endPoint)){
			canvas.drawRect(endPoint.x, startPoint.y,
					startPoint.x, endPoint.y, paint);

		}
	}
	
	/**
	 * 1点の緯度経度を点で描画するメソッド．
	 * @param geoPoint 描画する緯度経度
	 * @param canvas 描画対象
	 * @param mapView 描画する緯度経度が存在するmapView
	 * @param paint 描画オプション
	 */
	public static <T extends MyGeoPoint> void drawGeoPoint(T geoPoint, 
			Canvas canvas, MapView mapView, Paint paint){
		if(geoPoint == null){
			return;
		}
		Point p = mapView.getProjection().toPixels(geoPoint.getCenterGeoPoint(), null);
		if(p != null && isInsideWindow(mapView, p)){
			canvas.drawCircle(p.x, p.y, 3, paint);
		}
	}
	
	/**
	 * TableRowのFactoryメソッド．
	 * @param c 配置するActivityのContext
	 * @return TableRow
	 */
	public static TableRow createTableRow(Context c){
		TableRow t = new TableRow(c);
		return t;
	}

	/**
	 * TextViewのFactoryメソッド．
	 * @param c 配置するActivityのContext
	 * @param text 表示するテキスト
	 * @return
	 */
	public static TextView createTextView(Context c, String text){
		TextView newView = new TextView(c);
		newView.setText(text);
		return newView;
	}

	/**
	 * 指定された緯度経度が、現在表示されているMapViewの内側に存在するかを判定するメソッド．
	 * @param view 判定対象のMapView
	 * @param p 判定したい緯度経度
	 * @return MapViewの内側に存在する場合はtrue．
	 */
	public static boolean isInsideWindow(MapView view, Point p){
		if(0 < p.x && p.x < view.getWidth() &&
				0 < p.y && p.y < view.getHeight()){
			return true;
		}
		return false;
	}
}
