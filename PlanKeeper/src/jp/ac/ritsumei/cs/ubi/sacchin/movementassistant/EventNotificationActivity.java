/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import jp.ac.ritsumei.cs.ubi.logger.client.api.matching.CompareOperation;
import jp.ac.ritsumei.cs.ubi.logger.client.api.matching.EventDetectionRequest;
import jp.ac.ritsumei.cs.ubi.logger.client.api.utility.MatchingConstants;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.net.CheckPointDownloadRunner;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.net.DailyPlanDownloadRunner;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.net.DrivingDirectionDownloadRunner;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.net.DropInSiteDownloadRunner;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.net.TrajectoryDownloadRunner;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.CheckPoint;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.DropInSite;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.MovementResult;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.PlanChecker;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.TransferPoint;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.TransitionBetweenDropInSite;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.setting.SettingActivity;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.simulation.EvaluationWriter;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.simulation.SimulaterHandler;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.ArrivedHandler;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.AssistUtils;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.MyGeoPoint;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.view.CheckPointOverlay;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.view.DateTimePicker;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.view.DrivingDirectionOverlay;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.view.DropInSitesOverlay;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * このアプリケーションのmain activityです．
 * 行動支援のシミュレーション機能あり．
 * 基本的にUI操作のみに徹するように作ってます．
 * @author sacchin
 */
public class EventNotificationActivity extends AssistMapActivity {
	public static final String PACKAGE_KEY = "jp.ac.ritsumei.cs.ubi.sacchin.movementassistant";
	private final int START_ASSIST = 0;
	private final int CHANGE_VIEW = 1;
	private final int SETTINGS = 2;

	/**
	 * シミュレーションをする場合，true.
	 */
	private boolean isSimulation = true;

	/**
	 * 行動支援が開始している場合，ture.
	 */
	private boolean isAssistingOfDaily = false;

	/**
	 * シミュレーション用のハンドラ．
	 */
	private SimulaterHandler simulationExecuter = new SimulaterHandler(this);

	/**
	 * ダウンロード用のプログレスダイアログ.
	 */
	private ProgressDialog progressDialog;

	/**
	 * 移動実績を描画するためのoverlays.
	 * TODO PlanOverlay and CheckPointOverlayは削除修正する可能性あり.
	 */
	private List<Overlay> overlayList;
	private DropInSitesOverlay dropInSitesOverlay;
	private DrivingDirectionOverlay ddOverlay;
	private CheckPointOverlay cpOverlay;

	/**
	 * ダウンロード用のハンドラ．
	 */
	private Handler downloadExecutor = new Handler();

	/**
	 * 以下Runnableクラス.
	 * TODO CheckPointDownloadRunnerは削除修正する可能性あり．
	 */
	public DailyPlanDownloadRunner dpdr = null;
	private DropInSiteDownloadRunner disdr = null;
	private DrivingDirectionDownloadRunner dddr = null;
	private TrajectoryDownloadRunner tdr = null;
	private CheckPointDownloadRunner cpdr = null;

	/**
	 * プランチェッカー.
	 */
	public PlanChecker pc = null;

	/**
	 * 立ち寄りポイント到着イベントのマップ
	 */
	private HashMap<String, EventDetectionRequest> enterDropInSiteRequestMap;

	/**
	 * 立ち寄りポイント出発イベントのマップ
	 */
	private HashMap<String, EventDetectionRequest> goOutDropInSiteRequestMap;

	/**
	 * 乗り換えポイント到着イベントのマップ
	 */
	private HashMap<String, EventDetectionRequest> enterTransferPointRequestMap;

	/**
	 * チェックポイント到着イベントのマップ
	 */
	private HashMap<String, EventDetectionRequest> enterCheckPointRequestMap;

	/**
	 * シミュレーション用の時間指定インターフェース
	 */
	private DateTimePicker picker;

	private MyGeoPoint latest;

	private EvaluationWriter ew = new EvaluationWriter();


