/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.EventNotificationActivity;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.R;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.net.HttpPostRunner;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.DropInSite;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.MovementResult;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.TransferPoint;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.TransitionBetweenDropInSite;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.MyGeoPoint;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.URLConstants;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.BalloonItemizedOverlay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 立ち寄りポイントを描画するOverlayクラス．
 * @author sacchin
 */
public class DropInSitesOverlay extends BalloonItemizedOverlay<OverlayItem>{
	/**
	 * 
	 */
	private EventNotificationActivity activity;

	/**
	 * バルーンがタップされた時に次に到着する立ち寄りポイントを可視化する時の描画情報．
	 */
	private Paint nextTransitionPaint;

	/**
	 * 次に立ち寄りポイントへ到着するまでの乗り換えポイントを可視化する時の描画情報．
	 */
	private Paint transferPaint;

	/**
	 * 立ち寄りポイントの緯度経度矩形を可視化する時の描画情報．
	 */
	private Paint areaPaint;

	/**
	 * 
	 */
	private ArrayList<MyGeoPoint> dropInSitesArray;

	/**
	 * 
	 */
	private HashMap<Long, DropInSite> dropInSitesMap;

	/**
	 * 
	 */
	private boolean isSimulation = false;

	/**
	 * バルーンがタップされた時に次に到着する立ち寄りポイントを可視化するためのリスト．
	 */
	private ArrayList<DropInSite> visibleDropInSites;

	/**
	 * 立ち寄りポイントを出発した時に、次に立ち寄りポイントへ到着するまでの乗り換えポイントを可視化するためのリスト．
	 */
	private ArrayList<MovementResult> visibleMovementResult;

	/**
	 * 
	 */
	private DropInSite selectedSite;

	/**
	 * 
	 */
	private DateTimePicker picker;

	/**
	 * 
	 */
	private EditText edtInput;
	
	private int index = 0;

	/**
	 * 
	 * @param defaultMarker
	 * @param activity
	 */
	public DropInSitesOverlay(Drawable defaultMarker, EventNotificationActivity activity) {
		super(boundCenter(defaultMarker), activity.getKubiwaMap());
		this.activity = activity;
		
		nextTransitionPaint = new Paint();
		nextTransitionPaint.setAntiAlias(true);
		nextTransitionPaint.setStrokeWidth(10);
		nextTransitionPaint.setColor(Color.RED);
		nextTransitionPaint.setAlpha(60);
		areaPaint = new Paint();
		areaPaint.setAntiAlias(true);
		areaPaint.setColor(Color.BLACK);
		areaPaint.setAlpha(60);
		transferPaint = new Paint();
		transferPaint.setAntiAlias(true);
		transferPaint.setColor(Color.BLUE);
		transferPaint.setAlpha(60);
	}

	public long getId(int index){
		if(dropInSitesArray.get(index) instanceof DropInSite){
			return ((DropInSite) dropInSitesArray.get(index)).getSiteId();
		}
		return -1;
	}

