/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.ac.ritsumei.cs.ubi.createplan.plancreator.ArrivalTimeEstimater;
import jp.ac.ritsumei.cs.ubi.createplan.plancreator.MovementResultGetter;
import jp.ac.ritsumei.cs.ubi.createplan.plancreator.DropInSiteGetter;
import jp.ac.ritsumei.cs.ubi.createplan.plancreator.DirectionGetter;
import jp.ac.ritsumei.cs.ubi.createplan.plancreator.TrajectoryGetter;
import jp.ac.ritsumei.cs.ubi.createplan.setting.MiningSettingsManager;

/**
 * doPostでは以下をサポート
 * 	立ち寄りポイントのラベリング
 * 	マイニング設定の反映
 * 
 * doGetでは以下をサポート
 * 	立ち寄りポイントのダウンロード
 * 	移動実績のダウンロード
 * 	非日常の移動プランのダウンロード
 * 	シュミレーション用の移動軌跡のダウンロード
 * @author sacchin
 *
 */
public class PlanCreator extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		String pattern = req.getParameter("pattern");
		if("labeling".equals(pattern)){
			resp.setStatus(insertLabel(req, resp.getWriter()) ? 
					HttpStatus.SC_OK : HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
		if("setting".equals(pattern)){
			resp.setStatus(insertSetting(req) ? 
					HttpStatus.SC_OK : HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * 立ち寄りポイントへラベル付けするメソッド
	 * @param req リクエスト内容
	 * @param pw レスポンス書き込み用
	 * @return 成功したならtrue
	 */
	private boolean insertLabel(HttpServletRequest req, PrintWriter pw){
		try {
			String label = req.getParameter("label");
			int dropInSiteId = Integer.parseInt(req.getParameter("id"));
			int devid = Integer.parseInt(req.getParameter("devid"));

			return DropInSiteGetter.insertLabel(devid, dropInSiteId, label, pw);
		} catch (NumberFormatException e) {
			pw.println(e.toString());
			e.printStackTrace();
		} catch (SQLException e) {
			pw.println(e.toString());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			pw.println(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			pw.println(e.toString());
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * マイニング設定を書き込むメソッド
	 * @param req リクエスト内容
	 * @return 成功したならtrue
	 */
	private boolean insertSetting(HttpServletRequest req){
		try {
			int day = Integer.parseInt(req.getParameter("interval"));
			int devid = Integer.parseInt(req.getParameter("devid"));
			int y = Integer.parseInt(req.getParameter("year"));
			int m = Integer.parseInt(req.getParameter("month"));
			int d = Integer.parseInt(req.getParameter("day"));
			
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, y);
			c.set(Calendar.MONTH, m);
			c.set(Calendar.DAY_OF_MONTH, d);
			c.set(Calendar.HOUR_OF_DAY, 5);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			Timestamp start = new Timestamp(c.getTimeInMillis());
			return MiningSettingsManager.insertSetting(devid, day, start);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		PrintWriter pw = resp.getWriter();
		
		String pattern = req.getParameter("pattern");
		
		if("dropinsites".equals(pattern)){
			try {
				putDropInSites(req, pw);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if("daily".equals(pattern)){
			try {
				putMovementResult(req, pw);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}else if("notdaily".equals(pattern)){
			try {
				putNonDailyPlan(req, pw);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if("trajectory".equals(pattern)){
			try {
				putTrajectory(req, pw);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if("estimate".equals(pattern)){
			try {
				putEstimatedTime(req, pw);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			try {
				JSONObject jObject = new JSONObject();
				jObject.put("status", "pattern error");
				pw.println(jObject.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 非日常の移動プランをレスポンスに追加するメソッド
	 * @param req リクエスト内容
	 * @param pw レスポンス書き込み用
	 * @throws JSONException
	 */
	private void putNonDailyPlan(HttpServletRequest req, PrintWriter pw) throws JSONException {
		JSONObject jObject = new JSONObject();
		try {
			double originLat = Double.parseDouble(req.getParameter("originLat"));
			double originLng = Double.parseDouble(req.getParameter("originLng"));
			double destinationLat = Double.parseDouble(req.getParameter("destinationLat"));
			double destinationLng = Double.parseDouble(req.getParameter("destinationLng"));
			int devid = Integer.parseInt(req.getParameter("devid"));
			DirectionGetter dg = new DirectionGetter(pw, devid);

			JSONObject result = dg.downloadDrivingDirection(
					originLat, originLng, destinationLat, destinationLng);
			
			if(result == null){
				jObject.put("status", "error");
			}else{
				jObject.put("status", "success");
				jObject.put("nondailyplan", result);
			}
		} catch (NumberFormatException e) {
			jObject.put("status", "lat or lng is incorrect");
		} catch (ServletException e) {
			jObject.put("status", "devid or interval is incorrect");
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw.println(jObject.toString());
	}
	
	/**
	 * 移動実績をレスポンスに追加するメソッド
	 * @param req リクエスト内容
	 * @param pw レスポンス書き込み用
	 * @throws JSONException
	 */
	private void putMovementResult(HttpServletRequest req, PrintWriter pw) throws JSONException {
		JSONObject jObject = new JSONObject();
		try {
			int devid = Integer.parseInt(req.getParameter("devid"));
			String transition = req.getParameter("transition");
			int fromID = Integer.parseInt(transition.split("to")[0]);
			int toID = Integer.parseInt(transition.split("to")[1]);
			
			JSONArray movementResult = 
					MovementResultGetter.selectMovementResult(devid, fromID, toID);
			if(movementResult == null){
				jObject.put("status", "can't create MovementResult");
			}else{
				jObject.put("status", "success");
				jObject.put("movementResult", movementResult);
			}
		} catch (NumberFormatException e) {
			jObject.put("status", "devid or interval is incorrect");
		} catch (JSONException e) {
			jObject.put("status", "JSON error");
		}
		pw.println(jObject.toString());
	}
	
	/**
	 * 立ち寄りポイントをレスポンスに追加するメソッド
	 * @param req リクエスト内容
	 * @param pw レスポンス書き込み用
	 * @throws JSONException
	 */
	private void putDropInSites(HttpServletRequest req, PrintWriter pw) throws JSONException {
		JSONObject jObject = new JSONObject();
		try {
			int day = Integer.parseInt(req.getParameter("interval")) * -1;
			int devid = Integer.parseInt(req.getParameter("devid"));
			JSONArray jArray = DropInSiteGetter.getDropInSites(day, devid);
			if(jArray == null){
				jObject.put("status", "can't create DropInSites");
			}else{
				jObject.put("status", "success");
				jObject.put("dropinsites", jArray);
			}
		} catch (NumberFormatException e) {
			jObject.put("status", "devid or interval is incorrect");
		} catch (SQLException e) {
			jObject.put("status", "can't create DropInSites");
		} catch (JSONException e) {
			jObject.put("status", "JSON error");
		}
		pw.println(jObject.toString());
	}
	
	/**
	 * シュミレーション用の移動軌跡をレスポンスに追加するメソッド
	 * @param req リクエスト内容
	 * @param pw レスポンス書き込み用
	 * @throws JSONException
	 */
	private void putTrajectory(HttpServletRequest req, PrintWriter pw) throws JSONException {
		JSONObject jObject = new JSONObject();
		try {
			int year = Integer.parseInt(req.getParameter("y"));
			int month = Integer.parseInt(req.getParameter("m"));
			int day = Integer.parseInt(req.getParameter("d"));
			int hour = Integer.parseInt(req.getParameter("h"));
			int minute = Integer.parseInt(req.getParameter("mi"));
			int interval = Integer.parseInt(req.getParameter("i"));
			
			int devid = Integer.parseInt(req.getParameter("devid"));
			JSONArray jArray = TrajectoryGetter.getTrajectory(year, month, day, hour, minute, interval, devid);
			if(jArray == null){
				jObject.put("status", "can't select a trajectory");
			}else{
				jObject.put("status", "success");
				jObject.put("trajectory", jArray);
			}
		} catch (NumberFormatException e) {
			jObject.put("status", "devid, time or date is incorrect");
		} catch (SQLException e) {
			jObject.put("status", "can't select a Trajectory");
		} catch (JSONException e) {
			jObject.put("status", "JSON error");
		}
		pw.println(jObject.toString());
	}
	
	private void putEstimatedTime(HttpServletRequest req, PrintWriter pw) throws JSONException {
		JSONObject jObject = new JSONObject();
		try {
			String stayPointRects = req.getParameter("stays");
			String latestPoint = req.getParameter("latestpoint");
			int elapseTimeSec = Integer.parseInt(req.getParameter("elapse"));
			int devid = Integer.parseInt(req.getParameter("devid"));
			
			JSONArray jArray = ArrivalTimeEstimater.estimate(stayPointRects, latestPoint, elapseTimeSec, devid);
			if(jArray == null){
				jObject.put("status", "can't estimate");
			}else{
				jObject.put("status", "success");
				jObject.put("trajectory", jArray);
			}
		} catch (NumberFormatException e) {
			jObject.put("status", "elapseTimeSec or devid is incorrect");
		} catch (SQLException e) {
			jObject.put("status", "can't select a Logs");
		} catch (JSONException e) {
			jObject.put("status", "JSON error");
		}
		pw.println(jObject.toString());
	}
}