	/**
	 * シミュレーション速度を変更するためのシークバー
	 */
	private OnSeekBarChangeListener locationSeekBarChangeListener = new OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if(progress == 0){
				simulationExecuter.setSleepTime(1000);
			}else{
				simulationExecuter.setSleepTime(501 - (5 * progress));
			}
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
	};

	/**
	 * ALCからのBroadcast Intetnを受け取るレシーバ．
	 */
	private LocationReciever locationReciever = new LocationReciever();

	/**
	 * ALCからのBroadcast Intetnを受け取るレシーバ．
	 * シミュレーションをしている場合, このクラスはSimulationExecuterから呼ばれます.
	 * @author sacchin
	 */
	public class LocationReciever extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			MyGeoPoint p = getPoint(intent);
			addGeoPoint(p, false);
		}

		public MyGeoPoint getPoint(Intent intent){
			double lat = intent.getDoubleExtra("lat", -1);
			double lng = intent.getDoubleExtra("lng", -1);
			float acc = intent.getFloatExtra("acc", -1);
			float speed = intent.getFloatExtra("speed", -1);
			long time = intent.getLongExtra("time", -1);
			return new MyGeoPoint(time, lat, lng, acc, speed);
		}
	}

	/**
	 * this method add GeoPoint to longPressOverlay to draw point.
	 * @param p this is latest GeoPoint.
	 * @param isDetect if this is true, red point is drawn. if this is false, black point is drawn.
	 */
	public void addGeoPoint(MyGeoPoint p, boolean isDetect) {
		if(isDetect){
			longPressOverlay.addGeoPoint(p, 1);
		}else{
			longPressOverlay.addGeoPoint(p, 0);
		}

		latest = p;

		if(pc != null){
			pc.addTrajectory(p);
		}

		if(timeText != null){
			timeText.setText(AssistUtils.formatToHMS(p.getTimestamp().getTime()));
		}
	}

	/**
	 * these are interfaces to bind with EventCatcher.
	 */
	protected IEventDetecter service = null;
	protected ServiceConnection conn = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder binder) {
			service = IEventDetecter.Stub.asInterface(binder);
			try {
				service.registerEventCallback(eventCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			try {
				service.unregisterEventCallback(eventCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			service = null;
		}
	};

	/**
	 * this is handler to receive a result of event detection from ALC.
	 */
	protected ArrivedHandler handler = new ArrivedHandler(this);
	protected IEventCallback eventCallback = new IEventCallback.Stub() {
		@Override
		public void arrivedAtDropInsite(String type, String name)
				throws RemoteException {
			handler.sendMessage(Message.obtain(handler, 
					ArrivedHandler.ARRIVED_AT_DROPINSITE, type + ":" + name));
		}
		@Override
		public void leaveFromDropInsite(String type, String name)
				throws RemoteException {
			handler.sendMessage(Message.obtain(handler, 
					ArrivedHandler.LEAVE_FROM_DROPINSITE, type + ":" + name));
		}
		public void arrivedAtTransferPoint(String type, String name)
				throws RemoteException {
			handler.sendMessage(Message.obtain(handler, 
					ArrivedHandler.ARRIVED_AT_TRANSFERPOINT, type + ":" + name));
		}
		@Override
		public void arrivedAtCheckPoint(String type, String name)
				throws RemoteException {
			handler.sendMessage(Message.obtain(handler, 
					ArrivedHandler.ARRIVED_AT_CHECKPOINT, type + ":" + name));
		}
	};

	/**
	 * this method is called at first.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TableLayout t = (TableLayout)findViewById(R.id.texttable);
		if(isSimulation){
			SeekBar bar = (SeekBar)((TableRow)t.getChildAt(2)).getChildAt(1);
			bar.setOnSeekBarChangeListener(locationSeekBarChangeListener);
		}else{
			t.removeViewAt(1);
			synchronized (locationReciever) {
				IntentFilter filter = new IntentFilter();
				filter.addAction("rawLocationLog");
				registerReceiver(locationReciever, filter);			
			}
			bindService(new Intent(IEventDetecter.class.getName()), conn, BIND_AUTO_CREATE);
		}
	}

	/**
	 * this method is called at last.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(!isSimulation){
			unbindService(conn);
			synchronized (locationReciever) {
				unregisterReceiver(locationReciever);			
			}
		}
		if(ew != null){
			ew.close();
		}
		if(simulationExecuter != null){
			simulationExecuter.stop();
		}
	}

	/**
	 * save settings when return from setting activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0:
			devid = settingPreference.getInt("devid", -1);
			interval = settingPreference.getInt("interval", -1);
			break;
		default:
			break;
		}
	}

	/**
	 * this method create menu titles.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, START_ASSIST, 0, "Start Assist")
		.setIcon(android.R.drawable.ic_menu_myplaces);
		menu.add(0, SETTINGS, 0, "Setting")
		.setIcon(android.R.drawable.ic_menu_edit);
		menu.add(0, CHANGE_VIEW, 0, "Change View")
		.setIcon(android.R.drawable.ic_menu_mapmode);
		return true;
	}

	/**
	 * this method is called if menu selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case CHANGE_VIEW:
			if(dropInSitesOverlay != null && kubiwaMap != null){
				int index = dropInSitesOverlay.countUp();
				meansOfTransportationText.setText("MovementReslt" + index + "'s TransferPoint");
				kubiwaMap.invalidate();
			}
			if(ddOverlay != null && kubiwaMap != null){
				ddOverlay.countUp();
				setPlanText();
				kubiwaMap.invalidate();
			}
			break;
		case START_ASSIST:
			if(devid == -1 || interval == -1){
				Toast.makeText(this, "Devid or Interval value is invalid.", Toast.LENGTH_SHORT).show();
				break;
			}
			if(isAssistingOfDaily){
				Toast.makeText(this, "Assist is Started!", Toast.LENGTH_SHORT).show();
				break;
			}
			if(pc == null){
				pc = new PlanChecker(this);
			}
			downloadDropInSites();
			isAssistingOfDaily = true;
			break;

		case SETTINGS:
			startActivityForResult(new Intent(this, SettingActivity.class), 0);
			break;
		}
		return false;
	}

	/**
	 * DropInSitesを描画するためにダウンロードするメソッド．
	 */
	public void downloadDropInSites(){
		disdr = new DropInSiteDownloadRunner(this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				disdr.download();
				downloadExecutor.post(disdr);
			}
		}).start();

		showProgressDialog("日常の移動", "移動実績生成&ダウンロード中...");
	}

	/**
	 * 何かをダウンロードする際に表示するダイアログ．
	 * @param title ダウンロードの内容を示すタイトル．
	 * @param Message ダイアログに表示するメッセージ．
	 */
	public void showProgressDialog(String title, String Message){
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
		}
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(title);
		progressDialog.setMessage(Message);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();
	}

	/**
	 * when DropInSites Download is finished, this is called.
	 * @param isNotSuccess if download is not success, this is true. 
	 * @param devid user's devid.
	 */
	public void startAssist(boolean isNotSuccess) {
		cancelProgressDialog();

		if(isNotSuccess){
			Toast.makeText(this, "can't download DropInSites!", Toast.LENGTH_SHORT).show();
			return;
		}
		addDropInSitesOverlay();
		createDropInSiteEvent();
		if(isSimulation){
			simulationExecuter.registEventDetections(enterDropInSiteRequestMap);

			picker = new DateTimePicker(this);

			AlertDialog.Builder textDialog = new AlertDialog.Builder(this);
			textDialog.setIcon(android.R.drawable.ic_dialog_map);
			textDialog.setTitle("いつのシミュレーションを行いますか？");
			textDialog.setView(picker.createIntervalPicker());
			textDialog.setPositiveButton("OK", new SimulationListener(this));
			textDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {}
			});
			textDialog.show();
		}else{
			registEventDetections(enterDropInSiteRequestMap);
		}
	}

	/**
	 * 現在表示中のProgressDialogを消す．
	 */
	public void cancelProgressDialog(){
		if(progressDialog != null && progressDialog.isShowing()){
			progressDialog.cancel();
		}
	}

	/**
	 * 立ち寄りポイントを可視化するOverlayを作成するメソッド．
	 */
	public void addDropInSitesOverlay(){
		overlayList = kubiwaMap.getOverlays();
		if(disdr == null){
			return;
		}

		Drawable drawable = getResources().getDrawable(android.R.drawable.ic_menu_compass);
		dropInSitesOverlay = new DropInSitesOverlay(drawable, this);
		dropInSitesOverlay.setDropInSites(disdr.getDropInSites());

		expandMap(kubiwaMap, disdr.getDropInSites());

		if(!overlayList.contains(dropInSitesOverlay)){
			overlayList.add(dropInSitesOverlay);
		}
		kubiwaMap.invalidate();
		disdr = null;
	}

	/**
	 * すべてのポイントが地図中に収まるように地図を拡大するメソッド．
	 * @param map 拡大するMapView．
	 * @param points 緯度経度群．
	 */
	public static <T extends MyGeoPoint> void expandMap(MapView map, ArrayList<T> points){
		int mostHighLat = 0;
		int mostHighLng = 0;
		int mostLowLat = Integer.MAX_VALUE;
		int mostLowLng = Integer.MAX_VALUE;
		for(T point : points){
			GeoPoint p = point.getCenterGeoPoint();
			if(mostHighLat < p.getLatitudeE6()){
				mostHighLat = p.getLatitudeE6();
			}else if(p.getLatitudeE6() < mostLowLat){
				mostLowLat = p.getLatitudeE6();
			}
			if(mostHighLng < p.getLongitudeE6()){
				mostHighLng = p.getLongitudeE6();
			}else if(p.getLongitudeE6() < mostLowLng){
				mostLowLng = p.getLongitudeE6();
			}
		}
		int centerLat = (mostHighLat + mostLowLat) / 2;
		int centerLng = (mostHighLng + mostLowLng) / 2;
		map.getController().animateTo(new GeoPoint(centerLat, centerLng));
		int latSpan = mostHighLat - centerLat;
		int lngSpan = mostHighLng - centerLng;
		map.getController().zoomToSpan(latSpan, lngSpan);
	}

	/**
	 * 立ち寄りポイントの到着・出発を判定するイベントを作成するメソッド．
	 */
	public void createDropInSiteEvent(){
		this.enterDropInSiteRequestMap = new HashMap<String, EventDetectionRequest>();
		this.goOutDropInSiteRequestMap = new HashMap<String, EventDetectionRequest>();

		int replyId = 0;
		HashMap<Long, DropInSite> points = dropInSitesOverlay.getDropInSites();
		for(Long id : points.keySet()){
			DropInSite point = points.get(id);
			EventDetectionRequest enterEvent = newEventDetectionRequest(
					PACKAGE_KEY + "." + replyId + ".Enter", 
					"DropInSite", String.valueOf(point.getSiteId()), 
					point, MatchingConstants.SMALLER_THAN);
			EventDetectionRequest goOutEvent = newEventDetectionRequest(
					PACKAGE_KEY + "." + replyId + ".GoOut", 
					"DropInSite", String.valueOf(point.getSiteId()), 
					point, MatchingConstants.LARGER_THAN);

			enterDropInSiteRequestMap.put(AssistUtils.ENTER_DROP_IN_SITE + String.valueOf(point.getSiteId()), enterEvent);
			goOutDropInSiteRequestMap.put(AssistUtils.GOOUT_DROP_IN_SITE + String.valueOf(point.getSiteId()), goOutEvent);
			Log.v("createArrivedAtDropInSiteEvent", "Enter" + String.valueOf(point.getSiteId()) +
					"and GoOut" + String.valueOf(point.getSiteId()));
			replyId++;
		}
	}

	/**
	 * ある緯度経度矩形に到着したまたは出発したイベントを作成するメソッド．
	 * @param replyID イベントのID．すべてのイベントで一意に．
	 * @param type どのようなタイプの矩形を判定するか．例：立ち寄りポイント，乗り換えポイント，チェックポイント．
	 * @param id このイベントのID．同じtypeのイベントで一意に．
	 * @param point 緯度経度のAccuracy矩形．
	 * @param operator 到着または出発．
	 * @return イベント．
	 */
	public <T extends MyGeoPoint> EventDetectionRequest newEventDetectionRequest(
			String replyID, String type, String id, T point, int operator){
		Intent reply = new Intent(replyID);
		reply.putExtra("type", type);
		reply.putExtra("id", id);
		CompareOperation predicate = 
				CompareOperation.create(MatchingConstants.DT_DOUBLE, MatchingConstants.SN_LOCATION,
						MatchingConstants.VN_LAT_LNG, operator, point.getAccuracyRect());
		predicate.setNumberOfDetection(MatchingConstants.KEEP);

		return new EventDetectionRequest(reply, predicate);
	}

	/**
	 * this is listener of simulation dialog.
	 * @author sacchin
	 */
	private class SimulationListener implements DialogInterface.OnClickListener{
		private EventNotificationActivity activity;

		public SimulationListener(EventNotificationActivity activity){
			this.activity = activity;
		}

		public void onClick(DialogInterface dialog, int whichButton) {
			tdr = new TrajectoryDownloadRunner(activity);
			tdr.setYear(picker.getYear());
			tdr.setMonth(picker.getMonth());
			tdr.setDay(picker.getDayOfMonth());
			tdr.setHour(picker.getHour());
			tdr.setMinute(picker.getMinute());
			tdr.setInterval(picker.getInterval());
			ew.openFile("log_" + devid + "_" + picker.getYear() + picker.getMonth() + picker.getDayOfMonth() + 
					picker.getHour() + picker.getMinute() + "_" + picker.getInterval() + ".txt");

			new Thread(new Runnable() {
				@Override
				public void run() {
					tdr.download();
					downloadExecutor.post(tdr);
				}
			}).start();

			showProgressDialog("シミュレーション", "移動ログをダウンロード中...");
		}
	}

	/**
	 * when Trajectory Download is finished, this is called.
	 * @param isNotSuccess if download is not success, this is true. 
	 * @param trajectory list of geopoint
	 */
	public void startSimulation(boolean isNotSuccess, ArrayList<MyGeoPoint> trajectory){
		cancelProgressDialog();
		if(isNotSuccess){
			Toast.makeText(this, "can't download Trajectory!", Toast.LENGTH_SHORT).show();
			return;
		}

		if(trajectory != null && !trajectory.isEmpty()){
			mMapController.animateTo(trajectory.get(0).getCenterGeoPoint());
			mMapController.setZoom(17);
			simulationExecuter.setTestCase(trajectory);
			simulationExecuter.start();
		}
		dropInSitesOverlay.setSimulation(true);
	}

	/**
	 * if simulationExecuter detect a arrive event, this is called.
	 * DropInStiteの場合、Enter"ID" 例："Enter7105"
	 * TransferPointの場合、"移動手段名":"IDtoID":"Order" 例："WALK:7105to7106:1"
	 */
	public void arriveAt(String key){
		if(key.contains(AssistUtils.ENTER_DROP_IN_SITE)){
			String type = enterDropInSiteRequestMap.get(key).getReply().getStringExtra("type");
			String id = enterDropInSiteRequestMap.get(key).getReply().getStringExtra("id");
			handler.sendMessage(Message.obtain(handler, 
					ArrivedHandler.ARRIVED_AT_DROPINSITE, type + ":" + id));
		}else{
			String type = enterTransferPointRequestMap.get(key).getReply().getStringExtra("type");
			String id = enterTransferPointRequestMap.get(key).getReply().getStringExtra("id");

			if("TransferPoint".equals(type)){
				handler.sendMessage(Message.obtain(handler, 
						ArrivedHandler.ARRIVED_AT_TRANSFERPOINT, type + ":" + id));
			}else{
				handler.sendMessage(Message.obtain(handler, 
						ArrivedHandler.ARRIVED_AT_CHECKPOINT, type + ":" + id));
			}
		}
	}

	/**
	 * when ALC detect a arrived event, this method called.
	 * @param siteId id of DropInSite which user enterd.
	 */
	public void downloadDailyPlan(long siteId){
		DropInSite point = dropInSitesOverlay.getDropInSite(siteId);
		if(point == null || devid == -1 || interval == -1){
			Toast.makeText(this, "Devid or Interval value is invalid.", Toast.LENGTH_SHORT).show();
			return;
		}
		ew.println(latest.getTimestamp() + ",Arraived at " + siteId);
		Toast.makeText(this, "Arraived at " + siteId + " and downloading...", Toast.LENGTH_SHORT).show();
		if(isSimulation){
			simulationExecuter.unregistAllEventDetection();
		}else{

		}

		dpdr = new DailyPlanDownloadRunner(point, dropInSitesOverlay.getDropInSites(), this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				dpdr.downloadDailyPlan();
				downloadExecutor.post(dpdr);
			}
		}).start();
	}

	/**
	 * 立ち寄りポイントを出発するイベントを登録するメソッド．
	 * @param isNotSuccess 移動実績のダウンロードに成功した場合，true．
	 * @param point 現在いる立ち寄りポイント．
	 */
	public void readyForLeave(boolean isNotSuccess, DropInSite point){
		if(isNotSuccess){
			Toast.makeText(this, "can't download Movement Result!", Toast.LENGTH_SHORT).show();
			ew.println(latest.getTimestamp() + ",can't download Movement Result!");
			return;
		}

		if(isSimulation){
			simulationExecuter.registEventDetection(
					AssistUtils.GOOUT_DROP_IN_SITE + String.valueOf(point.getSiteId()), 
					goOutDropInSiteRequestMap.get(AssistUtils.GOOUT_DROP_IN_SITE + String.valueOf(point.getSiteId())));
		}else{
			//TODO such as simulation
			EventDetectionRequest edr = 
					goOutDropInSiteRequestMap.get(AssistUtils.GOOUT_DROP_IN_SITE + String.valueOf(point.getSiteId()));
			if(edr == null){
				Toast.makeText(this, "There is not GoOut event", Toast.LENGTH_SHORT).show();
				return;
			}
			registEventDetection(edr);
		}
	}

	/**
	 * if simulationExecuter detect a leave event, this is called.
	 * DropInStiteの場合、Enter"ID" 例："Enter7105"
	 * TransferPointの場合、"移動手段名":"IDtoID":"Order" 例："WALK:7105to7106:1"
	 */
	public void leaveFrom(String key){
		if(key.contains(AssistUtils.GOOUT_DROP_IN_SITE)){
			String type = goOutDropInSiteRequestMap.get(key).getReply().getStringExtra("type");
			String id = goOutDropInSiteRequestMap.get(key).getReply().getStringExtra("id");
			handler.sendMessage(Message.obtain(handler, 
					ArrivedHandler.LEAVE_FROM_DROPINSITE, type + ":" + id));
		}
	}

	/**
	 * 立ち寄りポイントから出発したときに呼ばれるメソッド．
	 * 到着時間推定を開始する．
	 * @param siteId 出発した立ち寄りポイントID．
	 */
	public void startPlanCheck(long siteId){
		DropInSite point = dropInSitesOverlay.getDropInSite(siteId);
		if(point == null || devid == -1 || interval == -1){
			Toast.makeText(this, "Devid or Interval value is invalid.", Toast.LENGTH_SHORT).show();
			return;
		}
		readyForArrive(point);

		ArrayList<MovementResult> movementResults = dpdr.getMovementResults();
		if(movementResults == null){
			Toast.makeText(this, "Leave from " + siteId + " but movmentResults are not correct",
					Toast.LENGTH_SHORT).show();
			ew.println(latest.getTimestamp() + ",Leave from " + siteId + " but movmentResults are not correct");
		}else{
			Toast.makeText(this, "Start Plan Check! From " + siteId, Toast.LENGTH_SHORT).show();
			ew.println(latest.getTimestamp() + ",Start Plan Check! From " + siteId);
			dropInSitesOverlay.setVisibleMovementResult(movementResults);

			dropInSitesOverlay.setAllArrivalTimes(siteId, movementResults, latest.getTimestamp().getTime());
			dropInSitesOverlay.showArraivalTimes();
			setTransferPointEvent(movementResults);

			simulationExecuter.registEventDetections(enterTransferPointRequestMap);
			setCheckPointEvent(0, movementResults);
			simulationExecuter.registEventDetections(enterCheckPointRequestMap);

			pc.setMovementResults(movementResults);
			pc.startEstimate(siteId);
		}
	}

	/**
	 * 次に到着するであろう立ち寄りポイントすべてのイベントを登録する．
	 * @param point 現在いる立ち寄りポイント．
	 */
	public void readyForArrive(DropInSite point){
		if(isSimulation){
			simulationExecuter.unregistEventDetection(
					AssistUtils.GOOUT_DROP_IN_SITE + String.valueOf(point.getSiteId()));
			for(TransitionBetweenDropInSite t : point.getTransitions()){
				simulationExecuter.registEventDetection(
						AssistUtils.ENTER_DROP_IN_SITE + String.valueOf(t.getToID()), 
						enterDropInSiteRequestMap.get(AssistUtils.ENTER_DROP_IN_SITE + String.valueOf(t.getToID())));
			}
		}else{
			//TODO such as simulation
			unregistDropInSiteEvent(AssistUtils.GOOUT_DROP_IN_SITE, point);
			EventDetectionRequest edr = enterDropInSiteRequestMap.get(
							AssistUtils.ENTER_DROP_IN_SITE + String.valueOf(point.getSiteId()));
			if(edr == null){
				Toast.makeText(this, "There is not GoOut event", Toast.LENGTH_SHORT).show();
				return;
			}
			registEventDetection(edr);
		}
	}

	/**
	 * すべての乗り換えポイントへ到着するイベントを登録するメソッド．
	 * TODO 現在、すべての乗り換えポイントを登録しているが、直近の乗り換えポイントのみの方がいい？（移動手段誤判定の可能性）
	 * @param points 登録するポイントのリスト．
	 */
	public void setTransferPointEvent(ArrayList<MovementResult> movementResults){
		if(movementResults == null){
			return;
		}

		ArrayList<TransferPoint> points = new ArrayList<TransferPoint>();
		for(MovementResult mr : movementResults){
			points.addAll(mr.getAllTransferPoint());
		}

		this.enterTransferPointRequestMap = new HashMap<String, EventDetectionRequest>();

		int replyId = 0;
		for(TransferPoint point : points){
			String key = String.valueOf(point.getTransportationNames() + ":" + 
					point.getFromid() + "to" + point.getToid() + ":" + point.getOrder());
			EventDetectionRequest enterEvent = newEventDetectionRequest(
					PACKAGE_KEY + "." + replyId + ".Enter", "TransferPoint", 
					key, point, MatchingConstants.SMALLER_THAN);

			enterTransferPointRequestMap.put(
					AssistUtils.ENTER_TRANSFER + key, enterEvent);
			Log.v("setTransferPointEvent", "Enter" + key);
			replyId++;
		}
	}

	/**
	 * 各移動実績のorder番目の移動手段のチェックポイントを取得するメソッド．
	 * チェックポイントへ到着するイベントを登録するメソッド．
	 * @param order 移動手段のn番目．
	 * @param movementResults 移動実績．
	 */
	public void setCheckPointEvent(int order, ArrayList<MovementResult> movementResults){
		if(movementResults == null){
			return;
		}

		ArrayList<CheckPoint> points = new ArrayList<CheckPoint>();
		for(MovementResult mr : movementResults){
			points.addAll(mr.getCheckPoints(order));
		}
		this.enterCheckPointRequestMap = new HashMap<String, EventDetectionRequest>();

		int replyId = 0;
		for(CheckPoint point : points){
			String key = String.valueOf(point.getTransportationNames() + ":" + 
					point.getFromid() + "to" + point.getToid() + ":" + point.getOrder());
			EventDetectionRequest enterEvent = newEventDetectionRequest(
					PACKAGE_KEY + "." + replyId + ".Enter", "CheckPoint", 
					key, point, MatchingConstants.SMALLER_THAN);

			enterCheckPointRequestMap.put(
					AssistUtils.ENTER_CHECKPOINT + key, enterEvent);
			Log.v("setCheckPointEvent", "Enter" + key);
			replyId++;
		}
	}

	/**
	 * チェックポイントに到着した際に呼ばれるメソッド．
	 * 到着時間を推定する．
	 * @param key 到着判定したイベントのkey．
	 */
	public void estimateArrivedTime(String key){
		String transportationName = key.split(":")[0];
		try {
			long fromid = Long.parseLong(key.split(":")[1].split("to")[0]);
			long toid = Long.parseLong(key.split(":")[1].split("to")[1]);
			int order = Integer.parseInt(key.split(":")[2]);
			Toast.makeText(this, "Arraived at CP" + order + " of " + transportationName + " in " +
					fromid + " to " + toid, Toast.LENGTH_SHORT).show();
			ew.println(latest.getTimestamp() + "Arraived at CP" + order + " of " + transportationName + 
					" in " + fromid + " to " + toid);
			pc.arrivedAt(transportationName, fromid, toid, order);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * あるチェックポイントに到達した時に，
	 * -そのチェックポイントを保持している目的地のバルーンを更新する．
	 * -メイン画面に移動手段を表示させたい．
	 */
	public void visualizeStatus(long toId, long toTransfer, int statusTotransfer, 
			long toDestination, int statusToDestination){
		Log.v("estimateStatus", "transfer:" + toTransfer + ", destination:" + toDestination);
		dropInSitesOverlay.updateArrivalTimes(toId, toTransfer, toDestination);
	}

	/**
	 * 
	 * @param key
	 */
	public void selectTransportaion(String key){
		String transportationName = key.split(":")[0];
		try {
			long fromid = Long.parseLong(key.split(":")[1].split("to")[0]);
			long toid = Long.parseLong(key.split(":")[1].split("to")[1]);
			int order = Integer.parseInt(key.split(":")[2]);
			Toast.makeText(this, "Arraived at TP" + order + " of " + transportationName + " in " +
					fromid + " to " + toid, Toast.LENGTH_SHORT).show();
			ew.println(latest.getTimestamp() + "Arraived at TP" + order + " of " + transportationName + 
					" in " + fromid + " to " + toid);
			//TODO 到着した乗り換えポイント以外の移動を候補から削除
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void unregistDropInSiteEvent(String type, DropInSite point) {
		if(!AssistUtils.ENTER_DROP_IN_SITE.equals(type) && !AssistUtils.GOOUT_DROP_IN_SITE.equals(type)){
			return;
		}
		EventDetectionRequest enter = ("Enter".equals(type)) ? 
				enterDropInSiteRequestMap.get(type + String.valueOf(point.getSiteId())) :
					goOutDropInSiteRequestMap.get(type + String.valueOf(point.getSiteId()));
				if(enter != null){
					Intent intent = new Intent(MatchingConstants.QUERY_REMOVE);
					intent.putExtra("ID", enter.getIntentKey());
					Log.e("removeEnterDropInSiteEvent", type + " send " + String.valueOf(point.getSiteId()));
					sendBroadcast(intent);
				}
	}


	public void setArrivedAtCheckPointEvent(){
		//		for(String key : pdr.getPlanChecker().getRoutes().keySet()){
		//			Path route = pdr.getPlanChecker().getRoutes().get(key);
		//			final int BLOCK_SIZE = route.getPathBlocks().size();
		//			for(int j = 0 ; j < BLOCK_SIZE ; j++){
		//				PathBlock block = route.getPathBlocks().get(j);
		//				request.add(newEventDetectionRequest("ID" + replyId, route.getPathId(), String.valueOf(j), 
		//						block.getEndCheckPoint()));
		//				replyId++;
		//			}
		//		}
		//		for(String key : cpdr.getBranchPoints().keySet()){
		//			request.add(newEventDetectionRequest(
		//					"BP" + replyId, key.split(":")[0], key.split(":")[1], cpdr.getBranchPoints().get(key)));
		//			replyId++;
		//		}
	}

	public void setExpectedArrivalTime(Calendar c, DropInSite site){
		pc.addExpectedArrivalTime(site, c);
		Log.v("ArrivingListener", c.toString());
	}

	/**
	 * ALCへイベントを登録するメソッド．シミュレーションの際は使わない．
	 * @param request　イベント．
	 */
	public void registEventDetection(EventDetectionRequest request){
		if(request == null){
			return;
		}
		EventDetectionRequest query[] = new EventDetectionRequest[1];
		query[0] = request;
		try {
			Log.v("setEventDetection", query[0].toString());
			service.setIntentFilter(query[0].getReply().getAction());

			Intent requestIntent = new Intent(MatchingConstants.QUERY);
			requestIntent.putExtra(
					MatchingConstants.getKey(MatchingConstants.NOTIFICATION), query);
			sendBroadcast(requestIntent);

			Log.v("sendMatchingRequest", "Notification=" + query.length + " | " + requestIntent.toString());
		} catch (RemoteException e) {
			Log.e(getClass().getSimpleName(), e.toString(), e );
		}
	}

	/**
	 * ALCへイベント群を登録するメソッド．シミュレーションの際は使わない．
	 * @param requestMap イベント群．
	 */
	public void registEventDetections(HashMap<String, EventDetectionRequest> requestMap){
		if(requestMap == null || requestMap.isEmpty()){
			return;
		}
		EventDetectionRequest query[] = requestMap.values().toArray(new EventDetectionRequest[1]);
		try {
			int length = query.length;
			for(int i = 0 ; i < length ; i++){
				Log.v("setEventDetections", query[i].toString());
				service.setIntentFilter(query[i].getReply().getAction());
			}

			Intent requestIntent = new Intent(MatchingConstants.QUERY);
			requestIntent.putExtra(
					MatchingConstants.getKey(MatchingConstants.NOTIFICATION), query);
			sendBroadcast(requestIntent);

			Log.v("sendMatchingRequest", "Notification=" + query.length + " | " + requestIntent.toString());
		} catch (RemoteException e) {
			Log.e(getClass().getSimpleName(), e.toString(), e );
		}
	}

	public void addCheckPointOverlay(){
		overlayList = kubiwaMap.getOverlays();
		if(cpdr == null){
			return;
		}
		if(cpOverlay == null){
			cpOverlay = new CheckPointOverlay(this);
		}
		cpOverlay.addCheckPoints(cpdr.getAllPoints().get("3"));
		if(!overlayList.contains(cpOverlay)){
			overlayList.add(cpOverlay);
		}
		kubiwaMap.invalidate();
	}

	public void finishAssist(){
		//		Calendar finishTime = Calendar.getInstance();
		//		Toast.makeText(this, "Start:\n" + startTime.getTime() + "\nEnd:\n" + finishTime.getTime(), 
		//				Toast.LENGTH_LONG).show();
		//		resetTableLayout();
	}

	@Override
	public void downloadDrivingDirection(MyGeoPoint origin, MyGeoPoint destination) {
		if(origin == null || destination == null){
			return;
		}
		dddr = new DrivingDirectionDownloadRunner(this, origin, destination);
		new Thread(new Runnable() {
			@Override
			public void run() {
				dddr.download();
				downloadExecutor.post(dddr);
			}
		}).start();

		showProgressDialog("非日常の移動", "プラン生成&ダウンロード中...");
	}

	public void addDrivingDirectionOverlay(boolean isNotSuccess){
		cancelProgressDialog();

		if(isNotSuccess){
			return;
		}

		overlayList = kubiwaMap.getOverlays();
		if(dddr == null){
			return;
		}
		Drawable d = this.getResources().getDrawable(android.R.drawable.ic_menu_compass);
		ddOverlay = new DrivingDirectionOverlay(d, kubiwaMap, this);

		ddOverlay.setDddr(dddr);
		if(!overlayList.contains(ddOverlay)){
			overlayList.add(ddOverlay);
		}

		setPlanText();
		kubiwaMap.invalidate();
	}

	private void setPlanText() {
		if(ddOverlay == null || dddr == null){
			Log.e("setPlanText", "ddOverlay == null || dddr == null");
			return;
		}
		switch (ddOverlay.getCount()) {
		case 0:
			meansOfTransportationText.setText("Only Car");
			break;
		case 1:
			meansOfTransportationText.setText("Only Walk");
			break;
		case 2:
			meansOfTransportationText.setText("Only Bicycle");
			break;

		case 3:
			meansOfTransportationText.setText("Car Train Walk");
			break;
		case 4:
			meansOfTransportationText.setText("Walk Train Walk");
			break;
		case 5:
			meansOfTransportationText.setText("Bicycle Train Walk");
			break;
		default:
			break;
		}
	}

	public MapView getKubiwaMap(){
		return kubiwaMap;
	}

	public EvaluationWriter getWriter() {
		return ew;
	}
}