	/**
	 * 立ち寄りポイントを描画するメソッド．
	 * すべての立ち寄りポイントの矩形を灰色で，遷移先の立ち寄りポイントに赤丸を描画する．
	 */
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		for(Long id : dropInSitesMap.keySet()){
			ViewUtility.drawGeoRect(dropInSitesMap.get(id), canvas, mapView, areaPaint);
		}
		if(visibleDropInSites != null && !visibleDropInSites.isEmpty()){
			for(DropInSite point : visibleDropInSites){
				ViewUtility.drawGeoCircle(point, 30, canvas, mapView, nextTransitionPaint);
			}
		}
		if(visibleMovementResult != null && !visibleMovementResult.isEmpty()){
			MovementResult mr = visibleMovementResult.get(index);
			for(TransferPoint tp : mr.getAllTransferPoint()){
				ViewUtility.drawGeoRect(tp, canvas, mapView, transferPaint);
			}
		}
		super.draw(canvas, mapView, shadow);
	}

	/**
	 * 
	 * @param dropInSites
	 */
	public void setDropInSites(ArrayList<DropInSite> dropInSites) {
		if(dropInSites == null || dropInSites.isEmpty()){
			return;
		}
		if(this.dropInSitesArray == null){
			this.dropInSitesArray = new ArrayList<MyGeoPoint>();
		}else{
			this.dropInSitesArray.clear();
		}
		this.dropInSitesArray.addAll(dropInSites);

		if(this.dropInSitesMap == null){
			this.dropInSitesMap = new HashMap<Long, DropInSite>();
		}else{
			this.dropInSitesMap.clear();
		}

		for(DropInSite site : dropInSites){
			this.dropInSitesMap.put(site.getSiteId(), site);
		}
		populate();
	}

	/**
	 * 
	 */
	@Override
	protected OverlayItem createItem(int i) {
		if(dropInSitesArray == null){
			return null;
		}

		long id = getId(i);
		if(0 < id){
			DropInSite dropInSite = dropInSitesMap.get(id);
			String snipet = "";
			long estimated = dropInSite.getEstimatedArrivalTime();
			if(0 < estimated){
				snipet = "ここへは、" + new Date(estimated).toString() + "に到着します。\n";
			}
			snipet += "あなたはここに" + dropInSite.getStayCount() + "回立ち寄りました。\n平均滞在時間は" + dropInSite.getStayAverage() + "分です。";
			OverlayItem oi = new OverlayItem(dropInSite.getCenterGeoPoint(),
					String.valueOf("ここは '" + dropInSite.getSiteId()) + "' です。", snipet);

			Bitmap image = null;
			int count = dropInSite.getStayCount();
			if(0 <= count && count < 5){
				image = BitmapFactory.decodeResource(activity.getResources(), R.drawable.rare);
			}else if(5 <= count && count < 10){
				image = BitmapFactory.decodeResource(activity.getResources(), R.drawable.usually);
			}else{
				image = BitmapFactory.decodeResource(activity.getResources(), R.drawable.frequent);
			}

			Matrix matrix = new Matrix();  
			matrix.postScale(0.5f, 0.5f);
			image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);  
			oi.setMarker(boundCenter(new BitmapDrawable(activity.getResources(), image)));
			return oi;
		}

		return null;
	}

	/**
	 * 
	 */
	@Override
	public int size() {
		return dropInSitesArray.size();
	}

	/**
	 * 
	 */
	@Override
	protected void onBalloonOpen(int index) {
		if(!isSimulation){
			initVisibleDropInSites();
			long id = getId(index);
			if(0 < id){
				DropInSite selected = dropInSitesMap.get(id);
				ArrayList<TransitionBetweenDropInSite> transitions = selected.getTransitions();
				for(TransitionBetweenDropInSite t : transitions){
					visibleDropInSites.add(dropInSitesMap.get(t.getToID()));
				}
			}
		}
	}

	/**
	 * 
	 */
	@Override
	protected boolean onBalloonTap(int index, OverlayItem item) {
		long id = getId(index);
		if(0 < id){
			selectedSite = dropInSitesMap.get(id);
			if(selectedSite == null){
				return false;
			}

			AlertDialog.Builder textDialog = new AlertDialog.Builder(activity);
			textDialog.setIcon(android.R.drawable.ic_dialog_email);
			textDialog.setTitle("ここは " + selectedSite.getName() + " です。");
			textDialog.setMessage("どうしますか？");
			textDialog.setPositiveButton("到着時間設定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					specifiedArrivalTime(selectedSite);
				}
			});
			textDialog.setNeutralButton("名前をつける", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					labeling(selectedSite);
				}
			});
			textDialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {}
			});
			textDialog.show();

			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param site
	 */
	private void specifiedArrivalTime(DropInSite site) {
		picker = new DateTimePicker(activity);

		AlertDialog.Builder textDialog = new AlertDialog.Builder(activity);
		textDialog.setIcon(android.R.drawable.ic_dialog_map);
		textDialog.setTitle("いつ到着したいですか？");
		textDialog.setView(picker.createDateTimePicker());
		textDialog.setPositiveButton("OK", new ArrivingListener(site));
		textDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {}
		});
		textDialog.show();
	}

	/**
	 * 
	 * @param site
	 */
	private void labeling(DropInSite site) {
		edtInput = new EditText(activity);
		AlertDialog.Builder textDialog = new AlertDialog.Builder(activity);
		textDialog.setIcon(android.R.drawable.ic_dialog_email);
		textDialog.setTitle("ここの名前はなんですか？");
		textDialog.setView(edtInput);
		textDialog.setPositiveButton("OK", new PostingListener(site));
		textDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {}
		});
		textDialog.show();
	}

	/**
	 * 
	 * @author sacchin
	 *
	 */
	private class ArrivingListener implements DialogInterface.OnClickListener{
		private DropInSite site;
		public ArrivingListener(DropInSite site){
			this.site = site;
		}
		public void onClick(DialogInterface dialog, int whichButton) {
			activity.setExpectedArrivalTime(picker.getCalendar(), site);
		}
	}

	/**
	 * 
	 * @author sacchin
	 *
	 */
	private class PostingListener implements DialogInterface.OnClickListener{
		DropInSite updateSite = null;

		public PostingListener(DropInSite updateSite){
			this.updateSite = updateSite;
		}

		public void onClick(DialogInterface dialog, int whichButton) {
			List <NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(
					"pattern", "labeling"));
			params.add(new BasicNameValuePair(
					"id", String.valueOf(updateSite.getSiteId())));
			params.add(new BasicNameValuePair(
					"devid", String.valueOf(activity.getDevid())));
			params.add(new BasicNameValuePair(
					"label", edtInput.getText().toString()));

			HttpPostRunner hpr = new HttpPostRunner(
					URLConstants.SERVER_URL, params);
			hpr.setSuccessCallback(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(activity, "ここに「" + edtInput.getText().toString() + "」という名前をつけました。", 
							Toast.LENGTH_SHORT).show();
				}
			});
			hpr.setFailedCallback(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(activity, "エラーが発生して名前をつけることができませんでした。", 
							Toast.LENGTH_SHORT).show();
				}
			});
			new Thread(hpr).start();
		}
	}

	/**
	 * 
	 */
	public void clearVisibleDropInSites(){
		if(visibleDropInSites != null){
			visibleDropInSites.clear();
		}
	}

	/**
	 * 
	 * @return
	 */
	public HashMap<Long, DropInSite> getDropInSites() {
		return dropInSitesMap;
	}

	/**
	 * 
	 * @param siteId
	 * @return
	 */
	public DropInSite getDropInSite(long siteId) {
		return dropInSitesMap.get(siteId);
	}

	/**
	 * 指定された立ち寄りポイントから遷移する先のずべての立ち寄りポイントへ到着する時刻を格納する．
	 * その後、到着時刻を格納したポイントを可視化リストに追加する．
	 * @param fromId 指定された立ち寄りポイント．
	 * @param movementResults 指定された立ち寄りポイントからの2点間移動の統計．
	 */
	public void setAllArrivalTimes(long fromId, ArrayList<MovementResult> movementResults, long now) {
		if(movementResults == null){
			return;
		}
		
		initVisibleDropInSites();
		
		Log.v("setArrivalTimes", "There are " + visibleMovementResult.size() + " movementResults!");
		for(MovementResult mr : movementResults){
			HashMap<String, ArrayList<HashMap<Integer, Long>>> travel = mr.getMaximumTravel();
			if(travel == null){
				continue;
			}
			String key = travel.keySet().iterator().next();
			long travelTime = MovementResult.calcTravelTime(travel.get(key));
			if(0 < travelTime){
				Log.v("setAllArrivalTimes to " + mr.getToID(), (travelTime / 1000) + "sec");
				DropInSite site = dropInSitesMap.get(mr.getToID());
				if(site == null){
					continue;
				}
				site.setEstimatedArrivalTime(now + travelTime);
				visibleDropInSites.add(site);
			}
		}
	}
	
	/**
	 * 到着時間が推定された立ち寄りポイントへ到着する時刻を格納する．
	 * @param fromId 指定された立ち寄りポイントのID．
	 * @param toId 到着時間が推定された立ち寄りポイントのID．
	 * @param arrivalTime 
	 */
	public void setArrivalTimes(long fromId, long toId, long arrivalTime) {

	}
	
	/**
	 * 	 * 指定された立ち寄りポイントから遷移する先のずべての立ち寄りポイントへ到着する時刻を格納する．
	 * その後、到着時刻を格納したポイントを可視化リストに追加する．

	 * @param siteId
	 * @param arrivalTime
	 */
	public void updateArrivalTimes(long toId, long toTransfer, long toDestination) {
//		dropInSitesMap.get(toId).setEstimatedArrivalTime(System.currentTimeMillis());
//		visibleDropInSites.add(dropInSitesMap.get(toId));
	}
	
	
	public void setVisibleMovementResult(ArrayList<MovementResult> movementResults) {
		initVisibleMovementResult();
		visibleMovementResult.addAll(movementResults);
	}

	private void initVisibleDropInSites() {
		if(visibleDropInSites == null){
			visibleDropInSites = new ArrayList<DropInSite>();
		}
		if(!visibleDropInSites.isEmpty()){
			visibleDropInSites.clear();
		}
	}

	private void initVisibleMovementResult() {
		if(visibleMovementResult == null){
			visibleMovementResult = new ArrayList<MovementResult>();
		}
		if(!visibleMovementResult.isEmpty()){
			visibleMovementResult.clear();
		}
	}

	/**
	 * すべての次到着立ち寄りポイントへバルーンを表示する．
	 */
	public void showArraivalTimes(){
		createAndDisplayBalloonOverlays(visibleDropInSites);
	}

	/**
	 * シミュレーションしているかどうかを格納する．
	 * @param isSimulation シミュレーション中ならtrue
	 */
	public void setSimulation(boolean isSimulation) {
		this.isSimulation = isSimulation;
	}
	
	public int countUp(){
		if(visibleMovementResult != null){
			index++;
			if(visibleMovementResult.size() <= index){
				index = 0;
			}
			return index;
		}
		return -1;
	}
}