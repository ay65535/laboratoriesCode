/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.net;

import java.net.URISyntaxException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.CheckPointsCreater;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.PlanChecker;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.GeoPointUtils;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.HttpConnector;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.MyGeoPoint;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.URLConstants;

public class EstimatedArrivalTimeDownloader implements Runnable{
	private static String DOWNLOD_URL = URLConstants.SERVER_URL + "?pattern=estimate";
			
	private final int devid;
	private final PlanChecker pc;
	
	private long elapseTime;
	private ArrayList<double[]> stayPoints = null;
	private MyGeoPoint latestPoint;
	private boolean isNotSuccess = false;
	private long estimatedTimeToDestination;
	private long estimatedTimeToTransfer;
	private JSONArray path;
	private String transportationName;
	private long fromid;
	private long toid;
	private int order;
	
	public void initForDownload(String transportationName, long fromid, long toid, int order){
		this.fromid = fromid;
		this.toid = toid;
		this.transportationName = transportationName;
		this.order = order;
		this.elapseTime = GeoPointUtils.getTime(pc.getTrajectory());
		this.stayPoints = CheckPointsCreater.createCheckpoints(pc.getTrajectory());
		this.latestPoint = pc.getTrajectory().get(pc.getTrajectory().size() - 1);
	}

	public EstimatedArrivalTimeDownloader(int devid, PlanChecker pc){
		this.pc = pc;
		this.devid = devid;
	}
	
	public void download() {
		if(stayPoints == null){
			return;
		}
		
		try{
			StringBuilder sb = new StringBuilder();
			for(double[] rect : stayPoints){
				sb.append(rect[0] + "," + rect[1] + "," + rect[2] + "," + rect[3] + ":");
			}
			String latest = latestPoint.getLatitude() + "," + latestPoint.getLongtitude();
			String jsonStr = HttpConnector.downloadDataForALine(DOWNLOD_URL +
					"&stays=" + sb.toString() + 
					"&elapse=" + elapseTime + 
					"&latestpoint=" + latest + 
					"&devid=" + devid);
			JSONObject jObject = new JSONObject(jsonStr);
			String status = jObject.getString("status");
			if(!"success".equals(status)){
				isNotSuccess = true;
				return;
			}
			estimatedTimeToDestination = jObject.getLong("toDestination");
			estimatedTimeToTransfer = jObject.getLong("toTransfer");
			path = jObject.getJSONArray("path");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		pc.estimateStatus(isNotSuccess, path, transportationName, 
				estimatedTimeToDestination, estimatedTimeToTransfer, order, fromid, toid);
	}
}
