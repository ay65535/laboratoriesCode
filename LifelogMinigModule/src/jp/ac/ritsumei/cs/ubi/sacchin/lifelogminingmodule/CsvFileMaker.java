package jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.database.BlackholeConnector;
import jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.database.MovementResultConnector;
import jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.plan.DropInSite;
import jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.plan.GeoPointUtils;
import jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.plan.TransitionBetweenDropInSite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CsvFileMaker {
	/**
	 * マイニング結果を格納するDBへのコネクター．
	 */
	private MovementResultConnector mConnector = null;

	/**
	 * マイニング結果を格納するDBへのコネクター．
	 */
	private BlackholeConnector bConnector = null;
	
	/**
	 * 各ユーザマイニングの設定．
	 */
	private Map<Integer, Map<Integer, Timestamp>> settings = null;

	public CsvFileMaker(Map<Integer, Map<Integer, Timestamp>> settings){
		this.settings = settings;
		try {
			mConnector = new MovementResultConnector(
					"jdbc:mysql://localhost:3306/movement_result?useUnicode=true&characterEncoding=utf8", 
					"plancheckeruser", "n0pr1vacy");
			mConnector.createConnection();
			bConnector = new BlackholeConnector(
					"jdbc:mysql://exp-www.ubi.cs.ritsumei.ac.jp:3306/blackhole?useUnicode=true&characterEncoding=utf8", 
					"kubiwauser", "n0pr1vacy");
			bConnector.createConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * MovementResultConnectorをcloseするメソッド．
	 */
	public void close(){
		try {
			if(mConnector != null && !mConnector.isClosed()){
				mConnector.close();
			}
			if(bConnector != null && !bConnector.isClosed()){
				bConnector.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * DBからユーザの設定をすべて読み込むメソッド．
	 * @return 全ユーザの設定情報．
	 * @throws SQLException
	 */
	public Map<Integer, Map<Integer, Timestamp>> loadAllSettings() throws SQLException{
		mConnector.checkConection();
		if(mConnector == null || mConnector.isClosed()){
			return null;
		}
		try {
			settings = mConnector.selectAllMiningSetting();
			return settings;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param devid
	 * @throws SQLException
	 */
	public void writeS(int devid) throws SQLException{
		if(bConnector == null || bConnector.isClosed()){
			return;
		}

		PrintWriter sqlPrintOut = null;
		try {
			sqlPrintOut = new PrintWriter(new BufferedWriter(
					new FileWriter("/home/user/alldaySql" + devid + ".txt")));
			
			Calendar today = Calendar.getInstance();
//			today.setTime(new Date(System.currentTimeMillis()));
			today.set(Calendar.MONTH, Calendar.FEBRUARY);
			today.set(Calendar.DAY_OF_MONTH, 5);
			today.set(Calendar.HOUR_OF_DAY, 5);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);

			ArrayList<String> sqls = bConnector.selectInterval(devid, today, 14);
			for(String sql : sqls){
				sqlPrintOut.println(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(sqlPrintOut != null){
				sqlPrintOut.close();
			}
		}
	}

	/**
	 * 
	 * @param devid
	 * @throws SQLException
	 */
	public void writeDropInSites(int devid) throws SQLException{
		mConnector.checkConection();
		if(mConnector == null || settings == null || mConnector.isClosed()){
			return;
		}

		PrintWriter dropinsitePrintOut = null;
		PrintWriter sqlPrintOut = null;
		try {
			dropinsitePrintOut = new PrintWriter(new BufferedWriter(
					new FileWriter("/home/user/dropinsite" + devid + ".csv")));
			sqlPrintOut = new PrintWriter(new BufferedWriter(
					new FileWriter("/home/user/transitionSql" + devid + ".txt")));
			dropinsitePrintOut.println("devid,id,lat,lng,average,count,noise,sum,Transitions");
			JSONArray dropInSites = mConnector.selectDropInSites(devid);
			for(int i = 0 ; i < dropInSites.length() ; i++){
				DropInSite site = DropInSite.createDropInSite(dropInSites.getJSONObject(i));

				int sum = 0;
				for(TransitionBetweenDropInSite tbds : site.getTransitions()){
					for(Timestamp[] f : tbds.getTravelTimes()){
						sqlPrintOut.println("select time, lat, lng from LocationLog where devid =" +
								devid + " and provider = 'gps' and time between '" +
								f[0] + "' and '" + f[1] + "'");
					}
					sum += tbds.getTransitionCount();
				}

				dropinsitePrintOut.println(devid + "," + 
						site.getSiteId() + "," + 
						site.getLatitude() + "," + 
						site.getLongtitude() + "," + 
						site.getStayAverage() + "," + 
						site.getStayCount() + "," + 
						site.mayBeNoise()  + "," + 
						sum + "," + 
						site.getTransitions().size());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if(dropinsitePrintOut != null){
				dropinsitePrintOut.close();
			}
			if(sqlPrintOut != null){
				sqlPrintOut.close();
			}
		}
	}

	/**
	 * 
	 * @param devid
	 * @throws SQLException
	 */
	public void writeMovementResult(int devid) throws SQLException{
		mConnector.checkConection();
		if(mConnector == null || settings == null || mConnector.isClosed()){
			return;
		}

		PrintWriter printOut = null;
		try {
			printOut = new PrintWriter(new BufferedWriter(
					new FileWriter("/home/user/movementResult" + devid + ".csv")));
			printOut.println("fromto,multiple,single,i,j,k,l,checkpoints,sArea,eArea,cps");
			HashMap<String, JSONArray> movementResults = mConnector.selectMovementResult(devid);

			for(String key : movementResults.keySet()){
				JSONArray movementResult = movementResults.get(key);
				for(int i = 0 ; i < movementResult.length() ; i++){
					JSONObject o = movementResult.getJSONObject(i);
					JSONArray names = o.names();
					for(int j = 0 ; j < names.length() ; j++){
						JSONArray multiple = o.getJSONArray(names.getString(j));
						for(int k = 0 ; k < multiple.length() ; k++){
							JSONObject single = multiple.getJSONObject(k);
							String mode = single.getString("mode");
							JSONArray routes = single.getJSONArray("routes");
							for(int l = 0 ; l < routes.length() ; l++){
								JSONObject route = routes.getJSONObject(l);
								double sArea = calcArea(route.getJSONObject("startpoint"));
								double eArea = calcArea(route.getJSONObject("endpoint"));
								JSONArray cp = route.getJSONArray("checkpoints");
								
								String cps = "";
								for(int m = 0 ; m < cp.length() ; m++){
									cps += calcArea(cp.getJSONObject(m)) + ",";
								}
								printOut.println(key + "," + 
										names.getString(j) + "," + 
										mode + "," +
										i + "," + 
										j + "," + 
										k + "," + 
										l + "," + 
										route.getJSONArray("checkpoints").length() + "," +
										sArea + "," + 
										eArea + "," + 
										cps);
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if(printOut != null){
				printOut.close();
			}
		}
	}
	
	public double calcArea(JSONObject point){
		try {
			double m1 = GeoPointUtils.calcDistanceHubery(
					point.getDouble("minLat"), point.getDouble("minLng"), 
					point.getDouble("minLat"), point.getDouble("maxLng"), GeoPointUtils.GRS80);
			
			double m2 = GeoPointUtils.calcDistanceHubery(
					point.getDouble("minLat"), point.getDouble("minLng"), 
					point.getDouble("maxLat"), point.getDouble("minLng"), GeoPointUtils.GRS80);
			return (m1 * m2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 
	 * @param devid
	 * @throws SQLException
	 */
	public void writeMovementLogs(int devid) throws SQLException{
		mConnector.checkConection();
		if(mConnector == null || settings == null || mConnector.isClosed()){
			return;
		}

		PrintWriter printOut = null;
		try {
			printOut = new PrintWriter(new BufferedWriter(
					new FileWriter("/home/user/movementLogs" + devid + ".csv")));
			printOut.println("time,mode,1,2,3,");
			ArrayList<JSONArray> movementLogs = mConnector.selectMovemetLogs(devid);
			for(int i = 0 ; i < movementLogs.size() ; i++){
				JSONArray a = movementLogs.get(i);
				String modes = "";
				String points = "";
				String times = "";
				for(int k = 0 ; k < a.length() ; k++){
					JSONObject single = a.getJSONObject(k);
					times += new Timestamp(single.getJSONObject("startpoint").getLong("time")) + ",";
					modes += single.getString("mode");
					points += single.getJSONArray("checkpoints").length() + ",";
				}
				printOut.println(modes + "," + points + times);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if(printOut != null){
				printOut.close();
			}
		}
	}
}
