/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.net;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan.CheckPoint;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.HttpConnector;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class CheckPointDownloadRunner implements Runnable{
//	private static final String DOWNLOD_URL = "http://blackhole.ubi.cs.ritsumei.ac.jp/getCheckPoint?id=";
	private static final String DOWNLOD_URL = "http://192.168.2.112:8089/servlet/getCheckPoint?id=";
//	private static final String DOWNLOD_URL = "http://192.168.2.112:8089/servlet/getCheckPoint?id=6&threshold=10";
	private HashMap<String, ArrayList<CheckPoint>> eachPathPoints = null;
	private HashMap<String, CheckPoint> branchPoints = null;
	
	private final int PATHID;
	private final String REJECTS;
	
	public CheckPointDownloadRunner(int pathid, String rejects){
		this.PATHID = pathid;
		this.REJECTS = rejects;
		this.eachPathPoints = new HashMap<String, ArrayList<CheckPoint>>();
		this.branchPoints = new HashMap<String, CheckPoint>();
	}
	
	public HashMap<String, ArrayList<CheckPoint>> getAllPoints() {
		return eachPathPoints;
	}
	
	public HashMap<String, CheckPoint> getBranchPoints() {
		return branchPoints;
	}

	@Override
	public void run() {
		downloadCheckPoint();
	}

	private void downloadCheckPoint() {
		try{
			eachPathPoints.put(String.valueOf(0), downloadCheckPoint(0, "-1"));
			eachPathPoints.put(String.valueOf(1), downloadCheckPoint(1, "-1"));
			eachPathPoints.put(String.valueOf(2), downloadCheckPoint(2, "-1"));
			eachPathPoints.put(String.valueOf(3), downloadCheckPoint(3, "-1"));
			Log.v("CheckPointDownloadRunner.run", "points=" + eachPathPoints.size() + "," + eachPathPoints.keySet());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<CheckPoint> downloadCheckPoint(int id, String rejects) 
			throws IOException, JSONException, URISyntaxException{
		ArrayList<String> jsonStrs = 
				HttpConnector.downloadDataForEachLine(DOWNLOD_URL + id + "&reject=" + rejects);
		ArrayList<CheckPoint> points = new ArrayList<CheckPoint>();
		
		Log.v("CheckPointDownloadRunner.run", String.valueOf(jsonStrs.size()));
		final int SIZE = jsonStrs.size();
		for(int i = 0 ; i < SIZE; i++){
			JSONObject o = new JSONObject(jsonStrs.get(i));
			CheckPoint t = null;//CheckPoint.createMergedPoint(o);
			points.add(t);
		}
		return points;
	}
}
