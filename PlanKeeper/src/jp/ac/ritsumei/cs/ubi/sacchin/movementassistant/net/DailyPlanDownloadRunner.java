/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.net;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.EventNotificationActivity;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.DropInSite;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.MovementResult;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.TransitionBetweenDropInSite;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.HttpConnector;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.URLConstants;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * this is Runnable class to download DailyPlan.
 * @author sacchin
 *
 */
public class DailyPlanDownloadRunner implements Runnable{
	/**
	 * this is url of servlet.
	 */
	private static String DOWNLOD_URL = URLConstants.SERVER_URL + "?pattern=daily&";
	
	/**
	 * this is a DropInSite which user is arrived.
	 */
	private DropInSite now = null;
	
	/**
	 * all DropInSite.
	 */
	private HashMap<Long, DropInSite> dropInSites = null;

	/**
	 * MovementResultがある立ち寄りポイントからある立ち寄りポイントへ移動する２点間移動を表す．
	 * 到着した立ち寄りポイントから過去に移動したすべての立ち寄りポイントへの移動実績のリスト．
	 */
	private ArrayList<MovementResult> movementResults;
	
	/**
	 * if can't download, this is true.
	 */
	private boolean isNotSuccess = false;
	private EventNotificationActivity activity;

	/**
	 * this is constructor.
	 * @param now　到着した立ち寄りポイント．
	 * @param dropInSites すべての立ち寄りポイント．
	 * @param activity EventNotificationActivity.this
	 */
	public DailyPlanDownloadRunner(DropInSite now, HashMap<Long, DropInSite> dropInSites,
			EventNotificationActivity activity){
		this.activity = activity;
		this.now = now;
		this.dropInSites = dropInSites;
	}

	/**
	 * 移動実績をダウンロードするメソッド．
	 */
	public void downloadDailyPlan(){
		if(now == null || dropInSites == null){
			return;
		}
		if(movementResults == null){
			movementResults = new ArrayList<MovementResult>();
		}else{
			movementResults.clear();
		}

		ArrayList<TransitionBetweenDropInSite> transitions = now.getTransitions();
		for(TransitionBetweenDropInSite transition : transitions){
			try {
				String key = now.getSiteId() + "to" + transition.getToID();
				String jsonStr  = HttpConnector.downloadDataForALine(
						DOWNLOD_URL + "&devid=" + activity.getDevid() + "&transition=" + key);
				JSONObject jObject = new JSONObject(jsonStr);
				String status = jObject.getString("status");
				if(!"success".equals(status)){
					isNotSuccess = (isNotSuccess && true);
				}
				Log.v("downloadDailyPlan", "movementResult has " + 
						jObject.getJSONArray("movementResult").length() + "meansOfTransportation");
				
				MovementResult mr = MovementResult.createMovementResult(
						jObject.getJSONArray("movementResult"), key);
				if(mr != null){
					movementResults.add(mr);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isNotSuccess() {
		return isNotSuccess;
	}

	public ArrayList<MovementResult> getMovementResults() {
		return movementResults;
	}
	
	@Override
	public void run() {
		activity.readyForLeave(isNotSuccess, now);
	}
}
