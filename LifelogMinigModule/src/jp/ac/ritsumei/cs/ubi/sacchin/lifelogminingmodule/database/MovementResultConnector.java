/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.database;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.plan.DropInSite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 最終更新日 2013/01/27 18:00:00
 * 立ち寄りポイントと移動実績を格納するDBのへのコネクタ．
 *　このクラスでは，基本的なselect,insert,delete,updateしかしていないのでJavaDoc省略．
 * @author sacchin
 *
 */
public class MovementResultConnector extends SqlConnector{
	final String TRANSITION_UPDATE_SQL = 
			"update dropinsite set transition = ? where devid = ? and id = ?";
	final String DROPINSITE_IDSELECT_SQL = 
			"select * from dropinsite where name = ? and devid = ?;";
	final String DROPINSITE_DELETE_SQL = 
			"delete from dropinsite where devid = ?;";
	final String DROPINSITE_SELECT_SQL = 
			"select * from dropinsite where devid = ?;";
	final String DROPINSITE_INSERT_SQL = 
			"insert into dropinsite(name, stay_count, stay_average, lat, lng, radius, maybenoise, transition, devid, stay_times)" +
					" values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	
	final String DROPINSITE_LABEL_SELECT_SQL = 
			"select * from dropinsite_label where devid = ? and id = ?;";
	final String DROPINSITE_LABEL_INSERT_SQL = 
			"insert into dropinsite_label(id, devid, label) values (?, ?, ?);";
	final String DROPINSITE_LABEL_UPDATE_SQL = 
			"update dropinsite set label = ? where devid = ? and id = ?";
	
	final String MINING_SETTING_ALLSELECT_SQL = 
			"select * from mining_settings;";
	final String MINING_SETTING_SELECT_SQL = 
			"select * from mining_settings where devid = ?;";
	final String MINING_SETTING_INSERT_SQL = 
			"insert into mining_settings(devid, mining_interval) values(?, ?);";
	final String MINING_SETTING_UPDATE_SQL = 
			"update mining_settings set mining_interval = ? WHERE devid = ?;";

	final String MOVEMENT_RESULT_INSERT_SQL = 
			"insert into movement_result(devid, fromid, toid, result_json) values(?, ?, ?, ?);";
	final String MOVEMENT_RESULT_SELECT_SQL = 
			"select * from movement_result where devid = ? and fromid = ? and toid = ?;";
	final String MOVEMENT_RESULT_ALLSELECT_SQL = 
			"select * from movement_result where devid = ?;";
	final String MOVEMENT_RESULT_DELETE_SQL = 
			"delete from movement_result where devid = ?;";

	final String MOVEMENT_LOG_INSERT_SQL = 
			"insert into movement_logs(devid, mrid, result_json) values(?, ?, ?);";
	final String MOVEMENT_LOG_DELETE_SQL = 
			"delete from movement_logs where devid = ?;";
	final String MOVEMENT_LOG_SELECT_SQL = 
			"select * from movement_logs where devid = ?;";
	
	public MovementResultConnector(String dataBaseUrl, String dataBaseUser, String dataBasePassword) 
			throws IOException{
		super(dataBaseUrl, dataBaseUser, dataBasePassword);
	}
	
	private void close(PreparedStatement statement, ResultSet resultset)
			throws SQLException {
		if(statement != null && !statement.isClosed()){
			statement.close();
		}
		if(resultset != null && !resultset.isClosed()){
			resultset.close();
		}
	}
	
	public void checkConection(){
		try {
			if(connection == null || connection.isClosed()){
				System.out.println("status is [" + String.valueOf(connection) + "]");
				createConnection();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean updatetTransition(int id, int devid, String transition) throws SQLException{
		PreparedStatement statement = null;
		ResultSet resultset = null;
		if(id < 0 || devid < 0 || transition == null){
			return false;
		}
		try {
			statement = (PreparedStatement) connection.prepareStatement(TRANSITION_UPDATE_SQL);
			statement.setString(1, transition);
			statement.setInt(2, devid);
			statement.setInt(3, id);

			statement.execute();
			return true;
		}finally{
			close(statement, resultset);
		}
	}
	
	public int selectDropInSiteID(String name, int devid) throws SQLException{
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(DROPINSITE_IDSELECT_SQL);
			statement.setString(1, name);
			statement.setInt(2, devid);

			resultset = statement.executeQuery();

			if(resultset.next()){
				return resultset.getInt("id");
			}
			return -1;
		}finally{
			close(statement, resultset);
		}
	}
	
	public void deleteDropInSites(int devid) throws SQLException{
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(DROPINSITE_DELETE_SQL);
			statement.setInt(1, devid);

			statement.execute();
		}finally{
			close(statement, resultset);
		}
	}
	
	public JSONArray selectDropInSites(int devid) throws SQLException{
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(DROPINSITE_SELECT_SQL);
			statement.setInt(1, devid);

			resultset = statement.executeQuery();

			JSONArray jArray = new JSONArray();
			while(resultset.next()){
				JSONObject dropInSite = DropInSite.create(
						resultset.getInt("id"),
						resultset.getString("name"),
						resultset.getDouble("lat"),
						resultset.getDouble("lng"),
						resultset.getInt("radius"),
						resultset.getInt("stay_count"),
						resultset.getLong("stay_average"),
						resultset.getBoolean("maybenoise"),
						resultset.getString("transition"),
						resultset.getString("stay_times"),
						resultset.getInt("devid"));
				if(dropInSite != null){
					jArray.put(dropInSite);
				}
			}
			return jArray;

		}finally{
			close(statement, resultset);
		}
	}
	
