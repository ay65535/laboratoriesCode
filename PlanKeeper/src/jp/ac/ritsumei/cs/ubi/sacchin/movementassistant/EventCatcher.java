/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant;

import java.util.HashMap;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/**
 * receive events which request to Android Logger Client
 * @author sacchin
 */
public class EventCatcher extends Service {
	protected MyReceiver broadcastReceiver;
	private boolean isRegisted = false;

	protected final RemoteCallbackList<IEventCallback> callbackList = new RemoteCallbackList<IEventCallback>();
	protected IEventDetecter.Stub serviceBinder;
			
	protected static class ServiceStub extends IEventDetecter.Stub{
		protected final EventCatcher service;
		
		public ServiceStub(EventCatcher service){
			this.service = service;
		}

		@Override
		public void registerEventCallback(IEventCallback callback)
				throws RemoteException {
			service.callbackList.register(callback);
		}

		@Override
		public void unregisterEventCallback(IEventCallback callback)
				throws RemoteException {
			service.callbackList.unregister(callback);
		}

		@Override
		public void setIntentFilter(String id) throws RemoteException {
			if(service.broadcastReceiver != null && id != null){
				Log.v("setIntentFilter", id);
				IntentFilter filter = new IntentFilter();
				filter.addAction(id);
				service.registerReceiver(service.broadcastReceiver, filter);
				service.isRegisted = true;
			}else{
				Log.e(id, "service.broadcastReceiver == null || id == null");
			}
		}

		@Override
		public void removeIntentFilter(String id) throws RemoteException {
			if(service.broadcastReceiver != null && service.isRegisted){
				service.unregisterReceiver(service.broadcastReceiver);
				service.isRegisted = false;
			}
		}
	};

	public class MyReceiver extends BroadcastReceiver {
		protected final EventCatcher service;
		HashMap<String, Integer> counts = new HashMap<String, Integer>();
		
		public MyReceiver(EventCatcher service){
			this.service = service;
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			String replayID = intent.getAction();
			String type = intent.getStringExtra("type");
			if(replayID == null || type == null){
				Log.v("EventCatcher.onReceive", "replayID == null || type == null");
				return;
			}

			Integer count = counts.get(type + replayID);
			if(count == null){
				counts.put(type + replayID, 1);
				return;
			}
			
			if(3 < count.intValue()){
				if(replayID.contains("Enter") && type.contains("DropInSite")){
					onArrivedAtDropInSite(intent);
				}else if(replayID.contains("GoOut") && type.contains("DropInSite")){
					onLeaveFromDropInSite(intent);
				}else{
					throw new NullPointerException("Method onReceive was called. but action is null.");
				}
			}else{
				counts.put(type + replayID, count + 1);
			}
		}
	}
	
	protected void onArrivedAtBusStop(Intent intent){
//		String name = intent.getAction();
//		int callbackListLength = callbackList.beginBroadcast();
//		for (int i = 0 ; i < callbackListLength ; i++) {
//			try {
//				Log.v("broadcastMessage",  name);
//				callbackList.getBroadcastItem(i).arrivedAtCheckPoint(name);
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			}
//		}
//		callbackList.finishBroadcast();
	}
	
	protected void onArrivedAtCheckPoint(Intent intent){
		String type = intent.getStringExtra("type");
		String id = intent.getStringExtra("id");
		int callbackListLength = callbackList.beginBroadcast();
		for (int i = 0 ; i < callbackListLength ; i++) {
			try {
				callbackList.getBroadcastItem(i).arrivedAtCheckPoint(type, id);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		callbackList.finishBroadcast();
	}

	protected void onArrivedAtDropInSite(Intent intent){
		String type = intent.getStringExtra("type");
		String id = intent.getStringExtra("id");
		int callbackListLength = callbackList.beginBroadcast();
		for (int i = 0 ; i < callbackListLength ; i++) {
			try {
				callbackList.getBroadcastItem(i).arrivedAtDropInsite(type, id);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		callbackList.finishBroadcast();
	}
	
	protected void onLeaveFromDropInSite(Intent intent){
		String type = intent.getStringExtra("type");
		String id = intent.getStringExtra("id");
		int callbackListLength = callbackList.beginBroadcast();
		for (int i = 0 ; i < callbackListLength ; i++) {
			try {
				callbackList.getBroadcastItem(i).leaveFromDropInsite(type, id);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		callbackList.finishBroadcast();
	}
	
	/**
	 * サービスがバインドされたとき
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return serviceBinder;
	}

	/**
	 * 初期化などを行う
	 * ブロードキャストインテントのフィルターも設定する
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		serviceBinder = new ServiceStub(this);
		broadcastReceiver = new MyReceiver(this);
	}

	/**
	 * サービスが終了したとき
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(broadcastReceiver != null){
			unregisterReceiver(broadcastReceiver);
		}
		Toast.makeText(this, "STOP", Toast.LENGTH_SHORT).show();
	}
}