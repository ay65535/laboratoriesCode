package jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.database;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.plan.MyGeoPoint;

import org.json.JSONArray;
import org.json.JSONException;

import com.mysql.jdbc.PreparedStatement;

public class BlackholeConnector extends SqlConnector{

	final String TRAJECTORY_SELECT_SQL = 
			"select * from LocationLog where devid = ? and provider = 'gps' and time between ";

	public BlackholeConnector(String dataBaseUrl, String dataBaseUser, String dataBasePassword) 
			throws IOException{
		super(dataBaseUrl, dataBaseUser, dataBasePassword);
	}

	public ArrayList<String> selectInterval(int devid, Calendar today, int interval) throws SQLException{
		if(devid < 0 || interval < 0){
			return null;
		}

		Timestamp e = new Timestamp(today.getTimeInMillis());

		ArrayList<String> sqls = new ArrayList<String>();
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			while(true){
				today.add(Calendar.DAY_OF_MONTH, -1);
				Timestamp s = new Timestamp(today.getTimeInMillis());
				
				String sql = "select min(time), max(time) from LocationLog where devid = ?" +
						" and time between ? and ?";
				statement = (PreparedStatement) connection.prepareStatement(sql);
				statement.setInt(1, devid);
				statement.setTimestamp(2, s);
				statement.setTimestamp(3, e);
				
				System.out.println(statement);
				resultset = statement.executeQuery();
				if(resultset.next()){
					Timestamp a = resultset.getTimestamp("min(time)");
					Timestamp b = resultset.getTimestamp("max(time)");
					sqls.add("select time, lat, lng from LocationLog where devid =" +
							devid + " and provider = 'gps' and time between '" +
							a + "' and '" + b + "'");
					interval--;
				}
				e = s;
				if(interval < 0){
					break;
				}
			}
			return sqls;
		}finally{
			if(statement != null && !statement.isClosed()){
				statement.close();
			}
			if(resultset != null && !resultset.isClosed()){
				resultset.close();
			}
		}
	}

	public int selectCounts(String tableName, int devid, Timestamp s, Timestamp e) throws SQLException{
		if(s == null || e == null){
			return -1;
		}
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {
			String sql = "select count(*) from " + tableName + " where devid = ?" +
					" and time between ? and ?";
			statement = (PreparedStatement) connection.prepareStatement(sql);
			statement.setInt(1, devid);
			statement.setTimestamp(2, s);
			statement.setTimestamp(3, e);
			resultset = statement.executeQuery();
			resultset.next();
			int c = resultset.getInt("count(*)");
			return c;
		}finally{
			if(statement != null && !statement.isClosed()){
				statement.close();
			}
			if(resultset != null && !resultset.isClosed()){
				resultset.close();
			}
		}
	}

	public int selectLocationCounts(int year, int month, int day) throws SQLException{
		if(year < 0 || month < 0 || day < 0){
			return -1;
		}
		Statement statement = null;
		ResultSet resultset = null;
		try {
			statement = (Statement) connection.createStatement();
			String sql = "select count(*) from LocationLog where devid = 29 and " +
					"time between '" + year + "-" + month + "-" + day + " 00:00:00' " +
					"and '" + year + "-" + month + "-" + day + " 23:59:59'";
			resultset = statement.executeQuery(sql);
			resultset.next();
			int c = resultset.getInt("count(*)");
			return c;
		}finally{
			if(statement != null && !statement.isClosed()){
				statement.close();
			}
			if(resultset != null && !resultset.isClosed()){
				resultset.close();
			}
		}
	}

	public int selectStepCounts(int year, int month, int day) throws SQLException{
		if(year < 0 || month < 0 || day < 0){
			return -1;
		}
		Statement statement = null;
		ResultSet resultset = null;
		try {
			statement = (Statement) connection.createStatement();
			String sql = "select count(*) from step where devid = 29 and " +
					"time between '" + year + "-" + month + "-" + day + " 00:00:00' " +
					"and '" + year + "-" + month + "-" + day + " 23:59:59'";
			resultset = statement.executeQuery(sql);
			resultset.next();
			int c = resultset.getInt("count(*)");
			return c;
		}finally{
			if(statement != null && !statement.isClosed()){
				statement.close();
			}
			if(resultset != null && !resultset.isClosed()){
				resultset.close();
			}
		}
	}

	public ArrayList<MyGeoPoint> selectTrajectory(int devid, String from, String to) 
			throws SQLException, JSONException{
		PreparedStatement statement = null;
		ResultSet resultset = null;

		if(from.isEmpty() || to.isEmpty()){
			return null;
		}

		try {
			statement = (PreparedStatement) connection.prepareStatement(TRAJECTORY_SELECT_SQL + 
					"'" + from + "' and '" + to + "' order by time asc");
			statement.setInt(1, devid);

			System.out.println(statement);
			resultset = statement.executeQuery();

			ArrayList<MyGeoPoint> jArray = new ArrayList<MyGeoPoint>();
			while(resultset.next()){
				MyGeoPoint point = new MyGeoPoint(resultset.getTimestamp("time").getTime(),
						resultset.getDouble("lat"), resultset.getDouble("lng"), 
						resultset.getDouble("acc"), resultset.getDouble("speed"));	
				jArray.add(point);
			}
			return jArray;

		}finally{
			if(statement != null && !statement.isClosed()){
				statement.close();
			}
			if(resultset != null && !resultset.isClosed()){
				resultset.close();
			}
		}
	}
	
	public Map<Timestamp, JSONArray> selectCompass(String sql) throws SQLException, JSONException{
		Statement statement = null;
		ResultSet resultset = null;

		try {
			statement = connection.createStatement();
			System.out.println(sql);
			resultset = statement.executeQuery(sql);
			HashMap<Timestamp, JSONArray> r = new HashMap<Timestamp, JSONArray>();
			while(resultset.next()){
				r.put(resultset.getTimestamp("time"), new JSONArray(resultset.getString("value")));
			}
			return r;
		}finally{
			if(statement != null && !statement.isClosed()){
				statement.close();
			}
			if(resultset != null && !resultset.isClosed()){
				resultset.close();
			}
		}
	}
	
	public Map<Timestamp, JSONArray> selectOrientation(String sql) throws SQLException, JSONException{
		Statement statement = null;
		ResultSet resultset = null;

		try {
			statement = connection.createStatement();
			resultset = statement.executeQuery(sql);
			while(resultset.next()){
				HashMap<Timestamp, JSONArray> r = new HashMap<Timestamp, JSONArray>();
				r.put(resultset.getTimestamp("time"), new JSONArray(resultset.getString("value")));
				return r;
			}
			return null;
		}finally{
			if(statement != null && !statement.isClosed()){
				statement.close();
			}
			if(resultset != null && !resultset.isClosed()){
				resultset.close();
			}
		}
	}
}