	private void insert(String siteId, int count, int average, double lat,
			double lng, JSONArray transitions, boolean mayBeNoise, int devid, int radius, JSONArray stayTimes) throws SQLException {
		if(transitions == null || stayTimes == null){
			return;
		}
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(DROPINSITE_INSERT_SQL);
			statement.setString(1, siteId);
			statement.setInt(2, count);
			statement.setLong(3, average);
			statement.setDouble(4, lat);
			statement.setDouble(5, lng);
			statement.setInt(6, radius);
			statement.setBoolean(7, mayBeNoise);
			statement.setString(8, transitions.toString());
			statement.setInt(9, devid);
			statement.setString(10, stayTimes.toString());

			statement.execute();
		}finally{
			close(statement, resultset);
		}
	}
	
	public String selectDropInSiteLabel(int devid, int id) throws SQLException{
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(DROPINSITE_LABEL_SELECT_SQL);
			statement.setInt(1, devid);
			statement.setInt(2, id);

			resultset = statement.executeQuery();

			if(resultset.next()){
				return resultset.getString("label");
			}else{
				return "";
			}
		}finally{
			close(statement, resultset);
		}
	}

	public boolean insertDropInSiteLabel(int devid, int id, String label) throws SQLException{
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(DROPINSITE_LABEL_INSERT_SQL);
			statement.setInt(1, id);
			statement.setInt(2, devid);
			statement.setString(3, label);

			statement.execute();
			return true;
		}finally{
			close(statement, resultset);
		}
	}
	
	public boolean updatetDropInSiteLabel(int devid, int id, String label) throws SQLException{
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(DROPINSITE_LABEL_UPDATE_SQL);
			statement.setString(1, label);
			statement.setInt(2, devid);
			statement.setInt(3, id);

			statement.execute();
			return true;
		}finally{
			close(statement, resultset);
		}
	}
	
	public Map<Integer, Map<Integer, Timestamp>> selectAllMiningSetting() throws SQLException{
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(MINING_SETTING_ALLSELECT_SQL);
			resultset = statement.executeQuery();

			Map<Integer, Map<Integer, Timestamp>> settings = new HashMap<Integer, Map<Integer, Timestamp>>();
			while(resultset.next()){
				Map<Integer, Timestamp> setting = new HashMap<Integer, Timestamp>();
				int devid = resultset.getInt("devid");
				int interval = resultset.getInt("mining_interval");
				Timestamp start = resultset.getTimestamp("start");
				setting.put(interval, start);
				settings.put(devid, setting);
			}
			return settings;
		}finally{
			close(statement, resultset);
		}
	}
	
	public int selectMiningSetting(int devid) throws SQLException{
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(MINING_SETTING_SELECT_SQL);
			statement.setInt(1, devid);

			resultset = statement.executeQuery();

			if(resultset.next()){
				return resultset.getInt("mining_interval");
			}else{
				return -1;
			}
		}finally{
			close(statement, resultset);
		}
	}
	
	public Map<String, Integer> selectMiningSetting() throws SQLException{
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(MINING_SETTING_SELECT_SQL);
			resultset = statement.executeQuery();
			
			HashMap<String, Integer> settings = new HashMap<String, Integer>();
			while(resultset.next()){
				int devid = resultset.getInt("devid");
				int interval = resultset.getInt("mining_interval");
				settings.put(String.valueOf(devid), interval);
			}
			return settings;
		}finally{
			close(statement, resultset);
		}
	}

	public boolean updatetMiningSetting(int devid, int interval) throws SQLException{
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(MINING_SETTING_UPDATE_SQL);
			statement.setInt(1, interval);
			statement.setInt(2, devid);

			statement.execute();
			return true;
		}finally{
			close(statement, resultset);
		}
	}
	
	public boolean insertMiningSetting(int devid, int interval) throws SQLException{
		if(devid < 0 || interval <= 0){
			return false;
		}
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(MINING_SETTING_INSERT_SQL);
			statement.setInt(1, devid);
			statement.setInt(2, interval);

			statement.execute();
			return true;
		}finally{
			close(statement, resultset);
		}
	}
	
	public void insertDropInSite(JSONObject site, int devid) throws SQLException{
		if(site == null || devid < 0){
			return;
		}
		try {
			String siteId = String.valueOf(site.getLong("id"));
			int count = site.getInt("count");
			int average = site.getInt("average");
			double lat = (site.getDouble("maxLat") + site.getDouble("minLat")) / 2;	
			double lng = (site.getDouble("maxLng") + site.getDouble("minLng")) / 2;
			JSONArray transitions = site.getJSONArray("transition");
			JSONArray stayTimes = site.getJSONArray("stayTime");
			int radius = site.getInt("radius");
			boolean mayBeNoise = site.getBoolean("isNoise");

			insert(siteId, count, average, lat, lng, transitions, 
					mayBeNoise, devid, radius, stayTimes);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, JSONArray> selectMovementResult(int devid) throws SQLException{
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(MOVEMENT_RESULT_ALLSELECT_SQL);
			statement.setInt(1, devid);

			resultset = statement.executeQuery();
			
			HashMap<String, JSONArray> result = new HashMap<String, JSONArray>();
			while(resultset.next()){
				String ids = resultset.getInt("fromid") + "to" + resultset.getInt("toid");
				result.put(ids, new JSONArray(resultset.getString("result_json")));
			}
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
		}finally{
			close(statement, resultset);
		}
		return null;
	}
	
	public void insertMovemetResult(JSONArray movementResult, int devid, long fromId, long toId) throws SQLException{
		if(movementResult == null || devid < 0 || fromId < 0 || toId < 0){
			return;
		}

		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(MOVEMENT_RESULT_INSERT_SQL);
			statement.setInt(1, devid);
			statement.setLong(2, fromId);
			statement.setLong(3, toId);
			statement.setString(4, movementResult.toString());

			statement.execute();
		}finally{
			close(statement, resultset);
		}
	}

	public void insertMovemetLog(JSONArray movementLog, int devid, int mrId) throws SQLException{
		if(movementLog == null || devid < 0 || mrId < 0){
			return;
		}

		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(MOVEMENT_LOG_INSERT_SQL);
			statement.setInt(1, devid);
			statement.setLong(2, mrId);
			statement.setString(3, movementLog.toString());

			statement.execute();
		}finally{
			close(statement, resultset);
		}
	}
	
	public int deleteMovementLogs(int devid) throws SQLException{
		if(devid < 0){
			return -1;
		}
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(MOVEMENT_LOG_DELETE_SQL);
			statement.setInt(1, devid);

			return statement.executeUpdate();
		}finally{
			close(statement, resultset);
		}
	}
	
	public int deleteMovementResults(int devid) throws SQLException{
		if(devid < 0){
			return -1;
		}
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(MOVEMENT_RESULT_DELETE_SQL);
			statement.setInt(1, devid);

			return statement.executeUpdate();
		}finally{
			close(statement, resultset);
		}
	}

	public int selectMovemetResultID(int devid, long fromId, long toId) throws SQLException{
		if(devid < 0 || fromId < 0 || toId < 0){
			return -1;
		}

		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(MOVEMENT_RESULT_SELECT_SQL);
			statement.setInt(1, devid);
			statement.setLong(2, fromId);
			statement.setLong(3, toId);

			ResultSet r = statement.executeQuery();
			if(r.next()){
				return r.getInt("id");
			}
			return -1;
		}finally{
			close(statement, resultset);
		}
	}

	public JSONArray selectMovemetResult(int devid, long fromId, long toId) throws SQLException{
		if(devid < 0 || fromId < 0 || toId < 0){
			return null;
		}

		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(MOVEMENT_RESULT_SELECT_SQL);
			statement.setInt(1, devid);
			statement.setLong(2, fromId);
			statement.setLong(3, toId);

			ResultSet r = statement.executeQuery();
			if(r.next()){
				return new JSONArray(r.getString("result_json"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}finally{
			close(statement, resultset);
		}
		return null;
	}
	
	public ArrayList<JSONArray> selectMovemetLogs(int devid) throws SQLException{
		if(devid < 0){
			return null;
		}

		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(MOVEMENT_LOG_SELECT_SQL);
			statement.setInt(1, devid);

			ResultSet r = statement.executeQuery();
			ArrayList<JSONArray> result = new ArrayList<JSONArray>();
			while(r.next()){
				result.add(new JSONArray(r.getString("result_json")));
			}
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
		}finally{
			close(statement, resultset);
		}
		return null;
	}
}
