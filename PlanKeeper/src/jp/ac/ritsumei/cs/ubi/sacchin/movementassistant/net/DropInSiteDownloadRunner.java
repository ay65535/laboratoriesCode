/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.net;

import java.net.URISyntaxException;
import java.util.ArrayList;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.EventNotificationActivity;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.DropInSite;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.HttpConnector;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.URLConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * this is Runnable class to download DropInSites.
 * @author sacchin
 *
 */
public class DropInSiteDownloadRunner implements Runnable{
	/**
	 * this is url of servlet.
	 */
	private static final String DOWNLOD_URL = URLConstants.SERVER_URL + "?pattern=dropinsites";
	
	/**
	 * list of DropInSites.
	 */
	private ArrayList<DropInSite> dropInSites = null;
	
	/**
	 * if can't download, this is true.
	 */
	private boolean isNotSuccess = false;
	private EventNotificationActivity activity = null;

	/**
	 * this is constructor.
	 * @param activity EventNotificationActivity.this
	 */
	public DropInSiteDownloadRunner(EventNotificationActivity activity){
		this.activity = activity;
	}

	/**
	 * download DropInSites from servlet and convert JSON format into ArrayLst.
	 */
	public void download() {
		if(dropInSites == null){
			dropInSites = new ArrayList<DropInSite>();
		}else{
			dropInSites.clear();
		}
		
		try{
			String jsonStr = HttpConnector.downloadDataForALine(DOWNLOD_URL + "&interval=" +
					activity.getInterval() + "&devid=" + activity.getDevid());
			JSONObject jObject = new JSONObject(jsonStr);
			String status = jObject.getString("status");
			if(!"success".equals(status)){
				isNotSuccess = true;
				return;
			}
			
			JSONArray jArray = jObject.getJSONArray("dropinsites");
			final int SIZE = jArray.length();
			for(int i = 0 ; i < SIZE; i++){
				JSONObject o = jArray.getJSONObject(i);
				DropInSite temp = DropInSite.createDropInSite(o);
				if(!temp.mayBeNoise()){
					dropInSites.add(temp);
				}
			}
			Log.v("DropInSiteDownloadRunner", "download " + dropInSites.size() + " site");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * return list of DropInSites.
	 * @return list of DropInSites.
	 */
	public ArrayList<DropInSite> getDropInSites() {
		return dropInSites;
	}

	/**
	 * if this.download() id finish, this is called.
	 */
	@Override
	public void run() {
		activity.startAssist(isNotSuccess);
	}
}
